package com.sega.vimarket.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.fragment.ProductDetailFragment;
import com.sega.vimarket.fragment.ProductDetailFragmentUser;

import butterknife.BindBool;
import butterknife.ButterKnife;

public class ProductActivity extends AppCompatActivity {
    @BindBool(R.bool.is_tablet)
    boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "oke");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        if (isTablet && savedInstanceState == null) {
            loadDetailFragmentWith("null","null");
        }


    }

    public void loadDetailFragmentWith(String productId,String productUserId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ViMarket.product_ID, productId);
        args.putString(ViMarket.user_ID, productUserId);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment, fragment).commit();
    }
    public void loadDetailFragmentUser(String productId,String productUserId) {
        ProductDetailFragmentUser fragment = new ProductDetailFragmentUser();
        Bundle args = new Bundle();
        args.putString(ViMarket.product_ID, productId);
        args.putString(ViMarket.user_ID, productUserId);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment, fragment).commit();
    }
    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
