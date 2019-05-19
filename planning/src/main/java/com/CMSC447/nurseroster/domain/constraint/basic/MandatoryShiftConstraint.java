package com.CMSC447.nurseroster.domain.constraint.basic;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.NurseRoster;
import com.CMSC447.nurseroster.domain.ShiftAssignment;
import com.CMSC447.nurseroster.domain.constraint.ScheduleConstraint;

public class MandatoryShiftConstraint extends ScheduleConstraint {

	public MandatoryShiftConstraint() throws JSONException {
		super(-1, 1, true, null);
	}

	@Override
	public HardSoftScore score(ArrayList<ShiftAssignment> shiftAssignments) {
		for(ShiftAssignment assignment: shiftAssignments) {
			if (assignment.shift.mandatory && assignment.employee.id == NurseRoster.NULL_EMPLOYEE_ID) {
				return this.toScore(-1);
			}
		}
		
		return this.toScore(0);
	}

	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {}

}
