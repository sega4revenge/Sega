package com.sega.vimarket.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.sega.vimarket.R;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.widget.CircleImageView;
import com.sega.vimarket.widget.RobotoBoldTextView;
import com.sega.vimarket.widget.RobotoLightTextView;
import com.sega.vimarket.widget.RobotoRegularTextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**a
 * Created by HOHOANGLINH on 9/1/2016.
 */
public class ManagementUser extends CActivity implements Toolbar.OnMenuItemClickListener,BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    CircleImageView userpic;
    RobotoBoldTextView username;
    RobotoRegularTextView email, rate;
    RobotoLightTextView name, useremail, phone, address, area;
    GalleryPhoto galleryPhoto;
    final int GALLERY_REQUEST = 22131;
    String selectedPhoto;
    String newname, newphone, newaddress;
    RequestQueue requestQueue;
    String userimage;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    SessionManager session;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managementuser);
        ButterKnife.bind(this);
        verifyStoragePermissions(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.action_home));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        Log.e("ManageUser",ProductDrawerFragment.userobj.phone);
        toolbar.setTitle(getResources().getString(R.string.myaccount));


    }

    @OnClick(R.id.iconuser)
    public void onIconuser() {
        galleryPhoto = new GalleryPhoto(getApplicationContext());
        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);

    }

    @OnClick(R.id.edit_inforuser)
    public void onEditInforuser() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_user, null);
        dialogBuilder.setView(dialogView);
        session = new SessionManager(this);
        final EditText edt1=(EditText) dialogView.findViewById(R.id.edtedit1);
        final EditText edt2=(EditText) dialogView.findViewById(R.id.edtedit2);
        final EditText edt3=(EditText) dialogView.findViewById(R.id.edtedit3);
        final ImageView img = (ImageView) dialogView.findViewById(R.id.ld_icon);
        img.setImageResource(R.drawable.icon_star);

        edt1.setText(session.getLoginName());
        edt2.setText(session.getLoginPhone());
        edt3.setText(session.getLoginAddress());

        dialogBuilder.setPositiveButton(R.string.btneditok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                newname = String.valueOf(edt1.getText());
                newphone = String.valueOf(edt2.getText());
                newaddress = String.valueOf(edt3.getText());
                name.setText(newname);
                phone.setText(newphone);
                address.setText(newaddress);
//                Log.e("Test",name + " " + phone + " "+ address);
            }
        });
        dialogBuilder.setNegativeButton(R.string.btneditcancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                                        dialog.cancel();

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
}
    @OnClick(R.id.btnEdit)
    public void onbtnEdit(){
    if (selectedPhoto != null){
        System.out.println("1");
        try {
            Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(1024, 1024).getBitmap();
            String encodedImage = ImageBase64.encode(bitmap);

            Log.d("IMAGE", encodedImage);


            HashMap<String, String> postData = new HashMap<>();
            postData.put("image", encodedImage);
            PostResponseAsyncTask task = new PostResponseAsyncTask(ManagementUser.this, postData, new AsyncResponse() {

                @Override
                public void processFinish(String s) {
                    Log.e("Image",s);
                    //                    Toast.makeText(getApplicationContext(),s.substring(1,60),Toast.LENGTH_LONG).show();
                    //                    Log.e(TAG,s.substring(1,60));

                    userimage = s.substring(1, s.length() - 1);
//                    Toast.makeText(getApplicationContext(),userimage,Toast.LENGTH_LONG).show();

//                userimage = s;
//                    Log.d(TAG,productimage[1] + " va " + productimage[3]);
                    requestQueue = Volley.newRequestQueue(ManagementUser.this);

                    StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_EDITUSER, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(ManagementUser.this, response, Toast.LENGTH_SHORT).show();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ManagementUser.this,"Error",Toast.LENGTH_LONG).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> param = new HashMap<>();
                            param.put("userid", String.valueOf(session.getLoginId()));
                            param.put("userimage",userimage);
                            param.put("name", String.valueOf(name.getText()));
                            param.put("phone", String.valueOf(phone.getText()));
                            param.put("address", String.valueOf(address.getText()));

                            return  param;

                        }

                    };

                    requestQueue.add(request);


                }
            });

            task.execute(AppConfig.URL_IMAGEUSER);

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


        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    "Something Wrong while encoding photos", Toast.LENGTH_SHORT).show();
        }

    }
    else {
        System.out.println("2");

        userimage = session.getLoginPic();
        Toast.makeText(getApplicationContext(),userimage,Toast.LENGTH_LONG).show();

//                userimage = s;
//                    Log.d(TAG,productimage[1] + " va " + productimage[3]);
        requestQueue = Volley.newRequestQueue(ManagementUser.this);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_EDITUSER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ManagementUser.this, response, Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ManagementUser.this,"Error",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("userid", String.valueOf(session.getLoginId()));
                param.put("userimage",userimage);
                param.put("name", String.valueOf(name.getText()));
                param.put("phone", String.valueOf(phone.getText()));
                param.put("address", String.valueOf(address.getText()));

                return  param;

            }

        };

        requestQueue.add(request);
    }

}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQUEST){
                Uri uri = data.getData();

                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                selectedPhoto = photoPath;
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    userpic.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something Wrong while choosing photos", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


//        toolbar.inflateMenu(R.menu.menu_share);
//        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.action_home));
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });





    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
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

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
    public void init() {
        userpic = (CircleImageView) findViewById(R.id.iconuser);
        Glide.with(getApplicationContext()).load(session.getLoginPic()).placeholder(R.drawable.empty_photo).dontAnimate().override(100, 100).into(userpic);
        username = (RobotoBoldTextView) findViewById(R.id.drawer_username);
        email = (RobotoRegularTextView) findViewById(R.id.drawer_email);
        rate = (RobotoRegularTextView) findViewById(R.id.drawer_rate);

        username.setText(session.getLoginName());
        email.setText(session.getLoginEmail());
        rate.setText(session.getLoginRate());

        name = (RobotoLightTextView) findViewById(R.id.sellername);
        useremail = (RobotoLightTextView) findViewById(R.id.selleremail);
        phone = (RobotoLightTextView) findViewById(R.id.sellerphone);
        address = (RobotoLightTextView) findViewById(R.id.productaddress);
        area = (RobotoLightTextView) findViewById(R.id.sellerarea);

//        Toast.makeText(getApplicationContext(), ProductDrawerFragment.userobj.getPhone(), Toast.LENGTH_SHORT).show();
        name.setText(session.getLoginName());
        useremail.setText(session.getLoginEmail());
        phone.setText(session.getLoginPhone());
        address.setText(session.getLoginAddress());
        area.setText(session.getLoginArea());

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
}
