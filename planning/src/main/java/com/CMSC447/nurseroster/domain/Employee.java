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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.constraint.PersonalConstraint;
import com.CMSC447.nurseroster.fileio.Input;

public class Employee  {
	public ArrayList<PersonalConstraint> constraints;
	public ArrayList<Role> roles;
	public int id;

    //constructor
	public Employee(int id, ArrayList<Role> roles, ArrayList<PersonalConstraint> constraints){
		this.id = id;
		this.constraints = constraints;
		this.roles = roles;
	}

    public HardSoftScore score(ArrayList<ShiftAssignment> shiftAssignments){
    	HardSoftScore score = HardSoftScore.ZERO;

        for(PersonalConstraint constraint : constraints){
        	score.add(constraint.score(shiftAssignments, this));
        }
        for(Role role : roles){
            for(PersonalConstraint constraint : role.constraints) {
            	score.add(constraint.score(shiftAssignments, this));
            }
        }

        return score;
    }
    
    public boolean equals(Object other) {
    	if (!other.getClass().equals(this.getClass())) {
    		return false;
    	}
    	return this.id == ((Employee)other).id;
    }
    
    public static Employee NULL() {
    	// Null Employee fills all roles
        ArrayList<Role> allRoles = new ArrayList<Role>(Input.roles);
        
        // Null Employee has no personal constraints;
        ArrayList<PersonalConstraint> noConstraints = new ArrayList<PersonalConstraint>(0);
        
    	return new Employee(-1, allRoles, noConstraints);
    }
}
