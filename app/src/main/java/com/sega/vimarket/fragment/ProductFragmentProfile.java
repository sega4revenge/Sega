package com.sega.vimarket.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.sega.vimarket.R;
import com.sega.vimarket.Tricks.ViewPagerEx;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.activity.ProductActivity;
import com.sega.vimarket.activity.ProductDetailActivity;
import com.sega.vimarket.adapter.ProductAdapter;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.model.Product;
import com.sega.vimarket.widget.ItemPaddingDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


/**
 * Created by Sega on 16/01/2017.
 */


public class ProductFragmentProfile extends Fragment implements  ViewPagerEx.OnPageChangeListener, ProductAdapter.OnproductClickListener {

    SessionManager session;

    private Context context;

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


    @BindBool(R.bool.is_tablet)
    boolean isTablet;
    // Toolbar
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    AsyncTask<Void, Void, String> asyncTask;


      View progressCircle;
      View errorMessage;



    int height, width;




    FloatingActionButton floatingActionsMenu;

    Unbinder unbinder;


    //     Comment



    private String sellerid;
    private boolean error;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_product_profile, container, false);

        setRetainInstance(true);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        session = new SessionManager(getActivity());
        height = displaymetrics.heightPixels;
        context = getContext();
        width = displaymetrics.widthPixels;
        unbinder = ButterKnife.bind(this, v);
        floatingActionsMenu = (FloatingActionButton) v.findViewById(R.id.fab_menu_main);
     /*  errorMessage = v.findViewById(R.id.error_message);*/
    floatingActionsMenu.setVisibility(View.GONE);

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.action_home));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        progressCircle = v.findViewById(R.id.progress_circle);
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

        errorMessage = v.findViewById(R.id.error_message);

        // Download product details if new instance, else restore from saved instance


        unbinder = ButterKnife.bind(this, v);
        // Initialize variables

        pageToDownload = 1;
        sellerid = getArguments().getString(ViMarket.seller_ID);

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
                        asyncTask = new CustomerAsyncTask().execute();
                        System.out.println(pageToDownload);

                    }
                }

            }
        });



        swipeRefreshLayout.setColorSchemeResources(R.color.accent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Toggle visibility
                errorMessage.setVisibility(View.GONE);
                progressCircle.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                // Remove cache
                // Download again
                pageToDownload = 1;
                adapter = null;
                asyncTask = new CustomerAsyncTask().execute();

            }
        });
        // Get the products list

        if (savedInstanceState == null || !savedInstanceState.containsKey(ViMarket.product_LIST)) {

            asyncTask = new CustomerAsyncTask().execute();


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

                asyncTask = new CustomerAsyncTask().execute();

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
        if (layoutManager != null && adapter != null) {
            outState.putBoolean(ViMarket.IS_LOADING, isLoading);
            outState.putBoolean(ViMarket.IS_LOCKED, isLoadingLocked);
            outState.putInt(ViMarket.PAGE_TO_DOWNLOAD, pageToDownload);
            outState.putParcelableArrayList(ViMarket.product_LIST, adapter.productList);

        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Runtime.getRuntime().gc();
    }

    // Toolbar menu click




    // JSON parsing and display


    private void onDownloadSuccessful() {
        //        Toast.makeText(getActivity(),point,Toast.LENGTH_LONG).show();


        isLoading = false;
        errorMessage.setVisibility(View.GONE);
        progressCircle.setVisibility(View.GONE);
        loadingMore.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);
        adapter.notifyDataSetChanged();


        refreshLayout();
    }

    public void refreshLayout() {
        Parcelable state = layoutManager.onSaveInstanceState();
        layoutManager = new GridLayoutManager(getContext(), getNumberOfColumns());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.onRestoreInstanceState(state);
    }
    private void onDownloadFailed() {
        errorMessage.setVisibility(View.VISIBLE);
        progressCircle.setVisibility(View.GONE);


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




  /*  private void downloadproductsList() {
        if (adapter == null) {
            adapter = new ProductAdapter(context, this);
            recyclerView.setAdapter(adapter);

        }

        StringRequest Listreq = new StringRequest(Request.Method.POST,
                                                  AppConfig.URL_PRODUCTUSER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                    rate = session.getCurrency();

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
                  *//*  if (isTablet && pageToDownload == 1 && adapter.productList.size() > 0) {
                        ((ProductActivity) getActivity()).loadDetailFragmentWith(adapter.productList.get(0).productid + "",String.valueOf(adapter.productList.get(0).userid));
                    }*//*
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

    }*/

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

            ((ProductActivity) getActivity()).loadDetailFragmentWith(String.valueOf(adapter.productList.get(position).productid), String.valueOf(adapter.productList.get(position).userid));
        }
        else {
            //                Toast.makeText(getActivity(),"4",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra(ViMarket.product_ID, String.valueOf(adapter.productList.get(position).productid));
            intent.putExtra(ViMarket.user_ID, String.valueOf(adapter.productList.get(position).userid));
            startActivity(intent);
        }
    }

    public void getData(String string){
        try {
            JSONObject jObj = new JSONObject(string);



            JSONArray feedArray = jObj.getJSONArray("feed");
            for (int i = 0; i < feedArray.length(); i++) {
                final JSONObject feedObj = (JSONObject) feedArray.get(i);
                //add product to list products
                ArrayList<String> productimg = new ArrayList<>(Arrays.asList(feedObj.getString("productimage").split(",")));

                adapter.productList.add(new Product(feedObj.getInt("productid"),
                                                    feedObj.getString("productname"),
                                                    feedObj.getLong("price"),
                                                    feedObj.getInt("userid"),
                                                    feedObj.getString("username"),
                                                    feedObj.getString("categoryname"),
                                                    feedObj.getString("productaddress"),
                                                    feedObj.getString("areaproduct"),
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



            // Load detail fragment if in tablet mode

            pageToDownload++;
            error = false;
        } catch (Exception ex) {
            // JSON parsing error
            error = true;
            ex.printStackTrace();
        }
    }
    public void clearadapter() {
        if (adapter == null) {
            adapter = new ProductAdapter(context, this);
            recyclerView.setAdapter(adapter);

        }
    }
    public class CustomerAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLoading = true;
            clearadapter();

        }

        @Override
        protected String doInBackground(Void... params) {
            int cacheSize = 10 * 1024 * 1024; // 10 MiB

            Cache cache = new Cache(new File(getActivity().getApplication().getCacheDir(), "okdata"), cacheSize);
            CacheControl cacheControl = new CacheControl.Builder().maxAge(2, TimeUnit.HOURS).build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
            RequestBody body = new FormBody.Builder()
                    .add("page", pageToDownload + "")
                    .add("userid", sellerid)
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(AppConfig.URL_PRODUCTUSER)
                    .cacheControl(cacheControl)
                    .post(body)
                    .build();
            okhttp3.Response forceCacheResponse = null;
            try {
                forceCacheResponse = client.newCall(request).execute();

                String responsestring = forceCacheResponse.body().string();

                if(forceCacheResponse.isSuccessful()){
                    getData(responsestring);

                }

                else{
                    onDownloadFailed();

                }


            } catch (Exception e) {

                e.printStackTrace();
                error=true;

            }

            return "ok";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (error) {
                onDownloadFailed();
            }
            else {

                onDownloadSuccessful();
            }
        }
    }

}
