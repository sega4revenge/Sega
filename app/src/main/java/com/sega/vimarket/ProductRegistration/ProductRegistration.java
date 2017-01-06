package com.sega.vimarket.ProductRegistration;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sega.vimarket.R;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.fragment.ProductDrawerFragment;
import com.sega.vimarket.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import co.dift.ui.SwipeToAction;

/**a
 * Created by HOHOANGLINH on 26-Dec-16.
 */

public class ProductRegistration extends AppCompatActivity {

    RecyclerView recyclerView;
    ItemsAdapter adapter;
    SwipeToAction swipeToAction;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // facebook image library

        setContentView(R.layout.product_registration);
//        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.action_home));
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//        toolbar.setTitle(R.string.btn_sell);



        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener<Item>() {
            @Override
            public boolean swipeLeft(final Item itemData) {
                final int pos = removeBook(itemData);
                StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_DELETENOTIFICATION, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Delete", "Delete Response: " + response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<>();

                        param.put("userid", String.valueOf(ProductDrawerFragment.userobj.userid));
                        param.put("text", itemData.getTitle());
                        return param;

                    }

                };
                VolleySingleton.getInstance(getApplicationContext()).requestQueue.add(request);
                displaySnackbar(itemData.getTitle() + " removed", "Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addBook(pos, itemData);
                        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_ADDNOTIFICATION, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Add", "Add Response: " + response);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> param = new HashMap<>();

                                param.put("userid", String.valueOf(ProductDrawerFragment.userobj.userid));
                                param.put("text", itemData.getTitle());
                                return param;

                            }

                        };
                        VolleySingleton.getInstance(getApplicationContext()).requestQueue.add(request);
                    }
                });

                return true;
            }

            @Override
            public boolean swipeRight(Item itemData) {
                displaySnackbar(itemData.getTitle() + " loved", null, null);
                return true;
            }

            @Override
            public void onClick(Item itemData) {
                displaySnackbar(itemData.getTitle() + " clicked", null, null);

            }

            @Override
            public void onLongClick(Item itemData) {
                displaySnackbar(itemData.getTitle() + " long clicked", null, null);

            }
        });

        downloadNotification();

//        populate();

        // use swipeLeft or swipeRight and the elem position to swipe by code
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                swipeToAction.swipeRight(2);
//            }
//        }, 3000);
    }
    private void downloadNotification() {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                                                 AppConfig.URL_GETNOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jObj = new JSONObject(response);
                    JSONArray feedArray = jObj.getJSONArray("rate");
                    adapter = new ItemsAdapter(items);
                    for (int i = 0; i < feedArray.length(); i++) {
                        final JSONObject feedObj = (JSONObject) feedArray.get(i);
                        //add product to list product
                        items.add(new Item(feedObj.getString("text")));

                    }
                    recyclerView.setAdapter(adapter);
                } catch (Exception ex) {
                    // JSON parsing error
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("userid", String.valueOf(ProductDrawerFragment.userobj.userid));
                return params;
            }
        };
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(getApplicationContext()).requestQueue.add(strReq);
    }

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.secondary));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }

    private int removeBook(Item book) {
        int pos = items.indexOf(book);
        items.remove(book);
        adapter.notifyItemRemoved(pos);

        return pos;
    }

    private void addBook(int pos, Item book) {
        items.add(pos, book);
        adapter.notifyItemInserted(pos);
    }
}

