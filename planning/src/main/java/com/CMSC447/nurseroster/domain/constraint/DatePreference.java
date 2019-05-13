package com.CMSC447.nurseroster.domain.constraint;

import org.drools.core.rule.Declaration;
import org.drools.core.spi.Constraint;
import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.Shift;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Christopher
 *
 *	Indicates a preference for shifts that start during some date range
 */
public class DatePreference extends Preference {

    public DatePreference(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}

	public LocalDate startDate;
    public LocalDate endDate;
    
    protected boolean matches(Shift shift) {
    	boolean startInRange = startDate.isBefore(shift.startTime.toLocalDate()) || startDate.isEqual(shift.startTime.toLocalDate());
    	boolean endInRange = endDate.isBefore(shift.endTime.toLocalDate()) || endDate.isEqual(shift.endTime.toLocalDate());
    	return startInRange && endInRange;
    }

	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {
		// TODO Auto-generated method stub
		
	}
}
