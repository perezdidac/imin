package com.imin;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public class AboutActivity extends ActionBarActivity {

    // Object references
    TextView textAppName;
    TextView textAppVersion;
    TextView textCompanyName;
    TextView textWebsite;
    ImageView imageFacebook;
    ImageView imageTwitter;
    ImageView imageGoogle;
    ImageView imageMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize objects
        initializeObjects();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initializeObjects() {
        // Get object references
        textAppName = (TextView) findViewById(R.id.textAppName);
        textAppVersion = (TextView) findViewById(R.id.textAppVersion);
        textCompanyName = (TextView) findViewById(R.id.textCompanyName);
        textWebsite = (TextView) findViewById(R.id.textWebsite);
        imageFacebook = (ImageView) findViewById(R.id.imageFacebook);
        imageTwitter = (ImageView) findViewById(R.id.imageTwitter);
        imageGoogle = (ImageView) findViewById(R.id.imageGoogle);
        imageMail = (ImageView) findViewById(R.id.imageMail);

        // Set app version
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            textAppVersion.setText(versionName);
        } catch (NameNotFoundException e) {
            // Hide version if error
            textAppVersion.setVisibility(View.GONE);
        }

        // Change fonts
        textAppName.setTypeface(Imin.fontBold);
        textAppVersion.setTypeface(Imin.fontLight);
        textCompanyName.setTypeface(Imin.fontRegular);
        textWebsite.setTypeface(Imin.fontRegular);

        // Set listeners
        textWebsite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch Website
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_full_website)));
                startActivity(browserIntent);
            }
        });

        imageFacebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                launchUrl(getString(R.string.url_facebook));
            }
        });

        imageTwitter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                launchUrl(getString(R.string.url_twitter));
            }
        });

        imageGoogle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                launchUrl(getString(R.string.url_google));
            }
        });

        imageMail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                launchUrl(getString(R.string.url_mail));
            }
        });
    }

    private void launchUrl(String url) {
        // Launch Website
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(urlIntent);
    }
}
