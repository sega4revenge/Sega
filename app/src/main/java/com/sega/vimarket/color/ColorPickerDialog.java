package com.sega.vimarket.color;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sega.vimarket.R;

public class ColorPickerDialog extends Dialog implements View.OnClickListener, ColorPickerAdapter.OnItemClickListener {
    private OnColorSelectedListener listener;

    public ColorPickerDialog(Context context) {
        super(context);
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }

    @Override
    public void onItemClick(Colorful.ThemeColor color) {
        dismiss();
        if (listener!=null) {
            listener.onColorSelected(color);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_colorpicker);

        RecyclerView recycler = ((RecyclerView) findViewById(R.id.colorful_color_picker_recycler));
        Toolbar toolbar = ((Toolbar) findViewById(R.id.colorful_color_picker_toolbar));

        toolbar.setNavigationOnClickListener(this);
        toolbar.setBackgroundColor(getContext().getResources().getColor(Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
        toolbar.setTitle(R.string.select_color);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        recycler.setLayoutManager(new GridLayoutManager(getContext(), 4));
        ColorPickerAdapter adapter = new ColorPickerAdapter(getContext());
        adapter.setOnItemClickListener(this);
        recycler.setAdapter(adapter);
    }

    public interface OnColorSelectedListener {
        void onColorSelected(Colorful.ThemeColor color);
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener=listener;
    }
}
