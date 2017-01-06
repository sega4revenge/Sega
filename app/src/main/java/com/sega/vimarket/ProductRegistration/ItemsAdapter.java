package com.sega.vimarket.ProductRegistration;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sega.vimarket.R;

import java.util.List;

import co.dift.ui.SwipeToAction;

/**a
 * Created by HOaHOANGLINH on 26-Dec-16.
 */

class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Item> items;


    /** References to the views for each data item **/
    private class ItemsViewHolder extends SwipeToAction.ViewHolder<Item> {
        TextView titleView;


        ItemsViewHolder(View v) {
            super(v);

            titleView = (TextView) v.findViewById(R.id.title);

        }
    }

    /** Constructor **/
    ItemsAdapter(List<Item> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);

        return new ItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);
        ItemsViewHolder vh = (ItemsViewHolder) holder;
        vh.titleView.setText(item.getTitle());
        vh.data = item;
    }
}