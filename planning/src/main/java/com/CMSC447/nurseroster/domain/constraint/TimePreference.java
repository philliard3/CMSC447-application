package com.CMSC447.nurseroster.domain.constraint;

import org.drools.core.rule.Declaration;
import org.drools.core.spi.Constraint;
import org.json.JSONException;
import org.json.JSONObject;

import com.CMSC447.nurseroster.domain.Shift;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TimePreference extends Preference {

    public TimePreference(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}
	LocalTime startTime;
    LocalTime endTime;
    
	@Override
	protected boolean matches(Shift shift) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {
		// TODO Auto-generated method stub
		
	}

}
