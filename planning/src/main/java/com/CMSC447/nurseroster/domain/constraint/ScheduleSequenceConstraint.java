package com.CMSC447.nurseroster.domain.constraint;

import org.drools.core.rule.Declaration;
import org.drools.core.spi.Constraint;
import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.PersonalSequence;
import com.CMSC447.nurseroster.domain.ShiftAssignment;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ScheduleSequenceConstraint extends ScheduleConstraint{
    public ScheduleSequenceConstraint(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}

	PersonalSequence shiftSequence;
    int minMatches;
    int maxMatches;
    Period repeatPeriod;
    LocalDate repeatStart;

    @Override
    public HardSoftScore score(List<ShiftAssignment> shiftAssignments) {


        return HardSoftScore.ZERO;
    }

	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {
		// TODO Auto-generated method stub
		
	}


}
