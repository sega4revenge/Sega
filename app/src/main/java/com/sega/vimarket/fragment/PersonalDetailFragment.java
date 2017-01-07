package com.sega.vimarket.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.maps.android.SphericalUtil;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.activity.ProductActivity;
import com.sega.vimarket.activity.ProductDetailActivity;
import com.sega.vimarket.adapter.ProductAdapter;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.loader.MyValueFormatter;
import com.sega.vimarket.model.Comments;
import com.sega.vimarket.model.Product;
import com.sega.vimarket.model.Rate;
import com.sega.vimarket.model.User;
import com.sega.vimarket.service.GPSTracker;
import com.sega.vimarket.util.TextUtils;
import com.sega.vimarket.util.VolleySingleton;
import com.sega.vimarket.widget.CircleImageView;
import com.sega.vimarket.widget.ItemPaddingDecoration;
import com.sega.vimarket.widget.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**a
 * Created by HOHOANGLINH on 16-Nov-16.
 */
public class PersonalDetailFragment extends Fragment implements Toolbar.OnMenuItemClickListener, ViewPagerEx.OnPageChangeListener, ProductAdapter.OnproductClickListener, OnLikeListener {

    SessionManager session;
    public static HashMap<String, Double> currency;
    private Context context;
    public Currency curr;
    Double rate;
    private ProductAdapter adapter;
    private GridLayoutManager layoutManager;
    private int pageToDownload;
    private static final int TOTAL_PAGES = 999;
    private int viewType;

    private boolean isLoading;
    private boolean isLoadingLocked;


    @BindView(R.id.loading_more)
    View loadingMore;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.product_grid)
    RecyclerView recyclerView;


    ArrayList<Comments> commentslist = new ArrayList<>();


    private String id, sellerid;
    private Product product;
    private User seller;
    @BindBool(R.bool.is_tablet)
    boolean isTablet;
    // Toolbar
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_text_holder)
    View toolbarTextHolder;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.productdate)
    TextView tvproductdate;
    @BindView(R.id.price)
    TextView tvprice;

    @BindView(R.id.username)
    TextView tvusername;

    @BindView(R.id.progress_circle)
    View progressCircle;
    View errorMessage;
    @BindView(R.id.product_detail_holder)
    NestedScrollView productHolder;

    @BindView(R.id.poster_image)
    CircleImageView posterImage;

    @BindView(R.id.product_name)
    TextView productTitle;

    @BindView(R.id.ratingDetail)
    SimpleRatingBar ratingDetail;
    @BindView(R.id.ratingBar)
    SimpleRatingBar ratingBar;
    @BindView(R.id.txtrate)
    TextView txtrate;
    @BindView(R.id.txtratecount)
    TextView txtratecount;

    @BindView(R.id.thumb_button)
    LikeButton thumbButton;
    int height, width;


    RequestQueue requestQueue;
    String point, favorite;
    @BindView(R.id.chart)
    HorizontalBarChart chart;
    Rate ratee;
    Unbinder unbinder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_personal_detail, container, false);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        session = new SessionManager(getActivity());
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
       errorMessage = v.findViewById(R.id.error_message);
        unbinder = ButterKnife.bind(this, v);

        toolbar.setOnMenuItemClickListener(this);
        if (!isTablet) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.action_home));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                    Runtime.getRuntime().gc();
                }
            });
        }
        // Download product details if new instance, else restore from saved instance
        if (savedInstanceState == null || !(savedInstanceState.containsKey(ViMarket.product_ID)
                && savedInstanceState.containsKey(ViMarket.product_OBJECT) && savedInstanceState.containsKey(ViMarket.seller_DETAIL))) {
            id = getArguments().getString(ViMarket.product_ID);
            sellerid = getArguments().getString(ViMarket.user_ID);

            rate = getArguments().getDouble(ViMarket.currency_RATE);
            if (TextUtils.isNullOrEmpty(id)) {
                progressCircle.setVisibility(View.GONE);
                toolbarTextHolder.setVisibility(View.GONE);
                toolbar.setTitle("");
            }
            else {
                downloadproductDetails(id);
                downloadRate();
            }
        }
        else {
            id = savedInstanceState.getString(ViMarket.product_ID);
            product = savedInstanceState.getParcelable(ViMarket.product_OBJECT);
            seller = savedInstanceState.getParcelable(ViMarket.seller_DETAIL);
            onDownloadSuccessful();
        }
        // Setup FAB
        thumbButton.setOnLikeListener(this);
        ratingBar.setStepSize();
        ratingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, final float rating,
                                        boolean fromUser) {

                requestQueue = Volley.newRequestQueue(getContext());

                StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_RATE, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //                        Toast.makeText(getContext(),response.toString(), Toast.LENGTH_SHORT).show();
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<>();
                        param.put("userrated", session.getLoginId()+"");
                        param.put("userrating", sellerid);
                        param.put("point", String.valueOf(rating));

                        return param;

                    }

                };

                requestQueue.add(request);
                //                Toast.makeText(getActivity(),String.valueOf(rating),Toast.LENGTH_SHORT).show();
            }
        });

        currency = new HashMap<>();
        context = getContext();
        unbinder = ButterKnife.bind(this, v);
        // Initialize variables

        pageToDownload = 1;
        viewType = getArguments().getInt(ViMarket.VIEW_TYPE);

        // Setup RecyclerView
        adapter = new ProductAdapter(context, this);
        //        floatingActionsMenu.setVisibility(View.VISIBLE);
        layoutManager = new GridLayoutManager(context, getNumberOfColumns());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ItemPaddingDecoration(context));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Load more if RecyclerView has reached the end and isn't already loading
                if (layoutManager.findLastVisibleItemPosition() == adapter.productList.size() - 1 && !isLoadingLocked && !isLoading) {
                    if (pageToDownload < TOTAL_PAGES) {
                        loadingMore.setVisibility(View.VISIBLE);
                        downloadproductsList();
                    }
                }
            }
        });


        //        recyclerView.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener() {
        //            @Override
        //            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //                if (oldScrollY < scrollY) {
        //                    System.out.println("A");
        //                    floatingActionsMenu.hideMenuButton(true);
        //                } else {
        //                    System.out.println("B");
        //                    floatingActionsMenu.showMenuButton(true);
        //                }
        //            }
        //        });
        // Setup swipe refresh
        //Toast.makeText(getActivity(),ProductDrawerFragment.userobj.userid+"",Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setColorSchemeResources(R.color.accent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Toggle visibility
                errorMessage.setVisibility(View.GONE);
                progressCircle.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                // Remove cache
                VolleySingleton.getInstance(context).requestQueue.getCache().remove(AppConfig.URL_PRODUCTDETAIL);
                // Download again
                pageToDownload = 1;
                adapter = null;
                downloadproductsList();
            }
        });
        // Get the products list
        if (savedInstanceState == null || !savedInstanceState.containsKey(ViMarket.product_LIST)) {

            downloadCurrency();

        }
        else {
            adapter.productList = savedInstanceState.getParcelableArrayList(ViMarket.product_LIST);
            pageToDownload = savedInstanceState.getInt(ViMarket.PAGE_TO_DOWNLOAD);
            isLoadingLocked = savedInstanceState.getBoolean(ViMarket.IS_LOCKED);
            isLoading = savedInstanceState.getBoolean(ViMarket.IS_LOADING);
            // Download again if stopped, else show list
            if (isLoading) {
                if (pageToDownload == 1) {
                    progressCircle.setVisibility(View.VISIBLE);
                    loadingMore.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                }
                else {
                    progressCircle.setVisibility(View.GONE);
                    loadingMore.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }

                downloadCurrency();

            }
            else {
                onDownloadSuccessful();
            }
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (product != null && id != null) {
            outState.putString(ViMarket.product_ID, id);
            outState.putParcelable(ViMarket.product_OBJECT, product);
            outState.putParcelable(ViMarket.seller_DETAIL, seller);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
        Runtime.getRuntime().gc();
    }

    // Toolbar menu click
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            if (product != null) {
                // Share the product
                String shareText = getString(R.string.action_share_text, product.productname, product.username);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, product.productname);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText + "\n" + product.productimage);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_share_using)));
                // Send event to Google Analytics

            }
        }
        return true;
    }

    private void downloadRate() {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                                                 AppConfig.URL_RATEDETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jObj = new JSONObject(response);
                    JSONArray feedArray = jObj.getJSONArray("rate");
                    ratee = new Rate();
                    for (int i = 0; i < feedArray.length(); i++) {
                        final JSONObject feedObj = (JSONObject) feedArray.get(i);
                        //add product to list product

                        switch (feedObj.getInt("point")) {
                            case 1:
                                ratee.onestar = Integer.parseInt(feedObj.getString("count"));
                                System.out.println(ratee.onestar);
                                break;
                            case 2:
                                ratee.twostar = Integer.parseInt(feedObj.getString("count"));
                                System.out.println(ratee.twostar);
                                break;
                            case 3:
                                ratee.threestar = Integer.parseInt(feedObj.getString("count"));
                                System.out.println(ratee.threestar);
                                break;
                            case 4:
                                ratee.fourstar = Integer.parseInt(feedObj.getString("count"));
                                System.out.println(ratee.fourstar);
                                break;
                            case 5:
                                ratee.fivestar = Integer.parseInt(feedObj.getString("count"));
                                System.out.println(ratee.fivestar);
                                break;
                        }
                    }
                    onDownloadRateSuccessful();
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
                params.put("userid", sellerid + "");
                return params;
            }
        };
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(getActivity()).requestQueue.add(strReq);
    }

    public void onDownloadRateSuccessful() {
        ArrayList<BarDataSet> dataSets;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(ratee.fivestar, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(ratee.fourstar, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(ratee.threestar, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(ratee.twostar, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(ratee.onestar, 4); // May
        valueSet1.add(v1e5);


        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "");
        barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);


        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        BarData data = new BarData(getXAxisValues(), dataSets);
        //        barDataSet1.setValueTextSize(2);
        data.setValueFormatter(new MyValueFormatter());
        chart.setData(data);
        chart.getXAxis().setEnabled(false); // hides horizontal grid lines inside chart
        YAxis leftAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(false); // hides horizontal grid lines with below line
        leftAxis.setEnabled(false); // hides vertical grid lines  inside chart
            /*chart.animateXY(2000, 2000);*/ // for animating reviews display
        chart.invalidate();

        chart.setDescription("");    // Hide the description

        chart.setPinchZoom(true);

        leftAxis.setDrawLabels(true);
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        return xAxis;
    }

    // JSON parsing and display
    private void downloadproductDetails(final String id) {
        String urlToDownload = AppConfig.URL_PRODUCTDETAIL;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                                                 urlToDownload, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ok", "Login Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject feedObj = jObj.getJSONObject("product");
                    ArrayList<String> productimg = new ArrayList<>(Arrays.asList(feedObj.getString("productimage").split(",")));
                    //add product to list products
                    product = new Product(feedObj.getInt("productid"),
                                          feedObj.getString("productname"),
                                          feedObj.getLong("price") / rate,
                                          feedObj.getInt("userid"),
                                          feedObj.getString("username"),
                                          feedObj.getString("categoryname"),
                                          feedObj.getString("productaddress"),
                                          feedObj.getString("areaproduct"),
                                          feedObj.getString("producttype"),
                                          feedObj.getString("productstatus"),
                                          productimg,
                                          feedObj.getString("productdate"),
                                          feedObj.getString("description"),
                                          feedObj.getString("sharecount"),
                                          Double.parseDouble(feedObj.getString("lat")),
                                          Double.parseDouble(feedObj.getString("lot"))
                    );
                    seller = new User();
                    seller.userid = Integer.parseInt(feedObj.getString("userid"));
                    seller.setEmail(feedObj.getString("email"));
                    seller.setPhone(feedObj.getString("phone"));
                    seller.userpic = (feedObj.getString("userpic"));
                    seller.count = feedObj.getString("count");
                    seller.rate = (double) Math.round(Double.parseDouble(feedObj.getString("rate")) * 10) / 10 + "";
                    JSONArray feedArray = feedObj.getJSONArray("comments");
                    commentslist.clear();

                    point = feedObj.getString("point");
                    favorite = feedObj.getString("favorite");
                    for (int i = 0; i < feedArray.length(); i++) {
                        final JSONObject feedComment = (JSONObject) feedArray.get(i);
                        //add product to list products
                        Comments comment = new Comments(feedComment.getString("userid"), feedComment.getString("username"), feedComment.getString("productid"), feedComment.getString("time"),
                                                        feedComment.getString("contentcomment"), feedComment.getString("userpic"), feedComment.getString("rate"));
                        commentslist.add(comment);
                        //add product to sqlite
                    }
                    onDownloadSuccessful();
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    onDownloadFailed();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onDownloadFailed();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("productid", id);
                params.put("userrated", session.getLoginId()+"");
                params.put("userrating", sellerid);
                return params;
            }
        };
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(getActivity()).requestQueue.add(strReq);
    }

    private void onDownloadSuccessful() {
        //        Toast.makeText(getActivity(),point,Toast.LENGTH_LONG).show();
        if (point.equalsIgnoreCase("null")) {
            ratingBar.setRating(0);
        }
        else {
            ratingBar.setRating(Float.parseFloat(point));
        }
        //        sellername.setSelected(true);
        //        sellerphone.setSelected(true);
        //        selleremail.setSelected(true);
        //        productcategory.setSelected(true);
        //        producttype.setSelected(true);
        //        productaddress.setSelected(true);
        // Toggle visibility
        if (favorite.equalsIgnoreCase("true")) {
            //            likee.setVisibility(View.VISIBLE);
            //            unlikee.setVisibility(View.GONE);
            thumbButton.setLiked(true);
        }
        else {
            //            likee.setVisibility(View.GONE);
            //            unlikee.setVisibility(View.VISIBLE);
            thumbButton.setLiked(false);

        }
        productHolder.setVisibility(View.VISIBLE);
        //        floatingActionsMenu.setVisibility(View.VISIBLE);
        // Set title and tagline
        if (TextUtils.isNullOrEmpty(product.productname)) {
            toolbar.setTitle(product.productname);
            toolbarTextHolder.setVisibility(View.GONE);
        }
        else {
            toolbar.setTitle("");
            toolbarTextHolder.setVisibility(View.VISIBLE);
            toolbarTitle.setText(getString(R.string.personalpage) + " " + product.username);
            //            toolbarSubtitle.setText(seller.email);
        }

        Glide.with(getContext()).load(seller.userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(100, 100).into(posterImage);

        productTitle.setText(product.username);
        tvusername.setText(seller.rate);
        tvprice.setText(seller.phone);

        tvproductdate.setText(seller.email);

        isLoading = false;
        errorMessage.setVisibility(View.GONE);
        progressCircle.setVisibility(View.GONE);
        loadingMore.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);
        adapter.notifyDataSetChanged();
        txtrate.setText(seller.rate);
        ratingDetail.setRating(Float.parseFloat(seller.rate));
        txtratecount.setText(seller.count);
        refreshLayout();
    }


    private void onDownloadFailed() {
        errorMessage.setVisibility(View.VISIBLE);
        progressCircle.setVisibility(View.GONE);
        productHolder.setVisibility(View.GONE);
        toolbarTextHolder.setVisibility(View.GONE);
        toolbar.setTitle("");
        isLoading = false;
        if (pageToDownload == 1) {
            progressCircle.setVisibility(View.GONE);
            loadingMore.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setVisibility(View.GONE);
            errorMessage.setVisibility(View.VISIBLE);
        }
        else {
            progressCircle.setVisibility(View.GONE);
            loadingMore.setVisibility(View.GONE);
            errorMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(true);
            isLoadingLocked = true;
        }
    }

    public void refreshLayout() {
        Parcelable state = layoutManager.onSaveInstanceState();
        layoutManager = new GridLayoutManager(getContext(), getNumberOfColumns());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.onRestoreInstanceState(state);
    }

    private void downloadCurrency() {

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                                                          AppConfig.API_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                if (response != null) {
                    try {

                        JSONObject ratesObject = response
                                .getJSONObject("rates");

                        Double usdrate = ratesObject.getDouble("VND");
                        currency.put("USD", usdrate);
                        downloadproductsList();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        // Adding request to volley request queue
        jsonReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(getActivity()).requestQueue.add(jsonReq);
    }

    private void downloadproductsList() {
        if (adapter == null) {
            adapter = new ProductAdapter(context, this);
            recyclerView.setAdapter(adapter);

        }

        StringRequest Listreq = new StringRequest(Request.Method.POST,
                                                  AppConfig.URL_PRODUCTUSER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    curr = Currency.getInstance(Locale.getDefault());
                    if (curr.getCurrencyCode().equals("VND")) {
                        rate = 1.0;
                    }

                    else {
                        rate = currency.get("USD");
                    }
                    JSONObject jObj = new JSONObject(response);
                    JSONArray feedArray = jObj.getJSONArray("feed");
                    for (int i = 0; i < feedArray.length(); i++) {
                        final JSONObject feedObj = (JSONObject) feedArray.get(i);
                        //add product to list products
                        ArrayList<String> productimg = new ArrayList<>(Arrays.asList(feedObj.getString("productimage").split(",")));

                        adapter.productList.add(new Product(feedObj.getInt("productid"),
                                                            feedObj.getString("productname"),
                                                            feedObj.getLong("price") / rate,
                                                            feedObj.getInt("userid"),
                                                            feedObj.getString("username"),
                                                            feedObj.getString("categoryname"),
                                                            feedObj.getString("productaddress"),
                                                            feedObj.getString("areaproduct"),
                                                            feedObj.getString("producttype"),
                                                            feedObj.getString("productstatus"),
                                                            productimg,
                                                            feedObj.getString("productdate"),
                                                            feedObj.getString("description"),
                                                            feedObj.getString("sharecount"),
                                                            Double.parseDouble(feedObj.getString("lat")),
                                                            Double.parseDouble(feedObj.getString("lot"))

                        ));

                        //add product to sqlite
                    }

                    if (viewType == ViMarket.VIEW_TYPE_NEAR) {
                        Collections.sort(adapter.productList,
                                         new Comparator<Product>() {
                                             @Override
                                             public int compare(Product lhs, Product rhs) {
                                                 double lhsDistance = SphericalUtil.computeDistanceBetween(
                                                         lhs.location, GPSTracker.mLastestLocation);
                                                 double rhsDistance = SphericalUtil.computeDistanceBetween(
                                                         rhs.location, GPSTracker.mLastestLocation);
                                                 return (int) (lhsDistance - rhsDistance);
                                             }
                                         }
                        );
                    }
                    // Load detail fragment if in tablet mode
                  /*  if (isTablet && pageToDownload == 1 && adapter.productList.size() > 0) {
                        ((ProductActivity) getActivity()).loadDetailFragmentWith(adapter.productList.get(0).productid + "",String.valueOf(adapter.productList.get(0).userid));
                    }*/
                    pageToDownload++;
                    onDownloadSuccessful();
                } catch (Exception ex) {
                    // JSON parsing error
                    onDownloadFailed();
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onDownloadFailed();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("page", pageToDownload + "");
                params.put("userid", sellerid);
                return params;
            }
        };
        Listreq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(getActivity()).requestQueue.add(Listreq);
        //Toast.makeText(getActivity(),pageToDownload + " " +ProductDrawerFragment.userobj.userid+"",Toast.LENGTH_SHORT).show();

    }

    @OnClick(R.id.try_again)
    public void onTryAgainClicked() {
        errorMessage.setVisibility(View.GONE);
        progressCircle.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //        mDemoSlider.stopAutoCycle();

    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == Activity.RESULT_CANCELED) {

                downloadproductDetails(id);
                downloadRate();
            }
        }
    }

    public int getNumberOfColumns() {
        // Get screen width
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float widthPx = displayMetrics.widthPixels;
        if (isTablet) {
            widthPx = widthPx / 3;
        }
        // Calculate desired width
        SharedPreferences preferences = context.getSharedPreferences(ViMarket.TABLE_USER, Context.MODE_PRIVATE);
        if (preferences.getInt(ViMarket.VIEW_MODE, ViMarket.VIEW_MODE_GRID) == ViMarket.VIEW_MODE_GRID) {
            float desiredPx = getResources().getDimensionPixelSize(R.dimen.product_card_width);
            int columns = Math.round(widthPx / desiredPx);
            return columns > 2 ? columns : 2;
        }
        else {
            float desiredPx = getResources().getDimensionPixelSize(R.dimen.product_list_card_width);
            int columns = Math.round(widthPx / desiredPx);
            return columns > 1 ? columns : 1;
        }
    }

    @Override
    public void onStop() {


        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onproductClicked(int position) {
        if (isTablet) {
            //                Toast.makeText(getActivity(),"3",Toast.LENGTH_LONG).show();

            ((ProductActivity) getActivity()).loadDetailFragmentWith(String.valueOf(adapter.productList.get(position).productid),String.valueOf(adapter.productList.get(position).userid));
        }
        else {
            //                Toast.makeText(getActivity(),"4",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra(ViMarket.product_ID, String.valueOf(adapter.productList.get(position).productid));
            intent.putExtra(ViMarket.user_ID, String.valueOf(adapter.productList.get(position).userid));
            startActivity(intent);
        }
    }

    @Override
    public void liked(LikeButton likeButton) {
        requestQueue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_FAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //                        Toast.makeText(getContext(),response.toString(), Toast.LENGTH_SHORT).show();
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("userrated", String.valueOf(session.getLoginId()));
                param.put("userrating", String.valueOf(seller.userid));
                return param;

            }

        };

        requestQueue.add(request);
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        requestQueue = Volley.newRequestQueue(getContext());
        //
        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_FAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //                        Toast.makeText(getContext(),response.toString(), Toast.LENGTH_SHORT).show();
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("userrated", String.valueOf(session.getLoginId()));
                param.put("userrating", String.valueOf(seller.userid));
                return param;

            }

        };

        requestQueue.add(request);
    }
}
