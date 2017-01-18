package com.sega.vimarket.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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
import com.sega.vimarket.Tricks.ViewPagerEx;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.activity.CommentActivity;
import com.sega.vimarket.activity.ProductActivity;
import com.sega.vimarket.activity.ProductDetailActivity;
import com.sega.vimarket.activity.ProfileProduct;
import com.sega.vimarket.activity.ProfileProductSold;
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
import com.sega.vimarket.view.MaterialStyledDialog;
import com.sega.vimarket.widget.CircleImageView;
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
import java.util.List;
import java.util.Map;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**a
 * Created by HOHOANGLINH on 16-Nov-16.
 */
public class PersonalDetailFragment extends Fragment implements Toolbar.OnMenuItemClickListener, ViewPagerEx.OnPageChangeListener, ProductAdapter.OnproductClickListener, OnLikeListener {

    private static final int TOTAL_PAGES = 999;
    public static HashMap<String, Double> currency;
    public Currency curr;
    SessionManager session;
    Double rate;
    ArrayList<Comments> commentslist = new ArrayList<>();
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


    /*    @BindView(R.id.loading_more)
        View loadingMore;
        @BindView(R.id.swipe_refresh)
        SwipeRefreshLayout swipeRefreshLayout;
        @BindView(R.id.product_grid)
        RecyclerView recyclerView;*/
    @BindView(R.id.username)
    TextView tvusername;
  /*  @BindView(R.id.progress_circle)
    View progressCircle;
    View errorMessage;*/
    @BindView(R.id.product_detail_holder)
    NestedScrollView productHolder;
    @BindView(R.id.poster_image)
    CircleImageView posterImage;
    @BindView(R.id.product_name)
    TextView productTitle;
    @BindView(R.id. ratingtext)
    TextView ratingtext;
    @BindView(R.id.ratingDetail)
    SimpleRatingBar ratingDetail;
    @BindView(R.id.ratingBar)
    SimpleRatingBar ratingBar;
    @BindView(R.id.txtrate)
    TextView txtrate;
    @BindView(R.id.txtratecount)
    TextView txtratecount;
    SimpleRatingBar ratingBardialog;
    @BindView(R.id.thumb_button)
    LikeButton thumbButton;
    int height, width;
    @BindView(R.id.toolbar2)
CollapsingToolbarLayout  toolbar2;
    RequestQueue requestQueue;
    String point="", favorite="";
    @BindView(R.id.chart)
    HorizontalBarChart chart;
    Rate ratee;
    Unbinder unbinder;
    @BindView(R.id.layoutratingbar)
    LinearLayout layoutratingbar;
         @BindView(R.id.comments_holder)
      View comments_holder;
//     Comment
    float ratingpoint;
      @BindView(R.id.comments_see_all)
      View commentsallbutton;
      @BindViews({R.id.movie_cast_item1, R.id.movie_cast_item2, R.id.movie_cast_item3})
      List<View> movieCastItems;
      @BindViews({R.id.userimage1, R.id.userimage2, R.id.userimage3})
      List<CircleImageView> usercommentimage;
      @BindViews({R.id.usercomments1, R.id.usercomments2, R.id.usercomments3})
      List<TextView> usercommentname;
      @BindViews({R.id.comments1, R.id.comments2, R.id.comments3})
      List<TextView> usercommentcontent;
      @BindViews({R.id.datecomment1, R.id.datecomment2, R.id.datecomment3})
      List<TextView> usercommenttime;
      @BindViews({R.id.user_rating1, R.id.user_rating2, R.id.user_rating3})
      List<TextView> usercommentrate;
    MaterialStyledDialog.Builder dialogHeader_4;
    View customView;
    String favoritecount;
    private Context context;
    private ProductAdapter adapter;
    private GridLayoutManager layoutManager;
    private int pageToDownload;
    private int viewType;
    private boolean isLoading;
    private boolean isLoadingLocked;
    private String id, sellerid;
    private Product product;
    private User seller;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_personal_detail, container, false);

        setRetainInstance(true);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        session = new SessionManager(getActivity());
        height = displaymetrics.heightPixels;
        context = getContext();
        width = displaymetrics.widthPixels;
        unbinder = ButterKnife.bind(this, v);
     /*  errorMessage = v.findViewById(R.id.error_message);*/



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
        if(session.getColor()==-1)
        {
            toolbar2.setContentScrimColor(getResources().getColor(R.color.primary));
        }
        else
        {
            toolbar2.setContentScrimColor((session.getColor()));
        }


        // Download product details if new instance, else restore from saved instance
        final LayoutInflater inflatertemp = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customView = inflatertemp.inflate(R.layout.layout_rating_dialog,container, false);
        final EditText comments = (EditText) customView.findViewById(R.id.commentdialog);
        ratingBardialog = (SimpleRatingBar)customView.findViewById(R.id.ratingBardialog);
        // Setup FAB
        thumbButton.setOnLikeListener(this);
        ratingBar.setStepSize();
        ratingBar.setIndicator(true);
        layoutratingbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                new MaterialStyledDialog.Builder(context)
                        .setHeaderDrawable(R.drawable.background)
                        .setTitle("Sweet!")
                        .setCancelable(false)
                        .setDescription(getResources().getString(R.string.addrate))
                        .setPositiveText(getResources().getString(R.string.submit))
                        .setCustomView(customView,20,20,20,0)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                                StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_RATE, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        System.out.println(response);
                                        //                        Toast.makeText(getContext(),response.toString(), Toast.LENGTH_SHORT).show();

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
                                        param.put("point", String.valueOf(ratingpoint));
                                        param.put("contentcomment",comments.getText().toString());
                                        return param;

                                    }

                                };
                                request.setTag(this.getClass().getName());
                                VolleySingleton.getInstance(getActivity()).requestQueue.add(request);
                                downloadRate();
                                downloadproductDetails(id);


                            }
                        })
                        .setNegativeText(getResources().getString(R.string.notnow))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ratingpoint = 0;
                                ratingBardialog.setRating(0);
                                ((ViewGroup)customView.getParent()).removeView(customView);
                            }
                        }).show();

            }
        });
        ratingBardialog.setStepSize();

         ratingBardialog.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, final float rating,
                                        boolean fromUser) {
                ratingpoint = rating;


                //                Toast.makeText(getActivity(),String.valueOf(rating),Toast.LENGTH_SHORT).show();
            }
        });

        currency = new HashMap<>();

        unbinder = ButterKnife.bind(this, v);
        // Initialize variables

        pageToDownload = 1;
        viewType = getArguments().getInt(ViMarket.VIEW_TYPE);

        // Setup RecyclerView
        adapter = new ProductAdapter(context, this);
        //        floatingActionsMenu.setVisibility(View.VISIBLE);
        layoutManager = new GridLayoutManager(context, getNumberOfColumns());
  /*      recyclerView.setHasFixedSize(true);
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
        });*/


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
      /*  swipeRefreshLayout.setColorSchemeResources(R.color.accent);
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
        });*/
        // Get the products list
        if (savedInstanceState == null || !(savedInstanceState.containsKey(ViMarket.product_ID)
                && savedInstanceState.containsKey(ViMarket.product_OBJECT) && savedInstanceState.containsKey(ViMarket.seller_DETAIL))) {
            id = getArguments().getString(ViMarket.product_ID);
            sellerid = getArguments().getString(ViMarket.user_ID);

            rate = getArguments().getDouble(ViMarket.currency_RATE);
            if (TextUtils.isNullOrEmpty(id)) {
              /*  progressCircle.setVisibility(View.GONE);*/
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
            point = savedInstanceState.getString("point");
            favorite = savedInstanceState.getString("favorite");
            ratee = savedInstanceState.getParcelable("rate");

                    onDownloadSuccessful();
                    onDownloadRateSuccessful();
        }
        if (savedInstanceState == null || !savedInstanceState.containsKey(ViMarket.product_LIST)) {

            downloadproductsList();

        }
        else {
            adapter.productList = savedInstanceState.getParcelableArrayList(ViMarket.product_LIST);
            pageToDownload = savedInstanceState.getInt(ViMarket.PAGE_TO_DOWNLOAD);
            isLoadingLocked = savedInstanceState.getBoolean(ViMarket.IS_LOCKED);
            isLoading = savedInstanceState.getBoolean(ViMarket.IS_LOADING);

            // Download again if stopped, else show list
            if (isLoading) {
                if (pageToDownload == 1) {
                /*    progressCircle.setVisibility(View.VISIBLE);
                    loadingMore.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.GONE);*/
                }
                else {
           /*         progressCircle.setVisibility(View.GONE);
                    loadingMore.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);*/
                }

                downloadproductsList();

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
            outState.putParcelable("rate", ratee);
            outState.putString("point", point);
            outState.putString("favorite", favorite);
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
        commentslist.clear();
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
                        System.out.println(feedObj);
                        //add product to list product





                            //add product to list products

                            //add product to sqlite

                        switch (feedObj.getInt("point")) {
                            case 1:
                                ratee.onestar +=1;

                                break;
                            case 2:
                                ratee.twostar +=1;

                                break;
                            case 3:
                                ratee.threestar +=1;

                                break;
                            case 4:
                                ratee.fourstar+=1;

                                break;
                            case 5:
                                ratee.fivestar +=1;

                                break;

                        }
                        favoritecount = jObj.getString("count");
                        Comments comment = new Comments(feedObj.getString("userid"), feedObj.getString("username"),feedObj.getString("time"),
                                                        feedObj.getString("contentcomment"), feedObj.getString("userpic"), feedObj.getString("point"));
                        commentslist.add(comment);
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
        tvusername.setText(favoritecount +" "+ getString(R.string.favorites));


        if (commentslist.size() == 0) {
            movieCastItems.get(0).setVisibility(View.GONE);
            movieCastItems.get(1).setVisibility(View.GONE);
            movieCastItems.get(2).setVisibility(View.GONE);
        } else if (commentslist.size() == 1) {
            movieCastItems.get(0).setVisibility(View.VISIBLE);
            // 0
            Glide.with(getActivity()).load(commentslist.get(0).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(0));
            usercommentname.get(0).setText(commentslist.get(0).username);
            usercommentcontent.get(0).setText(commentslist.get(0).contentcomment);
            usercommentrate.get(0).setText(commentslist.get(0).rate);
            usercommenttime.get(0).setText(DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(commentslist.get(0).time),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
            // Hide views
            movieCastItems.get(2).setVisibility(View.GONE);
            movieCastItems.get(1).setVisibility(View.GONE);
            // Fix padding
            int padding = getResources().getDimensionPixelSize(R.dimen.dist_large);
            comments_holder.setPadding(padding, padding, padding, padding);
        } else if (commentslist.size() == 2) {
            movieCastItems.get(0).setVisibility(View.VISIBLE);
            movieCastItems.get(1).setVisibility(View.VISIBLE);
            // 1
            Glide.with(getActivity()).load(commentslist.get(1).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(1));
            usercommentname.get(1).setText(commentslist.get(1).username);
            usercommentcontent.get(1).setText(commentslist.get(1).contentcomment);
            usercommentrate.get(1).setText(commentslist.get(1).rate);
            usercommenttime.get(1).setText(DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(commentslist.get(1).time),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
            // 0
            Glide.with(getActivity()).load(commentslist.get(0).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(0));
            usercommentname.get(0).setText(commentslist.get(0).username);
            usercommentcontent.get(0).setText(commentslist.get(0).contentcomment);
            usercommentrate.get(0).setText(commentslist.get(0).rate);
            usercommenttime.get(0).setText(DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(commentslist.get(0).time),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
            // Hide views
            movieCastItems.get(2).setVisibility(View.GONE);
            // Fix padding
            int padding = getResources().getDimensionPixelSize(R.dimen.dist_large);
            comments_holder.setPadding(padding, padding, padding, padding);
        } else if (commentslist.size() >= 3) {
            movieCastItems.get(0).setVisibility(View.VISIBLE);
            movieCastItems.get(1).setVisibility(View.VISIBLE);
            movieCastItems.get(2).setVisibility(View.VISIBLE);
            // 2
            Glide.with(getActivity()).load(commentslist.get(2).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(2));
            usercommentname.get(2).setText(commentslist.get(2).username);
            usercommentcontent.get(2).setText(commentslist.get(2).contentcomment);
            usercommentrate.get(2).setText(commentslist.get(2).rate);
            usercommenttime.get(2).setText(DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(commentslist.get(2).time),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
            // 1
            Glide.with(getActivity()).load(commentslist.get(1).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(1));
            usercommentname.get(1).setText(commentslist.get(1).username);
            usercommentcontent.get(1).setText(commentslist.get(1).contentcomment);
            usercommentrate.get(1).setText(commentslist.get(1).rate);
            usercommenttime.get(1).setText(DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(commentslist.get(1).time),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
            // 0
            Glide.with(getActivity()).load(commentslist.get(0).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(0));
            usercommentname.get(0).setText(commentslist.get(0).username);
            usercommentcontent.get(0).setText(commentslist.get(0).contentcomment);
            usercommentrate.get(0).setText(commentslist.get(0).rate);
            usercommenttime.get(0).setText(DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(commentslist.get(0).time),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
            // Hide show all button
            if (commentslist.size() == 3) {
                int padding = getResources().getDimensionPixelSize(R.dimen.dist_large);
                comments_holder.setPadding(padding, padding, padding, padding);
            }
        }
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
                    if(feedObj.getString("rate").equals("null"))
                        seller.rate = "0";
                    else
                    {
                        seller.rate = (double) Math.round(Double.parseDouble(feedObj.getString("rate")) * 10) / 10 + "";
                    }

                    point = feedObj.getString("point");
                    favorite = feedObj.getString("favorite");
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
        if (point.equalsIgnoreCase("null")||point.equalsIgnoreCase("")) {
            ratingBar.setRating(0);
        }
        else {
            ratingBar.setFocusable(false);
            ratingBar.setFocusableInTouchMode(false);
            ratingBar.setRating(Float.parseFloat(point));
            layoutratingbar.setClickable(false);
            ratingtext.setText(getString(R.string.ratetextcomplete));
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

            toolbar.setTitle("");
            toolbarTextHolder.setVisibility(View.VISIBLE);
            toolbarTitle.setText(getString(R.string.personalpage) + " " + product.username);
            //            toolbarSubtitle.setText(seller.email);


        Glide.with(getContext()).load(seller.userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(100, 100).into(posterImage);

        productTitle.setText(product.username);
        tvprice.setText(seller.phone);

        tvproductdate.setText(seller.email);

        isLoading = false;
    /*    errorMessage.setVisibility(View.GONE);
        progressCircle.setVisibility(View.GONE);
        loadingMore.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);
        adapter.notifyDataSetChanged();*/
        txtrate.setText(seller.rate);
        ratingDetail.setRating(Float.parseFloat(seller.rate));
        txtratecount.setText(seller.count);

      /*  refreshLayout();*/
    }
     @OnClick(R.id.comments_see_all)
        public void onComment() {
            Intent intent = new Intent(getActivity(), CommentActivity.class);

            intent.putExtra(ViMarket.COMMENT_TYPE, ViMarket.COMMENT_TYPE_CAST);
            intent.putExtra(ViMarket.user_name, seller.username);
            intent.putExtra(ViMarket.user_ID, seller.userid);
            intent.putExtra(ViMarket.COMMENT_LIST, commentslist);
            startActivityForResult(intent, 1);
        }
    @OnClick(R.id.product_sell)
    public void onProduct() {
    /*    Intent i = new Intent(getActivity(), ProfileProduct.class);



        i.putExtra(ViMarket.seller_ID, seller.userid);
        i.putExtra(ViMarket.product_LIST, adapter.productList);
        startActivityForResult(i, 1);*/
        Intent intent = new Intent(getActivity(), ProfileProduct.class);

        intent.putExtra(ViMarket.COMMENT_TYPE, ViMarket.COMMENT_TYPE_CAST);
        intent.putExtra(ViMarket.user_name, seller.username);
        intent.putExtra(ViMarket.seller_ID, seller.userid+"");

        startActivity(intent);
    }
    @OnClick(R.id.product_sold)
    public void onProductSold() {
    /*    Intent i = new Intent(getActivity(), ProfileProduct.class);



        i.putExtra(ViMarket.seller_ID, seller.userid);
        i.putExtra(ViMarket.product_LIST, adapter.productList);
        startActivityForResult(i, 1);*/
        Intent intent = new Intent(getActivity(), ProfileProductSold.class);

        intent.putExtra(ViMarket.COMMENT_TYPE, ViMarket.COMMENT_TYPE_CAST);
        intent.putExtra(ViMarket.user_name, seller.username);
        intent.putExtra(ViMarket.seller_ID, seller.userid+"");

        startActivity(intent);
    }
    private void onDownloadFailed() {
       /* errorMessage.setVisibility(View.VISIBLE);
        progressCircle.setVisibility(View.GONE);*/
        productHolder.setVisibility(View.GONE);
        toolbarTextHolder.setVisibility(View.GONE);
        toolbar.setTitle("");
        isLoading = false;
        if (pageToDownload == 1) {
         /*   progressCircle.setVisibility(View.GONE);
            loadingMore.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setVisibility(View.GONE);
            errorMessage.setVisibility(View.VISIBLE);*/
        }
        else {
          /*  progressCircle.setVisibility(View.GONE);
            loadingMore.setVisibility(View.GONE);
            errorMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(true);*/
            isLoadingLocked = true;
        }
    }

  /*  public void refreshLayout() {
        Parcelable state = layoutManager.onSaveInstanceState();
        layoutManager = new GridLayoutManager(getContext(), getNumberOfColumns());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.onRestoreInstanceState(state);
    }*/


    private void downloadproductsList() {
       /* if (adapter == null) {
            adapter = new ProductAdapter(context, this);
            recyclerView.setAdapter(adapter);

        }*/

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

  /*  @OnClick(R.id.try_again)
    public void onTryAgainClicked() {
        errorMessage.setVisibility(View.GONE);
        progressCircle.setVisibility(View.VISIBLE);
    }*/


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
                downloadRate();

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
                downloadRate();
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
