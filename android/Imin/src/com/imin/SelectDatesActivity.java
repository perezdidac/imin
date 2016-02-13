package com.imin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.analytics.tracking.android.EasyTracker;
import com.imin.widgets.CalendarView;
import com.imin.widgets.DialogInformation;

import java.util.ArrayList;

public class SelectDatesActivity extends ActionBarActivity {

    // Objects that must be saved
    private CalendarView calendar;

    // Imin application object
    private Imin imin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dates);
        Imin.overrideFonts(findViewById(android.R.id.content));

        // Get the Imin application object
        imin = ((Imin) getApplication());

        // Initialize objects
        initializeObjects();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save the values
        savedInstanceState.putInt("year", calendar.getYear());
        savedInstanceState.putInt("month", calendar.getMonth());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the values
        int year = savedInstanceState.getInt("year");
        int month = savedInstanceState.getInt("month");
        calendar.setMonth(month, year);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check source activity
        if (requestCode == Imin.REQUEST_CODE_EVENT_CREATED) {
            // Check activity result
            if (resultCode == Imin.RESULT_CODE_EVENT_CREATED_OK) {
                // Event created, return
                setResult(Imin.RESULT_CODE_SELECT_DATES_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_dates, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initializeObjects() {
        // Native views
        calendar = (CalendarView) findViewById(R.id.calendar);
        Button btnNext = (Button) findViewById(R.id.btnNext);

        // Set calendar fonts
        calendar.setFonts();

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        if (imin.newDates == null) {
            imin.newDates = new ArrayList<CalendarView.Date>();
        }

        calendar.setSelectedDays(imin.newDates);
    }

    private void next() {
        if (imin.newDates.isEmpty()) {
            // Notify error
            DialogInformation.build(this).show(getString(R.string.please_select_at_least_one_date), DialogInformation.ICON_ALERT);
        } else {
            // imin.getUser().setEventCreated(false);
            Intent intent = new Intent(this, EventCreatedActivity.class);
            startActivityForResult(intent, Imin.REQUEST_CODE_EVENT_CREATED);
        }
    }
}
