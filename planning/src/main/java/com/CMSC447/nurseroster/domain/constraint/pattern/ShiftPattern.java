package com.CMSC447.nurseroster.domain.constraint.pattern;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.javatuples.Pair;

import com.CMSC447.nurseroster.domain.Role;
import com.CMSC447.nurseroster.domain.Shift;


public class ShiftPattern {
    // All ranges are inclusive to allow for expressions like "must start at noon" or "must be 8 hours"

    // The days of the week that the shift can be on
    public ArrayList<DayOfWeek> allowedDaysOfWeek;

    // The types of shift that are required
    // i.e. Must be weekend or must be night
    public ArrayList<String> requiredTypes;

    // The types of shift that are banned
    // i.e. cannot be a morning shift
    public ArrayList<String> bannedTypes;

    // The times that the shift can start and end
    // i.e. must start between noon and 2 PM
    public ArrayList<Pair<LocalTime, LocalTime>> startTimeRanges;
    public ArrayList<Pair<LocalTime, LocalTime>> endTimeRanges;

    // The dateTime range that the shift can start in
    // i.e. can start between 5 pm on 4/29/2019 and 2 am on 5/10/2019
    public ArrayList<Pair<LocalDateTime, LocalDateTime>> dateTimeRanges;

    // How long the shift can be in hours
    // i.e. must be between 8 hours and 8.5 hours
    public ArrayList<Pair<Float, Float>> lengthRanges;

    // The locations that are allowed
    public ArrayList<String> allowedLocations;

    public ShiftPattern(
        ArrayList<DayOfWeek> allowedDaysOfWeek,
        ArrayList<String> requiredTypes,
        ArrayList<String> bannedTypes,
        ArrayList<Pair<LocalTime, LocalTime>> startTimeRanges,
        ArrayList<Pair<LocalTime, LocalTime>> endTimeRanges,
        ArrayList<Pair<LocalDateTime, LocalDateTime>> dateTimeRanges,
        ArrayList<Pair<Float, Float>> lengthRanges,
        ArrayList<String> allowedLocations
        ){
            this.allowedDaysOfWeek = allowedDaysOfWeek;
            this.requiredTypes = requiredTypes;
            this.bannedTypes = bannedTypes;
            this.startTimeRanges = startTimeRanges;
            this.endTimeRanges = endTimeRanges;
            this.dateTimeRanges = dateTimeRanges;
            this.lengthRanges = lengthRanges;
            this.allowedLocations = allowedLocations;
        }

    // Returns true if hour:minute is in the specified range. Inclusive
    // For example if the Pair is (11:00, 11:30) then hour must be 11 and minute can be 0, 30, or anything in between
    public boolean timeInRange(int hour, int minute, Pair<LocalTime, LocalTime> range){
        int minMinutes = 60 * range.getValue0().getHour() + range.getValue0().getMinute();
        int maxMinutes = 60 * range.getValue1().getHour() + range.getValue1().getMinute();
        int actualMinutes = 60 * hour + minute;
        return minMinutes <= actualMinutes && actualMinutes <= maxMinutes;
    }

    // Returns true if the datetime is in the specified range. Inclusive
    public boolean dateTimeInRange(LocalDateTime datetime, Pair<LocalDateTime, LocalDateTime> range){

        // check if datetime is at or after the minimum time
        boolean afterOrEqualToMin = datetime.isAfter(range.getValue0()) || datetime.isEqual(range.getValue0());

        // check if datetime is at or before the maximum time
        boolean beforeOrEqualToMax = datetime.isBefore(range.getValue1()) || datetime.isEqual(range.getValue1());

        // Combine booleans
        return afterOrEqualToMin && beforeOrEqualToMax;
    }

    public boolean allowedDayOfWeek(Shift shift){
        // Check if the shift is on one of the allowed days
        LocalDateTime shiftStart = shift.startTime; // Get start time
        DayOfWeek shiftDayOfWeek = shiftStart.getDayOfWeek(); // Get day of week
        return allowedDaysOfWeek.contains(shiftDayOfWeek);
    }

    public boolean hasRequiredTypes(Shift shift){
        // Check if all of them are present
        ArrayList<String> shiftTypes = shift.types; // Get types of shift
        for(int i = 0; i < requiredTypes.size(); i++){ // For each required type
            if (!shiftTypes.contains(requiredTypes.get(i))){ // Check if shift has type
                return false;
            } 
        }
        return true; // If the loop finished, all required types are present
    }

    public boolean hasBannedTypes(Shift shift){
        // Check if any of them are present
        ArrayList<String> shiftTypes = shift.types; // Get types of shift
        for(int i = 0; i < shiftTypes.size(); i++){ // For each type in the shift
            if (bannedTypes.contains(shiftTypes.get(i))){ // Check if type is banned
                return true;
            } 
        }
        return false; // If loop concluded, no banned types are present
    }

    public boolean startsInStartRanges(Shift shift){
        // Check if the shift starts in any of them
        LocalDateTime shiftStart = shift.startTime; // Get start time
        int hour = shiftStart.getHour(); // Extract hours and minutes
        int minute = shiftStart.getMinute();

        // Iterate through the allowed ranges
        for (int i = 0; i<startTimeRanges.size(); i++){
            // If the shift is in the current range, we are done and can exit the loop
            if (timeInRange(hour, minute, startTimeRanges.get(i))){
                return true;
            }
        }
        return false;
    }

    public boolean endsInEndRanges(Shift shift){
        // Check if the shift end in any of them
        LocalDateTime shiftEnd = shift.endTime; // Get start time
        int hour = shiftEnd.getHour(); // Extract hours and minutes
        int minute = shiftEnd.getMinute();

        // Iterate through the allowed ranges
        for (int i = 0; i<endTimeRanges.size(); i++){
            // If the shift is in the current range, we are done and can exit the loop
            if (timeInRange(hour, minute, endTimeRanges.get(i))){
                return true;
            }

            // Otherwise, go to the next allowed range
            i++;
        }
        return false;
    }

    public boolean startsInDateTimeRanges(Shift shift){
        // Check if the shift starts in any of them
        LocalDateTime shiftStart = shift.startTime; // Get start time

        // Iterate through the allowed ranges
        for(int i = 0; i<dateTimeRanges.size(); i++){
            // If the shift is in the current range, we are done and can exit the loop
            if (dateTimeInRange(shiftStart, dateTimeRanges.get(i))){
                return true;
            }
        }

        return false;
    }

    public boolean lengthInRanges(Shift shift){
        // check if it has a valid length

        LocalDateTime shiftStart = shift.startTime; // Get start time
        LocalDateTime shiftEnd = shift.endTime; // Get end time

        long minutesLength = shiftStart.until(shiftEnd, ChronoUnit.MINUTES);

        float hoursLength = minutesLength / 60;
        
        // Iterate through the allowed ranges
        for (int i = 0; i<lengthRanges.size(); i++){
            Pair<Float, Float> lengthRange = lengthRanges.get(i);
            // If the shift is in the current range, we are done and can exit the loop
            if (lengthRange.getValue0() <= hoursLength && hoursLength <= lengthRange.getValue1()){
                return true;
            }
        }

        return false;
    }

    public boolean atAllowedLocation(Shift shift){
        for(int i = 0; i < allowedLocations.size(); i++){
            if (shift.location.equals(allowedLocations.get(i))){
                return true;
            }
        }
        return false;
    }
    
    public boolean Allowed(Shift shift){
        for(int i = 0; i < allowedLocations.size(); i++){
            if (shift.location.equals(allowedLocations.get(i))){
                return true;
            }
        }
        return false;
    }

    public boolean matches(Shift shift){
        // If the user has specified which days of the week are allowed and the shift isn't on any of them
        if (allowedDaysOfWeek != null && !allowedDayOfWeek(shift)){
            return false;
        }

        // If the user has specified which shift types are required and any are missing
        if (requiredTypes != null && !hasRequiredTypes(shift)){
            return false;
        }

        // If the user has specified which shift types are not allowed and any are present
        if (bannedTypes != null && hasBannedTypes(shift)){
            return false;
        }

        // If the user has specified time ranges the shift must start in
        if (startTimeRanges != null && !startsInStartRanges(shift)){
            return false;
        }

        // If the user has specified time ranges the shift must end in
        if (endTimeRanges != null && !endsInEndRanges(shift)){
            return false;
        }

        // If the user has specified date ranges the shift must be in
        if (dateTimeRanges != null && !startsInDateTimeRanges(shift)){
            return false;
        }

        // If the user has specified how long the shift can be
        if (lengthRanges != null && !lengthInRanges(shift)){
            return false;
        }

        // If the user has specified what locations are allowed
        if (allowedLocations != null && !atAllowedLocation(shift)){
            return false;
        }

        return true;
    }
}
