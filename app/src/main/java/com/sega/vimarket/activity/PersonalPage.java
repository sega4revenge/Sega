package com.sega.vimarket.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.fragment.PersonalDetailFragment;

import butterknife.BindBool;

/**a
 * Created by HOHOANGLINH on 16-Nov-16.
 */
public class PersonalPage extends AppCompatActivity {
    String cur;
    Double rate;
    String userId;
    @BindBool(R.bool.is_tablet)
    boolean isTablet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        if (savedInstanceState == null) {
            String productId ;
            Intent intent = getIntent();
            Uri data = intent.getData();
            if (data == null) {
                // Not loading from deep link
                productId = getIntent().getStringExtra(ViMarket.product_ID);
                userId = getIntent().getStringExtra(ViMarket.user_ID);
                cur = getIntent().getStringExtra(ViMarket.currency_ID);
                rate = getIntent().getDoubleExtra(ViMarket.currency_RATE,1.0);
                loadPersonalDetailsOf(productId);
            } else {
                // Loading from deep link
                String[] parts = data.toString().split("/");
                productId = parts[parts.length - 1];
                switch (productId) {
                    // Load product Lists
                    case "product":
                        loadPersonalOfType(0);
                        break;
                    case "top-rated":
                        loadPersonalOfType(1);
                        break;
                    case "upcoming":
                        loadPersonalOfType(2);
                        break;
                    case "now-playing":
                        loadPersonalOfType(3);
                        break;
                    // Load details of a particular product
                    default:
                        int dashPosition = productId.indexOf("-");
                        if (dashPosition != -1) {
                            productId = productId.substring(0, dashPosition);
                        }
                        loadPersonalDetailsOf(productId);
                        break;
                }
            }
        }
    }

    private void loadPersonalDetailsOf(String productId) {
        PersonalDetailFragment fragment = new PersonalDetailFragment();
        Bundle args = new Bundle();

        args.putString(ViMarket.product_ID, productId);
        args.putString(ViMarket.user_ID,userId);
        args.putString(ViMarket.currency_ID, cur);
        args.putDouble(ViMarket.currency_RATE, rate);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.product_detail_container, fragment).commit();
    }

    @SuppressLint("CommitPrefEdits")
    private void loadPersonalOfType(int viewType) {
        SharedPreferences.Editor editor = getSharedPreferences(ViMarket.TABLE_USER, MODE_PRIVATE).edit();
        editor.putInt(ViMarket.LAST_SELECTED, viewType);

        editor.commit();
        startActivity(new Intent(this, com.sega.vimarket.activity.ProductActivity.class));
        finish();
    }


}
