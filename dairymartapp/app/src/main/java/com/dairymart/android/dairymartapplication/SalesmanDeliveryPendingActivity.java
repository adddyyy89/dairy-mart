package com.dairymart.android.dairymartapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SalesmanDeliveryPendingActivity extends AppCompatActivity {

    TextView errorTextView;

    LinearLayout nav_home;
    LinearLayout nav_activity;
    LinearLayout nav_delivery;
    LinearLayout nav_ledger;

    JSONObject pendingDeliveries;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesman_delivery_pending); // Create activity_welcome.xml

        //loadData();
        nav_home = findViewById(R.id.salesman_delivery_nav_home);
        nav_activity = findViewById(R.id.salesman_delivery_nav_activity);
        nav_delivery = findViewById(R.id.salesman_delivery_nav_delivery);
        nav_ledger = findViewById(R.id.salesman_delivery_nav_ledger);
        TextView pendingTab = findViewById(R.id.pendingTab);
        TextView deliveredTab = findViewById(R.id.deliveredTab);

        nav_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SalesmanDeliveryPendingActivity.this, SalesmanDashboardActivity.class));

            }
        });

        nav_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SalesmanDeliveryPendingActivity.this, SalesmanActivityOrdersActivity.class));

            }
        });

        nav_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SalesmanDeliveryPendingActivity.this, SalesmanDeliveryPendingActivity.class));

            }
        });

        nav_ledger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SalesmanDeliveryPendingActivity.this, SalesmanLedgerDashboardActivity.class));

            }
        });

        pendingTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalesmanDeliveryPendingActivity.this, SalesmanDeliveryPendingActivity.class));
            }
        });

        deliveredTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalesmanDeliveryPendingActivity.this, SalesmanDeliveryDeliveredActivity.class));
            }
        });

    }

    private void loadData() {
        String url = "null"; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SalesmanDeliveryPendingActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SalesmanDeliveryPendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPreferences = getSharedPreferences("dairymart", Context.MODE_PRIVATE);
                headers.put("Authorization", sharedPreferences.getString("auth", null));
                return headers;
            }
        };
        queue.add(request);
    }


}
