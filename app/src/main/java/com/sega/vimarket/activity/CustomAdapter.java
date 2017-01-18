package com.sega.vimarket.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sega.vimarket.R;

/**
 * Created by Sega on 18/01/2017.
 */


public class CustomAdapter extends BaseAdapter {

    String[] result;
    Context context;
    int[] imageId;

    /**
     * Constructor này dùng để khởi tạo các giá trị
     * từ CustomListViewActivity truyền vào
     *
     * @param context  : là Activity từ CustomListView
     * @param imageId: Là danh sách image của list item truyền từ Main
     * @param result   : Danh sách nội dung của list item truyền từ Main
     */
    public CustomAdapter(Context context, String[] result, int[] imageId) {
        this.context = context;
        this.result = result;
        this.imageId = imageId;
    }

    //Trả về độ dài của mảng chứa nội dung list item
    @Override
    public int getCount() {
        return result.length;
    }

    //Trả về vị trí của mảng chứa nội dung list item
    @Override
    public Object getItem(int position) {
        return position;
    }

    //Trả về vị trí của mảng image list item
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * hàm dùng để custom layout, ta phải override lại hàm này
     * từ CustomListViewActivity truyền vào
     *
     * @param position     : là vị trí của phần tử trong danh sách Item
     * @param convertView: convertView, dùng nó để xử lý Item
     * @param parent       : Danh sách  truyền từ Main
     * @return View: trả về chính convertView
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item, parent, false);

        TextView tvNoiDung = (TextView) rowView.findViewById(R.id.tvNoiDung);
        ImageView imgAvatar = (ImageView) rowView.findViewById(R.id.imgAvatatr);

        //lấy Nội dung của Item ra để thiết lập nội dung item cho đúng
        tvNoiDung.setText(result[position]);
        //lấy ImageView ra để thiết lập hình ảnh cho đúng
        imgAvatar.setImageResource(imageId[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();
            }
        });
        //trả về View này, tức là trả luôn về các thông số mới mà ta vừa thay đổi
        return rowView;
    }
}