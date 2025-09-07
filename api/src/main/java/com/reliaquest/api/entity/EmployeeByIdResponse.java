package com.reliaquest.api.entity;

import lombok.Data;

@Data
public class EmployeeByIdResponse {
	private String status;
	private Employee data;
}
