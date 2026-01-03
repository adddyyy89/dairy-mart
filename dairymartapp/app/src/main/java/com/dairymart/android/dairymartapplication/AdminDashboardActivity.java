package com.dairymart.android.dairymartapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AdminDashboardActivity extends AppCompatActivity {

    private Timer timer;
    private final Handler handler = new Handler();

    TextView newOrdersTextView;
    TextView pendingOrdersTextView;
    TextView inDeliveryOrdersTextView;
    TextView deliveredOrdersTextView;

    LinearLayout ordersLayout;
    LinearLayout usersLayout;
    LinearLayout settingsLayout;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        newOrdersTextView = findViewById(R.id.new_orders);
        pendingOrdersTextView = findViewById(R.id.pending_orders);
        inDeliveryOrdersTextView = findViewById(R.id.order_in_delivery);
        deliveredOrdersTextView = findViewById(R.id.order_delivered);

        //ordersLayout.findViewById(R.id.nav_orders);
        //usersLayout.findViewById(R.id.nav_users);
        //settingsLayout.findViewById(R.id.nav_settings);


        initTexts();

        startTimer();

        /*ordersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class));
            }
        });

        usersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, AdminUsersActivity.class));
            }
        });

        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, AdminSettingsActivity.class));
            }
        });*/

    }

    @SuppressLint("SetTextI18n")
    private void initTexts() {
        pendingOrdersTextView.setText(pendingOrdersTextView.getText() + getPendingOrders());
        newOrdersTextView.setText(newOrdersTextView.getText() + getNewOrders());
        inDeliveryOrdersTextView.setText(inDeliveryOrdersTextView.getText() + getInDeliveryOrders());
        deliveredOrdersTextView.setText(deliveredOrdersTextView.getText() + getDeliveredOrders());
    }

    private void startTimer() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        initTexts();
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 3000); // Start timer with 0 delay and 1-second interval
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimerTask();
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private String getPendingOrders() {
        //return callService("");
        return "0";
    }

    private String getNewOrders() {
        //return callService("");
        return "0";
    }

    private String getInDeliveryOrders() {
        //return callService("");
        return "0";
    }

    private String getDeliveredOrders() {
        //return callService("");
        return "0";
    }

    private String callService(String url) {
        int count = 0;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                //String credentials = phoneNumber + ":" + password;
                //String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                //headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(request);

        return String.valueOf(count);
    }
}
