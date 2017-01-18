package com.sega.vimarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.adapter.MessengerAdapter;
import com.sega.vimarket.color.CActivity;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.model.ChatModel;
import com.sega.vimarket.model.Messenger;
import com.sega.vimarket.model.Room;
import com.sega.vimarket.util.VolleySingleton;
import com.sega.vimarket.widget.ItemPaddingDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by Sega on 09/01/2017.
 */


public class MessengerActivity extends CActivity implements MessengerAdapter.OnMessengerClickListener {

    Toolbar toolbar;
    ArrayList<Room> roomlist = new ArrayList<>();
    String sellername, sellerpic, message, timestamp;
    SessionManager session;
    boolean load = false;
    ArrayList<String> pic = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> id = new ArrayList<>();
    private MessengerAdapter arrayAdapter;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot o : dataSnapshot.getChildren()) {
                searchroom(o.getKey());
                System.out.println(o.getKey());

            }


            arrayAdapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_messenger);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.action_home));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle(R.string.inbox);
        session = new SessionManager(this);
        RecyclerView recycleView = (RecyclerView) findViewById(R.id.messenger_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessengerActivity.this);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(layoutManager);
        recycleView.addItemDecoration(new ItemPaddingDecoration(MessengerActivity.this));
        arrayAdapter = new MessengerAdapter(MessengerActivity.this, this);
        getroom(session.getLoginId() + "");

        recycleView.setAdapter(arrayAdapter);


      /*  recycleView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(MessengerActivity.this, ChatActivity.class);
                    intent.putExtra("room_name", ((TextView) view).getText().toString());
                    intent.putExtra("user_name", name);
                startActivity(intent);
            }
        });*/
    }

    @Override
    public void onMessengerClicked(int position) {


        Intent i = new Intent(MessengerActivity.this, ChatActivity.class);
        i.putExtra(ViMarket.seller_ID, Integer.parseInt(id.get(position)));
        i.putExtra(ViMarket.user_ID, session.getLoginId());
        i.putExtra(ViMarket.seller_name, name.get(position));
        i.putExtra("sellerpic", pic.get(position));
        load = true;
        startActivity(i);

    }

    public void getroom(final String id) {

        final String TAG = MessengerActivity.class.getSimpleName();
        // Hide all views
        StringRequest strReq = new StringRequest(Request.Method.POST,
                                                 AppConfig.API_MESSENGERGET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);


                    for (int i = 0; i < jsonArray.length(); i++) {
                        final JSONObject feedObj = (JSONObject) jsonArray.get(i);
                        //add product to list products
                        roomlist.add(new Room(feedObj.getString("room"), feedObj.getString("roomname"), feedObj.getString("roompic")));
                        System.out.println(feedObj.getString("room"));
                        //add product to sqlite
                    }


                    // Load detail fragment if in tablet mode

                    getRoomDetail();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                params.put("userid", id);
                return params;
            }
        };

        // Adding request to request queue
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(this).requestQueue.add(strReq);

    }

    public void getRoomDetail() {
        /*for(int i = 0;i<roomlist.size();i++)
        {

            System.out.println(roomlist.get(i).room);
            final int finalI = i;
            arrayAdapter.MessengerList.clear();*/
         /*   root.orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator it = dataSnapshot.getChildren().iterator();
                    ChatModel a = ((DataSnapshot)it.next()).getValue(ChatModel.class);
                    System.out.println(a.getMessage());
                  *//*  Messenger temp = new Messenger(roomlist.get(finalI).username, a.getTimeStamp(), a.getMessage(), roomlist.get(finalI).userpic);
                    if (arrayAdapter.MessengerList.contains(temp)) {
                        arrayAdapter.MessengerList.add(temp);
                    }else
*//*



                    arrayAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/


        root.orderByKey().addValueEventListener(listener);

 /*   root.orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator it = dataSnapshot.getChildren().iterator();
                    ChatModel a = ((DataSnapshot)it.next()).getValue(ChatModel.class);
                    System.out.println(a.getMessage());
                  *//*  Messenger temp = new Messenger(roomlist.get(finalI).username, a.getTimeStamp(), a.getMessage(), roomlist.get(finalI).userpic);
                    if (arrayAdapter.MessengerList.contains(temp)) {
                        arrayAdapter.MessengerList.add(temp);
                    }else
*//*



                    arrayAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

          /*  FirebaseDatabase.getInstance().getReference().orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println(dataSnapshot.getKey());
                    Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                    List<User> users = new ArrayList<>();
                    while (dataSnapshots.hasNext()) {
                        DataSnapshot dataSnapshotChild = dataSnapshots.next();
                        User user = dataSnapshotChild.getValue(User.class);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
*/


    }

    private void searchroom(final String key) {

        String[] temp = key.split("-");
        if (temp[0].equals(session.getLoginId() + "") || (temp[1].equals(session.getLoginId() + ""))) {
            root.child(key).limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!load) {


                        Iterator it = dataSnapshot.getChildren().iterator();
                        try {
                            ChatModel a = ((DataSnapshot) it.next()).getValue(ChatModel.class);

                            message = a.getMessage();
                            timestamp = a.getTimeStamp();

                            String[] temp = key.split("-");
                            if (temp[0].equals(session.getLoginId() + "")) {
                                Messenger tempmes = messenger(temp[1]);

                                arrayAdapter.MessengerList.add(tempmes);

                                id.add(temp[1]);


                            }
                            else if (temp[1].equals(session.getLoginId() + "")) {
                                Messenger tempmes = messenger(temp[0]);

                                arrayAdapter.MessengerList.add(tempmes);

                                id.add(temp[0]);


                            }

                      /*  temp2 = new Messenger(a.getUserModel().getName(), a.getTimeStamp(), a.getMessage(), a.getUserModel().getPhoto_profile());
                        arrayAdapter.MessengerList.add(temp2);*/


                        } catch (NoSuchElementException e) {
                            Toast.makeText(getApplicationContext(), "no chat", Toast.LENGTH_SHORT).show();
                        }

                        arrayAdapter.notifyDataSetChanged();
                        root.removeEventListener(listener);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public Messenger messenger(String id) {


        OkHttpClient client = new OkHttpClient.Builder()


                .build();
        RequestBody body = new FormBody.Builder()
                .add("id", id + "")

                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(AppConfig.URL_GETUSER)

                .post(body)
                .build();
        okhttp3.Response forceCacheResponse = null;
        try {
            forceCacheResponse = client.newCall(request).execute();
            System.out.println(forceCacheResponse.code());
            String responsestring = forceCacheResponse.body().string();
            System.out.println(responsestring);
            if (forceCacheResponse.isSuccessful()) {
                JSONObject jObj = new JSONObject(responsestring);
                JSONObject user = jObj.getJSONObject("user");
                sellername = user.getString("name");
                sellerpic = user.getString("userpic");
                pic.add(sellerpic);
                name.add(sellername);
            }

            else {


            }


        } catch (Exception e) {

            e.printStackTrace();


        }

        return new Messenger(sellername, timestamp, message, sellerpic);

    }


}