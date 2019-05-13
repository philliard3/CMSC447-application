package com.CMSC447.nurseroster.fileio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeParser {
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	
	public static LocalDateTime parseDateTime(String str) {
		return LocalDateTime.parse(str, dateTimeFormatter);
	}
	
	public static LocalDate parseDate(String str) {
		return LocalDate.parse(str, dateFormatter);
	}
	
	public static LocalTime parseTime(String str) {
		return LocalTime.parse(str, timeFormatter);
	}
	
	public static String dateTimeString(LocalDateTime datetime) {
		return dateTimeFormatter.format(datetime);
	}
	
	public static String dateString(LocalDate date) {
		return dateFormatter.format(date);
	}
	
	public static String timeString(LocalTime time) {
		return timeFormatter.format(time);
	}
}
