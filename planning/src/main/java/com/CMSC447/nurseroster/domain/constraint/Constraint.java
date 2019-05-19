package com.CMSC447.nurseroster.domain.constraint;

import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

public abstract class Constraint {
	public final int id;
	public final int priority;
    public final boolean isHard;
    
    public Constraint(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
    	this.id = id;
    	this.priority = priority;
    	this.isHard = isHard;
    	this.buildFromJSON(params);
    }
    
    
    protected abstract void buildFromJSON(JSONObject params) throws JSONException;
    
    /**
     * Converts an integer into a HardSoftScore
     * 
     * @param score The score to be converted
     * @return A HardSoftScore with score in its Hard or Soft value depending on if this is a hard or soft constraint
     */
    protected HardSoftScore toScore(int score) {
    	if (this.isHard) {
    		return HardSoftScore.ofHard(score * priority);
    	}
    	else {
    		return HardSoftScore.ofSoft(score * priority);
    	}
    }
}
