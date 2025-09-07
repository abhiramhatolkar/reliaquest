package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.DeleteResponse;
import com.reliaquest.api.dto.EmployeeCreateRequest;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.entity.EmployeeByIdResponse;
import com.reliaquest.api.entity.EmployeeResponse;
import com.reliaquest.api.exception.BadRequestException;
import com.reliaquest.api.exception.ServerErrorException;
import com.reliaquest.api.service.EmployeeService;


import com.reliaquest.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private static final String BASE_URL = "http://localhost:8112/api/v1";
  private static final String EMPLOYEE_URL = BASE_URL + "/employee";
  private static final String EMPLOYEE_BY_ID_URL = BASE_URL + "/employee/";
  private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

  private final RestTemplate restTemplate;

  @Override
  @Cacheable(value = "employees", key = "'all'")
  public List<Employee> getAllEmployees() {
    logger.info("Cache miss. Fetching all employees from HTTP API.");
    try {
        return restTemplate.getForObject(EMPLOYEE_URL, EmployeeResponse.class).getData();
    } catch (RestClientException e) {
        handleRestClientException("Failed to fetch employees from external API", e);
        return null;
    }
  }

  @Override
  @Cacheable(value = "employee", key = "#id")
  public EmployeeByIdResponse getEmployeeById(String id) {
    validateId(id);
    logger.info("Cache miss. Fetching employee by id from HTTP API.");

    try {
        return restTemplate.getForObject(EMPLOYEE_BY_ID_URL + id, EmployeeByIdResponse.class);
    } catch (RestClientException e) {
        handleRestClientException("Error while fetching employee with ID: " + id, e);
        return null;
    }
  }

  @Override
  @Cacheable(value = "employeesByName", key = "#name.toLowerCase()")
  public List<Employee> getEmployeesByNameSearch(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new BadRequestException("Name parameter cannot be empty");
    }

    try {
      List<Employee> employees = getAllEmployees();
      return employees.stream()
              .filter(employee -> employee.getEmployeeName() != null &&
                      employee.getEmployeeName().equalsIgnoreCase(name.trim()))
              .collect(Collectors.toList());
    } catch (Exception e) {
      handleRestClientException("Error searching employees by name: " + name, e);
      return null;
    }
  }

  @Override
  @Cacheable(value = "highestSalary", key = "'max'")
  public int getHighestSalaryOfEmployees() {
    try {
      List<Employee> employeeList = getTopHighestEarningEmployees(1);
      if (employeeList.isEmpty()) {
        throw new NotFoundException("No employees available to determine highest salary");
      }
      return employeeList.get(0).getEmployeeSalary();
    } catch (Exception e) {
        handleRestClientException("Error determining highest salary", e);
        return 0;
    }
  }

  @Override
  @Cacheable(value = "topEarners", key = "#size")
  public List<Employee> getTopHighestEarningEmployees(int size) {
      if (size <= 0) {
        throw new BadRequestException("Size must be a positive number");
      }

      try {
        List<Employee> employeeList = getAllEmployees();
        if (employeeList.isEmpty()) {
          throw new NotFoundException("No employees available");
        }

        return employeeList.stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeSalary).reversed())
                .limit(size)
                .collect(Collectors.toList());
      } catch (Exception e) {
        handleRestClientException("Error fetching top earning employees", e);
        return null;
      }
  }

  @Override
  @Caching(
    put = @CachePut(value = "employee", key = "#result.data.id", condition = "#result != null"),
    evict = {
      @CacheEvict(value = "employees", key = "'all'"),
      @CacheEvict(value = "topEarners", allEntries = true),
      @CacheEvict(value = "highestSalary", allEntries = true),
      @CacheEvict(value = "employeesByName", allEntries = true)
    }
  )
  public EmployeeByIdResponse createEmployee(EmployeeCreateRequest employee) {
    try {
      HttpEntity<EmployeeCreateRequest> requestEntity = new HttpEntity<>(employee, getJsonHeaders());
      ResponseEntity<EmployeeByIdResponse> response = restTemplate.exchange(
              EMPLOYEE_URL,
              HttpMethod.POST,
              requestEntity,
              EmployeeByIdResponse.class
      );
      return response.getBody();
    } catch (Exception e) {
      handleRestClientException("Error creating employee", e);
      return null;
    }
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "employee", key = "#name", beforeInvocation = true),
    @CacheEvict(value = "employees", key = "'all'"),
    @CacheEvict(value = "topEarners", allEntries = true),
    @CacheEvict(value = "highestSalary", allEntries = true),
    @CacheEvict(value = "employeesByName", allEntries = true)
  })
  public void deleteEmployee(String name) {
    validateId(name);

    try {
      Map<String, Object> requestBody = new HashMap<>();
      requestBody.put("name", name);
      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, getJsonHeaders());

      restTemplate.exchange(
              EMPLOYEE_URL,
              HttpMethod.DELETE,
              requestEntity,
              DeleteResponse.class
      );
    } catch (Exception e) {
        handleRestClientException("Error deleting employee", e);
        return;
    }
  }


  private HttpHeaders getJsonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private void validateId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new BadRequestException("Employee ID cannot be empty");
    }
  }

  private void handleRestClientException(String message, Exception e) {
    if (e instanceof HttpClientErrorException.BadRequest) {
      throw new BadRequestException("Bad request: " + e.getMessage(), e);
    } else if (e instanceof HttpClientErrorException.NotFound) {
      throw new NotFoundException("Resource not found: " + e.getMessage(), e);
    } else if (e instanceof HttpServerErrorException) {
      throw new ServerErrorException("Server error: " + e.getMessage(), e);
    } else if (e instanceof RestClientException) {
      throw new ServerErrorException(message, e);
    } else {
      throw new ServerErrorException("Unexpected error: " + e.getMessage(), e);
    }
  }
}
