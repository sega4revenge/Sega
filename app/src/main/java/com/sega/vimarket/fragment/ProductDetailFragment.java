package com.sega.vimarket.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.activity.ChatActivity;
import com.sega.vimarket.activity.CommentActivity;
import com.sega.vimarket.activity.Fullscreen;
import com.sega.vimarket.activity.PersonalPage;
import com.sega.vimarket.activity.ProductActivity;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.loader.MyValueFormatter;
import com.sega.vimarket.model.Comments;
import com.sega.vimarket.model.Product;
import com.sega.vimarket.model.Rate;
import com.sega.vimarket.model.User;
import com.sega.vimarket.model.Utils;
import com.sega.vimarket.service.GPSTracker;
import com.sega.vimarket.util.TextUtils;
import com.sega.vimarket.util.VolleySingleton;
import com.sega.vimarket.widget.CircleImageView;
import com.sega.vimarket.widget.RobotoLightTextView;
import com.sega.vimarket.widget.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ProductDetailFragment extends Fragment implements OnMenuItemClickListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1234;

    boolean error;
    ArrayList<Comments> commentslist = new ArrayList<>();
    SessionManager session;
    private Unbinder unbinder;


    private String id, userid;
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
    @BindView(R.id.overlaytext)
    TextView tvdistance;
    @BindView(R.id.toolbar_subtitle)
    TextView toolbarSubtitle;

    @BindView(R.id.progress_circle)
    View progressCircle;
    @BindView(R.id.error_message)
    View errorMessage;
    @BindView(R.id.product_detail_holder)
    NestedScrollView productHolder;
    @BindView(R.id.fab_menu)
    FloatingActionMenu floatingActionsMenu;

    @BindView(R.id.product_overview_value)
    TextView productoverview;
    // Basic info
    @BindView(R.id.product_name)
    TextView productTitle;

    @BindView(R.id.product_sharecount)
    TextView productsharecount;

    @BindView(R.id.sellername)
    RobotoLightTextView sellername;
    @BindView(R.id.selleremail)
    RobotoLightTextView selleremail;
    @BindView(R.id.sellerphone)
    RobotoLightTextView sellerphone;
    @BindView(R.id.productaddress)
    RobotoLightTextView productaddress;
    @BindView(R.id.producttype)
    RobotoLightTextView producttype;
    @BindView(R.id.productcategory)
    RobotoLightTextView productcategory;
        @BindView(R.id.comments_holder)
        View comments_holder;
//     Comment
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
    @BindView(R.id.ratingDetail)
    SimpleRatingBar ratingDetail;

    @BindView(R.id.txtrate)
    TextView txtrate;
    @BindView(R.id.txtratecount)
    TextView txtratecount;
    private NumberFormat format;
    private Double rate;
    int height, width;

    private SliderLayout mDemoSlider;
    AsyncTask<Void, Void, String> asyncTask;
    String point;
    Rate ratee;
    @BindView(R.id.chart)
    HorizontalBarChart chart;
    LinearLayout maps;


    // Fragment lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_product_detail, container, false);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        session = new SessionManager(getActivity());
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        final Uri data = getActivity().getIntent().getData();
        sellername = (RobotoLightTextView)v.findViewById(R.id.sellername);
        mDemoSlider = (SliderLayout) v.findViewById(R.id.slider);
        unbinder = ButterKnife.bind(this, v);
        toolbar.inflateMenu(R.menu.menu_share);
        Locale current = getActivity().getResources().getConfiguration().locale;
        if (current.getCountry().equals("VN"))
            format = NumberFormat.getCurrencyInstance();
        else
            format = NumberFormat.getCurrencyInstance(Locale.US);

        // Setup toolbar
        toolbar.setTitle(R.string.loading);
        toolbar.setOnMenuItemClickListener(this);

        if (!isTablet) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.action_home));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(data==null) {
                        getActivity().finish();
                        Runtime.getRuntime().gc();
                    }
                    else{
                        Intent i = new Intent(getActivity(), ProductActivity.class);
                        startActivity(i);
                        getActivity().finish();
                        Runtime.getRuntime().gc();
                    }

                }
            });
        }
        // Download product details if new instance, else restore from saved instance
        if (savedInstanceState == null || !(savedInstanceState.containsKey(ViMarket.product_ID)
                && savedInstanceState.containsKey(ViMarket.product_OBJECT) && savedInstanceState.containsKey(ViMarket.seller_DETAIL))) {
            id = getArguments().getString(ViMarket.product_ID);
            userid = getArguments().getString(ViMarket.user_ID);
            rate = session.getCurrency();

            if (TextUtils.isNullOrEmpty(id)) {
                progressCircle.setVisibility(View.GONE);
                toolbarTextHolder.setVisibility(View.GONE);
                toolbar.setTitle("");
            } else {

                asyncTask = new CustomerAsyncTask().execute();
            }

        } else {

            id = savedInstanceState.getString(ViMarket.product_ID);
            product = savedInstanceState.getParcelable(ViMarket.product_OBJECT);
            seller = savedInstanceState.getParcelable(ViMarket.seller_DETAIL);
            onDownloadSuccessful();
            //onDownloadRateSuccessful();

        }
        // Setup FAB
        productHolder.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX,
                                       int oldScrollY) {
                if (oldScrollY < scrollY) {
                    floatingActionsMenu.hideMenuButton(true);
                } else {
                    floatingActionsMenu.showMenuButton(true);
                }
            }
        });
        maps = (LinearLayout) v.findViewById(R.id.maps);
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(ViMarket.MAPS_INTENT_URI +
                                                                 Uri.encode(product.productaddress + ", " + product.areaproduct)));
                                startActivity(intent);
            }
        });


        return v;
    }

    public void showAnimationBanner() {
        for (int i = 0; i < product.productimage.size(); i++) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .image(product.productimage.get(i).toString())
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putInt("index", i);

            mDemoSlider.addSlider(textSliderView);

        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Send screen name to analytics
        mDemoSlider.startAutoCycle();

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

    }

    // Toolbar menu click
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            if (product != null) {
                // Share the product
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, product.productname);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                       getResources().getString(R.string.add_product_name) +": "+ product.productname +
                                               "\n" + getResources().getString(R.string.sellername) +": "+ product.username +
                                               "\n" + getResources().getString(R.string.add_price) + ": " +format.format(product.price) +
                                               "\n" + getResources().getString(R.string.add_description) + ": "+ product.description +
                                               "\n" + product.productimage.get(0) +
                                               "\n" + getResources().getString(R.string.linkshare)+ ": "+"http://freemarkets.ga/link.php?productid="+product.productid+"&userid="+seller.userid);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_share_using)));
                // Send event to Google Analytics

        }
//        if (item.getItemId() == R.id.action_map) {
//            if (product != null) {
//                // Share the product
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(Constants.MAPS_INTENT_URI +
//                                                 Uri.encode(product.productaddress + ", " + product.areaproduct)));
//                startActivity(intent);
//            }
//        }
        }
        return true;
    }

    // JSON parsing and display
    private void downloadRate() {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("userid", userid+"")
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(AppConfig.URL_RATEDETAIL)
                .post(body)
                .build();

        try {
            String responsestring = client.newCall(request).execute().body().string();
            Log.d("ok", "Login Response: " + responsestring);
            try {

                JSONObject jObj = new JSONObject(responsestring);
                JSONArray feedArray = jObj.getJSONArray("rate");
                ratee = new Rate();
                for (int i = 0; i < feedArray.length(); i++) {
                    final JSONObject feedObj = (JSONObject) feedArray.get(i);
                    //add product to list product

                    switch (feedObj.getInt("point")) {
                        case 1:
                            ratee.onestar = Integer.parseInt(feedObj.getString("count"));

                            break;
                        case 2:
                            ratee.twostar = Integer.parseInt(feedObj.getString("count"));

                            break;
                        case 3:
                            ratee.threestar = Integer.parseInt(feedObj.getString("count"));

                            break;
                        case 4:
                            ratee.fourstar = Integer.parseInt(feedObj.getString("count"));

                            break;
                        case 5:
                            ratee.fivestar = Integer.parseInt(feedObj.getString("count"));

                            break;
                    }
                }
                error=false;
            } catch (Exception ex) {
                // JSON parsing error
                ex.printStackTrace();
                error=true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            error=true;
        }

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

    private void downloadproductDetails(final String id) {


        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("productid", id)
                .add("userrated", AppConfig.USERID_ID)
                .add("userrating", userid)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(AppConfig.URL_PRODUCTDETAIL)
                .post(body)
                .build();

        try {
            String responsestring = client.newCall(request).execute().body().string();
            Log.d("ok", "Login Response: " + responsestring);
            try {
                JSONObject jObj = new JSONObject(responsestring);
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
                seller.count = feedObj.getString("count");
                seller.rate = (double) Math.round(Double.parseDouble(feedObj.getString("rate")) * 10) / 10 + "";
                JSONArray feedArray = feedObj.getJSONArray("comments");
                commentslist.clear();

                point = feedObj.getString("point");
                for (int i = 0; i < feedArray.length(); i++) {
                    final JSONObject feedComment = (JSONObject) feedArray.get(i);
                    //add product to list products
                    Comments comment = new Comments(feedComment.getString("userid"), feedComment.getString("username"), feedComment.getString("productid"), feedComment.getString("time"),
                                                    feedComment.getString("contentcomment"), feedComment.getString("userpic"), feedComment.getString("rate"));
                    commentslist.add(comment);
                    //add product to sqlite
                }
                error=false;
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                error=true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            error=true;
        }


    }

    private void onDownloadSuccessful() {
        sellername.setSelected(true);
        sellerphone.setSelected(true);
        selleremail.setSelected(true);
        productcategory.setSelected(true);
        producttype.setSelected(true);
        productaddress.setSelected(true);
        // Toggle visibility
        progressCircle.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        productHolder.setVisibility(View.VISIBLE);
        floatingActionsMenu.setVisibility(View.VISIBLE);
        // Set title and tagline
        if (TextUtils.isNullOrEmpty(product.productname)) {
            toolbar.setTitle(product.productname);
            toolbarTextHolder.setVisibility(View.GONE);
        } else {
            toolbar.setTitle("");
            toolbarTextHolder.setVisibility(View.VISIBLE);
            toolbarTitle.setText(product.productname);
            toolbarSubtitle.setText(product.username);
        }
        productTitle.setText(product.productname);

        tvprice.setText(format.format(product.price));
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(product.productdate),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        tvproductdate.setText(timeAgo);

        String distance =
                Utils.formatDistanceBetween(GPSTracker.mLastestLocation, product.location);
        if (android.text.TextUtils.isEmpty(distance)) {
            tvdistance.setVisibility(View.GONE);
        } else {
            tvdistance.setVisibility(View.VISIBLE);
            tvdistance.setText(distance);
        }
        productoverview.setText(product.description);
        productsharecount.setText(product.sharecount);
        sellername.setText(product.username);
        selleremail.setText(seller.getEmail());
        sellerphone.setText(seller.getPhone());

        txtrate.setText(seller.rate);
        txtratecount.setText(seller.count);
        ratingDetail.setRating(Float.parseFloat(seller.rate));
        productaddress.setText(product.productaddress + ", " + product.areaproduct);
        producttype.setText(product.producttype);
        productcategory.setText(product.categoryname);
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
                    Glide.with(getContext()).load(commentslist.get(2).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(2));
                    usercommentname.get(2).setText(commentslist.get(2).username);
                    usercommentcontent.get(2).setText(commentslist.get(2).contentcomment);
                    usercommentrate.get(2).setText(commentslist.get(2).rate);
                    usercommenttime.get(2).setText(DateUtils.getRelativeTimeSpanString(
                            Long.parseLong(commentslist.get(2).time),
                            System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
                    // 1
                    Glide.with(getContext()).load(commentslist.get(1).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(1));
                    usercommentname.get(1).setText(commentslist.get(1).username);
                    usercommentcontent.get(1).setText(commentslist.get(1).contentcomment);
                    usercommentrate.get(1).setText(commentslist.get(1).rate);
                    usercommenttime.get(1).setText(DateUtils.getRelativeTimeSpanString(
                            Long.parseLong(commentslist.get(1).time),
                            System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
                    // 0
                    Glide.with(getContext()).load(commentslist.get(0).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(0));
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
        showAnimationBanner();
    }

    private void onDownloadFailed() {
        errorMessage.setVisibility(View.VISIBLE);
        progressCircle.setVisibility(View.GONE);
        productHolder.setVisibility(View.GONE);
        toolbarTextHolder.setVisibility(View.GONE);
        toolbar.setTitle("");
    }

    // Click events
   /* @OnClick(R.id.button_photos)
    public void onPhotosButtonClicked() {
        Intent intent = new Intent(getActivity(), PhotoActivity.class);
        intent.putExtra(ViMarket.product_ID, product.id);
        intent.putExtra(ViMarket.product_NAME, product.title);
        startActivity(intent);
    }
    @OnClick(R.id.button_reviews)
    public void onReviewsButtonClicked() {
        Intent intent = new Intent(getActivity(), ReviewActivity.class);
        intent.putExtra(ViMarket.product_ID, product.imdbId);
        intent.putExtra(ViMarket.product_NAME, product.title);
        startActivity(intent);
    }
    @OnClick(R.id.button_videos)
    public void onVideosButtonClicked() {
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putExtra(ViMarket.product_ID, product.id);
        intent.putExtra(ViMarket.product_NAME, product.title);
        startActivity(intent);*/
    @OnClick(R.id.try_again)
    public void onTryAgainClicked() {
        errorMessage.setVisibility(View.GONE);
        progressCircle.setVisibility(View.VISIBLE);
     /*   downloadproductDetails(id);*/
    }

        @OnClick(R.id.comments_see_all)
        public void onComment() {
            Intent intent = new Intent(getActivity(), CommentActivity.class);

            intent.putExtra(ViMarket.COMMENT_TYPE, ViMarket.COMMENT_TYPE_CAST);
            intent.putExtra(ViMarket.product_NAME, product.productname);
            intent.putExtra(ViMarket.product_ID, product.productid);
            intent.putExtra(ViMarket.COMMENT_LIST, commentslist);
            startActivityForResult(intent, 1);
        }

    // FAB related functions
    @OnClick(R.id.fab_call)
    public void onWatchedButtonClicked() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                                              android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if
                    (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                                                         android.Manifest.permission.CALL_PHONE)) {
                Toast.makeText(getActivity(), "Its Necessary To Call", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(getActivity(),
                                                  new String[]{Manifest.permission.CALL_PHONE},
                                                  MY_PERMISSIONS_REQUEST_CALL_PHONE);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                                                  new String[]{Manifest.permission.CALL_PHONE},
                                                  MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + seller.getPhone()));
            startActivity(intent);
        }
    }

    @OnClick(R.id.fab_sms)
    public void onToWatchButtonClicked() {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", seller.getPhone());
        //        smsIntent.putExtra("sms_body"  , "Test");

        try {
            startActivity(smsIntent);
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(),
                           "SMS failed, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }
    @OnClick(R.id.fab_messenger)
    public void onToMessengerButtonClicked() {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(ViMarket.user_ID, session.getLoginId());
        intent.putExtra(ViMarket.seller_ID, product.userid);
        intent.putExtra(ViMarket.user_name, session.getLoginName());
        intent.putExtra(ViMarket.seller_name, product.username);
        startActivity(intent);
        mDemoSlider.stopAutoCycle();


    }

    @OnClick(R.id.personal_page)
    public void onPersonalPage() {
        //        Bundle args = new Bundle();
        //        args.putInt(ViMarket.VIEW_TYPE, ViMarket.VIEW_TYPE_NEW);
        //        fragment = new ProductListFragment();
        //        fragment.setArguments(args);
        //        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        //        transaction.replace(R.id.product_detail_container, fragment, ViMarket.TAG_GRID_FRAGMENT);
        //        transaction.commit();
        Intent intent = new Intent(getActivity(), PersonalPage.class);
        intent.putExtra(ViMarket.product_ID, id);
        intent.putExtra(ViMarket.user_ID, userid);
        mDemoSlider.stopAutoCycle();
        startActivity(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(asyncTask!= null && asyncTask.getStatus() == AsyncTask.Status.RUNNING)
            asyncTask.cancel(true);
        mDemoSlider.stopAutoCycle();
        mDemoSlider.removeAllSliders();
        VolleySingleton.getInstance(getActivity()).requestQueue.cancelAll(this.getClass().getName());


    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                mDemoSlider.stopAutoCycle();
                mDemoSlider.removeAllSliders();
                downloadproductDetails(id);
                downloadRate();
            }
        }
    }

    @Override
    public void onStop() {

        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed

        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        Intent i = new Intent(getActivity(), Fullscreen.class);
        i.putExtra("pos", slider.getBundle().getInt("index"));
        i.putExtra("data", product.productimage);
        startActivity(i);
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
    public class CustomerAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Void... params) {
           downloadproductDetails(id);
            downloadRate();
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
                onDownloadRateSuccessful();
            }
        }
    }

}
