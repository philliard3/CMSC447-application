package com.CMSC447.nurseroster.domain.constraint.pattern;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class Sequence {
    public ArrayList<ShiftPattern> shiftPatterns;
    public ArrayList<Pair<Duration, Duration>> startTimeDifferenceRanges;
    public ArrayList<Pair<Duration, Duration>> endTimeDifferenceRanges;
    public ArrayList<Pair<Duration, Duration>> timeGapSizeRanges;
    
    public enum TimeDiffMode{
    	START, END, GAP
    };
    
    public Sequence(ArrayList<ShiftPattern> patterns, ArrayList<Pair<Duration, Duration>> timeDiffs, TimeDiffMode diffMode){
    	this.shiftPatterns = patterns;
    	switch(diffMode) {
    	case START:
    		startTimeDifferenceRanges = timeDiffs;
    		break;
    	case END:
    		endTimeDifferenceRanges = timeDiffs;
    		break;
    	case GAP:
    		timeGapSizeRanges = timeDiffs;
    		break;
    	}
    }
}
