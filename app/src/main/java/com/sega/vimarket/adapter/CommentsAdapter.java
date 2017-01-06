package com.sega.vimarket.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.sega.vimarket.model.Comments;
import com.sega.vimarket.R;
import com.sega.vimarket.util.TextUtils;
import com.sega.vimarket.widget.CircleImageView;
import com.sega.vimarket.widget.RobotoRegularTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;



/**a
 * Created by Sega on 8/4/2016.
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    public ArrayList<Comments> CommentsList;
    private OnCommentsClickListener onCommentsClickListener;
    // Constructor

    public CommentsAdapter(Context context, OnCommentsClickListener onCommentsClickListener) {
        this.context = context;
        CommentsList = new ArrayList<>();
        this.onCommentsClickListener = onCommentsClickListener;
    }

    // RecyclerView methods
    @Override
    public int getItemCount() {
        return CommentsList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comments, parent, false);
        return new CommentsViewHolder(v, onCommentsClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Comments Comments = CommentsList.get(position);
        final CommentsViewHolder holder = (CommentsViewHolder) viewHolder;
        if (TextUtils.isNullOrEmpty(Comments.userpic)) {
            holder.CommentImage.setImageResource(R.drawable.default_cast_square);
        }
        else {
            Glide.with(context).load(Comments.userpic).placeholder(R.drawable.empty_photo).dontAnimate().override(120, 120).into(holder.CommentImage);
        }
        holder.CommentName.setText(Comments.username);
        holder.CommentData.setText(Comments.contentcomment);
        holder.CommentRate.setText(Comments.rate);
        holder.CommentTime.setText(DateUtils.getRelativeTimeSpanString(
                Long.parseLong(Comments.time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
    }

    // ViewHolder
    class CommentsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_item)
        View CommentItem;
        @BindView(R.id.commentpic)
        CircleImageView CommentImage;
        @BindView(R.id.commentname)
        RobotoRegularTextView CommentName;
        @BindView(R.id.commentdata)
        RobotoRegularTextView CommentData;
        @BindView(R.id.commentrate)
        RobotoRegularTextView CommentRate;
        @BindView(R.id.commentdate)
        RobotoRegularTextView CommentTime;

        CommentsViewHolder(final ViewGroup itemView,
                           final OnCommentsClickListener onCommentsClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            CommentItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentsClickListener.onCommentsClicked(getAdapterPosition());
                }
            });
        }
    }

    // Click listener interface
    public interface OnCommentsClickListener {
        void onCommentsClicked(final int position);
    }
}