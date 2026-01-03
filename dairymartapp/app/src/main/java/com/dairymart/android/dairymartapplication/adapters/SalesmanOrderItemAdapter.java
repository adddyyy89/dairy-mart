package com.dairymart.android.dairymartapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.dairymart.android.dairymartapplication.R;

import java.util.ArrayList;

public class SalesmanOrderItemAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<SalesmanOrderItem> orderItems;
    private final LayoutInflater inflatar;

    public SalesmanOrderItemAdapter(Context context, ArrayList<SalesmanOrderItem> orderItems) {
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
        return orderItems.get(position).getOrderId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflatar.inflate(R.layout.list_salesman_order_items, parent, false);
        }

        TextView retailerName = view.findViewById(R.id.order_list_retailer_name);
        TextView orderStatus = view.findViewById(R.id.order_list_order_status);
        TextView orderId = view.findViewById(R.id.order_list_order_id);
        TextView orderTime =  view.findViewById(R.id.order_list_order_timestamp);
        TextView orderAmount =  view.findViewById(R.id.order_list_order_amount);


        SalesmanOrderItem item = orderItems.get(position);

        retailerName.setText(item.getRetailerName() + " - " + item.getRetailerId());
        orderStatus.setText(item.getOrderStatusMessage());
        orderId.setText("Order Id: " + item.getOrderId());
        orderTime.setText("Order Time: " + item.getOrderTimestamp());
        orderAmount.setText("₹ " + item.getOrderAmount());


        return view;
    }
}
