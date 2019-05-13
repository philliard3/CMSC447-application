package com.CMSC447.nurseroster.domain.constraint;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.Shift;
import com.CMSC447.nurseroster.domain.ShiftAssignment;

public abstract class Preference extends PersonalConstraint {


	public Preference(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}

	public boolean isPositive;
    
    protected abstract boolean matches(Shift shift);
    
    public HardSoftScore score(List<ShiftAssignment> shiftAssignments, Employee employee) {
    	int matches = 0;
        
        for (ShiftAssignment assignment: shiftAssignments) {
        	if (assignment.employee.equals(employee) && this.matches(assignment.shift)) {
        		matches++;
        	}
        }
        
        int score = matches;
        
        if (!isPositive) {
        	score *= -1;
        }
        
        return toScore(score);
    }
}
