package com.sega.vimarket.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.fragment.ProductDetailFragment;
import com.sega.vimarket.service.GPSTracker;

public class ProductDetailActivity extends CActivity {
    Double rate;
    String userId;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        session = new SessionManager(this);
        if(GPSTracker.mLastestLocation==null)
        {
            GPSTracker gps = new GPSTracker(this);
            gps.getLocation();
        }

        Intent i = new Intent(getApplicationContext(),WelcomeActivity.class);
        if (savedInstanceState == null) {
            String productId;
            Intent intent = getIntent();
            Uri data = intent.getData();
            if (data == null) {
                // Not loading from deep link
                productId = getIntent().getStringExtra(ViMarket.product_ID);
                userId = getIntent().getStringExtra(ViMarket.user_ID);
                rate = getIntent().getDoubleExtra(ViMarket.currency_RATE,1.0);
                loadproductDetailsOf(productId,userId);
            } else {
                // Loading from deep link
                String[] parts = data.toString().split("/");
                productId = parts[parts.length - 1];
                System.out.println(productId);
                switch (productId) {
                    // Load product Lists
                    case "link.php":
                        if(session.isLoggedIn())
                        loadproductsOfType(0);
                        else
                        {
                            startActivity(i);
                            finish();
                        }
                        break;
                    // Load details of a particular product
                    default:
                        if(session.isLoggedIn()) {
                            String[] abc = productId.split("&");
                            productId = abc[0].substring(abc[0].lastIndexOf("=") + 1);
                            userId = abc[1].substring(abc[1].lastIndexOf("=") + 1);
                            System.out.println(userId + " " + productId);
                            loadproductDetailsOf(productId,userId);
                        }
                        else{

                        startActivity(i);
                        finish();}
                        break;
                }
            }
        }
    }

    private void loadproductDetailsOf(String productId,String userId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();

        args.putString(ViMarket.product_ID, productId);
        args.putString(ViMarket.user_ID,userId);
        args.putDouble(ViMarket.currency_RATE, session.getCurrency());
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.product_detail_container, fragment).commit();
    }

    @SuppressLint("CommitPrefEdits")
    private void loadproductsOfType(int viewType) {
        SharedPreferences.Editor editor = getSharedPreferences(ViMarket.TABLE_USER, MODE_PRIVATE).edit();
        editor.putInt(ViMarket.LAST_SELECTED, viewType);

        editor.commit();
        startActivity(new Intent(this, com.sega.vimarket.activity.ProductActivity.class));
        finish();
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
