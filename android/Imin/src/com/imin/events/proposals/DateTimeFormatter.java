package com.imin.events.proposals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;

import com.imin.R;

public class DateTimeFormatter {

	public static GregorianCalendar getDate(String date) {
		// Get day, month and, year
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String day = date.substring(6, 8);

		// Format the date
		GregorianCalendar gregorianDate = new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month),
				Integer.parseInt(day));

		return gregorianDate;
	}

	public static String formatDate(DateTime dateTime) {
		// Take the day in the following format: yyyymmdd
		String date = dateTime.getDate();
		return formatDate(date);
	}

	public static String formatFullDate(DateTime dateTime) {
		// Take the day in the following format: yyyymmdd
		String date = dateTime.getDate();
		return formatFullDate(date);
	}

	public static String formatDay(Context context, String date) {
		// Return "Mon", "Tue", etc.
		GregorianCalendar gregorianDateTime = getDate(date);
		int dayOfWeek = gregorianDateTime.get(GregorianCalendar.DAY_OF_WEEK);

		String day = "";

		switch (dayOfWeek) {
		case GregorianCalendar.MONDAY:
			day = context.getString(R.string.monday);
			break;
		case GregorianCalendar.TUESDAY:
			day = context.getString(R.string.tuesday);
			break;
		case GregorianCalendar.WEDNESDAY:
			day = context.getString(R.string.wednesday);
			break;
		case GregorianCalendar.THURSDAY:
			day = context.getString(R.string.thursday);
			break;
		case GregorianCalendar.FRIDAY:
			day = context.getString(R.string.friday);
			break;
		case GregorianCalendar.SATURDAY:
			day = context.getString(R.string.saturday);
			break;
		case GregorianCalendar.SUNDAY:
			day = context.getString(R.string.sunday);
			break;
		}

		return day;
	}

	public static String formatLongDate(String date) {
		GregorianCalendar gregorianDateTime = getDate(date);
		DateFormat dateInstance = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());

		// Return the formatted date
		return dateInstance.format(gregorianDateTime.getTime());
	}

	public static String formatDate(String date) {
		GregorianCalendar gregorianDateTime = getDate(date);
		DateFormat dateInstance = new SimpleDateFormat("dd/MM", Locale.getDefault());

		// Return the formatted date
		return dateInstance.format(gregorianDateTime.getTime());
	}

	public static String formatFullDate(String date) {
		GregorianCalendar gregorianDateTime = getDate(date);
		DateFormat dateInstance = new SimpleDateFormat("E dd/MM/yy", Locale.getDefault());

		// Return the formatted date
		return dateInstance.format(gregorianDateTime.getTime());
	}

	public static String formatTime(DateTime dateTime) {
		// Take the time in the following format: hhmm
		String time = dateTime.getTime();
		return formatTime(time);
	}

	public static String formatTime(String time) {
		// Get hour and minute
		String hour = time.substring(0, 2);
		String minute = time.substring(2, 4);

		String formattedTime = hour + ":" + minute;
		return formattedTime;
	}

	public static String formatDateTime(DateTime dateTime) {
		// Return formatted
		return formatDateTime(dateTime.getName());
	}

	public static String formatFullDateTime(DateTime dateTime) {
		// Return formatted
		return formatFullDateTime(dateTime.getName());
	}

	public static String formatDateTime(String dateTime) {
		// Separate date and time
		String date = dateTime.substring(0, 8);
		String time = dateTime.substring(8, 12);

		// Build the result
		String formattedDateTime = formatDate(date) + " " + formatTime(time);
		return formattedDateTime;
	}

	public static String formatFullDateTime(String dateTime) {
		// Separate date and time
		String date = dateTime.substring(0, 8);
		String time = dateTime.substring(8, 12);

		// Build the result
		String formattedDateTime = formatFullDate(date) + " " + formatTime(time);
		return formattedDateTime;
	}

}
