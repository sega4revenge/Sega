package com.sega.vimarket.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemPaddingDecoration extends RecyclerView.ItemDecoration {
    private int mItemOffset;

    public ItemPaddingDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    public ItemPaddingDecoration(@NonNull Context context) {
        this(context.getResources().getDimensionPixelSize(com.sega.vimarket.R.dimen.recycler_item_padding));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
    }
}
