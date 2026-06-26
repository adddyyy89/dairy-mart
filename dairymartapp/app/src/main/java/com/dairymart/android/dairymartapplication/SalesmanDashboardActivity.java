package com.dairymart.android.dairymartapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dairymart.android.dairymartapplication.adapters.SalesmanDashboardRecentTransaction;
import com.dairymart.android.dairymartapplication.adapters.SalesmanDashboardRecentTransactionAdapter;
import com.dairymart.android.dairymartapplication.util.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SalesmanDashboardActivity extends AppCompatActivity {

    Properties properties;

    TextView errorTextView;
    TextView balanceVal;
    TextView orderPlacedVal;
    TextView crateAssignedVal;
    TextView engagedCrateVal;

    LinearLayout nav_activity;
    LinearLayout nav_delivery;
    LinearLayout nav_ledger;

    View fabMenuItems;
    FloatingActionButton mainFab;
    boolean isFabExpanded = false;

    ListView recentTransactions;
    SalesmanDashboardRecentTransactionAdapter salesmanDashboardRecentTransactionAdapter;

    SharedPreferences sharedPreferences;

    JSONObject salesmanData;

    int userId;
    String userTypeId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("dairymart", Context.MODE_PRIVATE);
        userId = Integer.parseInt(sharedPreferences.getString("userid", null));
        userTypeId = sharedPreferences.getString("usertypeid", null);

        properties = PropertiesUtil.loadConfig(this);


        // check if user is salesman
        boolean isValidUser = validateUserRole(userTypeId);
        
        if(isValidUser) {
            setContentView(R.layout.activity_salesman_dashboard_2); // Create activity_welcome.xml

            loadData();

            nav_activity = findViewById(R.id.nav_activity);
            nav_delivery = findViewById(R.id.nav_delivery);
            nav_ledger = findViewById(R.id.nav_ledger);

            recentTransactions = findViewById(R.id.transactionListView);


            balanceVal = findViewById(R.id.salesman_dashboard_balance);
            balanceVal.setText("₹ -2,494.00");

            orderPlacedVal = findViewById(R.id.salesman_dashboard_orderplaced);
            orderPlacedVal.setText("1");

            crateAssignedVal = findViewById(R.id.salesman_dashboard_crateassigned);
            crateAssignedVal.setText("94");

            engagedCrateVal = findViewById(R.id.salesman_dashboard_engagedcrate);
            engagedCrateVal.setText("94");

            fabMenuItems = findViewById(R.id.fab_menu_items);
            mainFab = findViewById(R.id.main_fab);

            mainFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFabExpanded) {
                        fabMenuItems.setVisibility(View.GONE);
                        mainFab.setImageResource(android.R.drawable.ic_input_add);
                        isFabExpanded = false;
                    } else {
                        fabMenuItems.setVisibility(View.VISIBLE);
                        mainFab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                        isFabExpanded = true;
                    }
                }
            });

            nav_activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(SalesmanDashboardActivity.this, SalesmanActivityOrdersActivity.class));

                }
            });

            nav_delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(SalesmanDashboardActivity.this, SalesmanDeliveryPendingActivity.class));

                }
            });

            nav_ledger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(SalesmanDashboardActivity.this, SalesmanLedgerDashboardActivity.class));

                }
            });

            loadData();
        }
    }

    private boolean validateUserRole(String userTypeId) {
        if(userTypeId.equalsIgnoreCase("2"))
            return true;
        return false;
    }

    private void loadData() {
        String baseUrl = properties.getProperty("dairymartServerUrl");
        String url = baseUrl + "/salesman/dashboard/get/" + userId; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(SalesmanDashboardActivity.this, response, Toast.LENGTH_SHORT).show();

                        updateUI(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SalesmanDashboardActivity.this, error.getMessage() != null ? error.getMessage() : "On Error Response", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", sharedPreferences.getString("auth", null));
                return headers;
            }
        };
        queue.add(request);
    }

    private void updateUI(String response) {
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(response);

            crateAssignedVal.setText(String.valueOf(jsonObject.getJSONObject("map").getInt("cratesassigned")));
            balanceVal.setText(String.valueOf(jsonObject.getJSONObject("map").getDouble("walletbalance")));
            orderPlacedVal.setText(String.valueOf(jsonObject.getJSONObject("map").getInt("ordersplaced")));


            // Update recent transactions
            ArrayList<SalesmanDashboardRecentTransaction> recentTransactionsList = new ArrayList<>();
            JSONArray recentTransactionsArray = jsonObject.getJSONObject("map").getJSONObject("recenttransactions").getJSONArray("myArrayList");
            for(int indx=0; indx<recentTransactionsArray.length(); indx++) {
                long transactionId = ((JSONObject) recentTransactionsArray.get(indx)).getJSONObject("map").getLong("transactionsId");
                long ledgerId = ((JSONObject) recentTransactionsArray.get(indx)).getJSONObject("map").getLong("ledgerId");
                String amount = ((JSONObject) recentTransactionsArray.get(indx)).getJSONObject("map").getString("amount");
                String retailerName = ((JSONObject) recentTransactionsArray.get(indx)).getJSONObject("map").getJSONObject("ledger").getJSONObject("map").getJSONObject("retailer").getJSONObject("map").getString("firstName");
                String transactionTimestamp = ((JSONObject) recentTransactionsArray.get(indx)).getJSONObject("map").getString("createdOn");

                boolean isCredit = ((JSONObject) recentTransactionsArray.get(indx)).getJSONObject("map").getBoolean("credit");
                String transactionType = "Credited";
                if(!isCredit) {
                    transactionType = "Debited";

                }

                recentTransactionsList.add(new SalesmanDashboardRecentTransaction(transactionId, ledgerId, amount, transactionType, retailerName, transactionTimestamp));

            }

            salesmanDashboardRecentTransactionAdapter = new SalesmanDashboardRecentTransactionAdapter(this, recentTransactionsList);
            recentTransactions.setAdapter(salesmanDashboardRecentTransactionAdapter);


        }
        catch(JSONException e) {
            Toast.makeText(SalesmanDashboardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
