/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.CMSC447.nurseroster.domain;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.constraint.ScheduleConstraint;

@PlanningSolution
public class NurseRoster {

	public static final int NULL_EMPLOYEE_ID = -1;
    
	@ProblemFactCollectionProperty
    private ArrayList<ScheduleConstraint> scheduleConstraints;

    public HardSoftScore score;


	private ArrayList<ShiftAssignment> shiftAssignments;
	
	@ProblemFactCollectionProperty
	private ArrayList<Shift> shifts;
    
    private ArrayList<Employee> employees;
    
    public NurseRoster(ArrayList<Employee> employees, ArrayList<Shift> shifts) {
    	this.employees = employees;
    	Random rand = new Random();
    	
    	this.shifts = shifts;
    	this.shiftAssignments = new ArrayList<ShiftAssignment>();
    	
    	for(Shift shift: shifts) {
    		Employee employee = employees.get(rand.nextInt(employees.size()));
    		ShiftAssignment assignment = new ShiftAssignment(employee, shift);
    		this.shiftAssignments.add(assignment);
    	}
    	
    }
    
    private HashMap<Integer, ArrayList<Shift>> getShiftsByEmployeeID(){
    	HashMap<Integer, ArrayList<Shift>> shiftsByEmployeeID = new HashMap<Integer, ArrayList<Shift>>();
    	
    	for (Employee employee: employees) {
    		shiftsByEmployeeID.put(employee.id, new ArrayList<Shift>());
    	}
    	
    	for (ShiftAssignment assignment: shiftAssignments) {
    		shiftsByEmployeeID.get(assignment.employee).add(assignment.shift);
    	}
    	
    	return shiftsByEmployeeID;
    }

    @PlanningScore
    public HardSoftScore getScore() {
        score = HardSoftScore.ZERO;
        
        HashMap<Integer, ArrayList<Shift>> shiftsByEmployeeID = getShiftsByEmployeeID();
        
        // Sum of employee scores
        for (Employee employee: employees) {
        	score.add(employee.score(shiftsByEmployeeID.get(employee.id)));
        }

        // Sum of schedule scores
        for(ScheduleConstraint scheduleConstraint :scheduleConstraints ) {
            score.add(scheduleConstraint.score(shiftAssignments));
        }

        return score;
    }

    @PlanningEntityCollectionProperty
    public ArrayList<ShiftAssignment> getShiftAssignments() {
        return shiftAssignments;
    }

    @ValueRangeProvider(id="employeeRange")
    public ArrayList<Employee> getEmployees() {
        return employees;
    }
    
    /**
	 * @return the scheduleConstraints
	 */
    @ProblemFactCollectionProperty
	public ArrayList<ScheduleConstraint> getScheduleConstraints() {
		return scheduleConstraints;
	}

}
