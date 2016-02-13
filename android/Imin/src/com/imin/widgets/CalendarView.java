package com.imin.widgets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.imin.Imin;
import com.imin.R;

public class CalendarView extends FrameLayout {

	public class Date {
		public int year;
		public int month;
		public int day;
	}

	// Inflater
	private LayoutInflater inflater;

	// View references
	private Button btnPreviousMonth;
	private Button btnNextMonth;
	private Button btnMonth;
	private LinearLayout layoutColumnOne;
	private LinearLayout layoutColumnTwo;
	private LinearLayout layoutColumnThree;
	private LinearLayout layoutColumnFour;
	private LinearLayout layoutColumnFive;
	private LinearLayout layoutColumnSix;
	private LinearLayout layoutColumnSeven;

	// Variables
	private List<View> dayLayouts;
	private int currentMonth;
	private int currentYear;

	private List<CalendarView.Date> selectedDays;

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeView(context);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView(context);
	}

	public CalendarView(Context context) {
		super(context);
		initializeView(context);
	}

	public void setFonts() {
		// Change fonts for the day layouts
		for (View dayLayout : dayLayouts) {
			Button dayLayoutButton = (Button) dayLayout;
			dayLayoutButton.setTypeface(Imin.fontLight);
		}
	}

	private void initializeView(Context context) {
		// Get the inflater
		inflater = LayoutInflater.from(context);

		// Inflate the main view
		View view = inflater.inflate(R.layout.layout_calendar, null);

		if (!isInEditMode()) {
			// Get the view references
			btnMonth = (Button) view.findViewById(R.id.btnMonth);
			btnPreviousMonth = (Button) view.findViewById(R.id.btnPreviousMonth);
			btnNextMonth = (Button) view.findViewById(R.id.btnNextMonth);

			// Set handlers
			btnPreviousMonth.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					previousMonth();
				}
			});

			btnNextMonth.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					nextMonth();
				}
			});
		}

		// Get view references for the days
		layoutColumnOne = (LinearLayout) view.findViewById(R.id.layoutColumnOne);
		layoutColumnTwo = (LinearLayout) view.findViewById(R.id.layoutColumnTwo);
		layoutColumnThree = (LinearLayout) view.findViewById(R.id.layoutColumnThree);
		layoutColumnFour = (LinearLayout) view.findViewById(R.id.layoutColumnFour);
		layoutColumnFive = (LinearLayout) view.findViewById(R.id.layoutColumnFive);
		layoutColumnSix = (LinearLayout) view.findViewById(R.id.layoutColumnSix);
		layoutColumnSeven = (LinearLayout) view.findViewById(R.id.layoutColumnSeven);

		// Set the initial month
		setMonth();

		// Populate days
		populateDays();

		// Finally, add the view into the frame layout
		addView(view);
	}

	private void previousMonth() {
		if (currentMonth <= 0) {
			currentMonth = 11;
			--currentYear;
		} else {
			--currentMonth;
		}

		populateDays();
	}

	private void nextMonth() {
		if (currentMonth >= 11) {
			currentMonth = 0;
			++currentYear;
		} else {
			++currentMonth;
		}

		populateDays();
	}

	private void setMonth() {
		// Build a calendar object from the current time
		Calendar calendar = Calendar.getInstance();

		// Change the current month
		setMonth(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
	}

	public int getMonth() {
		return currentMonth;
	}

	public int getYear() {
		return currentYear;
	}

	public void setMonth(int month, int year) {
		// Save
		currentMonth = month;
		currentYear = year;

		// Finally, populate the calendar grid
		populateDays();
	}

	private void updateMonthName() {
		String monthName = getMonthText(currentMonth) + " " + currentYear;
		btnMonth.setText(monthName);
	}

	private void populateDays() {
		if (isInEditMode()) {
			return;
		}

		// Update month name
		updateMonthName();

		// Remove previous views
		layoutColumnOne.removeAllViews();
		layoutColumnTwo.removeAllViews();
		layoutColumnThree.removeAllViews();
		layoutColumnFour.removeAllViews();
		layoutColumnFive.removeAllViews();
		layoutColumnSix.removeAllViews();
		layoutColumnSeven.removeAllViews();

		List<LinearLayout> columns = new ArrayList<LinearLayout>(7);
		columns.add(layoutColumnOne);
		columns.add(layoutColumnTwo);
		columns.add(layoutColumnThree);
		columns.add(layoutColumnFour);
		columns.add(layoutColumnFive);
		columns.add(layoutColumnSix);
		columns.add(layoutColumnSeven);

		if (dayLayouts == null) {
			dayLayouts = new ArrayList<View>();
		}

		// Get the week day of the first day of the month
		Calendar calendar = Calendar.getInstance();
		calendar.set(currentYear, currentMonth, 1);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int firstDayOfWeek = getDayNumber(dayOfWeek);

		for (int k = 0; k < firstDayOfWeek; ++k) {
			// Get the column reference
			LinearLayout layoutColumn = columns.get(k);

			// Add an empty calendar day
			View layoutDay = inflater.inflate(R.layout.layout_calendar_day, null);
			layoutColumn.addView(layoutDay);
			dayLayouts.add(layoutDay);
		}

		// Now add all the days
		for (int k = 0; k < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); ++k) {
			// Get the day of the week for that day
			calendar.set(Calendar.DAY_OF_MONTH, k + 1);
			dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

			// Add the calendar day
			int numDayOfWeek = getDayNumber(dayOfWeek);
			LinearLayout layoutColumn = columns.get(numDayOfWeek);
			Button layoutDay = (Button) inflater.inflate(R.layout.layout_calendar_day, null);
			String day = String.valueOf(k + 1);
			layoutDay.setText(day);

			long timeOne = Calendar.getInstance().getTime().getTime();
			long timeTwo = calendar.getTime().getTime();
		    long oneDay = 1000 * 60 * 60 * 24;
		    long delta = (timeTwo - timeOne) / oneDay;
			if (delta >= 0) {
				// Set button event handler
				layoutDay.setTag(k);
				layoutDay.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						selectDay(view);
					}
				});
			} else {
				// Paint the day
				layoutDay.setEnabled(false);
				layoutDay.setBackgroundResource(R.drawable.button_calendar_day_disabled_background);
			}

			layoutColumn.addView(layoutDay);
			dayLayouts.add(layoutDay);
		}

		// Fill blanks the rest of missing days
		for (LinearLayout layoutColumn : columns) {
			int missingDays = 6 - layoutColumn.getChildCount();

			for (int k = 0; k < missingDays; ++k) {
				// Add an empty calendar day
				View layoutDay = inflater.inflate(R.layout.layout_calendar_day, null);
				layoutColumn.addView(layoutDay);
				dayLayouts.add(layoutDay);
			}
		}
		
		// Paint selected days
		if (selectedDays != null) {
			paintSelectedDays();
		}

		// Update fonts
		setFonts();
	}
	
	private void paintSelectedDays() {
		for (int k = 0; k < dayLayouts.size(); ++k) {
			View view = dayLayouts.get(k);
			
			// Get the selected day
			if (view.getTag() == null) {
				continue;
			}
			
			int selectedDay = (Integer) view.getTag();
			int selectedMonth = currentMonth;
			int selectedYear = currentYear;
	
			// Button casting
			Button button = (Button) view;
	
			if (isDaySelected(selectedYear, selectedMonth, selectedDay)) {
				// Has been selected
				button.setBackgroundResource(R.drawable.button_calendar_day_selected_background);
				button.setTextColor(getResources().getColor(R.color.white));
			} else {
				// Has been deselected
				button.setBackgroundResource(R.drawable.button_calendar_day_background);
				button.setTextColor(getResources().getColor(R.color.black));
			}	
		}
	}

	private void selectDay(View view) {
		// Get the selected day
		int selectedDay = (Integer) view.getTag();
		int selectedMonth = currentMonth;
		int selectedYear = currentYear;

		// Button casting
		Button button = (Button) view;

		if (switchDay(selectedYear, selectedMonth, selectedDay)) {
			// Is selected
			button.setBackgroundResource(R.drawable.button_calendar_day_selected_background);
			button.setTextColor(getResources().getColor(R.color.white));
		} else {
			// Is not selected
			button.setBackgroundResource(R.drawable.button_calendar_day_background);
			button.setTextColor(getResources().getColor(R.color.black));
		}
	}
	
	private boolean isDaySelected(int year, int month, int day) {
		boolean found = false;
		// Check if is already selected
		for (int k = 0; k < selectedDays.size(); ++k) {
			Date date = selectedDays.get(k);

			if (date.year == year && date.month == month && date.day == day) {
				found = true;
				break;
			}
		}
		
		return found;
	}

	private boolean switchDay(int year, int month, int day) {
		boolean found = false;
		// Check if is already selected
		for (int k = 0; k < selectedDays.size(); ++k) {
			Date date = selectedDays.get(k);

			if (date.year == year && date.month == month && date.day == day) {
				selectedDays.remove(k);
				found = true;
				break;
			}
		}

		if (!found) {
			Date date = new Date();
			date.year = year;
			date.month = month;
			date.day = day;
			selectedDays.add(date);
		}

		return !found;
	}

	private static int getDayNumber(int dayOfWeek) {
		int numDayOfWeek;

		switch (dayOfWeek) {
		default:
		case Calendar.MONDAY:
			numDayOfWeek = 0;
			break;
		case Calendar.TUESDAY:
			numDayOfWeek = 1;
			break;
		case Calendar.WEDNESDAY:
			numDayOfWeek = 2;
			break;
		case Calendar.THURSDAY:
			numDayOfWeek = 3;
			break;
		case Calendar.FRIDAY:
			numDayOfWeek = 4;
			break;
		case Calendar.SATURDAY:
			numDayOfWeek = 5;
			break;
		case Calendar.SUNDAY:
			numDayOfWeek = 6;
			break;
		}

		return numDayOfWeek;
	}

	private String getMonthText(int month) {
		String monthName;

		switch (month) {
		default:
		case 0:
			monthName = getContext().getString(R.string.january);
			break;
		case 1:
			monthName = getContext().getString(R.string.february);
			break;
		case 2:
			monthName = getContext().getString(R.string.march);
			break;
		case 3:
			monthName = getContext().getString(R.string.april);
			break;
		case 4:
			monthName = getContext().getString(R.string.may);
			break;
		case 5:
			monthName = getContext().getString(R.string.june);
			break;
		case 6:
			monthName = getContext().getString(R.string.july);
			break;
		case 7:
			monthName = getContext().getString(R.string.august);
			break;
		case 8:
			monthName = getContext().getString(R.string.september);
			break;
		case 9:
			monthName = getContext().getString(R.string.october);
			break;
		case 10:
			monthName = getContext().getString(R.string.november);
			break;
		case 11:
			monthName = getContext().getString(R.string.december);
			break;
		}

		return monthName;
	}

	public List<CalendarView.Date> getSelectedDays() {
		return selectedDays;
	}

	public void setSelectedDays(List<Date> selectedDays) {
		this.selectedDays = selectedDays;
	}

}
