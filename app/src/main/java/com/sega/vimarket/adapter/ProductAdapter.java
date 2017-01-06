package com.sega.vimarket.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sega.vimarket.model.Product;
import com.sega.vimarket.model.Utils;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.service.GPSTracker;
import com.sega.vimarket.util.TextUtils;
import com.sega.vimarket.widget.AutoResizeTextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int imageWidth;
    private NumberFormat format;
    private SharedPreferences sharedPref;
    public ArrayList<Product> productList;
    private final OnproductClickListener onproductClickListener;

    // Constructor
    public ProductAdapter(Context context, OnproductClickListener onproductClickListener) {
        this.context = context;
        this.productList = new ArrayList<>();
        this.onproductClickListener = onproductClickListener;
        sharedPref = context.getSharedPreferences(ViMarket.TABLE_USER, Context.MODE_PRIVATE);
        imageWidth = sharedPref.getInt(ViMarket.THUMBNAIL_SIZE,
                                       0);   // Load image width for grid view
        Locale current = context.getResources().getConfiguration().locale;
        if (current.getCountry().equals("VN")) {
            format = NumberFormat.getCurrencyInstance();
        }
        else {
            format = NumberFormat.getCurrencyInstance(Locale.US);
        }
    }

    // RecyclerView methods
    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (sharedPref.getInt(ViMarket.VIEW_MODE, ViMarket.VIEW_MODE_GRID));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ViMarket.VIEW_MODE_GRID) {
            // GRID MODE
            final ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_product_grid, parent, false);
            ViewTreeObserver viewTreeObserver = v.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                // Update width integer and save to storage for next use
                                int width = v.findViewById(R.id.product_poster).getWidth();
                                if (width > imageWidth) {
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putInt(ViMarket.THUMBNAIL_SIZE, width);
                                    editor.apply();
                                }
                                // Unregister LayoutListener
                                if (Build.VERSION.SDK_INT >= 16) {
                                    v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                                else {
                                    v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                }
                            }
                        });
            }
            return new ProductGridViewHolder(v, onproductClickListener);
        }
        else if (viewType == ViMarket.VIEW_MODE_LIST) {
            // LIST MODE
            ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_product_list, parent, false);
            return new ProductListViewHolder(v, onproductClickListener);
        }
        else {
            // COMPACT MODE
            ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_product_compact, parent, false);
            return new ProductCompactViewHolder(v, onproductClickListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(0);
        Product product = productList.get(position);
        if (viewType == ViMarket.VIEW_MODE_GRID) {
            // GRID MODE


            ProductGridViewHolder productViewHolder = (ProductGridViewHolder) viewHolder;


            productViewHolder.price.setText(format.format(product.price));
            // Title and year
            productViewHolder.area.setText(product.areaproduct);
            productViewHolder.productName.setText(product.productname);
            productViewHolder.username.setText(product.username);
            productViewHolder.productRating.setText(product.sharecount);

            String distance =
                    Utils.formatDistanceBetween(GPSTracker.mLastestLocation, product.location);
            if (android.text.TextUtils.isEmpty(distance)) {
                productViewHolder.distance.setVisibility(View.GONE);
            }
            else {
                productViewHolder.distance.setVisibility(View.VISIBLE);
                productViewHolder.distance.setText(distance);
            }
            // Load image
            if (!TextUtils.isNullOrEmpty(product.productimage.get(0).toString())) {
                String imageUrl = product.productimage.get(0).toString();
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.empty_photo)
                        .dontAnimate()
                        .override(120, 120)
                        .into(productViewHolder.imageView);

                productViewHolder.imageView.setVisibility(View.VISIBLE);
                productViewHolder.defaultImageView.setVisibility(View.GONE);
            }
            else {
                productViewHolder.defaultImageView.setVisibility(View.VISIBLE);
                productViewHolder.imageView.setVisibility(View.GONE);
            }
            // Display product rating
            //            if (TextUtils.isNullOrEmpty(product.price+"") || product.categoryname.equals("0")) {
            //                productViewHolder.productRatingIcon.setVisibility(View.GONE);
            //                productViewHolder.productRating.setVisibility(View.GONE);
            //            } else {
            productViewHolder.productRatingIcon.setVisibility(View.VISIBLE);
            productViewHolder.productRating.setVisibility(View.VISIBLE);
            //                productViewHolder.productRating.setText(product.price+"");
            //            }
        }
        else if (viewType == ViMarket.VIEW_MODE_LIST) {
            // LIST MODE
            ProductListViewHolder productViewHolder = (ProductListViewHolder) viewHolder;

            productViewHolder.price.setText(format.format(product.price));
            // Title, year and overview
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(product.productdate),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            productViewHolder.productdate.setText(timeAgo);
            productViewHolder.area.setText(product.areaproduct);
            productViewHolder.productName.setText(product.productname);
            productViewHolder.username.setText(product.username);
            productViewHolder.productRating.setText(product.sharecount);
            productViewHolder.overview.setText(product.description);
            String distance =
                    Utils.formatDistanceBetween(GPSTracker.mLastestLocation, product.location);
            if (android.text.TextUtils.isEmpty(distance)) {
                productViewHolder.distance.setVisibility(View.GONE);
            }
            else {
                productViewHolder.distance.setVisibility(View.VISIBLE);
                productViewHolder.distance.setText(distance);
            }
            // Load image
            if (TextUtils.isNullOrEmpty(product.productimage.get(0).toString())) {
                productViewHolder.imageView.setVisibility(View.GONE);
                productViewHolder.defaultImageView.setVisibility(View.VISIBLE);
            }
            else {

                String imageUrl = product.productimage.get(0).toString();
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.empty_photo)
                        .dontAnimate()
                        .override(120, 120)
                        .into(productViewHolder.imageView);
                productViewHolder.imageView.setVisibility(View.VISIBLE);
                productViewHolder.defaultImageView.setVisibility(View.GONE);
            }
            // Display product rating
            //            if (TextUtils.isNullOrEmpty(product.productname) || product.productname.equals("0")) {
            //                productViewHolder.productRatingIcon.setVisibility(View.GONE);
            //                productViewHolder.productRating.setVisibility(View.GONE);
            //            } else {
            productViewHolder.productRatingIcon.setVisibility(View.VISIBLE);
            productViewHolder.productRating.setVisibility(View.VISIBLE);
            //                productViewHolder.productRating.setText(product.productname);
            //            }
        }
        else {
            // COMPACT MODE
            final ProductCompactViewHolder productViewHolder = (ProductCompactViewHolder) viewHolder;
            // Title and year
            productViewHolder.productName.setText(product.productname);
            productViewHolder.username.setText(product.username);
            productViewHolder.productRating.setText(product.sharecount);
            productViewHolder.area.setText(product.areaproduct);

            productViewHolder.price.setText(format.format(product.price));
            String distance =
                    Utils.formatDistanceBetween(GPSTracker.mLastestLocation, product.location);
            if (android.text.TextUtils.isEmpty(distance)) {
                productViewHolder.distance.setVisibility(View.GONE);
            }
            else {
                productViewHolder.distance.setVisibility(View.VISIBLE);
                productViewHolder.distance.setText(distance);
            }
            // Load image
            if (TextUtils.isNullOrEmpty(product.productimage.get(0).toString())) {
                productViewHolder.productImage.setImageResource(R.drawable.default_backdrop_circle);
            }
            else {

                String imageUrl = product.productimage.get(0).toString();

                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.empty_photo)
                        .dontAnimate()
                        .override(120, 120)
                        .into(productViewHolder.productImage);
            }
            // Display product rating
           /* if (TextUtils.isNullOrEmpty(product.areaproduct) || product.areaproduct.equals("0")) {
                productViewHolder.productRatingIcon.setVisibility(View.GONE);
                productViewHolder.productRating.setVisibility(View.GONE);
            } else {*/
            productViewHolder.productRatingIcon.setVisibility(View.VISIBLE);
            productViewHolder.productRating.setVisibility(View.VISIBLE);
                /*productViewHolder.productRating.setText(product.areaproduct);*/
        }
    }

    // ViewHolders
    class ProductGridViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_card)
        CardView cardView;
        @BindView(R.id.product_poster_default)
        ImageView defaultImageView;
        @BindView(R.id.product_poster)
       ImageView imageView;
        @BindView(R.id.product_name)
        TextView productName;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.product_rating)
        TextView productRating;
        @BindView(R.id.area)
        TextView area;
        @BindView(R.id.rating_icon)
        ImageView productRatingIcon;
        @BindView(R.id.overlaytext)
        TextView distance;
        @BindView(R.id.price)
        TextView price;

        ProductGridViewHolder(final ViewGroup itemView,
                              final OnproductClickListener onproductClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onproductClickListener.onproductClicked(getAdapterPosition());
                }
            });
        }
    }

    class ProductListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_card)
        CardView cardView;
        @BindView(R.id.product_poster_default)
        ImageView defaultImageView;
        @BindView(R.id.product_poster)
       ImageView imageView;
        @BindView(R.id.product_name)
        TextView productName;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.productdate)
        TextView productdate;
        @BindView(R.id.overlaytext)
        TextView distance;
        @BindView(R.id.product_description)
        AutoResizeTextView overview;
        @BindView(R.id.product_rating)
        TextView productRating;
        @BindView(R.id.area)
        TextView area;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.rating_icon)
        ImageView productRatingIcon;

        ProductListViewHolder(final ViewGroup itemView,
                              final OnproductClickListener onproductClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onproductClickListener.onproductClicked(getAdapterPosition());
                }
            });
        }
    }

    class ProductCompactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_item)
        View productItem;
        @BindView(R.id.product_poster_default)
        ImageView defaultImageView;
        @BindView(R.id.product_image)
       ImageView productImage;
        @BindView(R.id.product_name)
        TextView productName;
        @BindView(R.id.product_user)
        TextView username;
        @BindView(R.id.product_rating)
        TextView productRating;
        @BindView(R.id.overlaytext)
        TextView distance;
        @BindView(R.id.rating_icon)
        ImageView productRatingIcon;
        @BindView(R.id.area)
        TextView area;
        @BindView(R.id.price)
        TextView price;

        ProductCompactViewHolder(final ViewGroup itemView,
                                 final OnproductClickListener onproductClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            productItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onproductClickListener.onproductClicked(getAdapterPosition());
                }
            });
        }
    }

    // Click listener interface
    public interface OnproductClickListener {
        void onproductClicked(final int position);
    }
}