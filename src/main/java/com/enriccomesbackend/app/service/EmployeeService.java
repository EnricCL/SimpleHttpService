package com.enriccomesbackend.app.service;

import java.util.Optional;

import com.enriccomesbackend.app.entity.Employee;



public interface EmployeeService {
	
public Iterable<Employee> findAll();
	
	public Optional<Employee> findById(Long id);
	
	public Employee save(Employee employee);
	
	public void deleteById(Long id);

}
