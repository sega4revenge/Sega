package com.sega.vimarket.activity;

import android.app.Fragment;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.fragment.ProductDetailFragment;
import com.sega.vimarket.fragment.ProductDetailFragmentUser;
import com.sega.vimarket.fragment.SettingsFragment;

import butterknife.BindBool;
import butterknife.ButterKnife;

public class ProductActivity extends CActivity {
    @BindBool(R.bool.is_tablet)
    boolean isTablet;
    Fragment fragment ;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        session = new SessionManager(this);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "oke");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        if (isTablet && savedInstanceState == null) {
            if(session.getLastpage().equals("setting")){
                loadSettingFragment();
                System.out.println(session.getLastpage());
            }

            else {

                loadDetailFragmentWith("null", "null");
            }
        }


    }

    public void loadDetailFragmentWith(String productId,String productUserId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ViMarket.product_ID, productId);
        args.putString(ViMarket.user_ID, productUserId);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().remove(fragment).commit();
        getFragmentManager().beginTransaction().replace(R.id.detail_fragment, fragment).commit();
    }
    public void loadSettingFragment() {
       fragment = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.detail_fragment, fragment).commit();


    }
    public void loadDetailFragmentUser(String productId,String productUserId) {
       fragment = new ProductDetailFragmentUser();
        Bundle args = new Bundle();
        args.putString(ViMarket.product_ID, productId);
        args.putString(ViMarket.user_ID, productUserId);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.detail_fragment, fragment).commit();
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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    /**a
     * Created by Sega on 15/08/2016.
     */

}
