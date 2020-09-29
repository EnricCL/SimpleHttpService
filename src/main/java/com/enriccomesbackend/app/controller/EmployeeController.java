package com.enriccomesbackend.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enriccomesbackend.app.entity.Employee;
import com.enriccomesbackend.app.service.EmployeeService;

@RestController
@RequestMapping("api/employees")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	//The employee type requires an enum, for example, five different types
	enum EmployeeType{
		PRESIDENT, MANAGER, ANALYST, CLERK, SALESMAN;
	}
	
	
	//Create a employee and includes it to the database
	@PostMapping
	public ResponseEntity<?> create (@RequestBody Employee employee){
		
		//Puts the salary according to employeeType
		String trade = employee.getEmployment().toUpperCase();
		EmployeeType employeeType;
		
		//if trade is not one of the five assigned type, an exception is caught and a not accepted status response is sent with a message.
		try {
			employeeType = EmployeeType.valueOf(trade);
		}catch(Exception e) {
			return ResponseEntity
						.status(HttpStatus.NOT_ACCEPTABLE)
						.body("The type of employee you entered is not accepted. Enter a President, a Manager, a Analyst, a Clerk or a Salesman.");
		}
		
		//else, type is correct and puts continues
		switch(employeeType) {
			case PRESIDENT:
				employee.setSalary(3000);
				break;
			case MANAGER:
				employee.setSalary(2500);
				break;
			case ANALYST:
				employee.setSalary(2000);
				break;
			case CLERK:
				employee.setSalary(1500);
				break;
			case SALESMAN:
				employee.setSalary(1000);
				break;					
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.save(employee));
		 
	}
	
	//Read all employees included in the database
	@GetMapping
	public ResponseEntity readAll(){
		
		//A list of employees is created
		try {
			List<Employee> employees = StreamSupport
					.stream(employeeService.findAll().spliterator(), false) //false = sequencial list
					.collect(Collectors.toList());
			
			//if the list is empty, warning. Else, continues	
			if(employees.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no employees!");
			}
			
			return ResponseEntity.status(HttpStatus.OK).body(employees);
			
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
	}
	
	//Read all employees by employment type
	@GetMapping("/{employment}")
	public ResponseEntity readAll(@PathVariable (name="employment") String type){
		
		EmployeeType employeeType;
		
		//if type is not one of the five assigned type, an exception is caught and a not accepted status response is sent with a message.
		try {
			employeeType = EmployeeType.valueOf(type.toUpperCase());
		}catch(Exception e) {
			return ResponseEntity
						.status(HttpStatus.NOT_ACCEPTABLE)
						.body("The type of employee you searched is not accepted. Search a President, a Manager, a Analyst, a Clerk or a Salesman.");
		}
				
		//A list of employees is created
		List <Employee> employees = StreamSupport
				.stream(employeeService.findAll().spliterator(), false) //false = sequencial list
				.collect(Collectors.toList());
 
		//Create new list for type of employment variable
		List <Employee> employeesType = new ArrayList<>();
		
		//For-each, compares one type of the list item to the type of the item in the other list
		for(Employee employee : employees) {
			String typeEmployees = employee.getEmployment().toString();
			if(typeEmployees.equals(type)) {
				employeesType.add(employee); //if equals, it's included to employeesType list
			}
		}
		
		//if the list is empty, warning. Else, continues	
		if(employeesType.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no employees of type " + type + "!");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(employeesType); //return a list of only one type of employment type
		
	}
	
	
	//Update the employee by id
	@PutMapping("/{id}")
	public ResponseEntity update(@RequestBody Employee employeeDetails, @PathVariable (name ="id") Long employeeId) {
		
		//Optional object because the object can be null
		Optional<Employee> employee = employeeService.findById(employeeId);
		
		//if employee with this id is not present, return a respone not found
		if(!employee.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		//else, continue and update data employee with a response created
		employee.get().setName(employeeDetails.getName());
		employee.get().setEmployment(employeeDetails.getEmployment());
		employee.get().setSalary(employeeDetails.getSalary());	
		return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.save(employee.get()));
	}
	
	//Delete the employee by id
	@DeleteMapping("/{id}")
	public ResponseEntity delete(@PathVariable (name="id") Long employeeId) {
		
		if(!employeeService.findById(employeeId).isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		employeeService.deleteById(employeeId);
		return ResponseEntity.ok().build();
	}
	
	
}
