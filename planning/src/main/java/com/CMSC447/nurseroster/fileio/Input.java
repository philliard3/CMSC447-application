package com.CMSC447.nurseroster.fileio;

import java.util.ArrayList;

import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.Role;
import com.CMSC447.nurseroster.domain.Shift;
import com.CMSC447.nurseroster.domain.constraint.ScheduleConstraint;

public class Input {
	public static ArrayList<Role> roles;
	
	/**
	 * Contains the employees
	 * At index 0 is the null employee
	 */
	public static ArrayList<Employee> employees;
	public static ArrayList<Shift> shifts;
	public static ArrayList<ScheduleConstraint> scheduleConstraints;

	public static Employee nullEmployee;
	
	public static Role getRole(int id) {
		for (Role role: roles) {
			if(role.id == id) {
				return role;
			}
		}
		
		return null;
	}
	
	public static Employee getEmployee(int id) {
		for (Employee employee: employees) {
			if (employee.id == id) {
				return employee;
			}
		}
		return null;
	}
}
