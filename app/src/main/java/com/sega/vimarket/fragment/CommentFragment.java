package com.sega.vimarket.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.adapter.CommentsAdapter;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.model.Comments;
import com.sega.vimarket.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**a
 * Created by Sega on 8/4/2016.
 */
public class CommentFragment extends Fragment implements CommentsAdapter.OnCommentsClickListener {
    private int userid;
    SessionManager session;
    private Unbinder unbinder;
    CommentsAdapter adapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_subtitle)
    TextView toolbarSubtitle;
    @BindView(R.id.comments_list)
    RecyclerView commentView;


    // Fragment lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comments, container, false);
        unbinder = ButterKnife.bind(this, v);
        userid = getArguments().getInt(ViMarket.user_ID);
        int commentType = getArguments().getInt(ViMarket.COMMENT_TYPE);
        String userName = getArguments().getString(ViMarket.user_name);
        toolbar.setTitle("");
        if (commentType == ViMarket.COMMENT_TYPE_CAST) {
            toolbarTitle.setText(R.string.ratetext);
        }
        session = new SessionManager(getActivity());
        toolbarSubtitle.setText(userName);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.action_home));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        adapter = new CommentsAdapter(getContext(), this);
        adapter.CommentsList = getArguments().getParcelableArrayList(ViMarket.COMMENT_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        commentView.setHasFixedSize(true);
        commentView.setLayoutManager(layoutManager);
        commentView.setAdapter(adapter);
        // Load Analytics Tracker

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Determine screen name
        downloadComment();
        getActivity().registerReceiver(this.appendChatScreenMsgReceiver, new IntentFilter("appendChatScreenMsg"));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Click events
    @Override
    public void onCommentsClicked(int position) {
        // TODO
    }



    private void downloadComment() {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_COMMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray feedArray = jObj.getJSONArray("comments");
                    adapter.CommentsList.clear();
                    for (int i = 0; i < feedArray.length(); i++) {
                        final JSONObject feedComment = (JSONObject) feedArray.get(i);
                        //add product to list products
                        Comments comment = new Comments(feedComment.getString("userid"), feedComment.getString("username"), feedComment.getString("time"),
                                                        feedComment.getString("contentcomment"), feedComment.getString("userpic"), feedComment.getString("rate"));
                        adapter.CommentsList.add(comment);
                    }
                    // Load detail fragment if in tablet mode
                    commentView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (Exception ex) {
                    // JSON parsing error
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid + "");
                return params;
            }
        };
        strReq.setTag(this.getClass().getName());
        VolleySingleton.getInstance(getActivity()).requestQueue.add(strReq);
    }

    BroadcastReceiver appendChatScreenMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            if (b != null) {
                if (b.getBoolean("reload")) {
                    downloadComment();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(appendChatScreenMsgReceiver);

    }
}
