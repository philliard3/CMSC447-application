package com.CMSC447.nurseroster.fileio;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.NurseRoster;
import com.CMSC447.nurseroster.domain.Shift;
import com.CMSC447.nurseroster.domain.ShiftAssignment;

public class SolutionWriter {
	private static JSONObject toJSON(HardSoftScore score, ArrayList<ShiftAssignment> assignments) throws JSONException {
		JSONObject output = new JSONObject();
		if (score.isFeasible()) {
			output.put("score", score.getSoftScore());
		}
		else {
			output.put("score", Integer.MIN_VALUE);
		}
		
		JSONArray outputAssignments = new JSONArray();
		for(ShiftAssignment assignment: assignments) {
			Shift shift = assignment.shift;
			Employee employee = assignment.employee;
			
			JSONObject shiftAssignment = new JSONObject();
			
			shiftAssignment.put("employee", assignment.employee.id);
			
			JSONObject shiftJSON = new JSONObject();
			
			shiftJSON.put("startTime", DateTimeParser.dateTimeString(shift.startTime));
			shiftJSON.put("endTime", DateTimeParser.dateTimeString(shift.endTime));
			
			JSONArray shiftTypes = new JSONArray();
			for(String type: shift.types) {
				shiftTypes.put(type);
			}
			
			shiftJSON.put("shiftTypes", shiftTypes);
			
			shiftJSON.put("location", shift.location);
			
			shiftAssignment.put("shift", shiftJSON);
			
			outputAssignments.put(shiftAssignment);
		}
		output.put("assignments", outputAssignments);
		return output;
	}
	
	public static boolean write(String filename, NurseRoster bestSolution) {
		HardSoftScore bestScore = bestSolution.getScore();
		try {
			FileWriter outputWriter = new FileWriter(filename);
			JSONObject object = toJSON(bestScore, bestSolution.getShiftAssignments());
			String jsonString = object.toString();
			if(jsonString == null) {
				return false;
			}
			outputWriter.write(jsonString);
			outputWriter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
}
