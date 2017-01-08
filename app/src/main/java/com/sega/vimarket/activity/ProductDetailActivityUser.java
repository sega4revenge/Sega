package com.sega.vimarket.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.fragment.ProductDetailFragmentUser;

public class ProductDetailActivityUser extends CActivity {
    String cur;
    Double rate;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        if (savedInstanceState == null) {
            String productId;
            Intent intent = getIntent();
            Uri data = intent.getData();
            if (data == null) {
                // Not loading from deep link
                productId = getIntent().getStringExtra(ViMarket.product_ID);
                userId = getIntent().getStringExtra(ViMarket.user_ID);
                cur = getIntent().getStringExtra(ViMarket.currency_ID);
                rate = getIntent().getDoubleExtra(ViMarket.currency_RATE,1.0);
                loadproductDetailsOf(productId);
            } else {
                // Loading from deep link
                String[] parts = data.toString().split("/");
                productId = parts[parts.length - 1];
                switch (productId) {
                    // Load product Lists
                    case "product":
                        loadproductsOfType(0);
                        break;
                    case "top-rated":
                        loadproductsOfType(1);
                        break;
                    case "upcoming":
                        loadproductsOfType(2);
                        break;
                    case "now-playing":
                        loadproductsOfType(3);
                        break;
                    // Load details of a particular product
                    default:
                        int dashPosition = productId.indexOf("-");
                        if (dashPosition != -1) {
                            productId = productId.substring(0, dashPosition);
                        }
                        loadproductDetailsOf(productId);
                        break;
                }
            }
        }
    }

    private void loadproductDetailsOf(String productId) {
        ProductDetailFragmentUser fragment = new ProductDetailFragmentUser();
        Bundle args = new Bundle();

        args.putString(ViMarket.product_ID, productId);
        args.putString(ViMarket.user_ID,userId);
        args.putString(ViMarket.currency_ID, cur);
        args.putDouble(ViMarket.currency_RATE, rate);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.product_detail_container, fragment).commit();
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
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
