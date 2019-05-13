package com.CMSC447.nurseroster.domain.constraint;

import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.CMSC447.nurseroster.domain.Shift;

public class ShiftPreference extends Preference {

    public ShiftPreference(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}

	public LocalDateTime startTime;
    public LocalDateTime endTime;
    
    protected boolean matches(Shift shift) {
    	return this.startTime.isEqual(shift.startTime) && this.endTime.isEqual(shift.endTime);
    }

	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {
		params.getString("start");		
	}

}
