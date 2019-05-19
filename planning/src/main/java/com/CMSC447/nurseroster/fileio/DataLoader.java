package com.CMSC447.nurseroster.fileio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.Role;
import com.CMSC447.nurseroster.domain.Shift;
import com.CMSC447.nurseroster.domain.constraint.Constraint;
import com.CMSC447.nurseroster.domain.constraint.ConstraintFactory;
import com.CMSC447.nurseroster.domain.constraint.PersonalConstraint;
import com.CMSC447.nurseroster.domain.constraint.ScheduleConstraint;
import com.CMSC447.nurseroster.domain.constraint.basic.MandatoryShiftConstraint;

public class DataLoader {
	
	public static Constraint processConstraint(JSONObject rawConstraint) throws JSONException{
		int id = rawConstraint.getInt("id");
		int priority = rawConstraint.getInt("priority");
		boolean isHard = rawConstraint.getBoolean("isHard");
		String type = rawConstraint.getString("type");
		JSONObject params = rawConstraint.getJSONObject("parameters");
		
		return ConstraintFactory.getConstraint(id, priority, isHard, type, params);
	}
	
	public static Role processRole(JSONObject rawRole) throws JSONException {
		int id = rawRole.getInt("id");
		ArrayList<PersonalConstraint> constraints = new ArrayList<PersonalConstraint>();
		
		JSONArray rawConstraints = rawRole.getJSONArray("constraints");
		
		for(int i = 0; i < rawConstraints.length(); i++) {
			Constraint constraint = processConstraint(rawConstraints.getJSONObject(i));
			if (constraint instanceof PersonalConstraint) {
				constraints.add((PersonalConstraint) constraint);
			}
			else {
				throw new JSONException("Roles Cannot Have Schedule Constraints");
			}
		}
		
		return new Role(id, constraints);
	}
	
	public static Employee processEmployee(JSONObject rawEmployee) throws JSONException {
		int id = rawEmployee.getInt("id");
		ArrayList<PersonalConstraint> constraints = new ArrayList<PersonalConstraint>();
		
		JSONArray rawConstraints = rawEmployee.getJSONArray("constraints");
		
		for(int i = 0; i < rawConstraints.length(); i++) {
			Constraint constraint = processConstraint(rawConstraints.getJSONObject(i));
			if (constraint instanceof PersonalConstraint) {
				constraints.add((PersonalConstraint) constraint);
			}
			else {
				throw new JSONException("Employees Cannot Have Schedule Constraints");
			}
		}
		
		ArrayList<Role> roles = new ArrayList<Role>();
		
		JSONArray rawRoleIDs = rawEmployee.getJSONArray("roles");
		for(int i = 0; i < rawRoleIDs.length(); i++) {
			roles.add(Input.getRole(rawRoleIDs.getInt(i)));
		}
		
		return new Employee(id, roles, constraints);
	}
	
	public static Shift processShift(JSONObject rawShift) throws JSONException {
		String startTimeString = rawShift.getString("startTime");
		String endTimeString = rawShift.getString("endTime");
		JSONArray rawTypes = rawShift.getJSONArray("shiftTypes");
		String location = rawShift.getString("location");
		boolean mandatory = rawShift.getBoolean("mandatory");
		JSONArray roleIDs = rawShift.getJSONArray("permittedRoles");
		
		ArrayList<Role> roles = new ArrayList<Role>();
		for(int i = 0; i < roleIDs.length(); i++) {
			roles.add(Input.getRole(roleIDs.getInt(i)));
		}
		
		LocalDateTime startTime = DateTimeParser.parseDateTime(startTimeString);
		LocalDateTime endTime = DateTimeParser.parseDateTime(endTimeString);
		
		ArrayList<String> types = new ArrayList<String>();
		for(int i = 0; i < rawTypes.length(); i++) {
			types.add(rawTypes.getString(i));
		}
		
		return new Shift(startTime, endTime, types, location, mandatory, roles);
	}
	
	public static boolean loadFile(String filename){
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)));
        }
        catch(IOException ex){
        	System.out.println(Paths.get(filename));
        	ex.printStackTrace();
            return false;
        }
        if(content.length() == 0){
        	System.out.println("No Content");
            return false;
        }
        
        
		try {
			JSONObject obj = new JSONObject(content);
			JSONArray rawRoles = obj.getJSONArray("roles");
	        JSONArray rawEmployees = obj.getJSONArray("employees");
	        JSONArray rawShifts = obj.getJSONArray("shifts");
	        JSONArray rawScheduleConstraints = obj.getJSONArray("scheduleConstraints");
	        
	        Input.roles = new ArrayList<Role>();
	        for(int i = 0; i < rawRoles.length(); i++) {
	        	Input.roles.add(processRole(rawRoles.getJSONObject(i)));
	        }
	        
	        
	        Input.shifts = new ArrayList<Shift>();
	        for(int i = 0; i < rawShifts.length(); i++) {
	        	Input.shifts.add(processShift(rawShifts.getJSONObject(i)));
	        }
	        
	        Collections.sort(Input.shifts);
	        
	        LocalDate firstShift = Input.shifts.get(0).startTime.toLocalDate();
	        
	        
	        Input.employees = new ArrayList<Employee>();
	        
	        // Create the null employee which represents an unassigned shift
	        Input.nullEmployee = Employee.NULL();
	        Input.employees.add(Input.nullEmployee);
	        
	        		
	        for(int i = 0; i < rawEmployees.length(); i++) {
	        	Input.employees.add(processEmployee(rawEmployees.getJSONObject(i)));
	        }
	        
	        
	        
	        Input.scheduleConstraints = new ArrayList<ScheduleConstraint>();
	        
	        // Create the schedule constraint that states that no mandatory shift can have the null employee
	        Input.scheduleConstraints.add(new MandatoryShiftConstraint());
	        
	        for(int i = 0; i < rawScheduleConstraints.length(); i++) {
				Constraint constraint = processConstraint(rawScheduleConstraints.getJSONObject(i));
				if (constraint instanceof ScheduleConstraint) {
					Input.scheduleConstraints.add((ScheduleConstraint) constraint);
				}
				else {
					throw new JSONException("Employees Cannot Have Schedule Constraints");
				}
			}
	        
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
    }
}
