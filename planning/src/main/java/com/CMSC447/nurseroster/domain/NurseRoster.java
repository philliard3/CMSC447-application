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

    @ProblemFactCollectionProperty
    public ArrayList<ScheduleConstraint> scheduleConstraints;

    public HardSoftScore score;

    @PlanningEntityCollectionProperty
    public ArrayList<ShiftAssignment> shiftAssignments;
    
    
    public ArrayList<Employee> employees;
    
    public NurseRoster(ArrayList<Employee> employees, ArrayList<Shift> shifts) {
    	this.employees = employees;
    	Random rand = new Random();
    	
    	this.shiftAssignments = new ArrayList<ShiftAssignment>();
    	
    	for(Shift shift: shifts) {
    		Employee employee = employees.get(rand.nextInt(employees.size()));
    		ShiftAssignment assignment = new ShiftAssignment(employee, shift);
    		this.shiftAssignments.add(assignment);
    	}
    	
    }
    

    @PlanningScore
    public HardSoftScore getScore() {
        HardSoftScore score = HardSoftScore.ZERO;
        
        // Sum of employee scores
        for (Employee employee: employees) {
        	score.add(employee.score(shiftAssignments));
        }

        // Sum of schedule scores
        for(ScheduleConstraint scheduleConstraint :scheduleConstraints ) {
            score.add(scheduleConstraint.score(shiftAssignments));
        }

        return score;
    }

    @PlanningEntityCollectionProperty
    public List<ShiftAssignment> getShiftAssignments() {
        return shiftAssignments;
    }

    @ValueRangeProvider(id="employeeRange")
    public List<Employee> getEmployees() {
        return employees;
    }

}
