/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.CMSC447.nurseroster.fileio.Input;
import com.CMSC447.nurseroster.solver.EmployeeStrengthComparator;
import com.CMSC447.nurseroster.solver.ShiftAssignmentDifficultyComparator;

@PlanningEntity(difficultyComparatorClass = ShiftAssignmentDifficultyComparator.class) 
public class ShiftAssignment implements Comparable<ShiftAssignment>{

	public ShiftAssignment() {
		this.employee = Input.nullEmployee;
		this.shift = Input.shifts.get(0);
	}
	
    // constructor
	public ShiftAssignment(Employee employee, Shift shift){
		this.employee = employee; 
		this.shift = shift;
	}

    @PlanningVariable(valueRangeProviderRefs = {"employeeRange"}, strengthComparatorClass = EmployeeStrengthComparator.class)
    public Employee employee;

    public final Shift shift;


    public int getShiftDateDayIndex() {
        return shift.startTime.getDayOfMonth();
    }
    
    public int compareTo(ShiftAssignment other) {
    	return this.shift.compareTo(other.shift);
    }
}
