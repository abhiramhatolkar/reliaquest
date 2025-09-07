package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.DeleteResponse;
import com.reliaquest.api.dto.EmployeeCreateRequest;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.entity.EmployeeByIdResponse;
import com.reliaquest.api.entity.EmployeeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

	@Mock
	private RestTemplate restTemplate;

	private EmployeeServiceImpl employeeService;
	private int age;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		employeeService = new EmployeeServiceImpl(restTemplate);
		age = 11;
	}

	@Test
	void testGetAllEmployees() {
		EmployeeResponse mockResponse = new EmployeeResponse();
		List<Employee> employees = Arrays.asList(
						new Employee("1", "Tiger Nixon", 320800, age, "Dynamic Hospitality Technician",""),
						new Employee("2", "Garrett Winters", 170750, age, "Lead", "")
		);
		mockResponse.setData(employees);
		when(restTemplate.getForObject(anyString(), eq(EmployeeResponse.class))).thenReturn(mockResponse);

		List<Employee> result = employeeService.getAllEmployees();

		assertEquals(employees, result);
	}

	@Test
	void testGetEmployeesByNameSearch() {
		EmployeeResponse mockResponse = new EmployeeResponse();
		List<Employee> employees = Arrays.asList(
						new Employee("1", "Tiger Nixon", 320800, age, "Dynamic Hospitality Technician",""),
						new Employee("2", "Garrett Winters", 170750, age, "Lead", "")
		);
		mockResponse.setData(employees);
		when(restTemplate.getForObject(anyString(), eq(EmployeeResponse.class))).thenReturn(mockResponse);

		String nameToSearch = "Garrett Winters";
		List<Employee> result = employeeService.getEmployeesByNameSearch(nameToSearch);

		assertEquals(1, result.size());
		assertEquals("Garrett Winters", result.get(0).getEmployeeName());;
	}

	@Test
	void testGetEmployeeById() {
		EmployeeByIdResponse mockResponse = new EmployeeByIdResponse();
		Employee employee = new Employee("1", "Tiger Nixon", 320800, age, "Dynamic Hospitality Technician","" );
		mockResponse.setData(employee);
		when(restTemplate.getForObject(anyString(), eq(EmployeeByIdResponse.class))).thenReturn(mockResponse);

		String idToFetch = "1";
		EmployeeByIdResponse result = employeeService.getEmployeeById(idToFetch);

		assertEquals(employee, result.getData());
	}

	@Test
	void testGetHighestSalaryOfEmployees() {
		EmployeeResponse mockResponse = new EmployeeResponse();
		List<Employee> employees = Arrays.asList(
						new Employee("1", "Tiger Nixon", 320800, age,  "Dynamic Hospitality Technician",""),
						new Employee("2", "Garrett Winters", 320809, age, "Lead", "")
		);
		mockResponse.setData(employees);
		when(restTemplate.getForObject(anyString(),eq(EmployeeResponse.class))).thenReturn(mockResponse);

		int result = employeeService.getHighestSalaryOfEmployees();

		assertEquals(320809, result);;
	}

	@Test
	void testGetTop10HighestEarningEmployeeNames() {
		EmployeeResponse mockResponse = new EmployeeResponse();
		List<Employee> employees = Arrays.asList(
						new Employee("2", "Tiger Nixon", 2, age, "", ""),
						new Employee("3", "Tiger Nixon", 3, age, "", ""),
						new Employee("4", "Tiger Nixon", 4, age, "", ""),
						new Employee("1", "Tiger Nixon", 1, age, "", ""),
						new Employee("5", "Tiger Nixon", 5, age, "", ""),
						new Employee("6", "Tiger Nixon", 6, age, "", ""),
						new Employee("7", "Tiger Nixon", 7, age, "", "" ),
						new Employee("8", "Tiger Nixon", 8, age, "", "" ),
						new Employee("9", "Tiger Nixon", 9, age, "", ""),
						new Employee("10", "Tiger Nixon", 10, age, "", "" ),
						new Employee("11", "Tiger Nixon", 11, age, "", "" )
		);
		mockResponse.setData(employees);
		when(restTemplate.getForObject(anyString(), eq(EmployeeResponse.class))).thenReturn(mockResponse);

		List<Employee> result = employeeService.getTopHighestEarningEmployees(10);

		assertEquals(10, result.size());
	}

	@Test
	void testCreateEmployee() throws Exception {
		EmployeeCreateRequest employeeRequest = new EmployeeCreateRequest();
		employeeRequest.setAge(25);
		employeeRequest.setName("Harry");
		employeeRequest.setSalary(20000);
		employeeRequest.setTitle("Lead");

		Employee employee = new Employee("1", "Harry", 20000, 25, "Lead", "harry@company.com");
		EmployeeByIdResponse mockResponse = new EmployeeByIdResponse();
		mockResponse.setData(employee);
		mockResponse.setStatus("Successfully processed request.");

		when(restTemplate.exchange(
						anyString(),
						any(),
						any(),
						eq(EmployeeByIdResponse.class))
		).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

		EmployeeByIdResponse result = employeeService.createEmployee(employeeRequest);

		assertEquals(mockResponse, result);
	}

	@Test
	void testDeleteEmployee() throws Exception {
		String idToDelete = "1";
		String expectedUrl = "http://localhost:8112/api/v1/employee";

		DeleteResponse mockDeleteResponse = new DeleteResponse();
		mockDeleteResponse.setData(true);

		ResponseEntity<DeleteResponse> responseEntity = new ResponseEntity<>(mockDeleteResponse, HttpStatus.OK);

		when(restTemplate.exchange(
						eq(expectedUrl),
						eq(HttpMethod.DELETE),
						any(HttpEntity.class),
						eq(DeleteResponse.class)
		)).thenReturn(responseEntity);

		employeeService.deleteEmployee(idToDelete);

		verify(restTemplate, times(1)).exchange(
						eq(expectedUrl),
						eq(HttpMethod.DELETE),
						any(HttpEntity.class),
						eq(DeleteResponse.class)
		);
	}
}

