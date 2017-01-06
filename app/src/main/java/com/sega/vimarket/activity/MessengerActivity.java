package com.sega.vimarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.fragment.MessengerFragment;

import butterknife.BindBool;

public class MessengerActivity extends AppCompatActivity {
    @BindBool(R.bool.is_tablet)
    boolean isTablet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        if (savedInstanceState == null) {
            MessengerFragment fragment = new MessengerFragment();
            Bundle args = new Bundle();
            args.putString(ViMarket.user_ID, getIntent().getStringExtra(ViMarket.user_ID));
            args.putString(ViMarket.user_name, getIntent().getStringExtra(ViMarket.user_name));
            args.putString(ViMarket.seller_ID, getIntent().getStringExtra(ViMarket.seller_ID));
            args.putString(ViMarket.seller_name, getIntent().getStringExtra(ViMarket.seller_name));
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.messenger_container, fragment).commit();
            if (isTablet) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

    }
}
