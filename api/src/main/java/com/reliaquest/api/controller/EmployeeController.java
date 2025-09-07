package com.reliaquest.api.controller;

import com.reliaquest.api.dto.EmployeeCreateRequest;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.entity.EmployeeByIdResponse;
import com.reliaquest.api.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employees Controller", description = "APIs for Managing Employees")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Get all employees", description = "Retrieve a list of all employees")
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Search employees by name", description = "Retrieve a list of employees matching the provided name")
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@RequestParam @NotBlank String name) {
        List<Employee> employees = employeeService.getEmployeesByNameSearch(name);
        if (employees.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employee by ID", description = "Retrieve employee details using their ID")
    @Override
    public ResponseEntity<Employee> getEmployeeById(@PathVariable @NotBlank String id) {
        EmployeeByIdResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee.getData());
    }

    @Operation(summary = "Get highest salary", description = "Retrieve the highest salary among all employees")
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        int highestSalary = employeeService.getHighestSalaryOfEmployees();
        return ResponseEntity.ok(highestSalary);
    }

    @Operation(summary = "Get top 10 highest earning employees", description = "Retrieve the names of top 10 employees with highest earnings")
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<Employee> employees = employeeService.getTopHighestEarningEmployees(10);
        if (employees.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<String> employeeNames = employees.stream()
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(employeeNames);
    }

    @Operation(summary = "Create a new employee", description = "Create a new employee with provided details")
    @Override
    public ResponseEntity<Employee> createEmployee(@RequestBody @Valid EmployeeCreateRequest employee) {
        EmployeeByIdResponse savedEmployee = employeeService.createEmployee(employee);
        return new ResponseEntity<>(savedEmployee.getData(), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete employee by ID", description = "Delete an employee based on their ID")
    @Override
    public ResponseEntity<String> deleteEmployeeById(@PathVariable @NotBlank String id) {
        EmployeeByIdResponse employeeResponse = employeeService.getEmployeeById(id);
        String employeeName = employeeResponse.getData().getEmployeeName();
        employeeService.deleteEmployee(employeeName);
        return ResponseEntity.ok("Employee with name " + employeeName + " got deleted successfully");
    }
}
