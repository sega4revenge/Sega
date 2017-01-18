package com.sega.vimarket.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.sega.vimarket.R;
import com.sega.vimarket.color.CActivity;

public class IntroActivity extends CActivity {
    public static int[] imgAvatar = {R.drawable.link, R.drawable.envelope,
            R.drawable.facebook, R.drawable.youtube, R.drawable.playstore};
    //Nội dung của từng Item trong ListView
    Toolbar toolbar;

    ListView lvCustomListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] tvNoiDung = {getResources().getString(R.string.about_contact_us),
                getResources().getString(R.string.about_website), getResources().getString(R.string.about_facebook),
                getResources().getString(R.string.about_youtube), getResources().getString(R.string.about_play_store)};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.action_home));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle(R.string.support);
        lvCustomListView = (ListView) findViewById(R.id.lvCustomListView);
        lvCustomListView.setAdapter(new CustomAdapter(IntroActivity.this, tvNoiDung, imgAvatar));
    }


}