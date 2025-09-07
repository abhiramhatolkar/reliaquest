package com.reliaquest.api.dto;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCreateRequest {

	@NotBlank(message = "Name cannot be blank")
	private String name;

	@Min(value = 1, message = "Salary must be greater than zero")
	private int salary;

	@Min(value = 16, message = "Age must be at least 16")
	@Max(value = 75, message = "Age must be at most 75")
	private int age;

	@NotBlank(message = "Title cannot be blank")
	private String title;
}