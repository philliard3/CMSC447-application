package com.CMSC447.nurseroster.domain.constraint;

import org.json.JSONException;
import org.json.JSONObject;

public class ConstraintFactory {
	public static Constraint getConstraint(int id, int priority, boolean isHard, String type, JSONObject params) throws JSONException {
		switch (type) {
		case "ShiftPreference":
			return new ShiftPreference(id, priority, isHard, params);
		default:
			throw new JSONException("Invalid Constraint Type");
		}
	}
}
