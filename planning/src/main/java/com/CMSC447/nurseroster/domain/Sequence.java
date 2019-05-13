package com.CMSC447.nurseroster.domain;

import org.javatuples.Pair;

import java.time.Duration;
import java.util.List;

public class Sequence {
    List<ShiftPattern> shiftPatterns;
    List<Pair<Duration, Duration>> startTimeDifferenceRanges;
    List<Pair<Duration, Duration>> endTimeDifferenceRanges;
    List<Pair<Duration, Duration>> timeGapSizeRanges;
    
    public Sequence(List<ShiftPattern> patterns, List<Pair<Duration, Duration>> startTimeDiff,
    		List<Pair<Duration, Duration>> endTimeDiff, List<Pair<Duration, Duration>> timeGap){
    	this.shiftPatterns = patterns;
    	this.startTimeDifferenceRanges = startTimeDiff;
    	this.endTimeDifferenceRanges = endTimeDiff;
    	this.timeGapSizeRanges = timeGap;
    }
    
    public Sequence(){
    	this.shiftPatterns = null; 
    	this.endTimeDifferenceRanges = null;
    	this.startTimeDifferenceRanges = null; 
    	this.timeGapSizeRanges = null;
    }
    
}
