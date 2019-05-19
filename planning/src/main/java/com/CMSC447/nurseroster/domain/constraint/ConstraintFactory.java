package com.CMSC447.nurseroster.domain.constraint;

import org.json.JSONException;
import org.json.JSONObject;

import com.CMSC447.nurseroster.domain.constraint.basic.HoursRequiredConstraint;
import com.CMSC447.nurseroster.domain.constraint.basic.RolePriorityConstraint;
import com.CMSC447.nurseroster.domain.constraint.sequence.PersonalSequenceConstraint;
import com.CMSC447.nurseroster.domain.constraint.sequence.ScheduleSequenceConstraint;

public class ConstraintFactory {
	public static Constraint getConstraint(int id, int priority, boolean isHard, String type, JSONObject params) throws JSONException {
		switch (type) {
		case "Preference":
			return new Preference(id, priority, isHard, params);
		case "HoursRequiredConstraint":
			return new HoursRequiredConstraint(id, priority, isHard, params);
		case "PersonalSequenceConstraint":
			return new PersonalSequenceConstraint(id, priority, isHard, params);
		case "ScheduleSequenceConstraint":
			return new ScheduleSequenceConstraint(id, priority, isHard, params);
		case "RolePriorityConstraint":
			return new RolePriorityConstraint(id, priority, isHard, params);
		default:
			throw new JSONException("Invalid Constraint Type");
		}
	}
}
