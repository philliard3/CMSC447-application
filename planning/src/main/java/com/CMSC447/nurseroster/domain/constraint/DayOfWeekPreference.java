package com.CMSC447.nurseroster.domain.constraint;

import java.time.DayOfWeek;

import org.json.JSONException;
import org.json.JSONObject;

import com.CMSC447.nurseroster.domain.Shift;

public class DayOfWeekPreference extends Preference {

    public DayOfWeekPreference(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}

	DayOfWeek dayOfWeek;

	@Override
	protected boolean matches(Shift shift) {
		return shift.startTime.getDayOfWeek() == this.dayOfWeek;
	}

	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {
		// TODO Auto-generated method stub
		
	}

    
}
