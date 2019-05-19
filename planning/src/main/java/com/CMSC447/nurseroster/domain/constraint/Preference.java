package com.CMSC447.nurseroster.domain.constraint;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.Shift;
import com.CMSC447.nurseroster.domain.ShiftAssignment;
import com.CMSC447.nurseroster.domain.constraint.pattern.ShiftPattern;
import com.CMSC447.nurseroster.fileio.DateTimeParser;

public class Preference extends PersonalConstraint {
	public Preference(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}

	public boolean isPositive;
	public ShiftPattern pattern;
	
	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {
		isPositive = params.getBoolean("isPositive");
		
		JSONObject rawPattern = params.getJSONObject("pattern");
		ArrayList<DayOfWeek> allowedDaysOfWeek = null;
        ArrayList<String> requiredTypes = null;
        ArrayList<String> bannedTypes = null;
        ArrayList<Pair<LocalTime, LocalTime>> startTimeRanges = null;
        ArrayList<Pair<LocalTime, LocalTime>> endTimeRanges = null;
        ArrayList<Pair<LocalDateTime, LocalDateTime>> dateTimeRanges = null;
        ArrayList<Pair<Float, Float>> lengthRanges = null;
        ArrayList<String> allowedLocations = null;
        
		if (rawPattern.has("daysOfWeek")) {
			JSONArray rawDaysOfWeek = rawPattern.getJSONArray("daysOfWeek");
			
			allowedDaysOfWeek = new ArrayList<DayOfWeek>();
			
			for(int j = 0; j < rawDaysOfWeek.length(); j++) {
				allowedDaysOfWeek.add(DayOfWeek.of(rawDaysOfWeek.getInt(j)));
			}
		}
		if(rawPattern.has("requiredTypes")) {
			JSONArray rawTypes = rawPattern.getJSONArray("requiredTypes");
			
			requiredTypes = new ArrayList<String>();
			
			for(int j = 0; j < rawTypes.length(); j++) {
				requiredTypes.add(rawTypes.getString(j));
			}
		}
		if(rawPattern.has("bannedTypes")) {
			JSONArray rawTypes = rawPattern.getJSONArray("bannedTypes");
			
			bannedTypes = new ArrayList<String>();
			
			for(int j = 0; j < rawTypes.length(); j++) {
				bannedTypes.add(rawTypes.getString(j));
			}
		}
		if(rawPattern.has("startTimeRanges")) {
			JSONArray rawRanges = rawPattern.getJSONArray("startTimeRanges");
			
			startTimeRanges = new ArrayList<Pair<LocalTime, LocalTime>>();

			for(int j = 0; j < rawRanges.length(); j++) {
				JSONArray rawRange = rawRanges.getJSONArray(j);
				LocalTime minTime = DateTimeParser.parseTime(rawRange.getString(0));
				LocalTime maxTime = DateTimeParser.parseTime(rawRange.getString(1));
				Pair<LocalTime, LocalTime> range = new Pair<LocalTime, LocalTime>(minTime, maxTime);
				startTimeRanges.add(range);
			}
		}
		if(rawPattern.has("endTimeRanges")) {
			JSONArray rawRanges = rawPattern.getJSONArray("endTimeRanges");
			
			endTimeRanges = new ArrayList<Pair<LocalTime, LocalTime>>();

			for(int j = 0; j < rawRanges.length(); j++) {
				JSONArray rawRange = rawRanges.getJSONArray(j);
				LocalTime minTime = DateTimeParser.parseTime(rawRange.getString(0));
				LocalTime maxTime = DateTimeParser.parseTime(rawRange.getString(1));
				Pair<LocalTime, LocalTime> range = new Pair<LocalTime, LocalTime>(minTime, maxTime);
				endTimeRanges.add(range);
			}
		}
		if(rawPattern.has("dateTimeRanges")) {
			JSONArray rawRanges = rawPattern.getJSONArray("dateTimeRanges");
			
			dateTimeRanges = new ArrayList<Pair<LocalDateTime, LocalDateTime>>();

			for(int j = 0; j < rawRanges.length(); j++) {
				JSONArray rawRange = rawRanges.getJSONArray(j);
				LocalDateTime minTime = DateTimeParser.parseDateTime(rawRange.getString(0));
				LocalDateTime maxTime = DateTimeParser.parseDateTime(rawRange.getString(1));
				Pair<LocalDateTime, LocalDateTime> range = new Pair<LocalDateTime, LocalDateTime>(minTime, maxTime);
				dateTimeRanges.add(range);
			}
		}
		if(rawPattern.has("lengthRanges")) {
			JSONArray rawRanges = rawPattern.getJSONArray("lengthRanges");
			
			lengthRanges = new ArrayList<Pair<Float, Float>>();

			for(int j = 0; j < rawRanges.length(); j++) {
				JSONArray rawRange = rawRanges.getJSONArray(j);
				float minLength = (float) rawRange.getDouble(0);
				float maxLength = (float) rawRange.getDouble(1);
				Pair<Float, Float> range = new Pair<Float, Float>(minLength, maxLength);
				lengthRanges.add(range);
			}
		}
		if(rawPattern.has("allowedLocations")) {
			JSONArray rawLocations = rawPattern.getJSONArray("allowedLocations");
			
			allowedLocations = new ArrayList<String>();
			
			for(int j = 0; j < rawLocations.length(); j++) {
				allowedLocations.add(rawLocations.getString(j));
			}
		}
		
		pattern = new ShiftPattern(allowedDaysOfWeek, requiredTypes, bannedTypes, startTimeRanges, endTimeRanges, dateTimeRanges, lengthRanges, allowedLocations);
	}

	@Override
	public HardSoftScore score(ArrayList<Shift> shifts, Employee employee) {
		int matches = 0;
        
        for (Shift shift: shifts) {
        	if (this.pattern.matches(shift)) {
        		matches++;
        	}
        }
        
        int score = matches;
        
        if (!isPositive) {
        	score *= -1;
        }
        
        return toScore(score);
	}
}
