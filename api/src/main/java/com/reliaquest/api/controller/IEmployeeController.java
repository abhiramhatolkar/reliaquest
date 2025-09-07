package com.reliaquest.api.controller;

import java.util.List;

import com.reliaquest.api.dto.EmployeeCreateRequest;
import com.reliaquest.api.entity.Employee;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Please <b>do not</b> modify this interface. If you believe there's a bug or the API contract does not align with our
 * mock web server... that is intentional. Good luck!
 *
 * @implNote It's uncommon to have a web controller implement an interface; We include such design pattern to
 * ensure users are following the desired input/output for our API contract, as outlined in the code assessment's README.
 *
 * @param <Entity> object representation of an Employee
 * @param <Input> object representation of a request body for creating Employee(s)
 */
public interface IEmployeeController {

    @GetMapping()
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<List<Employee>> getAllEmployees();

    @GetMapping("/search")
    @ApiResponse(responseCode = "400", description = "Invalid name parameter")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<List<Employee>> getEmployeesByNameSearch(@RequestParam @NotBlank String name);

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @ApiResponse(responseCode = "400", description = "Invalid ID format")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<Employee> getEmployeeById(@PathVariable @NotBlank String id);

    @GetMapping("/highest-salary")
    @ApiResponse(responseCode = "404", description = "No employees found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<Integer> getHighestSalaryOfEmployees();

    @GetMapping("/top-10-highest-earning")
    @ApiResponse(responseCode = "404", description = "No employees found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames();

    @PostMapping("/create")
    @ApiResponse(responseCode = "400", description = "Invalid employee data")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<Employee> createEmployee(@RequestBody @Valid  EmployeeCreateRequest employee);

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @ApiResponse(responseCode = "400", description = "Invalid ID format")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<String> deleteEmployeeById(@PathVariable @NotBlank String id);
}