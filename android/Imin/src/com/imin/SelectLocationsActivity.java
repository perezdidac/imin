package com.imin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.analytics.tracking.android.EasyTracker;
import com.imin.events.locations.LocationsListAdapter;
import com.imin.events.locations.LocationsListAdapter.LocationsListListener;
import com.imin.widgets.DialogInformation;

import java.util.ArrayList;

public class SelectLocationsActivity extends ActionBarActivity implements LocationsListListener {

    // List of objects in the layout
    private EditText textNewLocation;

    // Objects that must be saved
    private LocationsListAdapter locationsListAdapter;

    // Imin application object
    private Imin imin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_locations);
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
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the values
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
        if (requestCode == Imin.REQUEST_CODE_SELECT_DATES) {
            // Check activity result
            if (resultCode == Imin.RESULT_CODE_SELECT_DATES_OK) {
                // Event created, return
                setResult(Imin.RESULT_CODE_SELECT_LOCATIONS_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_locations, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initializeObjects() {
        Button btnAddLocation;
        Button btnNext;
        ListView listLocations;

        // Save object references
        textNewLocation = (EditText) findViewById(R.id.textNewLocation);
        btnAddLocation = (Button) findViewById(R.id.btnAddLocation);
        btnNext = (Button) findViewById(R.id.btnNext);
        listLocations = (ListView) findViewById(R.id.listLocations);

        // List of locations
        if (imin.newLocations == null) {
            imin.newLocations = new ArrayList<String>();
        }
        locationsListAdapter = new LocationsListAdapter(this, this, imin.newLocations);
        listLocations.setAdapter(locationsListAdapter);

        // Set listeners for handling events
        btnAddLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation();
            }
        });

        // Show/hide
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    private void addLocation() {
        // Check location name
        String location = textNewLocation.getText().toString();

        // Trim the string
        location = location.trim();

        // Check duplicates
        if (locationExists(location)) {
            // Show a toast
            DialogInformation.build(this).show(getString(R.string.that_location_name_has_been_already_used),
                    DialogInformation.ICON_ALERT);
        } else {
            if (location.length() > 0) {
                textNewLocation.setText("");

                // Add the location
                imin.newLocations.add(location);

                locationsListAdapter.notifyDataSetChanged();

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textNewLocation.getWindowToken(), 0);
            } else {
                // Show a toast
                DialogInformation.build(this).show(getString(R.string.please_introduce_a_name_for_the_new_location),
                        DialogInformation.ICON_ALERT);
            }
        }
    }

    private boolean locationExists(String location) {
        // Loop through the list of locations
        boolean found = false;

        for (int k = 0; k < imin.newLocations.size(); ++k) {
            if (imin.newLocations.get(k).equals(location)) {
                found = true;
                break;
            }
        }

        return found;
    }

    private void next() {
        // Get the current event
        if (imin.newLocations.isEmpty()) {
            // After notifing the user, check if we can add a location
            // Check location name
            String location = textNewLocation.getText().toString();

            // Trim the string
            location = location.trim();

            if (location.length() > 0) {
                imin.newLocations.add(location);
            } else {
                // List of locations empty
                DialogInformation.build(this).show(getString(R.string.please_introduce_a_name_for_the_new_location),
                        DialogInformation.ICON_ALERT);
                return;
            }
        }

        // Go to the next step
        imin.getUser().setEventCreated(false);
        Intent intent = new Intent(this, SelectDatesActivity.class);
        startActivityForResult(intent, Imin.REQUEST_CODE_SELECT_DATES);
    }

    @Override
    public void remove(int position) {
        // Remove the location from the list
        imin.newLocations.remove(position);
        locationsListAdapter.notifyDataSetChanged();
    }
}
