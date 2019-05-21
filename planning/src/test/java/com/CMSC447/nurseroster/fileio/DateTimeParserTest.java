package com.CMSC447.nurseroster.fileio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import junit.framework.TestCase;

public class DateTimeParserTest extends TestCase {

	public void testParseDateTime() {
		String testDateTimeString = "05/20/2019 17:00";
		LocalDateTime parsed = DateTimeParser.parseDateTime(testDateTimeString);
		assertTrue(parsed.isEqual(LocalDateTime.of(2019, 5, 20, 17, 0)));
	}

	public void testParseDate() {
		String testDate = "11/21/2018";
		LocalDate parsed = DateTimeParser.parseDate(testDate);
		assertTrue(parsed.isEqual(LocalDate.of(2018, 11, 21)));
	}

	public void testParseTime() {
		String testTime = "12:00";
		LocalTime parsed = DateTimeParser.parseTime(testTime);
		assertTrue(parsed.equals(LocalTime.NOON));
	}

	public void testDateTimeString() {
		LocalDateTime testDateTime = LocalDateTime.of(2019, 4, 22, 14, 1);
		String expectedOutput = "04/22/2019 14:01";
		assertEquals(expectedOutput, DateTimeParser.dateTimeString(testDateTime));
	}

	public void testDateString() {
		LocalDate testDate = LocalDate.of(2017, 8, 24);
		String expectedOutput = "08/24/2017";
		assertEquals(expectedOutput, DateTimeParser.dateString(testDate));
	}

	public void testTimeString() {
		LocalTime testTime = LocalTime.of(16, 54);
		String expectedOutput = "16:54";
		assertEquals(expectedOutput, DateTimeParser.timeString(testTime));
	}

}
