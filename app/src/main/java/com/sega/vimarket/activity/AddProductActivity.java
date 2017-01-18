package com.sega.vimarket.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.sega.vimarket.Animations.DescriptionAnimation;
import com.sega.vimarket.R;
import com.sega.vimarket.SliderLayout;
import com.sega.vimarket.SliderTypes.BaseSliderView;
import com.sega.vimarket.SliderTypes.TextSliderView;
import com.sega.vimarket.Tricks.ViewPagerEx;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.model.Image;
import com.sega.vimarket.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.drawable.ic_menu_camera;

/**
 * Created by Sega on 09/01/2017.
 */


public class AddProductActivity extends CActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int PLACE_PICKER_REQUEST = 3;
    private static final String TAG = "MAIN_ACTIVITY";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    final int GALLERY_REQUEST = 22131;
    final int GALLERY_REQUEST2 = 23312;
    final int GALLERY_REQUEST3 = 23313;


    //////////////////////////////////////////////////
//    AddressResultReceiver mResultReceiver;
    final int GALLERY_REQUEST4 = 23314;
    ImageView ivImage, ivImage2, ivImage3, ivImage4;
    LinearLayout ivGallery;
    GalleryPhoto galleryPhoto;
    String selectedPhoto, selectedPhoto2, selectedPhoto3, selectedPhoto4;
    EditText ten,gia,addressEdit,des,edtcategory,edtarea;
    AlertDialog.Builder Categorybuilder,Areabuilder;
    AlertDialog CategoryDialog,AreaDialog ;
    RequestQueue requestQueue;
    String productname;
    String price;
    String userid ;
    String categoryid;
    String productaddress ;
    String areaproduct;
    String productstatus ;
    String description;
    String[] productimage;
    String lat,lot,add;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    SliderLayout slide;
    boolean fetchAddress;
    int fetchType = Constants.USE_ADDRESS_LOCATION;
    SessionManager session;
    private ArrayList<Image> images = new ArrayList<>();
    private int REQUEST_CODE_PICKER = 2000;

    public static Bitmap getReducedBitmap(String imgPath, int MaxWidth, long MaxFileSize) {
        // MaxWidth = maximum image width
        // MaxFileSize = maximum image file size
        File file = new File(imgPath);
        long length = file.length();
        if (length == 0) {
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // read image file
            BitmapFactory.decodeFile(imgPath, options);
            int srcWidth = options.outWidth;
            int scale = 1;
            while ((srcWidth > MaxWidth) || (length > MaxFileSize)) {
                srcWidth /= 2;
                scale *= 2;
                length = length / 2;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // read again image file with new constraints
            return (BitmapFactory.decodeFile(imgPath, options));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addproduct);
        ButterKnife.bind(this);
        session = new SessionManager(this);
        ivImage = (ImageView)findViewById(R.id.ivImage);
        ivImage2 = (ImageView)findViewById(R.id.ivImage2);
        ivImage3 = (ImageView)findViewById(R.id.ivImage3);
        ivImage4 = (ImageView)findViewById(R.id.ivImage4);

        ivGallery = (LinearLayout)findViewById(R.id.ivGallery);
        //        ivGallery2 = (ImageView)findViewById(R.id.ivGallery2);
        //        ivGallery3 = (ImageView)findViewById(R.id.ivGallery3);
        //        ivGallery4 = (ImageView)findViewById(R.id.ivGallery4);
        ten = (EditText) findViewById(R.id.productname);
        gia = (EditText) findViewById(R.id.price);
        //        user = (EditText) findViewById(R.id.userid);
        edtcategory = (EditText) findViewById(  R.id.categoryid);
        slide = (SliderLayout)findViewById(R.id.slider);


        addressEdit = (EditText) findViewById(R.id.addressEdit);

        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.action_home));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle(R.string.btn_sell);


        verifyStoragePermissions(this);
        Button button = (Button)findViewById(R.id.button12345);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClicked(v);
            }
        });
    }

    public void start() {
        ImagePicker.create(this)
                .folderMode(true) // set folder mode (false by default)
                .folderTitle("Folder") // folder selection title
                .imageTitle("Tap to select") // image selection title
                .single() // single mode
                .multi() // multi mode (default mode)
                .limit(4) // max images can be selected (999 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                .origin(images) // original selected images, used in multi mode
                .start(REQUEST_CODE_PICKER); // start image picker activity with request code
    }

    public void init(){
        galleryPhoto = new GalleryPhoto(this);






        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
        ////////////////////////////////////////////////////////////////////////////




        final CharSequence[] itemscategory = getResources().getStringArray(R.array.danhmuc);
        Categorybuilder = new AlertDialog.Builder(AddProductActivity.this);
        Categorybuilder.setTitle(getResources().getString(R.string.category))
                .setSingleChoiceItems(itemscategory, 1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int item) {
                        Toast.makeText(getApplicationContext(), itemscategory[item], Toast.LENGTH_SHORT).show();
                        edtcategory.setText(itemscategory[item]);
                        switch (item) {
                            case 0:
                                categoryid = "1";
                                break;
                            case 1:
                                categoryid = "2";
                                break;
                            case 2:
                                categoryid = "3";
                                break;
                            case 3:
                                categoryid = "4";
                                break;
                            case 4:
                                categoryid = "5";
                                break;
                            case 5:
                                categoryid = "6";
                                break;
                            case 6:
                                categoryid = "7";
                                break;
                            case 7:
                                categoryid = "8";
                                break;
                            case 8:
                                categoryid = "9";
                                break;

                        }
                        CategoryDialog.dismiss();

                    }
                });
        CategoryDialog = Categorybuilder.create();
        final CharSequence[] itemsarea = getResources().getStringArray(R.array.area);
        Areabuilder = new AlertDialog.Builder(AddProductActivity.this);
        Areabuilder.setTitle(getResources().getString(R.string.edtarea))
                .setSingleChoiceItems(itemsarea, 1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int item) {
                        Toast.makeText(getApplicationContext(), itemsarea[item], Toast.LENGTH_SHORT).show();
                        edtarea.setText(itemsarea[item]);
                        areaproduct = itemsarea[item].toString();
                        AreaDialog.dismiss();

                    }
                });
        AreaDialog = Areabuilder.create();
        edtcategory.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = edtcategory.getInputType(); // backup the input type
                edtcategory.setInputType(InputType.TYPE_NULL); // disable soft input
                edtcategory.onTouchEvent(event); // call native handler
                edtcategory.setInputType(inType); // restore input type
                CategoryDialog.show();

                return true; // consume touch even
            }
        });
        addressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationPlacesIntent();
            }
        });
//        addressEdit.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                int inType = addressEdit.getInputType(); // backup the input type
//                addressEdit.setInputType(InputType.TYPE_NULL); // disable soft input
//                addressEdit.onTouchEvent(motionEvent); // call native handler
//                addressEdit.setInputType(inType); // restore input type
//                locationPlacesIntent();
//
//                return true;
//            }
//        });
        edtarea = (EditText) findViewById(R.id.productarea);
        edtarea.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = edtarea.getInputType(); // backup the input type
                edtarea.setInputType(InputType.TYPE_NULL); // disable soft input
                edtarea.onTouchEvent(event); // call native handler
                edtarea.setInputType(inType); // restore input type
                AreaDialog.show();

                return true; // consume touch even
            }
        });

        TextView currency = (TextView)findViewById(R.id.currency);
        currency.setText("VND");

        des = (EditText) findViewById(R.id.description);


//        mResultReceiver = new AddressResultReceiver(null);

//        fetchAddress = false;
//        fetchType = Constants.USE_ADDRESS_NAME;
//        addressEdit.setFocusable(true);
//        addressEdit.setFocusableInTouchMode(true);


    }
       /* public static Bitmap getReducedBitmap(String imagePath)
        {
             final float maxHeight = 1280.0f;
              final float maxWidth = 1280.0f;
           *//* // MaxWidth = maximum image width
            // MaxFileSize = maximum image file size
            File file = new File(imgPath);
            long length = file.length();
            if (length == 0)
                return null ;
            try
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                // read image file
                BitmapFactory.decodeFile(imgPath, options);
                int srcWidth = options.outWidth;
                int srcHeight = options.outHeight;
                int scale = 1;
                int width = MaxWidth ;
                while ((srcWidth > width) || (length > MaxFileSize))
                {
                    srcWidth /= 2;
                    srcHeight /= 2;
                    scale *= 2;
                    length = length / 2 ;
                }
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inSampleSize = scale;
                options.inScaled = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                // read again image file with new constraints
                return(BitmapFactory.decodeFile(imgPath, options)) ;
            }
            catch (Exception e)
            {
                return null ;
            }*//*
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float imgRatio = (float) actualWidth / (float) actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(imagePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            ExifInterface exif;
            try {
                exif = new ExifInterface(imagePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().length);
        }*/

    public void onButtonClicked(View view) {

        productname = ten.getText().toString();
        price = gia.getText().toString();
        productaddress = addressEdit.getText().toString();
        description = des.getText().toString();
        //   areaproduct = productarea.getSelectedItem().toString();
        //        if (productname == "" || price == "" || productaddress == "" || description == ""){
        //            Toast.makeText(getApplicationContext(),"THIẾU",Toast.LENGTH_LONG).show();
        //        }
        //        else {
        //        }
        ///////////////////////////////////////IMAGE/////////////////////////////////
        if      (selectedPhoto == null || selectedPhoto.equals("")
                || selectedPhoto2 == null || selectedPhoto2.equals("")
                || selectedPhoto3 == null || selectedPhoto3.equals("")
                || selectedPhoto4 == null || selectedPhoto4.equals("")){
            Toast.makeText(getApplicationContext(),R.string.toast_hinh, Toast.LENGTH_SHORT).show();
            return;}
        else if (productname.equals("") || price.equals("") || productaddress.equals("") || description.equals("")){
            Toast.makeText(getApplicationContext(),R.string.toast_thongtin, Toast.LENGTH_SHORT).show();
            return;
        }


        Bitmap bitmap = getReducedBitmap(selectedPhoto, 1024, 600000);
        Bitmap bitmap2 = getReducedBitmap(selectedPhoto2,1024,600000);
        Bitmap bitmap3 = getReducedBitmap(selectedPhoto3,1024,600000);
        Bitmap bitmap4 = getReducedBitmap(selectedPhoto4,1024,600000);

        assert bitmap != null;
        String encodedImage = ImageBase64.encode(bitmap);
        assert bitmap2 != null;
        String encodedImage2 = ImageBase64.encode(bitmap2);
        assert bitmap3 != null;
        String encodedImage3 = ImageBase64.encode(bitmap3);
        assert bitmap4 != null;
        String encodedImage4 = ImageBase64.encode(bitmap4);


        //            Log.d(TAG2, encodedImage);
        //            Log.d(TAG2, encodedImage2);


        HashMap<String, String> postData = new HashMap<>();
        postData.put("image", encodedImage);
        postData.put("image2", encodedImage2);
        postData.put("image3", encodedImage3);
        postData.put("image4", encodedImage4);

        PostResponseAsyncTask task = new PostResponseAsyncTask(AddProductActivity.this, postData, new AsyncResponse() {

            @Override
            public void processFinish(String s) {
                Log.e("Image", s);
                //                    Toast.makeText(getApplicationContext(),s.substring(1,60),Toast.LENGTH_LONG).show();
                //                    Log.e(TAG,s.substring(1,60));
                //                  productimage = s.substring(1, s.length() - 1);
                productimage = s.split(String.valueOf('"'));
                //                    Log.d(TAG,productimage[1] + " va " + productimage[3]);
                productname = ten.getText().toString();
                price = gia.getText().toString();
                userid = session.getLoginId()+"";
                // cateid = category.getSelectedItem().toString();


                productaddress = addressEdit.getText().toString();
                productstatus = "1";
                description = des.getText().toString();
                final String latlot = productaddress + " " + areaproduct;
                requestQueue = Volley.newRequestQueue(getApplicationContext());
//                Intent intent = new Intent(getApplicationContext(), GeocodeAddressIntentService.class);
//                intent.putExtra(Constants.RECEIVER, mResultReceiver);
//                intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
//                if (fetchType == Constants.USE_ADDRESS_NAME) {
//                    intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, latlot);
//                }
//
//                Log.e(TAG, "Starting Service");
//                startService(intent);
                //                    Log.e(TAG, productname + " " + price + " " + userid + " " + categoryid + " " + productaddress + " " + areaproduct + " " +
                //                            producttype + " " + productstatus + " " + productimage[0] + " "+ productimage[1] +" "+ description + " " + latitude + " " + longitude);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_ADDPRODUCT, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (!json.getBoolean("error")) {
                                Intent intent = new Intent(AddProductActivity.this, ProductDetailActivity.class);
                                intent.putExtra(ViMarket.product_ID, String.valueOf(json.getString("productid")));
                                intent.putExtra(ViMarket.user_ID, String.valueOf(json.getString("userid")));
                                startActivity(intent);
                                Log.e("ADD", response);
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),R.string.toast_error,Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<>();

                        param.put("productname", productname);
                        param.put("price", price);
                        param.put("userid", userid);
                        param.put("categoryid", categoryid);
                        param.put("productaddress", productaddress);
                        param.put("areaproduct", areaproduct);
                        param.put("productstatus",productstatus);
                        param.put("productimage",productimage[1]+","+productimage[3]+","+productimage[5]+","+productimage[7]);
                        param.put("description",description);
                        param.put("lot",lat+"");
                        param.put("lat",lot+"");

                        return  param;

                    }

                };

                requestQueue.add(request);
            }
        });

        task.execute(AppConfig.URL_IMAGE);

        task.setEachExceptionsHandler(new EachExceptionsHandler() {
            @Override
            public void handleIOException(IOException e) {
                Toast.makeText(getApplicationContext(),R.string.connect,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handleMalformedURLException(MalformedURLException e) {
                Toast.makeText(getApplicationContext(), "URL Error.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handleProtocolException(ProtocolException e) {
                Toast.makeText(getApplicationContext(), "Protocol Error.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                Toast.makeText(getApplicationContext(), "Encoding Error.",
                        Toast.LENGTH_SHORT).show();
            }
        });


        /////////////////////////////////////////////////////////////////////////////////




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place!=null){
                    LatLng latLng = place.getLatLng();
                    lot = String.valueOf(latLng.latitude);
                    lat = String.valueOf(latLng.longitude);
                    add = (String) place.getAddress();
                    addressEdit.setText(add);
//                    MapModel mapModel = new MapModel(latLng.latitude+"", latLng.longitude+"");
                    Log.e("LatLng",lat + " " + lot +" "+add);

//                    ChatModel chatModel = new ChatModel(userModel, Calendar.getInstance().getTime().getTime()+"", mapModel);
//                    mFirebaseDatabaseReference.child(room).push().setValue(chatModel);
                }
            }
        }
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

            for (int i = 0, l = images.size(); i < l; i++) {
                if(i==0)
                {
                    selectedPhoto = images.get(0).getPath();
                    Bitmap bitmap = getReducedBitmap(selectedPhoto,256,200000);
                    ivImage.setImageBitmap(bitmap);
                    ivImage2.setImageResource(ic_menu_camera);
                    ivImage3.setImageResource(ic_menu_camera);
                    ivImage4.setImageResource(ic_menu_camera);
                }
                else if(i==1){
                    selectedPhoto2 = images.get(1).getPath();
                    Bitmap bitmap = getReducedBitmap(selectedPhoto2,256,200000);
                    ivImage2.setImageBitmap(bitmap);
                    ivImage3.setImageResource(ic_menu_camera);
                    ivImage4.setImageResource(ic_menu_camera);
                }   else if(i==2){
                    selectedPhoto3 = images.get(2).getPath();
                    Bitmap bitmap = getReducedBitmap(selectedPhoto3,256,200000);
                    ivImage3.setImageBitmap(bitmap);
                    ivImage4.setImageResource(ic_menu_camera);
                }
                else if(i==3){
                    selectedPhoto4 = images.get(3).getPath();
                    Bitmap bitmap = getReducedBitmap(selectedPhoto4,256,200000);
                    ivImage4.setImageBitmap(bitmap);
                }

            }


        }
        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQUEST){
                Uri uri = data != null ? data.getData() : null;

                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                selectedPhoto = photoPath;
                Bitmap bitmap = getReducedBitmap(photoPath,256,200000);
                ivImage.setImageBitmap(bitmap);
            }
            else if(requestCode == GALLERY_REQUEST2)
            {
                assert data != null;
                Uri uri = data.getData();

                galleryPhoto.setPhotoUri(uri);
                String photoPath2 = galleryPhoto.getPath();
                selectedPhoto2 = photoPath2;
                Bitmap bitmap2 =getReducedBitmap(photoPath2,256,200000);
                ivImage2.setImageBitmap(bitmap2);
            }
            else if(requestCode == GALLERY_REQUEST3)
            {
                assert data != null;
                Uri uri = data.getData();

                galleryPhoto.setPhotoUri(uri);
                String photoPath3 = galleryPhoto.getPath();
                selectedPhoto3 = photoPath3;
                Bitmap bitmap3 = getReducedBitmap(photoPath3,256,200000);
                ivImage3.setImageBitmap(bitmap3);
            }
            else if(requestCode == GALLERY_REQUEST4)
            {
                assert data != null;
                Uri uri = data.getData();

                galleryPhoto.setPhotoUri(uri);
                String photoPath4 = galleryPhoto.getPath();
                selectedPhoto4= photoPath4;
                Bitmap bitmap4 = getReducedBitmap(photoPath4,256,200000);
                ivImage4.setImageBitmap(bitmap4);
            }
        }
        showAnimationBanner();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void showAnimationBanner() {
        slide.removeAllSliders();
        for (int i = 0; i < images.size(); i++) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .image(Uri.fromFile(new File(images.get(i).getPath())).toString())
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putInt("index", i);

            slide.addSlider(textSliderView);

        }
        slide.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slide.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slide.setCustomAnimation(new DescriptionAnimation());
        slide.setDuration(4000);
        slide.addOnPageChangeListener(this);
    }

    ///////////////////////////////////////////
    private void locationPlacesIntent(){

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    //    @SuppressLint("ParcelCreator")
//    class AddressResultReceiver extends ResultReceiver {
//        AddressResultReceiver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        protected void onReceiveResult(int resultCode, final Bundle resultData) {
//            if (resultCode == Constants.SUCCESS_RESULT) {
//                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        assert address != null;
//                        latitude = address.getLatitude();
//                        longitude = address.getLongitude();
//                        //Toast.makeText(getApplicationContext(), latitude + " " + longitude ,Toast.LENGTH_SHORT).show();
//
////                        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_ADDPRODUCT, new Response.Listener<String>() {
////                            @Override
////                            public void onResponse(String response) {
////                                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
////                                finish();
////                            }
////                        }, new Response.ErrorListener() {
////                            @Override
////                            public void onErrorResponse(VolleyError error) {
////                                Toast.makeText(getApplicationContext(),"Đã xảy ra lỗi, vui lòng thử lại sau!",Toast.LENGTH_LONG).show();
////                            }
////                        }){
////                            @Override
////                            protected Map<String, String> getParams() throws AuthFailureError {
////                                Map<String, String> param = new HashMap<>();
////
////                                param.put("productname", productname);
////                                param.put("price", price);
////                                param.put("userid", userid);
////                                param.put("categoryid", categoryid);
////                                param.put("productaddress", productaddress);
////                                param.put("areaproduct", areaproduct);
////                                param.put("productstatus",productstatus);
////                                param.put("productimage",productimage[1]+","+productimage[3]+","+productimage[5]+","+productimage[7]);
////                                param.put("description",description);
////                                param.put("lot",lat+"");
////                                param.put("lat",lot+"");
////
////                                return  param;
////
////                            }
////
////                        };
////
////                        requestQueue.add(request);
//
//                    }
//                });
//            }
//            else {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(getApplicationContext(), GeocodeAddressIntentService.class);
//                        intent.putExtra(Constants.RECEIVER, mResultReceiver);
//                        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
//                        if (fetchType == Constants.USE_ADDRESS_NAME) {
//                            intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, areaproduct);
//                        }
//
//                        Log.e(TAG, "Starting Service");
//                        startService(intent);
//                        //                        Toast.makeText(getApplicationContext(),resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
//                        //                        infoText.setText(resultData.getString(Constants.RESULT_DATA_KEY));
//
//                    }
//                });
//            }
//        }
//    }
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        else
        {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    init();
                }
        }
    }
}