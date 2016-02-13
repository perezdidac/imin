package com.imin;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.imin.contacts.Contact;
import com.imin.events.Event;
import com.imin.events.proposals.DateTime;
import com.imin.events.proposals.DateTimeFormatter;
import com.imin.events.proposals.Proposal;
import com.imin.events.responses.Response;
import com.imin.user.User;
import com.imin.widgets.CalendarView;
import com.imin.widgets.DialogContact;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Imin extends Application {

    // Hard coded options
    public static final boolean REMOVE_DATES_AFTER_CALENDAR = false;

    // Web services error codes
    public static final int ERROR_CODE_SUCCESS = 0;

    // Intent data keys
    public static final String EXTRA_EVENT_POLL_CLOSE = "close";

    // Codes between activities
    public static final int REQUEST_CODE_SPLASH = 100;
    public static final int REQUEST_CODE_DATE = 102;
    public static final int REQUEST_CODE_CREATE_EVENT = 103;
    public static final int REQUEST_CODE_CONTACTS = 104;
    public static final int REQUEST_CODE_EVENT_CREATED = 105;
    public static final int REQUEST_CODE_EVENT_MANAGEMENT = 107;
    public static final int REQUEST_CODE_CAMERA = 108;
    public static final int REQUEST_CODE_GALLERY = 109;
    public static final int REQUEST_CODE_WELCOME = 110;
    public static final int REQUEST_CODE_PROFILE = 111;
    public static final int REQUEST_CODE_SELECT_LOCATIONS = 112;
    public static final int REQUEST_CODE_SELECT_DATES = 113;
    public static final int REQUEST_CODE_POLL_DATETIMES = 106;
    public static final int REQUEST_CODE_POLL_LOCATIONS = 114;
    public static final int REQUEST_CODE_POLL_DATETIMES_CLOSE = 115;
    public static final int REQUEST_CODE_POLL_LOCATIONS_CLOSE = 116;

    public static final int RESULT_CODE_SPLASH_OK = 200;
    public static final int RESULT_CODE_SPLASH_NO_USER_ID = 201;
    public static final int RESULT_CODE_SPLASH_CANCEL = 202;
    public static final int RESULT_CODE_DATE_OK = 203;
    public static final int RESULT_CODE_CREATE_EVENT_OK = 204;
    public static final int RESULT_CODE_CONTACTS_OK = 205;
    public static final int RESULT_CODE_EVENT_CREATED_OK = 206;
    public static final int RESULT_CODE_EVENT_CREATED_NOK = 207;
    public static final int RESULT_CODE_EVENT_POLL_CLOSED = 211;
    public static final int RESULT_CODE_WELCOME_OK = 212;
    public static final int RESULT_CODE_PROFILE_OK = 213;
    public static final int RESULT_CODE_SELECT_LOCATIONS_OK = 214;
    public static final int RESULT_CODE_SELECT_DATES_OK = 215;
    public static final int RESULT_CODE_POLL_DATETIMES_OK = 216;
    public static final int RESULT_CODE_POLL_DATETIMES_ERROR = 217;
    public static final int RESULT_CODE_POLL_LOCATIONS_OK = 218;
    public static final int RESULT_CODE_POLL_LOCATIONS_ERROR = 219;
    public static final int RESULT_CODE_PROFILE_DELETE_ACCOUNT = 220;
    public static final int RESULT_CODE_CAMERA_OK = -1;

    // Event creation objects
    public List<CalendarView.Date> newDates;
    public List<String> newLocations;
    public List<Response> dateTimeResponses;

    // Objects
    private static Imin imin;
    private User user;
    private boolean initialized;
    private boolean userInitialized;

    // Fonts
    public static Typeface fontRegular;
    public static Typeface fontBold;
    public static Typeface fontLight;

    public Imin() {
        // Initialize the application
    }

    public static Imin imin() {
        return imin;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        imin = this;
        // Get the main objects
        user = new User(this);
        setUserInitialized(user.initialize());

        // Load fonts
        fontRegular = Typeface.createFromAsset(this.getAssets(), "fonts/merriweather_sans_regular.ttf");
        fontBold = Typeface.createFromAsset(this.getAssets(), "fonts/merriweather_sans_bold.ttf");
        fontLight = Typeface.createFromAsset(this.getAssets(), "fonts/merriweather_sans_light.ttf");

        // Default package
        this.getPackageManager().clearPackagePreferredActivities("com.imin");
    }

    public boolean initialize() {
        return user.initialize();
    }

    public User getUser() {
        return user;
    }

    public static void overrideFonts(View view) {
        try {
            if (view instanceof Button) {
                ((Button) view).setTypeface(fontRegular);
            } else if (view instanceof TextView) {
                ((TextView) view).setTypeface(fontRegular);
            } else if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(child);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

    public static void overrideFonts(View view, Typeface typeface) {
        try {
            if (view instanceof Button) {
                ((Button) view).setTypeface(typeface);
            } else if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            } else if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(child, typeface);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

    public String getDeviceId() {

        //============================================================
        // SOLUTION USING IMEI (NO TABLETS)

        /*
        // Get the Telephony Manager system service
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

		return telephonyManager.getDeviceId();
		*/

        //============================================================
        // SOLUTION USING ANDROID ID (API >= 9)

        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Public generic common methods
    public static void shareEvent(Context context, Event event) {
        String textShare = "http://" + context.getString(R.string.app_host) + "/imin/event.php?eventId=" + event.getId();
        shareText(context, textShare);
    }

    public static void shareApp(Context context) {
        String textShare = context.getString(R.string.tried_the_app) + " - " + "https://play.google.com/store/apps/details?id=com.imin";
        shareText(context, textShare);
    }

    private static void shareText(Context context, String textShare) {
        // Create the intent and fill the values
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.invitation);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, textShare);

        // Launch the activity
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_via)));
    }

    public static void addToCalendar(Context context, Event event) {
        if (event.isClosed()) {
            // Get the final dateTime proposal
            Proposal proposal = event.getFinalDateTimeProposal();
            DateTime dateTime = proposal.getDateTime();
            String date = dateTime.getDate();
            Calendar calendar = DateTimeFormatter.getDate(date);

            // Create the intent and fill the values
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", calendar.getTimeInMillis());
            intent.putExtra("allDay", true);
            // intent.putExtra("rrule", "FREQ=YEARLY");
            intent.putExtra("endTime", calendar.getTimeInMillis() + 60 * 60 * 1000);
            intent.putExtra("title", event.getName());

            // Launch the activity
            context.startActivity(intent);
        }
    }

    public static void showContactProfile(Context context, Contact contact) {
        // Show the contact profile dialog
        DialogContact dialogContact = DialogContact.build(context);
        dialogContact.show(contact);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaColumns.DATA};
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else
            return uri.getPath();
    }

    public Bitmap getBitmapFromIntent(Intent intent) {
        // Analyze the content of the selected picture
        Uri uri = intent.getData();

        Bitmap bitmap = null;

        if (uri != null) {
            String path = getPath(uri);

            if (path == null) {
                // Probably Google+ Photos application
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Open the Bitmap
                bitmap = BitmapFactory.decodeFile(path);
            }
        }

        return bitmap;
    }

    public void sortEvents(List<Event> events) {
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event left, Event right) {
                // Compare
                int left_number;
                int right_number;

                if (!left.isResponded() && !left.isAdmin() && !left.isClosed()) {
                    left_number = 0;
                } else if (!left.isResponded() && left.isAdmin() && !left.isClosed()) {
                    left_number = 1;
                } else if (left.isResponded() && !left.isClosed()) {
                    left_number = 2;
                } else {
                    // Closed
                    left_number = 3;
                }

                if (!right.isResponded() && !right.isAdmin() && !right.isClosed()) {
                    right_number = 0;
                } else if (!right.isResponded() && right.isAdmin() && !right.isClosed()) {
                    right_number = 1;
                } else if (right.isResponded() && !right.isClosed()) {
                    right_number = 2;
                } else {
                    // Closed
                    right_number = 3;
                }

                return left_number - right_number;
            }
        });
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isUserInitialized() {
        return userInitialized;
    }

    public void setUserInitialized(boolean userInitialized) {
        this.userInitialized = userInitialized;
    }

}
