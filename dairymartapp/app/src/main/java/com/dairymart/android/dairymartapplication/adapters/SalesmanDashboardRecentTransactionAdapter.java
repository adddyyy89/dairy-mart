package com.dairymart.android.dairymartapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dairymart.android.dairymartapplication.R;

import java.util.ArrayList;

public class SalesmanDashboardRecentTransactionAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<SalesmanDashboardRecentTransaction> transactions;
    private final LayoutInflater inflatar;

    public SalesmanDashboardRecentTransactionAdapter(Context context, ArrayList<SalesmanDashboardRecentTransaction> transactions) {
        this.context = context;
        this.transactions = transactions;
        this.inflatar = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return transactions.get(position).getTransactionId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflatar.inflate(R.layout.list_salesman_dashboard_recent_transactions_item, parent, false);
        }

        TextView transactionType = view.findViewById(R.id.transaction_type);
        TextView transactionAmount = view.findViewById(R.id.transaction_amount);
        TextView transactionRetailerName = view.findViewById(R.id.transaction_retailer_name);
        TextView transactionTimestamp = view.findViewById(R.id.transaction_timestamp);


        SalesmanDashboardRecentTransaction transaction = transactions.get(position);
        if(transaction.getTransactionType().toLowerCase().contains("debited")) {
            transactionType.setTextColor(0xFFFF0000);
        }

        transactionType.setText(transaction.getTransactionType());
        transactionAmount.setText(transaction.getAmount());
        transactionRetailerName.setText(transaction.getRetailerName());
        transactionTimestamp.setText(transaction.getTransactionTimestamp());

        return view;
    }
}
