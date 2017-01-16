package com.sega.vimarket.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.sega.vimarket.R;
import com.sega.vimarket.SliderTypes.BaseSliderView;
import com.sega.vimarket.Tricks.ViewPagerEx;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.activity.Fullscreen;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.model.Product;
import com.sega.vimarket.model.User;
import com.sega.vimarket.service.GeocodeAddressIntentService;
import com.sega.vimarket.util.Constants;
import com.sega.vimarket.util.TextUtils;
import com.sega.vimarket.util.VolleySingleton;
import com.sega.vimarket.widget.RobotoLightTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProductDetailFragmentUser extends Fragment implements OnMenuItemClickListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private Unbinder unbinder;
    String newproductname;
    String newproductprice;
    String newproductdesc;
    String newproductaddress;
    String newproductcategory;
    String newproductcategory2;
    String categoryid;
    String productimage1, productimage2, productimage3, productimage4;
    SessionManager session;

    RequestQueue requestQueue;




    AddressResultReceiver mResultReceiver;
    int fetchType = Constants.USE_ADDRESS_LOCATION;
    double latitude, longitude;
    boolean fetchAddress;

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

    @BindView(R.id.price)
    TextView tvprice;

    @BindView(R.id.toolbar_subtitle)
    TextView toolbarSubtitle;

    @BindView(R.id.progress_circle)
    View progressCircle;
    @BindView(R.id.error_message)
    View errorMessage;
    @BindView(R.id.product_detail_holder)
    NestedScrollView productHolder;

//    @BindView(R.id.poster_image1)
//    NetworkImageView posterImage1;
//    @BindView(R.id.poster_image_default1)
//    ImageView posterImageDefault1;
//
//    @BindView(R.id.poster_image2)
//    NetworkImageView posterImage2;
//    @BindView(R.id.poster_image_default2)
//    ImageView posterImageDefault2;
//
//    @BindView(R.id.poster_image3)
//    NetworkImageView posterImage3;
//    @BindView(R.id.poster_image_default3)
//    ImageView posterImageDefault3;
//
//    @BindView(R.id.poster_image4)
//    NetworkImageView posterImage4;
//    @BindView(R.id.poster_image_default4)
//    ImageView posterImageDefault4;
    // Image views
    @BindView(R.id.product_overview_value)
    TextView productoverview;
    // Basic info
    @BindView(R.id.product_name)
    TextView productTitle;

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

    private NumberFormat format;
    private Double rate;
    int height, width;


    GalleryPhoto galleryPhoto;
    final int GALLERY_REQUEST = 22131;
    final int GALLERY_REQUEST2 = 22132;

    final int GALLERY_REQUEST3 = 22133;
    final int GALLERY_REQUEST4 = 22134;
    String point;

    String selectedPhoto, selectedPhoto2, selectedPhoto3, selectedPhoto4;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_product_detail_user, container, false);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        session = new SessionManager(getActivity());
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
                    getActivity().finish();
                }
            });
        }
        // Download product details if new instance, else restore from saved instance
        if (savedInstanceState == null || !(savedInstanceState.containsKey(ViMarket.product_ID)
                && savedInstanceState.containsKey(ViMarket.product_OBJECT) && savedInstanceState.containsKey(ViMarket.seller_DETAIL))) {
            id = getArguments().getString(ViMarket.product_ID);
            userid = getArguments().getString(ViMarket.user_ID);
            rate = getArguments().getDouble(ViMarket.currency_RATE);
            if (TextUtils.isNullOrEmpty(id)) {
                progressCircle.setVisibility(View.GONE);
                toolbarTextHolder.setVisibility(View.GONE);
                toolbar.setTitle("");
            } else {
                downloadproductDetails(id);

            }
        } else {
            id = savedInstanceState.getString(ViMarket.product_ID);
            product = savedInstanceState.getParcelable(ViMarket.product_OBJECT);
            seller = savedInstanceState.getParcelable(ViMarket.seller_DETAIL);
            onDownloadSuccessful();


        }
        // Setup FAB
//        productHolder.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX,
//                                       int oldScrollY) {
//                if (oldScrollY < scrollY) {
//                    floatingActionsMenu.hideMenuButton(true);
//                }
//                else {
//                    floatingActionsMenu.showMenuButton(true);
//                }
//            }
//        });
        // Load Analytics Tracker
        mResultReceiver = new AddressResultReceiver();

        fetchAddress = false;
        fetchType = Constants.USE_ADDRESS_NAME;

//                Toast.makeText(getActivity(),ProductDrawerFragment.userobj.userid + " " + userid,Toast.LENGTH_LONG).show();



        return v;


    }

    //    public void showAnimationBanner() {
//        for(int i = 0;i<product.productimage.size();i++){
//            TextSliderView textSliderView = new TextSliderView(getActivity());
//            // initialize a SliderLayout
//            textSliderView
//                    .image(product.productimage.get(i).toString())
//                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
//                    .setOnSliderClickListener(this);
//
//            //add your extra information
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle()
//                    .putInt("index",i);
//
//            mDemoSlider.addSlider(textSliderView);
//
//        }
//        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
//        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
//        mDemoSlider.setDuration(4000);
//        mDemoSlider.addOnPageChangeListener(this);
//    }
    @Override
    public void onResume() {
        super.onResume();
        // Send screen name to analytics
        //mDemoSlider.startAutoCycle();

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
//        mDemoSlider.stopAutoCycle();
//        mDemoSlider.removeAllSliders();
        VolleySingleton.getInstance(getActivity()).requestQueue.cancelAll(this.getClass().getName());
        unbinder.unbind();
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
                    seller.setEmail(feedObj.getString("email"));
                    seller.setPhone(feedObj.getString("phone"));
                    point = feedObj.getString("point");

//                    JSONArray feedArray = feedObj.getJSONArray("comments");
//                    commentslist.clear();
//                    for (int i = 0; i < feedArray.length(); i++) {
//                        final JSONObject feedComment = (JSONObject) feedArray.get(i);
//                        //add product to list products
//                        Comments comment = new Comments(feedComment.getString("userid"), feedComment.getString("username"), feedComment.getString("productid"), feedComment.getString("time"),
//                                                        feedComment.getString("contentcomment"), feedComment.getString("userpic"), feedComment.getString("rate"));
//                        commentslist.add(comment);
//                        //add product to sqlite
//                    }
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
                Map<String, String> params;
                params = new HashMap<String, String>();
                params.put("productid", id);
                params.put("userrated", String.valueOf(session.getLoginId()));
                params.put("userrating", userid);
                return params;
            }
        };
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(getActivity()).requestQueue.add(strReq);
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

        //        floatingActionsMenu.setVisibility(View.VISIBLE);
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
        // Add share button to toolbar
    /*    backdropImage.setImageUrl(product.productimage,
                                  VolleySingleton.getInstance(getActivity()).imageLoader);*/
//        posterImageDefault1.setVisibility(View.GONE);
//
//        posterImage1.setImageUrl(product.productimage.get(0).toString(),
//                VolleySingleton.getInstance(getActivity()).imageLoader);
//        posterImage1.setErrorImageResId(R.drawable.empty_photo);
//
//        posterImageDefault2.setVisibility(View.GONE);
//
//        posterImage2.setImageUrl(product.productimage.get(1).toString(),
//                VolleySingleton.getInstance(getActivity()).imageLoader);
//        posterImage2.setErrorImageResId(R.drawable.empty_photo);
//
//        posterImageDefault3.setVisibility(View.GONE);
//
//        posterImage3.setImageUrl(product.productimage.get(2).toString(),
//                VolleySingleton.getInstance(getActivity()).imageLoader);
//        posterImage3.setErrorImageResId(R.drawable.empty_photo);
//
//        posterImageDefault4.setVisibility(View.GONE);
//
//        posterImage4.setImageUrl(product.productimage.get(3).toString(),
//                VolleySingleton.getInstance(getActivity()).imageLoader);
//        posterImage4.setErrorImageResId(R.drawable.empty_photo);

        productTitle.setText(product.productname);
//        tvusername.setText(product.username);
        tvprice.setText(format.format(product.price));

        productoverview.setText(product.description);
//
        sellername.setText(product.username);
        selleremail.setText(seller.getEmail());
        sellerphone.setText(seller.getPhone());
        productaddress.setText(product.productaddress + " " + product.areaproduct);
        productcategory.setText(product.categoryname);


        switch (product.categoryname) {
            case "Xe cộ":
                categoryid = "1";
                break;
            case "Bất động sản":
                categoryid = "2";
                break;
            case "Đồ điện tử":
                categoryid = "3";
                break;
            case "Thời trang, đồ dùng cá nhân":
                categoryid = "4";
                break;
            case "Nội ngoại thất, đồ gia dụng":
                categoryid = "5";
                break;
            case "Giải trí, thể thao, sở thích":
                categoryid = "6";
                break;
            case "Đồ dùng văn phòng, công nông nghiệp":
                categoryid = "7";
                break;
            case "Việc làm, dịch vụ":
                categoryid = "8";
                break;
            case "Các loại khác":
                categoryid = "9";
                break;
        }
        productimage1 = String.valueOf(product.productimage.get(0)).substring(22, String.valueOf(product.productimage.get(0)).length());
        productimage2 = String.valueOf(product.productimage.get(1)).substring(22, String.valueOf(product.productimage.get(1)).length());
        productimage3 = String.valueOf(product.productimage.get(2)).substring(22, String.valueOf(product.productimage.get(2)).length());
        productimage4 = String.valueOf(product.productimage.get(3)).substring(22, String.valueOf(product.productimage.get(3)).length());

        newproductname = product.productname;
        newproductprice = String.valueOf(product.price);
        newproductdesc = product.description;
        newproductaddress = product.productaddress;
        latitude = product.location.latitude;
        longitude = product.location.longitude;
        newproductcategory = product.categoryname;
//        if (commentslist.size() == 0) {
//            movieCastItems.get(0).setVisibility(View.GONE);
//            movieCastItems.get(1).setVisibility(View.GONE);
//            movieCastItems.get(2).setVisibility(View.GONE);
//        }
//        else if (commentslist.size() == 1) {
//            movieCastItems.get(0).setVisibility(View.VISIBLE);
//            // 0
//            Glide.with(getActivity()).load(commentslist.get(0).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(0));
//            usercommentname.get(0).setText(commentslist.get(0).username);
//            usercommentcontent.get(0).setText(commentslist.get(0).contentcomment);
//            usercommentrate.get(0).setText(commentslist.get(0).rate);
//            usercommenttime.get(0).setText(DateUtils.getRelativeTimeSpanString(
//                    Long.parseLong(commentslist.get(0).time),
//                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
//            // Hide views
//            movieCastItems.get(2).setVisibility(View.GONE);
//            movieCastItems.get(1).setVisibility(View.GONE);
//            // Fix padding
//            int padding = getResources().getDimensionPixelSize(R.dimen.dist_large);
////            comments_holder.setPadding(padding, padding, padding, padding);
//        }
//        else if (commentslist.size() == 2) {
//            movieCastItems.get(0).setVisibility(View.VISIBLE);
//            movieCastItems.get(1).setVisibility(View.VISIBLE);
//            // 1
//            Glide.with(getActivity()).load(commentslist.get(1).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(1));
//            usercommentname.get(1).setText(commentslist.get(1).username);
//            usercommentcontent.get(1).setText(commentslist.get(1).contentcomment);
//            usercommentrate.get(1).setText(commentslist.get(1).rate);
//            usercommenttime.get(1).setText(DateUtils.getRelativeTimeSpanString(
//                    Long.parseLong(commentslist.get(1).time),
//                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
//            // 0
//            Glide.with(getActivity()).load(commentslist.get(0).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(0));
//            usercommentname.get(0).setText(commentslist.get(0).username);
//            usercommentcontent.get(0).setText(commentslist.get(0).contentcomment);
//            usercommentrate.get(0).setText(commentslist.get(0).rate);
//            usercommenttime.get(0).setText(DateUtils.getRelativeTimeSpanString(
//                    Long.parseLong(commentslist.get(0).time),
//                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
//            // Hide views
//            movieCastItems.get(2).setVisibility(View.GONE);
//            // Fix padding
//            int padding = getResources().getDimensionPixelSize(R.dimen.dist_large);
//            comments_holder.setPadding(padding, padding, padding, padding);
//        }
//        else if (commentslist.size() >= 3) {
//            movieCastItems.get(0).setVisibility(View.VISIBLE);
//            movieCastItems.get(1).setVisibility(View.VISIBLE);
//            movieCastItems.get(2).setVisibility(View.VISIBLE);
//            // 2
//            Glide.with(getActivity()).load(commentslist.get(2).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(2));
//            usercommentname.get(2).setText(commentslist.get(2).username);
//            usercommentcontent.get(2).setText(commentslist.get(2).contentcomment);
//            usercommentrate.get(2).setText(commentslist.get(2).rate);
//            usercommenttime.get(2).setText(DateUtils.getRelativeTimeSpanString(
//                    Long.parseLong(commentslist.get(2).time),
//                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
//            // 1
//            Glide.with(getActivity()).load(commentslist.get(1).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(1));
//            usercommentname.get(1).setText(commentslist.get(1).username);
//            usercommentcontent.get(1).setText(commentslist.get(1).contentcomment);
//            usercommentrate.get(1).setText(commentslist.get(1).rate);
//            usercommenttime.get(1).setText(DateUtils.getRelativeTimeSpanString(
//                    Long.parseLong(commentslist.get(1).time),
//                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
//            // 0
//            Glide.with(getActivity()).load(commentslist.get(0).userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(usercommentimage.get(0));
//            usercommentname.get(0).setText(commentslist.get(0).username);
//            usercommentcontent.get(0).setText(commentslist.get(0).contentcomment);
//            usercommentrate.get(0).setText(commentslist.get(0).rate);
//            usercommenttime.get(0).setText(DateUtils.getRelativeTimeSpanString(
//                    Long.parseLong(commentslist.get(0).time),
//                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
//            // Hide show all button
//            if (commentslist.size() == 3) {
//                int padding = getResources().getDimensionPixelSize(R.dimen.dist_large);
//                comments_holder.setPadding(padding, padding, padding, padding);
//            }
//        }
        //showAnimationBanner();
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
//
//    @OnClick(R.id.poster_image1)
//    public void onPoster1() {
//        galleryPhoto = new GalleryPhoto(getActivity());
//        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
//    }
//
//    @OnClick(R.id.poster_image2)
//    public void onPoster2() {
//        galleryPhoto = new GalleryPhoto(getActivity());
//        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST2);
//    }
//
//    @OnClick(R.id.poster_image3)
//    public void onPoster3() {
//        galleryPhoto = new GalleryPhoto(getActivity());
//        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST3);
//    }
//
//    @OnClick(R.id.poster_image4)
//    public void onPoster4() {
//        galleryPhoto = new GalleryPhoto(getActivity());
//        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST4);
//    }

    @OnClick(R.id.edit_productname)
    public void onEditProductname() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//                .setMessage("Enter password");
        final FrameLayout frameView = new FrameLayout(getActivity());
        builder.setView(frameView);

        final AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.edit_layout1, frameView);
        final EditText edt1 = (EditText) dialoglayout.findViewById(R.id.edtedit1);
        final EditText edt2 = (EditText) dialoglayout.findViewById(R.id.edtedit2);
        final ImageView img = (ImageView) dialoglayout.findViewById(R.id.ld_icon);
        img.setImageResource(R.drawable.icon_votes);
        edt2.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt1.setText(product.productname);
        edt2.setText(String.valueOf(product.price));
        builder.setPositiveButton(R.string.btneditok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                         Write your code here to execute after dialog
                        newproductname = String.valueOf(edt1.getText());
                        newproductprice = String.valueOf(edt2.getText());

                        productTitle.setText(newproductname);
                        tvprice.setText(newproductprice);

//                                               Toast.makeText(getActivity(),
//                                                              "You clicked on YES :" +newproductname, Toast.LENGTH_SHORT)
//                                                       .show();
//                        dialog.cancel();

                    }
                });

        // Setting Negative "NO" Btn
        builder.setNegativeButton(R.string.btneditcancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
//                                               Toast.makeText(getActivity(),
//                                                              "You clicked on NO", Toast.LENGTH_SHORT)
//                                                       .show();
                        dialog.cancel();
//                        dialog.cancel();
                    }
                });
        builder.show();
    }


    @OnClick(R.id.edit_productoverview)
    public void onEditProductoverview() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                .setMessage("Enter password");
        final FrameLayout frameView = new FrameLayout(getActivity());
        builder.setView(frameView);

        final AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.edit_layout, frameView);
        final EditText edt = (EditText) dialoglayout.findViewById(R.id.edtedit);
        final ImageView img = (ImageView) dialoglayout.findViewById(R.id.ld_icon);
        img.setImageResource(R.drawable.icon_overview);
        //edt.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt.setText(product.description);
        builder.setPositiveButton(R.string.btneditok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                         Write your code here to execute after dialog
                        newproductdesc = String.valueOf(edt.getText());
                        productoverview.setText(newproductdesc);
//                    Toast.makeText(getActivity(),
//                            "You clicked on YES :" +newproductdesc, Toast.LENGTH_SHORT)
//                            .show();
//                        dialog.cancel();

                    }
                });

        // Setting Negative "NO" Btn
        builder.setNegativeButton(R.string.btneditcancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
//                    Toast.makeText(getActivity(),
//                            "You clicked on NO", Toast.LENGTH_SHORT)
//                            .show();
                        dialog.cancel();
//                        dialog.cancel();
                    }
                });
        builder.show();
    }

    @OnClick(R.id.edit_productcrew)
    public void onEditProductcrew() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                .setTitle(R.string.editaddressproduct);
//                .setMessage("Enter password");
        final FrameLayout frameView = new FrameLayout(getActivity());
        builder.setView(frameView);

        final AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.edit_layout2, frameView);
        final EditText edt1 = (EditText) dialoglayout.findViewById(R.id.edtedit1);
        final Spinner spn1 = (Spinner) dialoglayout.findViewById(R.id.edtedit2);
        final ImageView img = (ImageView) dialoglayout.findViewById(R.id.ld_icon);
        img.setImageResource(R.drawable.icon_star);


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                R.array.type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        // Apply the adapter to the spinner
        spn1.setAdapter(adapter2);
        final Spinner spn2 = (Spinner) dialoglayout.findViewById(R.id.edtedit3);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.danhmuc, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        // Apply the adapter to the spinner
        spn2.setAdapter(adapter);
//        spn2.setSelection(5);
        //edt.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt1.setText(product.productaddress);
        switch (product.categoryname) {
            case "Xe cộ":
                spn2.setSelection(0);
                break;
            case "Bất động sản":
                spn2.setSelection(1);
                break;
            case "Đồ điện tử":
                spn2.setSelection(2);
                break;
            case "Thời trang, đồ dùng cá nhân":
                spn2.setSelection(3);
                break;
            case "Nội ngoại thất, đồ gia dụng":
                spn2.setSelection(4);
                break;
            case "Giải trí, thể thao, sở thích":
                spn2.setSelection(5);
                break;
            case "Đồ dùng văn phòng, công nông nghiệp":
                spn2.setSelection(6);
                break;
            case "Việc làm, dịch vụ":
                spn2.setSelection(7);
                break;
            case "Các loại khác":
                spn2.setSelection(8);
                break;
        }
        builder.setPositiveButton(R.string.btneditok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                         Write your code here to execute after dialog
                        newproductaddress = String.valueOf(edt1.getText());
                        newproductcategory = spn2.getSelectedItem().toString();
                        newproductcategory2 = String.valueOf(spn2.getSelectedItemPosition());
                        switch (newproductcategory2) {
                            case "0":
                                categoryid = "1";
                                break;
                            case "1":
                                categoryid = "2";
                                break;
                            case "2":
                                categoryid = "3";
                                break;
                            case "3":
                                categoryid = "4";
                                break;
                            case "4":
                                categoryid = "5";
                                break;
                            case "5":
                                categoryid = "6";
                                break;
                            case "6":
                                categoryid = "7";
                                break;
                            case "7":
                                categoryid = "8";
                                break;
                            case "8":
                                categoryid = "9";
                                break;

                        }
                        productaddress.setText(newproductaddress);
                        producttype.setText(spn1.getSelectedItem().toString());
                        productcategory.setText(spn2.getSelectedItem().toString());
                        Intent intent = new Intent(getActivity(), GeocodeAddressIntentService.class);
                        intent.putExtra(Constants.RECEIVER, mResultReceiver);
                        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
                        if (fetchType == Constants.USE_ADDRESS_NAME) {
                            intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, newproductaddress);
                        }

                        Log.e("EDIT ADDRESS", "Starting Service");
//                        Log.e("EDIT ADDRESS",latitude + " " + longitude);
                        getActivity().startService(intent);
//                        Toast.makeText(getActivity(),
//                                "You clicked on YES :" +newproductaddress, Toast.LENGTH_SHORT)
//                                .show();
//                        dialog.cancel();

                    }


                });

        // Setting Negative "NO" Btn
        builder.setNegativeButton(R.string.btneditcancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
//                        Toast.makeText(getActivity(),
//                                "You clicked on NO", Toast.LENGTH_SHORT)
//                                .show();
                        dialog.cancel();
//                        dialog.cancel();
                    }
                });
        builder.show();
    }

    @OnClick(R.id.btnEdit)
    public void onEdit() {
        requestQueue = Volley.newRequestQueue(getActivity());
        Log.e("IMAGE1", productimage1);
        Log.e("IMAGE1", productimage2);

//        Toast.makeText(getActivity(),newproductname + " " +newproductprice + " " +newproductdesc + " " +newproductaddress + " " +newproducttype + " " +newproductcategory + " "+categoryid + " " + latitude + " " + longitude,Toast.LENGTH_LONG).show();
        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_EDITPRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), "Sửa sản phẩm " + response, Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                param.put("productname", newproductname);
                param.put("price", newproductprice);
                //param.put("userid", userid);
                param.put("categoryid", categoryid);
                param.put("productaddress", newproductaddress);
                // param.put("areaproduct", areaproduct);
                //param.put("productstatus",productstatus);
                //param.put("productimage",productimage[1]+","+productimage[3]);
                param.put("description", newproductdesc);
                param.put("lot", longitude + "");
                param.put("lat", latitude + "");
                param.put("productid", id);

                return param;

            }

        };

        requestQueue.add(request);
    }

    @OnClick(R.id.btnDelete)
    public void onDelete() {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle(R.string.confirm_delete);

        // Setting Dialog Message
        alertDialog2.setMessage(R.string.delete_detail);

        // Setting Icon to Dialog
        alertDialog2.setIcon(R.drawable.button_videos);

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton(R.string.btn_yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
//                                               Toast.makeText(getActivity(),
//                                                              "You clicked on YES", Toast.LENGTH_SHORT)
//                                                       .show();
                        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_DELETEPRODUCT, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Delete", "Delete Response: " + response);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> param = new HashMap<>();

                                param.put("productid", id);
                                param.put("productimage1", productimage1);
                                param.put("productimage2", productimage2);
                                param.put("productimage3", productimage3);
                                param.put("productimage4", productimage4);

                                return param;

                            }

                        };
                        VolleySingleton.getInstance(getActivity()).requestQueue.add(request);
                        getActivity().finish();
                    }
                });

        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton(R.string.btn_no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
//                                               Toast.makeText(getActivity(),
//                                                              "You clicked on NO", Toast.LENGTH_SHORT)
//                                                       .show();
//                                               dialog.cancel();
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();
        //Toast.makeText(getActivity(),"Delete",Toast.LENGTH_SHORT).show();
//        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_DELETEPRODUCT, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
////                Log.d("Delete", "Delete Response: " + response.toString());
////                Toast.makeText(getActivity(), "Xóa sản phẩm "+response.toString(), Toast.LENGTH_SHORT).show();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> param = new HashMap<String, String>();
//
//                param.put("productid",id);
//                return  param;
//
//            }
//
//        };
//        VolleySingleton.getInstance(getActivity()).requestQueue.add(request);
//        getActivity().finish();
    }

//    @OnClick(R.id.comments_see_all)
//    public void onComment() {
//        Intent intent = new Intent(getActivity(), CommentActivity.class);
//
//        intent.putExtra(ViMarket.COMMENT_TYPE, ViMarket.COMMENT_TYPE_CAST);
//        intent.putExtra(ViMarket.product_NAME, product.productname);
//        intent.putExtra(ViMarket.product_ID, product.productid);
//        intent.putExtra(ViMarket.COMMENT_LIST, commentslist);
//        startActivityForResult(intent, 1);
//    }

    /*   @OnClick(R.id.product_crew_see_all)
       public void onSeeAllCrewClicked() {
           Intent intent = new Intent(getActivity(), CreditActivity.class);
           intent.putExtra(ViMarket.CREDIT_TYPE, ViMarket.CREDIT_TYPE_CREW);
           intent.putExtra(ViMarket.product_NAME, product.title);
           intent.putExtra(ViMarket.CREDIT_LIST, product.crew);
           startActivity(intent);
       }
       @OnClick(R.id.product_cast_see_all)
       public void onSeeAllCastClicked() {
           Intent intent = new Intent(getActivity(), CreditActivity.class);
           intent.putExtra(ViMarket.CREDIT_TYPE, ViMarket.CREDIT_TYPE_CAST);
           intent.putExtra(ViMarket.product_NAME, product.title);
           intent.putExtra(ViMarket.CREDIT_LIST, product.cast);
           startActivity(intent);
       }

   */
    // FAB related functions
//    @OnClick(R.id.fab_call)
//    public void onWatchedButtonClicked() {
//        if (ContextCompat.checkSelfPermission(getActivity(),
//                                              android.Manifest.permission.CALL_PHONE)
//                != PackageManager.PERMISSION_GRANTED) {
//            if
//                    (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                                                                         android.Manifest.permission.CALL_PHONE)) {
//                Toast.makeText(getActivity(), "Its Necessary To Call", Toast.LENGTH_LONG).show();
//                ActivityCompat.requestPermissions(getActivity(),
//                                                  new String[]{Manifest.permission.CALL_PHONE},
//                                                  MY_PERMISSIONS_REQUEST_CALL_PHONE);
//            }
//            else {
//                ActivityCompat.requestPermissions(getActivity(),
//                                                  new String[]{Manifest.permission.CALL_PHONE},
//                                                  MY_PERMISSIONS_REQUEST_CALL_PHONE);
//            }
//        }
//        else {
//            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + seller.getPhone()));
//            startActivity(intent);
//        }
////    }
//
//    @OnClick(R.id.fab_sms)
//    public void onToWatchButtonClicked() {
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mDemoSlider.stopAutoCycle();
//        unbinder.unbind();

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
            }
        }
//        if (resultCode == RESULT_OK) {
//            if (requestCode == GALLERY_REQUEST) {
//                Uri uri = data.getData();
//
//                galleryPhoto.setPhotoUri(uri);
//                String photoPath = galleryPhoto.getPath();
//                selectedPhoto = photoPath;
//                try {
//                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
//                    posterImage1.setImageBitmap(getRotatedBitmap(bitmap));
//                } catch (FileNotFoundException e) {
//                    Toast.makeText(getActivity(),
//                            "Something Wrong while choosing photos", Toast.LENGTH_SHORT).show();
//                }
//            } else if (requestCode == GALLERY_REQUEST2) {
//                Uri uri = data.getData();
//
//                galleryPhoto.setPhotoUri(uri);
//                String photoPath2 = galleryPhoto.getPath();
//                selectedPhoto2 = photoPath2;
//                try {
//                    Bitmap bitmap2 = ImageLoader.init().from(photoPath2).requestSize(512, 512).getBitmap();
//                    posterImage2.setImageBitmap(getRotatedBitmap(bitmap2));
//                } catch (FileNotFoundException e) {
//                    Toast.makeText(getActivity(),
//                            "Something Wrong while choosing photos", Toast.LENGTH_SHORT).show();
//                }
//            } else if (requestCode == GALLERY_REQUEST3) {
//                Uri uri = data.getData();
//
//                galleryPhoto.setPhotoUri(uri);
//                String photoPath3 = galleryPhoto.getPath();
//                selectedPhoto3 = photoPath3;
//                try {
//                    Bitmap bitmap3 = ImageLoader.init().from(photoPath3).requestSize(512, 512).getBitmap();
//                    posterImage3.setImageBitmap(getRotatedBitmap(bitmap3));
//                } catch (FileNotFoundException e) {
//                    Toast.makeText(getActivity(),
//                            "Something Wrong while choosing photos", Toast.LENGTH_SHORT).show();
//                }
//            } else if (requestCode == GALLERY_REQUEST4) {
//                Uri uri = data.getData();
//
//                galleryPhoto.setPhotoUri(uri);
//                String photoPath4 = galleryPhoto.getPath();
//                selectedPhoto4 = photoPath4;
//                try {
//                    Bitmap bitmap3 = ImageLoader.init().from(photoPath4).requestSize(512, 512).getBitmap();
//                    posterImage4.setImageBitmap(getRotatedBitmap(bitmap3));
//                } catch (FileNotFoundException e) {
//                    Toast.makeText(getActivity(),
//                            "Something Wrong while choosing photos", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
    }

    private Bitmap getRotatedBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) 90);
        return Bitmap.createBitmap(source,
                                   0, 0, source.getWidth(), source.getHeight(), matrix, true);
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

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver() {
            super(null);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        assert address != null;
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                        Log.e("ABC", latitude + " " + longitude);
//                        Toast.makeText(getActivity(), latitude + " " + longitude ,Toast.LENGTH_SHORT).show();

                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), GeocodeAddressIntentService.class);
                        intent.putExtra(Constants.RECEIVER, mResultReceiver);
                        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
                        if (fetchType == Constants.USE_ADDRESS_NAME) {
                            intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, product.areaproduct);
                        }

                        Log.e("EDIT ADDRESS", "Starting Service");
                        getActivity().startService(intent);
                        //                        Toast.makeText(getApplicationContext(),resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
                        //                        infoText.setText(resultData.getString(Constants.RESULT_DATA_KEY));

                    }
                });
            }
        }


    }


}
