package com.sega.vimarket.fragment;

/**a
 * Created by Sega on 04/01/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sega.vimarket.R;
import com.sega.vimarket.activity.ChatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;





public class MessengerFragment extends Fragment {


    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messenger, container, false);

        ListView listView = (ListView) v.findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list_of_rooms);

        listView.setAdapter(arrayAdapter);
        root.orderByKey().equalTo("chatmodel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals("chatmodel")) {
                        set.add(child.getKey());
                    }
                }


                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ChatActivity.class);
             /*   intent.putExtra("room_name", ((TextView) view).getText().toString());
                intent.putExtra("user_name", name);*/
                startActivity(intent);
            }
        });
        return v;
    }

}