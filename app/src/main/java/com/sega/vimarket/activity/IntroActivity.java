package com.sega.vimarket.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.sega.vimarket.R;

public class IntroActivity extends AppCompatActivity {
    public static int[] imgAvatar = {R.drawable.link, R.drawable.envelope,
            R.drawable.facebook, R.drawable.youtube, R.drawable.playstore};
    //Nội dung của từng Item trong ListView


    ListView lvCustomListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] tvNoiDung = {getResources().getString(R.string.about_contact_us),
                getResources().getString(R.string.about_website), getResources().getString(R.string.about_facebook),
                getResources().getString(R.string.about_youtube), getResources().getString(R.string.about_play_store)};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        lvCustomListView = (ListView) findViewById(R.id.lvCustomListView);
        lvCustomListView.setAdapter(new CustomAdapter(IntroActivity.this, tvNoiDung, imgAvatar));
    }


}