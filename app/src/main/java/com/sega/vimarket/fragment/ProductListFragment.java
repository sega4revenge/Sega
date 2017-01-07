package com.sega.vimarket.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.activity.ProductActivity;
import com.sega.vimarket.activity.ProductDetailActivity;
import com.sega.vimarket.activity.ProductDetailActivityUser;
import com.sega.vimarket.adapter.ProductAdapter;
import com.sega.vimarket.addproduct.AddProduct;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.model.Product;
import com.sega.vimarket.service.GPSTracker;
import com.sega.vimarket.util.VolleySingleton;
import com.sega.vimarket.widget.ItemPaddingDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;

import butterknife.BindBool;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ProductListFragment extends Fragment implements ProductAdapter.OnproductClickListener {
    public boolean start = false;
    SessionManager session;
    private Context context;
    public Currency cur;
    public String category, area, fitter, search;
    Double rate;
    private Unbinder unbinder;
    private ProductAdapter adapter;
    private GridLayoutManager layoutManager;
    private int pageToDownload;
    private static final int TOTAL_PAGES = 999;
    private int viewType;
    private boolean isLoading;
    private boolean isLoadingLocked;
    @BindBool(R.bool.is_tablet)
    boolean isTablet;

    View errorMessage;
    View progressCircle;
    View loadingMore;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionsMenu;
    AsyncTask<Void, Void, String> asyncTask;
    boolean error = false;

    //    @BindView(R.id.fab_menu)
    //    FloatingActionMenu floatingActionsMenu;
    // Fragment lifecycle

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_product_list, container, false);
        unbinder = ButterKnife.bind(this, v);
        session = new SessionManager(getActivity());
        errorMessage = v.findViewById(R.id.error_message);
        progressCircle = v.findViewById(R.id.progress_circle);
        loadingMore = v.findViewById(R.id.loading_more);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) v.findViewById(R.id.product_grid);
        floatingActionsMenu = (FloatingActionButton) v.findViewById(R.id.fab_menu_main);

        context = getContext();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        // Initialize variables

        pageToDownload = 1;
        viewType = getArguments().getInt(ViMarket.VIEW_TYPE);

        search = getArguments().getString(ViMarket.search_text);
        category = getArguments().getString(ViMarket.cate_text);
        area = getArguments().getString(ViMarket.area_text);
        fitter = getArguments().getString(ViMarket.fitter_text);

        // Setup RecyclerView
        adapter = new ProductAdapter(context, this);
        //        floatingActionsMenu.setVisibility(View.VISIBLE);
        layoutManager = new GridLayoutManager(context, getNumberOfColumns());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ItemPaddingDecoration(context));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx < dy) {
                    floatingActionsMenu.hideButtonInMenu(true);
                }
                else {
                    floatingActionsMenu.showButtonInMenu(true);
                }
                // Load more if RecyclerView has reached the end and isn't already loading
                if (layoutManager.findLastVisibleItemPosition() == adapter.productList.size() - 1 && !isLoadingLocked && !isLoading) {

                    if (pageToDownload < TOTAL_PAGES) {
                        loadingMore.setVisibility(View.VISIBLE);
                        System.out.println("dang tai");
                        asyncTask = new CustomerAsyncTask().execute();
                    }
                }
            }
        });
        floatingActionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddProduct.class);
                startActivity(i);
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
        // Determine screen name


    }

    public void clearadapter() {
        if (adapter == null) {
            adapter = new ProductAdapter(context, this);
            recyclerView.setAdapter(adapter);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (layoutManager != null && adapter != null) {
            outState.putBoolean(ViMarket.IS_LOADING, isLoading);
            outState.putBoolean(ViMarket.IS_LOCKED, isLoadingLocked);
            outState.putInt(ViMarket.PAGE_TO_DOWNLOAD, pageToDownload);
            outState.putParcelableArrayList(ViMarket.product_LIST, adapter.productList);
            System.out.println("1231242151");
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        VolleySingleton.getInstance(context).requestQueue.cancelAll(this.getClass().getName());
        unbinder.unbind();
    }

    // JSON parsing and display


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LocationResult.hasResult(intent)) {
                LocationResult locationResult = LocationResult.extractResult(intent);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    GPSTracker.mLastestLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    private void downloadproductsList() {


        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("page", pageToDownload + "")
                .add("userid", session.getLoginId() + "")
                .add("search", search)
                .add("category", category)
                .add("area", area)
                .add("fitter", fitter)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(AppConfig.URL_LOCATIONPRODUCT)
                .post(body)
                .build();

        try {
            String responsestring = client.newCall(request).execute().body().string();
            try {

                cur = Currency.getInstance(Locale.getDefault());
                if (cur.getCurrencyCode().equals("VND")) {
                    rate = 1.0;
                }

                else {
                    rate = session.getCurrency();
                }
                JSONObject jObj = new JSONObject(responsestring);
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

                pageToDownload++;
                error = false;
            } catch (Exception ex) {
                // JSON parsing error
                error = true;
                ex.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Toast.makeText(getActivity(),pageToDownload + " " +ProductDrawerFragment.userobj.userid+"",Toast.LENGTH_SHORT).show();
    }

    private void downloadCurrency() {
        if (!ProductDrawerFragment.money) {
            OkHttpClient client = new OkHttpClient();


            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(AppConfig.API_URL)
                    .get()
                    .build();

            try {
                String responsestring = client.newCall(request).execute().body().string();
                try {
                    JSONObject response = new JSONObject(responsestring);
                    JSONObject ratesObject = response
                            .getJSONObject("rates");
                    ProductDrawerFragment.money = true;
                    Double usdrate = ratesObject.getDouble("VND");

                    session.setCurrency(usdrate);
                    downloadproductsList();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        else if (ProductDrawerFragment.money) {
            downloadproductsList();
        }


    }

    private void onDownloadSuccessful() {
        if (isTablet && adapter.productList.size() > 0) {
            ((ProductActivity) getActivity()).loadDetailFragmentWith(adapter.productList.get(0).productid + "", String.valueOf(adapter.productList.get(0).userid));
        }
        isLoading = false;
        errorMessage.setVisibility(View.GONE);
        progressCircle.setVisibility(View.GONE);
        loadingMore.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);
        adapter.notifyDataSetChanged();


    }

    private void onDownloadFailed() {
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

    // Helper methods
    public void refreshLayout() {
        Parcelable state = layoutManager.onSaveInstanceState();
        layoutManager = new GridLayoutManager(getContext(), getNumberOfColumns());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.onRestoreInstanceState(state);
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
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    // Click events
    @OnClick(R.id.try_again)
    public void onTryAgainClicked() {
        // Hide all views
        errorMessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setVisibility(View.GONE);
        // Show progress circle
        progressCircle.setVisibility(View.VISIBLE);
        // Try to download the data again
        pageToDownload = 1;
        adapter = null;
        downloadproductsList();
    }

    @Override
    public void onproductClicked(int position) {
        if (viewType == ViMarket.VIEW_TYPE_TO_SEE) {
            if (isTablet) {
                //                Toast.makeText(getActivity(),"1",Toast.LENGTH_LONG).show();
                ((ProductActivity) getActivity()).loadDetailFragmentUser(String.valueOf(adapter.productList.get(position).productid), String.valueOf(adapter.productList.get(position).userid));

            }
            else {
                //                                Toast.makeText(getActivity(),"2",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, ProductDetailActivityUser.class);
                intent.putExtra(ViMarket.product_ID, String.valueOf(adapter.productList.get(position).productid));
                intent.putExtra(ViMarket.user_ID, String.valueOf(adapter.productList.get(position).userid));

                startActivity(intent);
            }

        }
        else {
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
            downloadCurrency();
            return "ok";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (error) {
                onDownloadFailed();
            }
            else {
                System.out.println("23ok");
                onDownloadSuccessful();
            }
        }
    }

}
