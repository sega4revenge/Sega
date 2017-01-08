package com.sega.vimarket.activity;

/**
 * Created by Sega on 08/01/2017.
 */


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sega.vimarket.R;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.color.ColorPickerDialog;
import com.sega.vimarket.color.Colorful;



public class PreferenceActivity extends CActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //apply activity's theme if dark theme is enabled




        setContentView(R.layout.preference_activity);
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



    public static class SettingsFragment extends PreferenceFragment {

        //preferences
        static Preference first,second,third;

        ColorPickerDialog dialog;
        private SharedPreferences.OnSharedPreferenceChangeListener mListenerOptions;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting);

            //get the preferences
            first = findPreference("preferenceColor");
            second = findPreference("preferenceSupport");
            third = findPreference("preferenceAbout");
            dialog = new ColorPickerDialog(getActivity());
            dialog.setOnColorSelectedListener(new ColorPickerDialog.OnColorSelectedListener() {
                @Override
                public void onColorSelected(Colorful.ThemeColor color) {
                    //TODO: Do something with the color
                    Colorful.config(getActivity())
                            .primaryColor(color)
                            .accentColor(color)
                            .translucent(false)
                            .dark(false)
                            .apply();

                    dialog.dismiss();
                    getActivity().finish();
                    getActivity().overridePendingTransition(0, 0);
                    getActivity().startActivity(getActivity().getIntent());
                    getActivity().overridePendingTransition(0, 0);

                }
            });



            first.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {



                    //associate dialog with selected preference
                    dialog.show();
                    return false;
                }
            });

            second.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {



                    //associate dialog with selected preference
                    Intent i = new Intent (getActivity(),IntroActivity.class);
                    getActivity().startActivity(i);
                    return false;
                }
            });

            third.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {



                    //associate dialog with selected preference

                    return false;
                }
            });

            //initialize shared preference change listener
            //some preferences when enabled requires an app reboot

        }

        //register preferences changes
        @Override
        public void onResume() {
            super.onResume();

            //register preferences changes
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mListenerOptions);
        }

        //unregister preferences changes
        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListenerOptions);
            super.onPause();
        }

        //method to restart the app and apply the changes

    }
}
