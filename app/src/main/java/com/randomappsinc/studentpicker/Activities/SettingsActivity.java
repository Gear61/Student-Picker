package com.randomappsinc.studentpicker.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.randomappsinc.studentpicker.Adapters.SettingsAdapter;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by alexanderchiou on 10/13/15.
 */
public class SettingsActivity extends StandardActivity {
    public static final String SUPPORT_EMAIL = "chessnone@gmail.com";
    public static final String REPO_URL = "https://github.com/Gear61/Random-Name-Picker";

    @Bind(R.id.parent) CoordinatorLayout parent;
    @Bind(R.id.settings_options) ListView settingsOptions;
    @BindString(R.string.play_store_error) String playStoreError;
    @BindString(R.string.feedback_subject) String feedbackSubject;
    @BindString(R.string.send_email) String sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsOptions.setAdapter(new SettingsAdapter(this));
    }

    @OnItemClick(R.id.settings_options)
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id)
    {
        Intent intent = null;
        switch (position) {
            case 0:
                String uriText = "mailto:" + SUPPORT_EMAIL + "?subject=" + Uri.encode(feedbackSubject);
                Uri mailUri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, mailUri);
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(Intent.createChooser(sendIntent, sendEmail));
                return;
            case 1:
                Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                    Snackbar.make(parent, playStoreError, Snackbar.LENGTH_LONG).show();
                    return;
                }
                break;
            case 2:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL));
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
