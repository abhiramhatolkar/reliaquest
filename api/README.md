# Implement this API

#### In this assessment you will be tasked with filling out the functionality of different methods that will be listed further down.

These methods will require some level of API interactions with Mock Employee API at http://localhost:8112/api/v1/employee.

Please keep the following in mind when doing this assessment:
* clean coding practices
* test driven development
* logging
* scalability

### Endpoints to implement

_See `com.reliaquest.api.controller.IEmployeeController` for details._

getAllEmployees()

    output - list of employees
    description - this should return all employees

getEmployeesByNameSearch(...)

    path input - name fragment
    output - list of employees
    description - this should return all employees whose name contains or matches the string input provided

getEmployeeById(...)

    path input - employee ID
    output - employee
    description - this should return a single employee

getHighestSalaryOfEmployees()

    output - integer of the highest salary
    description - this should return a single integer indicating the highest salary of amongst all employees

getTop10HighestEarningEmployeeNames()

    output - list of employees
    description - this should return a list of the top 10 employees based off of their salaries

createEmployee(...)

    body input - attributes necessary to create an employee
    output - employee
    description - this should return a single employee, if created, otherwise error

deleteEmployeeById(...)

    path input - employee ID
    output - name of the employee
    description - this should delete the employee with specified id given, otherwise error

### Swagger Integration

To enable Swagger for API documentation, follow these steps:

1. Add the following dependencies to your `build.gradle` file:
   ```groovy
   implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0'

2. Access the Swagger UI: Once the application is running, navigate to the following URL to view the Swagger UI:  
   http://localhost:8111/swagger-ui/index.html
3. OpenAPI JSON: The OpenAPI specification can be accessed at:
   http://localhost:8111/v3/api-docs


### Testing
Please include proper integration and/or unit tests.
