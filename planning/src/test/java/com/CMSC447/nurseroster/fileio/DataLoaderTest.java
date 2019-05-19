package com.CMSC447.nurseroster.fileio;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Collections;

import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.NurseRoster;
import com.CMSC447.nurseroster.domain.Role;
import com.CMSC447.nurseroster.domain.Shift;
import com.CMSC447.nurseroster.domain.constraint.Constraint;
import com.CMSC447.nurseroster.domain.constraint.PersonalConstraint;
import com.CMSC447.nurseroster.domain.constraint.Preference;
import com.CMSC447.nurseroster.domain.constraint.ScheduleConstraint;
import com.CMSC447.nurseroster.domain.constraint.basic.HoursRequiredConstraint;
import com.CMSC447.nurseroster.domain.constraint.basic.MandatoryShiftConstraint;
import com.CMSC447.nurseroster.domain.constraint.basic.RolePriorityConstraint;
import com.CMSC447.nurseroster.domain.constraint.pattern.ShiftPattern;
import com.CMSC447.nurseroster.domain.constraint.sequence.PersonalSequenceConstraint;
import com.CMSC447.nurseroster.domain.constraint.sequence.ScheduleSequenceConstraint;

import junit.framework.TestCase;

public class DataLoaderTest extends TestCase {

	public void testProcessConstraint() {
		
		try {
			int testID = 0;
			int testPriority = 1;
			boolean testIsHard = false;
			String testType = "HoursRequiredConstraint";
			JSONObject testParams = new JSONObject();
			testParams.put("amount", 40);
			
			JSONObject period = new JSONObject();
			
			period.put("days", 7);

			period.put("months", 0);
			
			testParams.put("period", period);
			
			JSONObject rawConstraint = new JSONObject();
			rawConstraint.put("id", testID);
			rawConstraint.put("priority", testPriority);
			rawConstraint.put("isHard", testIsHard);
			rawConstraint.put("type", testType);
			rawConstraint.put("parameters", testParams);
			
			Constraint constraint = DataLoader.processConstraint(rawConstraint);
			
			assertTrue(constraint instanceof HoursRequiredConstraint);
			HoursRequiredConstraint hoursConstraint = (HoursRequiredConstraint) constraint;
			
			assertTrue(hoursConstraint.id == testID);
			assertTrue(hoursConstraint.priority == testPriority);
			assertTrue(hoursConstraint.isHard == testIsHard);
			assertTrue(hoursConstraint.amount == 40);
			assertTrue(hoursConstraint.period.equals(Period.ofDays(7)));
			
		} catch (JSONException e) {
			e.printStackTrace();
			fail("JSON Parsing Error");
		}
	}

	public void testProcessRole() {
		try {
			int testID = 1;
			JSONArray testConstraints = new JSONArray();
			for(int i = 0; i < 100; i++) {
				int testPriority = 1;
				boolean testIsHard = false;
				String testType = "HoursRequiredConstraint";
				JSONObject testParams = new JSONObject();
				testParams.put("amount", 40);
				
				JSONObject period = new JSONObject();
				
				period.put("days", 7);
	
				period.put("months", 0);
				
				testParams.put("period", period);
				
				JSONObject rawConstraint = new JSONObject();
				rawConstraint.put("id", i);
				rawConstraint.put("priority", testPriority);
				rawConstraint.put("isHard", testIsHard);
				rawConstraint.put("type", testType);
				rawConstraint.put("parameters", testParams);
				
				testConstraints.put(rawConstraint);	
			}
			
			JSONObject rawRole = new JSONObject();
			rawRole.put("id", testID);
			rawRole.put("constraints", testConstraints);
			
			Role role = DataLoader.processRole(rawRole);
			
			assertTrue(role.id == testID);
			assertTrue(role.constraints.size() == 100);
			for(int i = 0; i < 100; i++) {
				assertTrue(role.constraints.get(i) instanceof HoursRequiredConstraint);
				HoursRequiredConstraint constraint = (HoursRequiredConstraint) role.constraints.get(i);
				
				assertTrue(constraint.id == i);
				assertTrue(constraint.priority == 1);
				assertTrue(constraint.isHard == false);
				assertTrue(constraint.amount == 40);
				assertTrue(constraint.period.equals(Period.ofDays(7)));
			}
		} catch (JSONException e) {
			fail("JSON Parsing Error");
		}
	}

	public void testLoadFile() {
		String testInput = "test_input.json";
		assertTrue(DataLoader.loadFile(testInput));
		
		Collections.sort(Input.shifts);
		for(Shift shift: Input.shifts) {
			System.out.println(shift.startTime);
		}
		assertTrue(Input.shifts.get(0).startTime.isBefore(Input.shifts.get(Input.shifts.size()-1).startTime));
		
		// Test Shifts
		boolean[] foundShifts = {false, false, false};
		for(Shift shift: Input.shifts) {
			switch(shift.roles.size()) {
			case 1: // First test shift 
				assertFalse(foundShifts[0]);
				assertTrue(shift.mandatory);
				assertEquals(shift.roles.get(0).id, 2);
				assertEquals(shift.types.size(), 1);
				assertEquals(shift.types.get(0), "Morning Weekday");
				assertEquals(shift.location, "Nursery");
				assertTrue(shift.startTime.isEqual(LocalDateTime.of(2018, 7, 1, 7, 0)));
				assertTrue(shift.endTime.isEqual(LocalDateTime.of(2018, 7, 1, 15, 0)));
				foundShifts[0] = true;
				break;
			case 2: // Second test shift 
				assertFalse(foundShifts[1]);
				assertTrue(shift.mandatory);
				assertEquals(shift.roles.get(0).id, 1);
				assertEquals(shift.roles.get(1).id, 3);
				assertEquals(shift.types.size(), 1);
				assertEquals(shift.types.get(0), "Afternoon Weekday");
				assertEquals(shift.location, "Nursery");
				assertTrue(shift.startTime.isEqual(LocalDateTime.of(2018, 10, 24, 15, 0)));
				assertTrue(shift.endTime.isEqual(LocalDateTime.of(2018, 10, 24, 23, 0)));
				foundShifts[1] = true;
				break;
			case 3: // Third test shift
				assertFalse(foundShifts[2]);
				assertFalse(shift.mandatory);
				assertEquals(shift.roles.get(0).id, 2);
				assertEquals(shift.roles.get(1).id, 1);
				assertEquals(shift.roles.get(2).id, 3);
				assertEquals(shift.types.size(), 3);
				assertEquals(shift.types.get(0), "Night Weekend");
				assertEquals(shift.types.get(1), "Night");
				assertEquals(shift.types.get(2), "Weekend");
				assertEquals(shift.location, "ICU");
				assertTrue(shift.startTime.isEqual(LocalDateTime.of(2018, 7, 6, 19, 0)));
				assertTrue(shift.endTime.isEqual(LocalDateTime.of(2018, 7, 7, 7, 0)));
				foundShifts[2] = true;
				break;
			}
		}
		assertTrue(foundShifts[0]);
		assertTrue(foundShifts[1]);
		assertTrue(foundShifts[2]);
		
		// Test Roles
		boolean[] foundRoles = {false, false, false};
		for(Role role: Input.roles) {
			switch(role.id) {
			case 2: // test role
				assertFalse(foundRoles[0]);
				assertEquals(role.constraints.size(), 2);
				
				boolean[] foundConstraints = {false, false};
				for(PersonalConstraint constraint: role.constraints) {
					switch(constraint.id) {
					case 1:
						assertFalse(foundConstraints[0]);
						
						assertEquals(constraint.priority, 1);
						assertFalse(constraint.isHard);
						assertTrue(constraint instanceof HoursRequiredConstraint);
						HoursRequiredConstraint hoursConstraint = (HoursRequiredConstraint) constraint;
						assertEquals(hoursConstraint.amount, 40.0);
						assertTrue(hoursConstraint.period.equals(Period.ofDays(7)));
						foundConstraints[0] = true;
						break;
					case 2:
						assertFalse(foundConstraints[1]);
						
						assertEquals(constraint.priority, 3);
						assertFalse(constraint.isHard);
						assertTrue(constraint instanceof PersonalSequenceConstraint);
						PersonalSequenceConstraint sequenceConstraint = (PersonalSequenceConstraint) constraint;
						
						assertEquals(sequenceConstraint.info.minMatches, 1);
						assertEquals(sequenceConstraint.info.maxMatches, 1);
						assertTrue(sequenceConstraint.info.repeatStart.isEqual(LocalDateTime.of(2018, 7, 1, 0, 0)));
						assertTrue(sequenceConstraint.info.repeatEnd.isEqual(LocalDateTime.of(2019, 8, 1, 23, 59, 59)));
						assertTrue(sequenceConstraint.info.repeatPeriod.equals(Period.ofMonths(3)));
						
						assertTrue(sequenceConstraint.info.sequence.startTimeDifferenceRanges != null);
						assertTrue(sequenceConstraint.info.sequence.endTimeDifferenceRanges == null);
						assertTrue(sequenceConstraint.info.sequence.timeGapSizeRanges == null);
						
						assertEquals(sequenceConstraint.info.sequence.startTimeDifferenceRanges.size(), 4);
						
						for(Pair<Duration, Duration> range: sequenceConstraint.info.sequence.startTimeDifferenceRanges) {
							assertTrue(range.getValue0().equals(Duration.ofMinutes(1440)));
							assertTrue(range.getValue1().equals(Duration.ofMinutes(1440)));
						}
						
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.size(), 5);
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(0).allowedDaysOfWeek.get(0), DayOfWeek.MONDAY);
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(1).allowedDaysOfWeek.get(0), DayOfWeek.TUESDAY);
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(2).allowedDaysOfWeek.get(0), DayOfWeek.WEDNESDAY);
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(3).allowedDaysOfWeek.get(0), DayOfWeek.THURSDAY);
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(4).allowedDaysOfWeek.get(0), DayOfWeek.FRIDAY);
						
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(0).allowedDaysOfWeek.size(), 1);
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(1).allowedDaysOfWeek.size(), 1);
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(2).allowedDaysOfWeek.size(), 1);
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(3).allowedDaysOfWeek.size(), 1);
						assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(4).allowedDaysOfWeek.size(), 1);
						
						for(ShiftPattern pattern: sequenceConstraint.info.sequence.shiftPatterns) {
							assertTrue(pattern.allowedDaysOfWeek != null);
							assertTrue(pattern.requiredTypes == null);
							assertTrue(pattern.bannedTypes == null);
							assertTrue(pattern.startTimeRanges != null);
							assertTrue(pattern.endTimeRanges != null);
							assertTrue(pattern.dateTimeRanges == null);
							assertTrue(pattern.lengthRanges == null);
							assertTrue(pattern.allowedLocations != null);
							
							assertTrue(pattern.startTimeRanges.size() == 1);
							Pair<LocalTime, LocalTime> startTimeRange = pattern.startTimeRanges.get(0);
							
							assertTrue(startTimeRange.getValue0().equals(LocalTime.of(7, 0)));
							assertTrue(startTimeRange.getValue1().equals(LocalTime.of(7, 0)));
							
							assertTrue(pattern.endTimeRanges.size() == 1);
							Pair<LocalTime, LocalTime> endTimeRange = pattern.endTimeRanges.get(0);
							
							assertTrue(endTimeRange.getValue0().equals(LocalTime.of(15, 0)));
							assertTrue(endTimeRange.getValue1().equals(LocalTime.of(15, 0)));
							
							assertTrue(pattern.allowedLocations.size() == 1);
							
							assertEquals(pattern.allowedLocations.get(0), "ICU");
						}
						
						foundConstraints[1] = true;
						break;
					}
				}
				
				assertTrue(foundConstraints[0]);
				assertTrue(foundConstraints[1]);
				
				
				foundRoles[0] = true;
				break;
			case 1: // second test role
				assertFalse(foundRoles[1]);
				assertEquals(role.constraints.size(), 1);
				
				PersonalConstraint constraint = role.constraints.get(0);
				assertEquals(constraint.priority, 1);
				assertFalse(constraint.isHard);
				assertTrue(constraint instanceof HoursRequiredConstraint);
				HoursRequiredConstraint hoursConstraint = (HoursRequiredConstraint) constraint;
				assertEquals(hoursConstraint.amount, 160.0);
				assertTrue(hoursConstraint.period.equals(Period.ofMonths(1)));
				
				foundRoles[1] = true;
				break;
			case 3: // third test role
				assertFalse(foundRoles[2]);
				assertEquals(role.constraints.size(), 0);
				foundRoles[2] = true;
				break;
			}
		}
		
		// Test Employees
		assertEquals(Input.employees.size(), 4);
		
		boolean[] foundEmployee = {false, false, false};
		
		boolean foundNullEmployee = false;
		
		for(Employee employee: Input.employees) {
			switch(employee.id) {
			case NurseRoster.NULL_EMPLOYEE_ID:
				assertFalse(foundNullEmployee);
				
				assertEquals(employee.constraints.size(), 0);
				assertEquals(employee.roles.size(), Input.roles.size());
				assertTrue(employee.roles.containsAll(Input.roles));
				assertTrue(Input.roles.containsAll(employee.roles));
				
				foundNullEmployee = true;
				break;
				
			case 1:
				assertFalse(foundEmployee[0]);
				
				assertEquals(employee.roles.size(), 1);
				assertTrue(employee.roles.get(0).equals(Input.getRole(1)));
				
				assertEquals(employee.constraints.size(), 1);
				
				PersonalConstraint constraint = employee.constraints.get(0);
				assertEquals(constraint.priority, 1);
				assertTrue(constraint.isHard);
				
				assertTrue(constraint instanceof Preference);
				
				Preference preference = (Preference) constraint;
				
				assertFalse(preference.isPositive);
				assertTrue(preference.pattern.allowedDaysOfWeek != null);
				assertTrue(preference.pattern.requiredTypes == null);
				assertTrue(preference.pattern.bannedTypes == null);
				assertTrue(preference.pattern.startTimeRanges == null);
				assertTrue(preference.pattern.endTimeRanges == null);
				assertTrue(preference.pattern.dateTimeRanges == null);
				assertTrue(preference.pattern.lengthRanges == null);
				assertTrue(preference.pattern.allowedLocations == null);
				
				assertEquals(preference.pattern.allowedDaysOfWeek.size(), 1);
				assertEquals(preference.pattern.allowedDaysOfWeek.get(0), DayOfWeek.FRIDAY);
				
				foundEmployee[0] = true;
				break;
			case 2:
				assertFalse(foundEmployee[1]);
				
				assertEquals(employee.roles.size(), 1);
				assertTrue(employee.roles.get(0).equals(Input.getRole(2)));
				
				assertEquals(employee.constraints.size(), 1);
				
				PersonalConstraint constraint1 = employee.constraints.get(0);
				assertEquals(constraint1.priority, 1);
				assertFalse(constraint1.isHard);
				
				assertTrue(constraint1 instanceof Preference);
				
				Preference preference1 = (Preference) constraint1;
				
				assertTrue(preference1.isPositive);
				assertTrue(preference1.pattern.allowedDaysOfWeek == null);
				assertTrue(preference1.pattern.requiredTypes == null);
				assertTrue(preference1.pattern.bannedTypes == null);
				assertTrue(preference1.pattern.startTimeRanges != null);
				assertTrue(preference1.pattern.endTimeRanges == null);
				assertTrue(preference1.pattern.dateTimeRanges == null);
				assertTrue(preference1.pattern.lengthRanges == null);
				assertTrue(preference1.pattern.allowedLocations == null);
				
				assertEquals(preference1.pattern.startTimeRanges.size(), 1);
				Pair<LocalTime, LocalTime> range = preference1.pattern.startTimeRanges.get(0);
				
				assertTrue(range.getValue0().equals(LocalTime.of(15, 0)));
				assertTrue(range.getValue1().equals(LocalTime.of(23, 0)));
				
				foundEmployee[1] = true;
				break;
			case 3:
				assertFalse(foundEmployee[2]);
				
				assertEquals(employee.roles.size(), 2);
				assertTrue(employee.roles.contains(Input.getRole(1)));
				assertTrue(employee.roles.contains(Input.getRole(3)));
				
				assertEquals(employee.constraints.size(), 0);
				foundEmployee[2] = true;
				break;
			}
		}
		assertTrue(foundNullEmployee);
		assertTrue(foundEmployee[0]);
		assertTrue(foundEmployee[1]);
		assertTrue(foundEmployee[2]);
		
		
		// Test Schedule Constraints
		assertEquals(Input.scheduleConstraints.size(), 3);
		
		boolean[] foundConstraints = {false, false};
		
		boolean foundMandatoryShiftConstraint = false;
		
		for(ScheduleConstraint constraint: Input.scheduleConstraints) {
			switch(constraint.id) {
			case -1:
				assertFalse(foundMandatoryShiftConstraint);
				
				assertTrue(constraint instanceof MandatoryShiftConstraint);
				
				foundMandatoryShiftConstraint = true;
				break;
				
			case 9:
				assertFalse(foundConstraints[0]);
				
				assertEquals(constraint.priority, 1);
				assertFalse(constraint.isHard);
				assertTrue(constraint instanceof RolePriorityConstraint);
				RolePriorityConstraint priorityConstraint = (RolePriorityConstraint) constraint;
				
				assertEquals(priorityConstraint.types.size(), 2);
				assertEquals(priorityConstraint.priorities.size(), 2);
				
				assertTrue(priorityConstraint.types.contains("Morning Weekday"));
				assertTrue(priorityConstraint.types.contains("Weekday"));
				
				assertTrue(priorityConstraint.priorities.containsKey(Input.getRole(2)));
				assertTrue(priorityConstraint.priorities.containsKey(Input.getRole(3)));
				
				assertEquals(priorityConstraint.priorities.get(Input.getRole(2)), (float)1.0);
				assertEquals(priorityConstraint.priorities.get(Input.getRole(3)), (float)-2.5);
				
				foundConstraints[0] = true;
				break;
			case 10:
				assertFalse(foundConstraints[1]);
				
				
				
				assertEquals(constraint.priority, 2);
				assertFalse(constraint.isHard);
				assertTrue(constraint instanceof ScheduleSequenceConstraint);
				ScheduleSequenceConstraint sequenceConstraint = (ScheduleSequenceConstraint) constraint;
				
				assertEquals(sequenceConstraint.roles.size(), 2);
				assertTrue(sequenceConstraint.roles.contains(Input.getRole(1)));
				assertTrue(sequenceConstraint.roles.contains(Input.getRole(2)));
				
				assertEquals(sequenceConstraint.info.minMatches, 1);
				assertEquals(sequenceConstraint.info.maxMatches, 1);
				assertTrue(sequenceConstraint.info.repeatStart.isEqual(LocalDateTime.of(2018, 7, 1, 0, 0)));
				assertTrue(sequenceConstraint.info.repeatEnd.isEqual(LocalDateTime.of(2019, 8, 1, 23, 59, 59)));
				assertTrue(sequenceConstraint.info.repeatPeriod.equals(Period.ofDays(7)));
				
				assertTrue(sequenceConstraint.info.sequence.startTimeDifferenceRanges != null);
				assertTrue(sequenceConstraint.info.sequence.endTimeDifferenceRanges == null);
				assertTrue(sequenceConstraint.info.sequence.timeGapSizeRanges == null);
				
				assertEquals(sequenceConstraint.info.sequence.startTimeDifferenceRanges.size(), 4);
				
				for(Pair<Duration, Duration> range: sequenceConstraint.info.sequence.startTimeDifferenceRanges) {
					assertTrue(range.getValue0().equals(Duration.ofMinutes(1440)));
					assertTrue(range.getValue1().equals(Duration.ofMinutes(1440)));
				}
				
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.size(), 5);
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(0).allowedDaysOfWeek.get(0), DayOfWeek.MONDAY);
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(1).allowedDaysOfWeek.get(0), DayOfWeek.TUESDAY);
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(2).allowedDaysOfWeek.get(0), DayOfWeek.WEDNESDAY);
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(3).allowedDaysOfWeek.get(0), DayOfWeek.THURSDAY);
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(4).allowedDaysOfWeek.get(0), DayOfWeek.FRIDAY);
				
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(0).allowedDaysOfWeek.size(), 1);
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(1).allowedDaysOfWeek.size(), 1);
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(2).allowedDaysOfWeek.size(), 1);
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(3).allowedDaysOfWeek.size(), 1);
				assertEquals(sequenceConstraint.info.sequence.shiftPatterns.get(4).allowedDaysOfWeek.size(), 1);
				
				for(ShiftPattern pattern: sequenceConstraint.info.sequence.shiftPatterns) {
					assertTrue(pattern.allowedDaysOfWeek != null);
					assertTrue(pattern.requiredTypes == null);
					assertTrue(pattern.bannedTypes == null);
					assertTrue(pattern.startTimeRanges != null);
					assertTrue(pattern.endTimeRanges != null);
					assertTrue(pattern.dateTimeRanges == null);
					assertTrue(pattern.lengthRanges == null);
					assertTrue(pattern.allowedLocations != null);
					
					assertTrue(pattern.startTimeRanges.size() == 1);
					Pair<LocalTime, LocalTime> startTimeRange = pattern.startTimeRanges.get(0);
					
					assertTrue(startTimeRange.getValue0().equals(LocalTime.of(7, 0)));
					assertTrue(startTimeRange.getValue1().equals(LocalTime.of(7, 0)));
					
					assertTrue(pattern.endTimeRanges.size() == 1);
					Pair<LocalTime, LocalTime> endTimeRange = pattern.endTimeRanges.get(0);
					
					assertTrue(endTimeRange.getValue0().equals(LocalTime.of(15, 0)));
					assertTrue(endTimeRange.getValue1().equals(LocalTime.of(15, 0)));
					
					assertTrue(pattern.allowedLocations.size() == 1);
					
					assertEquals(pattern.allowedLocations.get(0), "ICU");
				}
				
				foundConstraints[1] = true;
				break;
			}
		}
		assertTrue(foundMandatoryShiftConstraint);
		assertTrue(foundConstraints[0]);
		assertTrue(foundConstraints[1]);
	}

}
