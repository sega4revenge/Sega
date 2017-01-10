package com.sega.vimarket.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.adapter.ChatFirebaseAdapter;
import com.sega.vimarket.adapter.CircleTransform;
import com.sega.vimarket.adapter.ClickListenerChatFirebase;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.model.ChatModel;
import com.sega.vimarket.model.FileModel;
import com.sega.vimarket.model.MapModel;
import com.sega.vimarket.model.UserModel;
import com.sega.vimarket.model.Utils;
import com.sega.vimarket.util.VolleySingleton;
import com.sega.vimarket.view.FullScreenImageActivity;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends CActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, ClickListenerChatFirebase {

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;

    static final String TAG = ChatActivity.class.getSimpleName();



    SessionManager session;

    private DatabaseReference mFirebaseDatabaseReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ChatFirebaseAdapter firebaseAdapter;
    //CLass Model
    private UserModel userModel;
    String urlPhotoUser,nameUser;
    //Views UI
    private RecyclerView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private EmojiconEditText edMessage;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    //File
    private File filePathImageCamera;
    Toolbar toolbar;
    String room;
    private ImageView ivUser;
    private TextView tvUser;
    int sellerid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        session = new SessionManager(this);
        if (!Utils.verificaConexao(this)){
            Utils.initToast(this,"Você não tem conexão com internet");
            finish();
        }else{
            bindViews();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ivUser = (ImageView)toolbar.findViewById(R.id.avatar);
            tvUser = (TextView)toolbar.findViewById(R.id.title);
            int userid =  getIntent().getIntExtra(ViMarket.user_ID,-1);
           sellerid =  getIntent().getIntExtra(ViMarket.seller_ID,-1);
            urlPhotoUser = getIntent().getStringExtra("sellerpic");
            System.out.println(urlPhotoUser);
            nameUser = getIntent().getStringExtra(ViMarket.seller_name);
            if(userid<sellerid)
                room = userid+"-"+sellerid;
            else
                room = sellerid+"-"+userid;
             root.orderByKey().equalTo(room).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount()==0){
                        Map<String,Object> map = new HashMap<>();
                        map.put(room,"");
                        root.updateChildren(map);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            verificaUsuarioLogado();




        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(Utils.URL_STORAGE_REFERENCE).child(Utils.FOLDER_STORAGE_IMG);

        if (requestCode == IMAGE_GALLERY_REQUEST){
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    sendFileFirebase(storageRef,selectedImageUri);
                }
            }
        }else if (requestCode == IMAGE_CAMERA_REQUEST){
            if (resultCode == RESULT_OK){
                if (filePathImageCamera != null && filePathImageCamera.exists()){
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName()+"_camera");
                    sendFileFirebase(imageCameraRef,filePathImageCamera);
                }
            }
        }else if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place!=null){
                    LatLng latLng = place.getLatLng();
                    MapModel mapModel = new MapModel(latLng.latitude+"", latLng.longitude+"");
                    ChatModel chatModel = new ChatModel(userModel, Calendar.getInstance().getTime().getTime()+"", mapModel);
                    mFirebaseDatabaseReference.child(room).push().setValue(chatModel);
                }
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.sendPhoto:
                photoCameraIntent();
                break;
            case R.id.sendPhotoGallery:
                photoGalleryIntent();
                break;
            case R.id.sendLocation:
                locationPlacesIntent();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Utils.initToast(this,"Google Play Services error.");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonMessage:
                sendMessageFirebase();
                break;
        }
    }


    @Override
    public void clickImageChat(View view, int position,String nameUser,String urlPhotoUser,String urlPhotoClick) {
        Intent intent = new Intent(this,FullScreenImageActivity.class);
        intent.putExtra("nameUser",nameUser);
        intent.putExtra("urlPhotoUser",urlPhotoUser);
        intent.putExtra("urlPhotoClick",urlPhotoClick);
        startActivity(intent);
    }

    @Override
    public void clickImageMapChat(View view, int position,String latitude,String longitude) {
        String uri = String.format("geo:%s,%s?z=17&q=%s,%s", latitude,longitude,latitude,longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }


    /**
     * Envia o arvquivo para o firebase
     */
    private void sendFileFirebase(StorageReference storageReference, final Uri file){
        if (storageReference != null){
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name+"_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"onFailure sendFileFirebase "+e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG,"onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    assert downloadUrl != null;
                    FileModel fileModel = new FileModel(downloadUrl.toString());
                    ChatModel chatModel = new ChatModel(userModel,"",Calendar.getInstance().getTime().getTime()+"",fileModel);
                    mFirebaseDatabaseReference.child(room).push().setValue(chatModel);
                }
            });
        }

    }

    /**
     * Envia o arvquivo para o firebase
     */
    private void sendFileFirebase(StorageReference storageReference, final File file){
        if (storageReference != null){
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(file));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"onFailure sendFileFirebase "+e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG,"onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    assert downloadUrl != null;
                    FileModel fileModel = new FileModel(downloadUrl.toString());
                    ChatModel chatModel = new ChatModel(userModel,"",Calendar.getInstance().getTime().getTime()+"",fileModel);
                    mFirebaseDatabaseReference.child(room).push().setValue(chatModel);
                }
            });
        }

    }

    /**
     * Obter local do usuario
     */
    private void locationPlacesIntent(){

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enviar foto tirada pela camera
     */
    private void photoCameraIntent(){
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto+"camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(filePathImageCamera));
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    /**
     * Enviar foto pela galeria
     */
    private void photoGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    /**
     * Enviar msg de texto simples para chat
     */
    private void sendMessageFirebase(){
        ChatModel model = new ChatModel(userModel,edMessage.getText().toString(), Calendar.getInstance().getTime().getTime()+"",null);
        mFirebaseDatabaseReference.child(room).push().setValue(model);
        edMessage.setText(null);
        firebaseAdapter.notifyDataSetChanged();




    }

    /**
     * Ler collections chatmodel Firebase
     */
    private void lerMessagensFirebase(String model){

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAdapter = new ChatFirebaseAdapter(mFirebaseDatabaseReference.child(model),userModel.getName(),this);
        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvListMessage.scrollToPosition(positionStart);
                }
            }
        });
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        rvListMessage.setAdapter(firebaseAdapter);
        setValues();
    }

    /**
     * Verificar se usuario está logado
     */
    private void verificaUsuarioLogado(){

            userModel = new UserModel(session.getLoginName()+","+session.getLoginId(),session.getLoginPic(), "12");
            lerMessagensFirebase(room);

        //========================================================
        final String TAG = ChatActivity.class.getSimpleName();
        // Hide all views
        StringRequest strReq = new StringRequest(Request.Method.POST,
                                                 AppConfig.API_MESSENGERPUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("userid", session.getLoginId() + "");
                params.put("room",room);
                params.put("roomname",nameUser);
                params.put("roompic",urlPhotoUser);
                return params;
            }
        };

        // Adding request to request queue
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(this).requestQueue.add(strReq);

    }

    /**
     * Vincular views com Java API
     */
    private void bindViews(){
        View contentRoot = findViewById(R.id.contentRoot);
        edMessage = (EmojiconEditText)findViewById(R.id.editTextMessage);
        ImageView btSendMessage = (ImageView) findViewById(R.id.buttonMessage);
        btSendMessage.setOnClickListener(this);
        ImageView btEmoji = (ImageView) findViewById(R.id.buttonEmoji);
        EmojIconActions emojIcon = new EmojIconActions(this, contentRoot, edMessage, btEmoji);
        emojIcon.ShowEmojIcon();
        rvListMessage = (RecyclerView)findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

    }

    /**
     * Sign Out no login
     */
    private void setValues(){

        Log.i("TAG", "imagem recebida "+urlPhotoUser);
        tvUser.setText(nameUser); // Name
        Glide.with(this).load(urlPhotoUser).centerCrop().transform(new CircleTransform(this)).override(40, 40).into(ivUser);


    }

}
