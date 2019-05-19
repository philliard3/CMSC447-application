package com.CMSC447.nurseroster.domain.constraint.basic;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.CMSC447.nurseroster.domain.Role;
import com.CMSC447.nurseroster.domain.ShiftAssignment;
import com.CMSC447.nurseroster.domain.constraint.ScheduleConstraint;
import com.CMSC447.nurseroster.fileio.Input;

public class RolePriorityConstraint extends ScheduleConstraint {
	
	private ArrayList<String> types;
	private HashMap<Role, Float> priorities;

	public RolePriorityConstraint(int id, int priority, boolean isHard, JSONObject params) throws JSONException {
		// Cannot be hard constraint
		super(id, priority, false, params);
	}

	@Override
	protected void buildFromJSON(JSONObject params) throws JSONException {
		JSONArray rawTypes = params.getJSONArray("types");
		JSONArray rawRoles = params.getJSONArray("roles");
		JSONArray rawPriorities = params.getJSONArray("priorities");
		
		int numRoles = Math.min(rawRoles.length(), rawPriorities.length());
		
		this.types = new ArrayList<String>();
		this.priorities = new HashMap<Role, Float>();
		
		for(int i = 0; i < rawTypes.length(); i++) {
			this.types.add(rawTypes.getString(i));
		}
		
		for(int i = 0; i < numRoles; i++) {
			Role role = Input.getRole(rawRoles.getInt(i));
			priorities.put(role, (float) rawPriorities.getInt(i));
		}
	}

	@Override
	public HardSoftScore score(ArrayList<ShiftAssignment> shiftAssignments) {
		float total = 0;
		
		// For each shift assignment
		for (ShiftAssignment assignment: shiftAssignments) {
			
			// For each type that the shift has
			for(String type: types) {
				
				// If the type is one which this constraint applies to, add the highest priority of any of its roles to the total
				if (assignment.shift.types.contains(type)) {
					
					// Find highest priority of any of employee's roles 
					float maxPriority = Float.NEGATIVE_INFINITY;
					for(Role role: assignment.employee.roles) {
						if (priorities.containsKey(role) && priorities.get(role) > maxPriority) {
							maxPriority = priorities.get(role);
						}
					}
					
					// If the employee has no roles with priorities, set the priority to 0
					if (maxPriority == Float.NEGATIVE_INFINITY) {
						maxPriority = 0;
					}
					
					// Add to the total
					total += maxPriority;
					
					break;
				}
			}
		}
		
		return toScore((int)total);
	}

}
