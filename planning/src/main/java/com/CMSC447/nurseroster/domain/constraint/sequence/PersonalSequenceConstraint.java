package com.CMSC447.nurseroster.domain.constraint.sequence;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Predicate;

import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.Shift;
import com.CMSC447.nurseroster.domain.constraint.PersonalConstraint;
import com.CMSC447.nurseroster.domain.constraint.pattern.Sequence;
import com.CMSC447.nurseroster.domain.constraint.pattern.ShiftPattern;
import com.CMSC447.nurseroster.fileio.DateTimeParser;

public class PersonalSequenceConstraint extends PersonalConstraint{
    public PersonalSequenceConstraint(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		super(id, priority, isHard, params);
	}

    SequenceConstraintInfo info;

	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {
		JSONObject rawSequence = params.getJSONObject("sequence");
		JSONArray rawPatterns = rawSequence.getJSONArray("patterns");
		JSONArray rawTimeDiffs = rawSequence.getJSONArray("timeDiffs");
		String rawDiffMode = rawSequence.getString("timeDiffMode");
		
		
		ArrayList<ShiftPattern> patterns = new ArrayList<ShiftPattern>();
		for(int i = 0; i < rawPatterns.length(); i++) {
			JSONObject rawPattern = rawPatterns.getJSONObject(i);
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
			
			ShiftPattern pattern = new ShiftPattern(allowedDaysOfWeek, requiredTypes, bannedTypes, startTimeRanges, endTimeRanges, dateTimeRanges, lengthRanges, allowedLocations);
			
			patterns.add(pattern);
		}
		
		
		
		ArrayList<Pair<Duration, Duration>> timeDiffs = new ArrayList<Pair<Duration, Duration>>();
		for(int i = 0; i < rawTimeDiffs.length(); i++) {
			JSONArray rawDiffRange = rawTimeDiffs.getJSONArray(i);
			Duration minMinutes = Duration.ofMinutes(rawDiffRange.getLong(0));
			Duration maxMinutes = Duration.ofMinutes(rawDiffRange.getLong(1));
			
			Pair<Duration, Duration> diffRange = new Pair<Duration, Duration>(minMinutes, maxMinutes);
			
			timeDiffs.add(diffRange);
		}
		
		Sequence.TimeDiffMode diffMode;
		switch (rawDiffMode.toLowerCase()) {
		case "start":
			diffMode = Sequence.TimeDiffMode.START;
			break;
		case "end":
			diffMode = Sequence.TimeDiffMode.END;
			break;
		case "gap":
			diffMode = Sequence.TimeDiffMode.GAP;
			break;
		default:
			throw new JSONException("Invalid time difference mode");
		}
		
		Sequence sequence = new Sequence(patterns, timeDiffs, diffMode);
		
		int minMatches = params.getInt("minMatches");
		int maxMatches = params.getInt("maxMatches");
		LocalDate repeatStart = DateTimeParser.parseDate(params.getString("repeatStart"));
		LocalDate repeatEnd = DateTimeParser.parseDate(params.getString("repeatEnd"));
		JSONObject rawPeriod = params.getJSONObject("repeatPeriod");
		int periodDays = rawPeriod.getInt("days");
		int periodMonths = rawPeriod.getInt("months");
		Period repeatPeriod = Period.of(0, periodMonths, periodDays);
		
		info = new SequenceConstraintInfo(sequence, minMatches, maxMatches, repeatStart.atStartOfDay(), repeatEnd.atTime(23, 59, 59), repeatPeriod);
	}

	@Override
	public HardSoftScore score(ArrayList<Shift> shifts, Employee employee) {
		ArrayList<Integer> matchesPerPeriod = countMatches(shifts);
		
		int total = 0;
		
		for(int matches: matchesPerPeriod) {
			if (matches < info.minMatches) {
				if (this.isHard) {
					return toScore(-1);
				}
				int error = info.minMatches - matches;
				total -= error * error;
			}
			if (matches < info.maxMatches) {
				if (this.isHard) {
					return toScore(-1);
				}
				int error = matches - info.maxMatches;
				total -= error * error;
			}
		}
		
		return toScore(total);
	}
	
	private class Node{
		public int shiftIndex = -1;
		public Node parent = null;
		public ArrayList<Node> children = new ArrayList<Node>();
	}

	private ArrayList<Integer> countMatches(ArrayList<Shift> shifts){
		shifts = new ArrayList<Shift>(shifts);
		Collections.sort(shifts);
		// Remove all shifts that start too early or too late to be included in the search
		shifts.removeIf(shift->shift.startTime.isBefore(info.repeatStart) || shift.startTime.isAfter(info.repeatEnd));
		
		ArrayList<ArrayList<Shift>> periods = new ArrayList<ArrayList<Shift>>();
		LocalDateTime periodStart = info.repeatStart;
		LocalDateTime periodEnd = periodStart.plus(info.repeatPeriod);
		int currentShiftIndex = 0;
		while(periodStart.isBefore(info.repeatEnd)) {
			ArrayList<Shift> period = new ArrayList<Shift>();
			while(currentShiftIndex < shifts.size() && shifts.get(currentShiftIndex).startTime.isBefore(periodEnd)) {
				period.add(shifts.get(currentShiftIndex));
				
				currentShiftIndex++;
			}
			
			periods.add(period);
			periodStart = periodEnd;
			periodEnd = periodStart.plus(info.repeatPeriod);
		}
		
		// Separate the timeDiffs and the mode to make processing easier
		
		ArrayList<Pair<Duration, Duration>> timeDiffs = null;
		Sequence.TimeDiffMode mode = null;
		if(info.sequence.startTimeDifferenceRanges != null) {
			timeDiffs = info.sequence.startTimeDifferenceRanges;
			mode = Sequence.TimeDiffMode.START;
		}
		else if(info.sequence.endTimeDifferenceRanges != null) {
			timeDiffs = info.sequence.endTimeDifferenceRanges;
			mode = Sequence.TimeDiffMode.END;
		}
		else if(info.sequence.timeGapSizeRanges != null) {
			timeDiffs = info.sequence.timeGapSizeRanges;
			mode = Sequence.TimeDiffMode.GAP;
		}
		
		ArrayList<Integer> counts = new ArrayList<Integer>(periods.size());
		
		// For each period
		for(ArrayList<Shift> period: periods) {
			
			// Build a tree representing all candidate sequences
			Node root = new Node();
			LinkedList<Node> currentLevel = new LinkedList<Node>();
			
			for(int i = 0; i < period.size(); i++) {
				if(info.sequence.shiftPatterns.get(0).matches(period.get(i))) {
					Node newNode = new Node();
					newNode.shiftIndex = i;
					newNode.parent = root;
					root.children.add(newNode);
					currentLevel.add(newNode);
				}
			}
			
			// For each shift pattern, build the next level
			for(int i = 1; i < info.sequence.shiftPatterns.size(); i++) {
				ShiftPattern pattern = info.sequence.shiftPatterns.get(i);
				Pair<Duration, Duration> range = timeDiffs.get(i-1);
				Duration minDiff = range.getValue0();
				Duration maxDiff = range.getValue1();
				
				LinkedList<Node> nextLevel = new LinkedList<Node>();
				
				// For each node in the current level, add all shifts that can come after the one in that node to the next level 
				for(Node node: currentLevel) {
					
					// Get the current shift
					Shift currentShift = period.get(node.shiftIndex);
					
					// The index of the first possible shift that can come after this one
					int nextShiftIndex = node.shiftIndex + 1;
					
					LocalDateTime nextShiftMin = null;
					LocalDateTime nextShiftMax = null;
					switch(mode) {
					case START: // Range of the next shift's start time
						nextShiftMin = currentShift.startTime.plus(minDiff);
						nextShiftMax = currentShift.startTime.plus(maxDiff);
						break;
					case GAP: // Range of the next shift's start time
					case END: // Range of the next shift's end time
						nextShiftMin = currentShift.endTime.plus(minDiff);
						nextShiftMax = currentShift.endTime.plus(maxDiff);
						break;
					}
					
					
					switch(mode) {
					case START: // Get all shifts with start time in range that match the shift pattern
					case GAP: // Get all shifts with start time in range that match the shift pattern
						
						// Go until the first shift with a start time in range
						while(nextShiftIndex < period.size() && period.get(nextShiftIndex).startTime.isBefore(nextShiftMin)) {
							nextShiftIndex++;
						}
						
						// Go until the last shift with a start time in range
						while(nextShiftIndex < period.size() && (period.get(nextShiftIndex).startTime.isBefore(nextShiftMax) || period.get(nextShiftIndex).startTime.isEqual(nextShiftMax))) {
							
							// If the shift matches the pattern, build a node and add it to the next level of the tree
							if (pattern.matches(period.get(nextShiftIndex))) {
								Node newNode = new Node();
								newNode.shiftIndex = nextShiftIndex;
								newNode.parent = node;
								node.children.add(newNode);
								nextLevel.add(newNode);							
							}
							nextShiftIndex++;
						}
						
						break;
					case END: // Get all shifts with end time in range that match the shift pattern
						// Go until the first shift with an end time in range
						while(nextShiftIndex < period.size() && period.get(nextShiftIndex).endTime.isBefore(nextShiftMin)) {
							nextShiftIndex++;
						}
						
						// Go until the last shift with an end time in range
						while(nextShiftIndex < period.size() && (period.get(nextShiftIndex).endTime.isBefore(nextShiftMax) || period.get(nextShiftIndex).endTime.isEqual(nextShiftMax))) {
							// If the shift matches the pattern, build a node and add it to the next level of the tree
							if (pattern.matches(period.get(nextShiftIndex))) {
								Node newNode = new Node();
								newNode.shiftIndex = nextShiftIndex;
								newNode.parent = node;
								node.children.add(newNode);
								nextLevel.add(newNode);							
							}
							nextShiftIndex++;
						}
						break;
					}
				}
				
				currentLevel = nextLevel;
			}
			
			// Construct a basic set of matches by traversing back from leaves
			HashSet<Integer> usedIndices = new HashSet<Integer>();
			int numMatches = 0;
			
			// For each leaf
			for(int i = 0; i < currentLevel.size(); i++) {
				Node currNode = currentLevel.get(i);
				ArrayList<Integer> indices = new ArrayList<Integer>(info.sequence.shiftPatterns.size());
				
				// Traverse back from the leaf, recording what shift indices are included in the sequence and stopping if an index was already used
				while(currNode.parent != null && !usedIndices.contains(currNode.shiftIndex)) {
					indices.add(currNode.shiftIndex);
					currNode = currNode.parent;
				}
				
				// If the root was reached without repeating a shift's usage, count another match and add the used shifts to the set
				if (currNode.parent == null) {
					usedIndices.addAll(indices);
					numMatches++;
				}
			}
			// Record the matches for the period
			counts.add(numMatches);
		}
		return counts;
	}
}

