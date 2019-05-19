package com.CMSC447.nurseroster.domain.constraint.sequence;

import java.time.LocalDateTime;
import java.time.Period;

import com.CMSC447.nurseroster.domain.constraint.pattern.Sequence;

public class SequenceConstraintInfo {

	public Sequence sequence;
	public int minMatches;
	public int maxMatches;
	public LocalDateTime repeatStart;
	public LocalDateTime repeatEnd;
	public Period repeatPeriod;
	
	 SequenceConstraintInfo(Sequence sequence, int minMatches, int maxMatches, LocalDateTime repeatStart, LocalDateTime repeatEnd, Period repeatPeriod) {
		 this.sequence = sequence;
		 this.minMatches = minMatches;
		 this.maxMatches = maxMatches;
		 this.repeatStart = repeatStart;
		 this.repeatEnd = repeatEnd;
		 this.repeatPeriod = repeatPeriod;
	}

}
