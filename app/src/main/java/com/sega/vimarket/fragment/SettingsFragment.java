package com.sega.vimarket.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.sega.vimarket.R;
import com.sega.vimarket.activity.IntroActivity;
import com.sega.vimarket.color.ColorPickerDialog;
import com.sega.vimarket.color.Colorful;
import com.sega.vimarket.config.SessionManager;

/**
 * Created by Sega on 09/01/2017.
 */


public  class SettingsFragment extends PreferenceFragment {
    boolean isTablet;
    //preferences
    static Preference first,second,third;
    SessionManager session;
    ColorPickerDialog dialog;
    private SharedPreferences.OnSharedPreferenceChangeListener mListenerOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        isTablet = getResources().getBoolean(R.bool.is_tablet);
        //get the preferences

        first = findPreference("preferenceColor");
        second = findPreference("preferenceSupport");
        third = findPreference("preferenceAbout");
        dialog = new ColorPickerDialog(getActivity());
        session = new SessionManager(getActivity());
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
                if(isTablet)
                {
                    getActivity().recreate();
                    session.setLastpage("setting");
                }

                else
                {
                    getActivity().recreate();
                    getActivity().overridePendingTransition(0, 0);

                }


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
                Intent i = new Intent (getActivity(), IntroActivity.class);
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