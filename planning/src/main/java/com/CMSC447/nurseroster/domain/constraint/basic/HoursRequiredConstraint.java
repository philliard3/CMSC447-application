package com.CMSC447.nurseroster.domain.constraint.basic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.Shift;
import com.CMSC447.nurseroster.domain.constraint.PersonalConstraint;
import com.CMSC447.nurseroster.fileio.Input;


public class HoursRequiredConstraint extends PersonalConstraint{
	public HoursRequiredConstraint(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}

	public double amount; 
	public Period period;
	

	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {
		this.amount = params.getDouble("amount");
		
		JSONObject rawPeriod = params.getJSONObject("period");
		
		int days = rawPeriod.getInt("days");
		int months = rawPeriod.getInt("months");
		
		this.period = Period.of(0, months, days);
	}

	@Override
	public HardSoftScore score(ArrayList<Shift> shifts, Employee employee) {
		Collections.sort(shifts);
        
        double sumSquaredError = 0;
        
        LocalDate periodStart = Input.shifts.get(0).startTime.toLocalDate();
        LocalDate periodEnd = periodStart.plus(period);
        int shiftIndex = 0;
        
        if(shifts.size() == 0) {
        	return toScore((int) -(amount*amount));
        }
        
        // While the last shift starts after the current period begins
        while(shifts.get(shifts.size()-1).startTime.isAfter(periodStart.atStartOfDay())) {
        	double totalHours = 0;
        	// While the index is in range for the shifts array
        	// and the current shift is within the period
        	while(shiftIndex < shifts.size() && 
        			shifts.get(shiftIndex).startTime.isBefore(periodEnd.atTime(23, 59))) {
        		totalHours += shifts.get(shiftIndex).hours();
        		shiftIndex++;
        	}
        	
        	double error = totalHours - this.amount;
        	sumSquaredError += error * error;
        	
        	periodStart = periodEnd;
        	periodEnd = periodStart.plus(period);
        }
        
        return toScore((int) -sumSquaredError);
        
	}
}
