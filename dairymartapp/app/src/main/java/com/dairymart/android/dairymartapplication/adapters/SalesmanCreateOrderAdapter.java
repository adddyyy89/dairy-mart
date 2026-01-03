package com.dairymart.android.dairymartapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dairymart.android.dairymartapplication.R;

import java.util.ArrayList;

public class SalesmanCreateOrderAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<SalesmanCreateOrderItem> orderItems;
    private final LayoutInflater inflatar;

    public SalesmanCreateOrderAdapter(Context context, ArrayList<SalesmanCreateOrderItem> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
        this.inflatar = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orderItems.size();
    }

    @Override
    public Object getItem(int position) {
        return orderItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return orderItems.get(position).getProductId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflatar.inflate(R.layout.list_salesman_create_order_items, parent, false);
        }

        TextView productNameTextView = view.findViewById(R.id.product_name);
        ImageView productImageView = view.findViewById(R.id.product_image);
        EditText productQuantityEditText = view.findViewById(R.id.product_quantity);
        productQuantityEditText.setText("0");

        SalesmanCreateOrderItem item = orderItems.get(position);
        productNameTextView.setText(item.getProductName());
        productQuantityEditText.setText("0");

        Glide.with(context).load(item.getProductImageUrl()).placeholder(R.drawable.box_24).into(productImageView);

        productQuantityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!productQuantityEditText.getText().toString().isEmpty()) {
                    int q = Integer.parseInt(productQuantityEditText.getText().toString());
                    item.setQuantity(q);
                }
            }
        });
        return view;
    }
}
