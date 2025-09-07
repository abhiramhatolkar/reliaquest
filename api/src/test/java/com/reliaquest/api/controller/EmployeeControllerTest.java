package com.reliaquest.api.controller;

import com.reliaquest.api.dto.EmployeeCreateRequest;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.entity.EmployeeByIdResponse;
import com.reliaquest.api.entity.EmployeeResponse;
import com.reliaquest.api.exception.NotFoundException;
import com.reliaquest.api.exception.ServerErrorException;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

	@Mock
	private EmployeeService employeeService;

	@InjectMocks
	private EmployeeController employeeController;

	private Employee employee1;
	private Employee employee2;
	private List<Employee> employeeList;

	@BeforeEach
	void setUp() {
		employee1 = new Employee("1", "John Doe", 100000, 30, "Developer", "john@example.com");
		employee2 = new Employee("2", "Jane Smith", 120000, 35, "Manager", "jane@example.com");
		employeeList = Arrays.asList(employee1, employee2);
	}

	@Test
	void getAllEmployees_ShouldReturnAllEmployees() {
		EmployeeResponse response = new EmployeeResponse();
		response.setData(employeeList);
		when(employeeService.getAllEmployees()).thenReturn(employeeList);

		ResponseEntity<List<Employee>> result = employeeController.getAllEmployees();

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(2, result.getBody().size());
		verify(employeeService, times(1)).getAllEmployees();
	}

	@Test
	void getEmployeesByNameSearch_ShouldReturnMatchingEmployees() {
		String searchName = "John";
		when(employeeService.getEmployeesByNameSearch(searchName)).thenReturn(List.of(employee1));

		ResponseEntity<List<Employee>> result = employeeController.getEmployeesByNameSearch(searchName);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(1, result.getBody().size());
		assertEquals("John Doe", result.getBody().get(0).getEmployeeName());
		verify(employeeService, times(1)).getEmployeesByNameSearch(searchName);
	}

	@Test
	void getEmployeeById_ShouldReturnEmployee() {
		String employeeId = "1";
		EmployeeByIdResponse response = new EmployeeByIdResponse();
		response.setData(employee1);
		when(employeeService.getEmployeeById(employeeId)).thenReturn(response);

		ResponseEntity<Employee> result = employeeController.getEmployeeById(employeeId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(employee1, result.getBody());
		verify(employeeService, times(1)).getEmployeeById(employeeId);
	}

	@Test
	void getHighestSalaryOfEmployees_ShouldReturnHighestSalary() {
		int highestSalary = 120000;
		when(employeeService.getHighestSalaryOfEmployees()).thenReturn(highestSalary);

		ResponseEntity<Integer> result = employeeController.getHighestSalaryOfEmployees();

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(highestSalary, result.getBody());
		verify(employeeService, times(1)).getHighestSalaryOfEmployees();
	}

	@Test
	void getTopTenHighestEarningEmployeeNames_ShouldReturnTop10Names() {
		when(employeeService.getTopHighestEarningEmployees(10)).thenReturn(employeeList);

		ResponseEntity<List<String>> result = employeeController.getTopTenHighestEarningEmployeeNames();

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(2, result.getBody().size());
		assertTrue(result.getBody().contains("John Doe"));
		assertTrue(result.getBody().contains("Jane Smith"));
		verify(employeeService, times(1)).getTopHighestEarningEmployees(10);
	}

	@Test
	void createEmployee_ShouldReturnCreatedEmployee() {
		EmployeeCreateRequest newEmployee = new EmployeeCreateRequest("New Employee", 80000, 25, "Trainee");
		Employee savedEmployee = new Employee("3", "New Employee", 80000, 25, "Trainee", "new@example.com");
		EmployeeByIdResponse response = new EmployeeByIdResponse();
		response.setData(savedEmployee);
		when(employeeService.createEmployee(newEmployee)).thenReturn(response);

		ResponseEntity<Employee> result = employeeController.createEmployee(newEmployee);

		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		assertEquals(savedEmployee, result.getBody());
		verify(employeeService, times(1)).createEmployee(newEmployee);
	}

	@Test
	void deleteEmployeeById_ShouldReturnSuccessMessage() {
		String employeeId = "1";
		String employeeName = "John Doe";

		EmployeeByIdResponse mockResponse = new EmployeeByIdResponse();
		Employee mockEmployee = new Employee();
		mockEmployee.setEmployeeName(employeeName);
		mockResponse.setData(mockEmployee);

		when(employeeService.getEmployeeById(employeeId)).thenReturn(mockResponse);
		doNothing().when(employeeService).deleteEmployee(employeeName);

		ResponseEntity<String> result = employeeController.deleteEmployeeById(employeeId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals("Employee with name John Doe got deleted successfully", result.getBody());

		verify(employeeService, times(1)).getEmployeeById(employeeId);
		verify(employeeService, times(1)).deleteEmployee(employeeName);
	}


	@Test
	void getAllEmployees_ShouldThrowException_WhenServiceFails() {
		when(employeeService.getAllEmployees()).thenThrow(new ServerErrorException("Service unavailable"));

		assertThrows(ServerErrorException.class, () -> {
			employeeController.getAllEmployees();
		});
		verify(employeeService, times(1)).getAllEmployees();
	}

	@Test
	void getEmployeeById_ShouldThrowException_WhenEmployeeNotFound() {
		String employeeId = "999";
		when(employeeService.getEmployeeById(employeeId)).thenThrow(new NotFoundException("Employee not found"));

		assertThrows(NotFoundException.class, () -> {
			employeeController.getEmployeeById(employeeId);
		});
		verify(employeeService, times(1)).getEmployeeById(employeeId);
	}
}
