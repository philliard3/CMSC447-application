package com.CMSC447.nurseroster.domain.constraint;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.ShiftAssignment;

public abstract class ScheduleConstraint extends Constraint{
    
    public ScheduleConstraint(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}

	public abstract HardSoftScore score(List<ShiftAssignment> shiftAssignments);

}
