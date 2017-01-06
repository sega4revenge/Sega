/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.sega.vimarket.addproduct;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.sega.vimarket.R;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.fragment.ProductDrawerFragment;

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

/**a
 * Created by Sega on 15/08/2016.
 */
public class AddProduct extends AppCompatActivity {

    ImageView  ivImage,ivImage2,ivImage3,ivImage4;
LinearLayout ivGallery;

    GalleryPhoto galleryPhoto;

    final int GALLERY_REQUEST = 22131;
    final int GALLERY_REQUEST2 = 23312;
    final int GALLERY_REQUEST3 = 23313;
    final int GALLERY_REQUEST4 = 23314;


    String selectedPhoto,selectedPhoto2,selectedPhoto3,selectedPhoto4;


    //////////////////////////////////////////////////
    AddressResultReceiver mResultReceiver;

    EditText ten,gia,addressEdit,des,edtcategory,edtarea,edttype;


    double latitude,longitude;
    AlertDialog.Builder Categorybuilder,Typebuilder,Areabuilder;
    AlertDialog CategoryDialog,TypeDialog,AreaDialog ;
    RequestQueue requestQueue;
    String productname;
    String price;
    String userid ;
    String categoryid;
    String productaddress ;
    String areaproduct;
    String producttype;
    String productstatus ;
    String description;
    String[] productimage;
    private ArrayList<Image> images = new ArrayList<>();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private int REQUEST_CODE_PICKER = 2000;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    boolean fetchAddress;
    int fetchType = Constants.USE_ADDRESS_LOCATION;

    private static final String TAG = "MAIN_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addproduct);
        ButterKnife.bind(this);

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
        Categorybuilder = new AlertDialog.Builder(AddProduct.this);
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
        Areabuilder = new AlertDialog.Builder(AddProduct.this);
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
        final CharSequence[] itemstype = getResources().getStringArray(R.array.type);
       Typebuilder = new AlertDialog.Builder(AddProduct.this);
        Typebuilder.setTitle(getResources().getString(R.string.type))
                .setSingleChoiceItems(itemstype, 1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int item) {
                        Toast.makeText(getApplicationContext(), itemstype[item], Toast.LENGTH_SHORT).show();
                        edttype.setText(itemstype[item]);
                        producttype = itemstype[item].toString();
                        TypeDialog.dismiss();

                    }
                });
       TypeDialog = Typebuilder.create();
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
        edttype = (EditText) findViewById(R.id.producttype);
        edttype.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = edttype.getInputType(); // backup the input type
                edttype.setInputType(InputType.TYPE_NULL); // disable soft input
                edttype.onTouchEvent(event); // call native handler
                edttype.setInputType(inType); // restore input type




                TypeDialog.show();

                return true; // consume touch even
            }
        });

        des = (EditText) findViewById(R.id.description);


        mResultReceiver = new AddressResultReceiver(null);

        fetchAddress = false;
        fetchType = Constants.USE_ADDRESS_NAME;
        addressEdit.requestFocus();

    }
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
            Toast.makeText(getApplicationContext(), "Vui lòng chọn đủ hình.", Toast.LENGTH_SHORT).show();
            return;}
        else if (productname.equals("") || price.equals("") || productaddress.equals("") || description.equals("")){
            Toast.makeText(getApplicationContext(), "Vui lòng điển đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }


        Bitmap bitmap = getReducedBitmap(selectedPhoto,1024,600000);
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

        PostResponseAsyncTask task = new PostResponseAsyncTask(AddProduct.this, postData, new AsyncResponse() {

            @Override
            public void processFinish(String s) {
                Log.e("Image",s);
                //                    Toast.makeText(getApplicationContext(),s.substring(1,60),Toast.LENGTH_LONG).show();
                //                    Log.e(TAG,s.substring(1,60));
//                  productimage = s.substring(1, s.length() - 1);
                productimage = s.split(String.valueOf('"'));
//                    Log.d(TAG,productimage[1] + " va " + productimage[3]);
                productname = ten.getText().toString();
                price = gia.getText().toString();
                userid = ProductDrawerFragment.userobj.userid + "";
                // cateid = category.getSelectedItem().toString();


                productaddress = addressEdit.getText().toString();
                productstatus = "Đang bán";
                description = des.getText().toString();
                final String latlot = productaddress + " " + areaproduct;


                requestQueue = Volley.newRequestQueue(getApplicationContext());


                Intent intent = new Intent(getApplicationContext(), GeocodeAddressIntentService.class);
                intent.putExtra(Constants.RECEIVER, mResultReceiver);
                intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
                if (fetchType == Constants.USE_ADDRESS_NAME) {
                    intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, latlot);
                }

                Log.e(TAG, "Starting Service");
                startService(intent);
//                    Log.e(TAG, productname + " " + price + " " + userid + " " + categoryid + " " + productaddress + " " + areaproduct + " " +
//                            producttype + " " + productstatus + " " + productimage[0] + " "+ productimage[1] +" "+ description + " " + latitude + " " + longitude);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();


            }
        });

        task.execute(AppConfig.URL_IMAGE);

        task.setEachExceptionsHandler(new EachExceptionsHandler() {
            @Override
            public void handleIOException(IOException e) {
                Toast.makeText(getApplicationContext(), "Cannot Connect to Server.",
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

            for (int i = 0, l = images.size(); i < l; i++) {
                if(i==0)
                {
                    selectedPhoto = images.get(0).getPath();
                    Bitmap bitmap = getReducedBitmap(selectedPhoto,256,200000);
                    ivImage.setImageBitmap(bitmap);
                }
                else if(i==1){
                    selectedPhoto2 = images.get(1).getPath();
                    Bitmap bitmap = getReducedBitmap(selectedPhoto2,256,200000);
                    ivImage2.setImageBitmap(bitmap);
                }   else if(i==2){
                    selectedPhoto3 = images.get(2).getPath();
                    Bitmap bitmap = getReducedBitmap(selectedPhoto3,256,200000);
                    ivImage3.setImageBitmap(bitmap);
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
    }


    ///////////////////////////////////////////

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        assert address != null;
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                        //Toast.makeText(getApplicationContext(), latitude + " " + longitude ,Toast.LENGTH_SHORT).show();

                        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_ADDPRODUCT, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),"Đã xảy ra lỗi, vui lòng thử lại sau!",Toast.LENGTH_LONG).show();
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
                                param.put("producttype", producttype);
                                param.put("productstatus",productstatus);
                                param.put("productimage",productimage[1]+","+productimage[3]+","+productimage[5]+","+productimage[7]);
                                param.put("description",description);
                                param.put("lot",longitude+"");
                                param.put("lat",latitude+"");

                                return  param;

                            }

                        };

                        requestQueue.add(request);

                    }
                });
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), GeocodeAddressIntentService.class);
                        intent.putExtra(Constants.RECEIVER, mResultReceiver);
                        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
                        if (fetchType == Constants.USE_ADDRESS_NAME) {
                            intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, areaproduct);
                        }

                        Log.e(TAG, "Starting Service");
                        startService(intent);
                        //                        Toast.makeText(getApplicationContext(),resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
                        //                        infoText.setText(resultData.getString(Constants.RESULT_DATA_KEY));

                    }
                });
            }
        }
    }
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


    public static Bitmap getReducedBitmap(String imgPath, int MaxWidth, long MaxFileSize)
    {
        // MaxWidth = maximum image width
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
            int scale = 1;
            while ((srcWidth > MaxWidth) || (length > MaxFileSize))
            {
                srcWidth /= 2;
                scale *= 2;
                length = length / 2 ;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // read again image file with new constraints
            return(BitmapFactory.decodeFile(imgPath, options)) ;
        }
        catch (Exception e)
        {
            return null ;
        }
    }
}
