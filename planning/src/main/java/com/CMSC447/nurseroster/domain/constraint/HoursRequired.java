package com.CMSC447.nurseroster.domain.constraint;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.Shift;
import com.CMSC447.nurseroster.domain.ShiftAssignment;


public class HoursRequired extends PersonalConstraint{
	public HoursRequired(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}

	public double amount; 
	public Period period;
	
	@Override
    public HardSoftScore score(List<ShiftAssignment> shiftAssignments, Employee employee) {
        ArrayList<Shift> shifts = new ArrayList<Shift>();
        for (ShiftAssignment assignment: shiftAssignments) {
        	if (assignment.employee.equals(employee)) {
        		shifts.add(assignment.shift);
        	}
        }
        
        Collections.sort(shifts);
        
        double sumSquaredError = 0;
        
        LocalDateTime periodStart = shifts.get(0).startTime;
        LocalDateTime periodEnd = periodStart.plus(period);
        int shiftIndex = 0;
        while(periodStart.isBefore(shifts.get(shifts.size()-1).startTime)) {
        	double totalHours = 0;
        	while(shiftIndex < shifts.size() && 
        			shifts.get(shiftIndex).startTime.isBefore(periodEnd)) {
        		totalHours += shifts.get(shiftIndex).hours();
        		shiftIndex++;
        	}
        	
        	double error = Math.abs(totalHours - this.amount);
        	sumSquaredError += error * error;
        	
        	periodStart = periodEnd;
        	periodStart.plus(period);
        }
        
        if (this.isHard) {
        	return HardSoftScore.ofHard((int) -sumSquaredError);
        }
        else {
        	return HardSoftScore.ofSoft((int) -sumSquaredError);
        }
    }

	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {
		// TODO Auto-generated method stub
		
	}
}
