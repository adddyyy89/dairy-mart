package com.dairymart.android.dairymartapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.dairymart.android.dairymartapplication.R;

import java.util.List;

public class SalesmanPendingOrderAdapter extends BaseAdapter {

    private final Context context;
    private final List<SalesmanPendingOrder> orders;
    private final LayoutInflater inflatar;

    public SalesmanPendingOrderAdapter(Context context, List<SalesmanPendingOrder> orders) {
        this.context = context;
        this.orders = orders;
        this.inflatar = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return orders.get(position).getOrderId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflatar.inflate(R.layout.list_salesman_pending_order, parent, false);
        }

        TextView shopName = view.findViewById(R.id.salesman_pending_order_card_shopname);
        TextView orderStatus = view.findViewById(R.id.salesman_pending_order_card_status);
        TextView fullAddress = view.findViewById(R.id.salesman_pending_order_card_fulladdress);
        TextView productCount = view.findViewById(R.id.salesman_pending_order_card_productcount);
        TextView orderCost = view.findViewById(R.id.salesman_pending_order_card_ordercost);
        TextView changeStatus = view.findViewById(R.id.salesman_pending_order_card_changestatus);

        SalesmanPendingOrder order = orders.get(position);

        shopName.setText(order.getRetailerDetail().getShopName());
        orderStatus.setText(order.getOrderStatus().getStatusDesc());
        fullAddress.setText(order.getRetailerDetail().getFullAddress() + "\n" + order.getRetailerDetail().getCityName());

        int totalProducts = 0;
        double totalCost = 0d;
        for(SalesmanPendingOrder.OrderDetail orderDetail : order.getOrderDetails()) {
            totalProducts += Integer.parseInt(orderDetail.getQuantity() == null || orderDetail.getQuantity().isEmpty() ? "0" : orderDetail.getQuantity());
            totalCost += Double.parseDouble(orderDetail.getPurchaseRate() == null ? "0" : orderDetail.getPurchaseRate()) * Integer.parseInt(orderDetail.getQuantity() == null || orderDetail.getQuantity().isEmpty() ? "0" : orderDetail.getQuantity());
        }
        productCount.setText(String.valueOf(totalProducts));
        orderCost.setText(String.valueOf(totalCost));

        switch (order.getOrderStatus().getStatusDesc()) {
            case("NEW"):
                changeStatus.setText("CONFIRMED");
                break;
            case ("CONFIRMED"):
                changeStatus.setText("DISPATCHED");
                break;
            case ("DISPATCHED"):
                changeStatus.setText("DELIVERED");
                break;
        }

        return view;
    }
}
