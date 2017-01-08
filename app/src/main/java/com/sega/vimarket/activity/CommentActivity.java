package com.sega.vimarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.fragment.CommentFragment;

import butterknife.BindBool;
import butterknife.ButterKnife;

/**a
 * Created by Sega on 8/4/2016.
 */
public class CommentActivity extends CActivity {
    @BindBool(R.bool.is_tablet)
    boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            CommentFragment fragment = new CommentFragment();
            Bundle args = new Bundle();
            args.putInt(ViMarket.COMMENT_TYPE, getIntent().getIntExtra(ViMarket.COMMENT_TYPE, 0));
            args.putInt(ViMarket.product_ID, getIntent().getIntExtra(ViMarket.product_ID, 0));
            args.putString(ViMarket.product_NAME, getIntent().getStringExtra(ViMarket.product_NAME));
            args.putParcelableArrayList(ViMarket.COMMENT_LIST, getIntent().getParcelableArrayListExtra(ViMarket.COMMENT_LIST));
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.comment_container, fragment).commit();
            if (isTablet) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        
    }
}
