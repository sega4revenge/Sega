package com.sega.vimarket.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.sega.vimarket.R;
import com.sega.vimarket.model.Messenger;
import com.sega.vimarket.util.TextUtils;
import com.sega.vimarket.widget.CircleImageView;
import com.sega.vimarket.widget.RobotoBoldTextView;
import com.sega.vimarket.widget.RobotoLightTextView;
import com.sega.vimarket.widget.RobotoRegularTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sega on 09/01/2017.
 */


public class MessengerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    public ArrayList<Messenger> MessengerList;
    private OnMessengerClickListener onMessengerClickListener;
    // Constructor

    public MessengerAdapter(Context context, OnMessengerClickListener onMessengerClickListener) {
        this.context = context;
        MessengerList = new ArrayList<>();
        this.onMessengerClickListener = onMessengerClickListener;
    }

    // RecyclerView methods
    @Override
    public int getItemCount() {
        return MessengerList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_messenger, parent, false);
        return new MessengerViewHolder(v, onMessengerClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Messenger Messenger = MessengerList.get(position);
        final MessengerViewHolder holder = (MessengerViewHolder) viewHolder;
        if (TextUtils.isNullOrEmpty(Messenger.userpic)) {
            holder.MessengerImage.setImageResource(R.drawable.default_cast_square);
        }
        else {
            Glide.with(context).load(Messenger.userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(holder.MessengerImage);
        }
        holder.MessengerName.setText(Messenger.username);
        holder.MessengerData.setText(Messenger.contentcomment);
        holder.MessengerTime.setText(DateUtils.getRelativeTimeSpanString(
                Long.parseLong(Messenger.time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
    }

    // ViewHolder
    class MessengerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.conversation_list_row)
        View MessengerItem;
        @BindView(R.id.conversation_list_avatar)
        CircleImageView MessengerImage;
        @BindView(R.id.conversation_list_name)
        RobotoBoldTextView MessengerName;
        @BindView(R.id.conversation_list_snippet)
        RobotoLightTextView MessengerData;
        @BindView(R.id.conversation_list_date)
        RobotoRegularTextView MessengerTime;

        MessengerViewHolder(final ViewGroup itemView,
                           final OnMessengerClickListener onMessengerClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            MessengerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMessengerClickListener.onMessengerClicked(getAdapterPosition());
                }
            });
        }
    }

    // Click listener interface
    public interface OnMessengerClickListener {
        void onMessengerClicked(final int position);
    }
}