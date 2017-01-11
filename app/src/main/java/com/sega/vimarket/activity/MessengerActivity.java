package com.sega.vimarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

/**
 * Created by Sega on 09/01/2017.
 */


public class MessengerActivity extends CActivity implements MessengerAdapter.OnMessengerClickListener {


    private MessengerAdapter arrayAdapter;
    ArrayList<Room> roomlist = new ArrayList<>();

    SessionManager session;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_messenger);

        session = new SessionManager(this);
        RecyclerView recycleView = (RecyclerView) findViewById(R.id.messenger_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessengerActivity.this);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(layoutManager);
        recycleView.addItemDecoration(new ItemPaddingDecoration(MessengerActivity.this));
        arrayAdapter = new MessengerAdapter(MessengerActivity.this, this);
        getroom(session.getLoginId()+"");

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
        String[] temp = roomlist.get(position).room.split("-");
        Intent i = new Intent(MessengerActivity.this,ChatActivity.class);
        if(temp[0].trim().equals(session.getLoginId()+""))
        {
            i.putExtra(ViMarket.seller_ID, Integer.parseInt(temp[1]));
        }
        else
            i.putExtra(ViMarket.seller_ID,Integer.parseInt(temp[0]));

        i.putExtra(ViMarket.user_ID, session.getLoginId());
        i.putExtra(ViMarket.seller_name, roomlist.get(position).username);

        i.putExtra("sellerpic", roomlist.get(position).userpic);
        startActivity(i);

    }
    public void getroom(final String id){
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
                        roomlist.add(new Room(feedObj.getString("room"),feedObj.getString("roomname"),feedObj.getString("roompic")));

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

    public void getRoomDetail()
    {
        for(int i = 0;i<roomlist.size();i++)
        {

            System.out.println(roomlist.get(i).room);
            final int finalI = i;
            root.child(roomlist.get(i).room).orderByChild("name").limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    arrayAdapter.MessengerList.clear();
                    Iterator it = dataSnapshot.getChildren().iterator();
                    ChatModel a = ((DataSnapshot)it.next()).getValue(ChatModel.class);
                    System.out.println(a.getMessage());
                    Messenger temp = new Messenger(roomlist.get(finalI).username, a.getTimeStamp(), a.getMessage(), roomlist.get(finalI).userpic);
                    arrayAdapter.MessengerList.add(temp);
                    arrayAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }
}