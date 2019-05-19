/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package com.CMSC447.nurseroster.solver;


import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.NurseRoster;
import com.CMSC447.nurseroster.domain.Shift;
import com.CMSC447.nurseroster.domain.constraint.ScheduleConstraint;

import java.util.ArrayList;
import java.util.HashMap;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;


public class NurseRosterEasyScoreCalculator implements EasyScoreCalculator<NurseRoster> {

	static long a = 0;
	
    @Override
    public HardSoftScore calculateScore(NurseRoster solution) {
    	HardSoftScore score = HardSoftScore.ZERO;
        
        HashMap<Integer, ArrayList<Shift>> shiftsByEmployeeID = solution.getShiftsByEmployeeID();
        
        // Sum of employee scores
        for (Employee employee: solution.getEmployees()) {
        	score = score.add(employee.score(shiftsByEmployeeID.get(employee.id)));
        	if(!score.isFeasible()) {
        		return score;
        	}
        }

        // Sum of schedule scores
        for(ScheduleConstraint scheduleConstraint : solution.getScheduleConstraints() ) {
            score = score.add(scheduleConstraint.score(solution.getShiftAssignments()));
            if(!score.isFeasible()) {
        		return score;
        	}
        }
        
        return score;
    }
}
