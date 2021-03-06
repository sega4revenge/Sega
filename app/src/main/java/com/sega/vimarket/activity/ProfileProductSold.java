package com.sega.vimarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.fragment.ProductSoldFragmentProfile;

import butterknife.ButterKnife;

/**
 * Created by Sega on 17/01/2017.
 */

public class ProfileProductSold extends CActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_profile);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            ProductSoldFragmentProfile fragment = new ProductSoldFragmentProfile();
            Bundle args = new Bundle();
            Bundle extras = getIntent().getExtras();
            System.out.println(getIntent().getStringExtra(ViMarket.seller_ID));
            args.putString(ViMarket.seller_ID,  getIntent().getStringExtra(ViMarket.seller_ID));
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.product_profile_container, fragment).commit();

        }
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

    }
}
