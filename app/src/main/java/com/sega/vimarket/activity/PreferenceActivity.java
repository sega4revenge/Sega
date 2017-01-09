package com.sega.vimarket.activity;

/**
 * Created by Sega on 08/01/2017.
 */


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;

import com.sega.vimarket.R;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.fragment.SettingsFragment;


public class PreferenceActivity extends CActivity {
    SessionManager session;
    int color,color2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //apply activity's theme if dark theme is enabled
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        color2 = typedValue.data;

        setContentView(R.layout.preference_activity);
        session = new SessionManager(this);
        session.setColor(color,color2 );
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //provide back navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {

            SettingsFragment settingsFragment = new SettingsFragment();
            getFragmentManager().beginTransaction().replace(R.id.content_frame, settingsFragment).commit();
        }

    }

    //close app
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
