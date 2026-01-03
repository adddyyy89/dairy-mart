package com.dairymart.android.dairymartapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.dairymart.android.dairymartapplication.adapters.SalesmanCreateOrderAdapter;
import com.dairymart.android.dairymartapplication.adapters.SalesmanCreateOrderItem;
import com.dairymart.android.dairymartapplication.adapters.SalesmanDashboardRecentTransaction;
import com.dairymart.android.dairymartapplication.adapters.SalesmanDashboardRecentTransactionAdapter;
import com.dairymart.android.dairymartapplication.util.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SalesmanCreateOrderActivity extends AppCompatActivity {

    Properties properties;

    TextView errorTextView;

    TextView retailerNameTextView;

    JSONObject orders;

    int retailerId;
    String retailerName;

    ListView productsListView;

    SalesmanCreateOrderAdapter createOrderAdapter;

    SharedPreferences sharedPreferences;
    int userId;
    String userTypeId;

    Button createOrderBtn;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesman_create_order); // Create activity_welcome.xml

        sharedPreferences = getSharedPreferences("dairymart", Context.MODE_PRIVATE);
        userId = Integer.parseInt(sharedPreferences.getString("userid", null));
        userTypeId = sharedPreferences.getString("usertypeid", null);

        retailerId = Integer.parseInt(getIntent().getStringExtra("create_order_for_shop_id"));
        retailerName = getIntent().getStringExtra("create_order_for_shop_name");
        retailerNameTextView = findViewById(R.id.retailerNameTextView);
        retailerNameTextView.setText(retailerName);

        productsListView = findViewById(R.id.productsListView);

        properties = PropertiesUtil.loadConfig(this);

        createOrderBtn = findViewById(R.id.checkoutButton);



        createOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SalesmanCreateOrderItem> orderPlaced = new ArrayList<>();
                for(int indx=0; indx<createOrderAdapter.getCount(); indx++){
                    SalesmanCreateOrderItem product = (SalesmanCreateOrderItem) createOrderAdapter.getItem(indx);
                    if(product.getQuantity() > 0) {
                        orderPlaced.add(product);
                    }
                }

                createOrder(orderPlaced);
            }
        });

        loadData();




    }

    private void createOrder(List<SalesmanCreateOrderItem> orderPlaced) {
        String baseUrl = properties.getProperty("dairymartServerUrl");
        String url = baseUrl + "/retailorder/add"; // Replace with your API endpoint

        JSONObject orderData = new JSONObject();
        try {
            orderData.put("retailerId", retailerId);
            orderData.put("branchId", 7);
            orderData.put("createdBy", userId);
            orderData.put("orderStatusId", 1);
            JSONArray orderDetails = new JSONArray();
            for(SalesmanCreateOrderItem item : orderPlaced) {
                JSONObject order = new JSONObject();
                order.put("productCode", item.getProductCode());
                order.put("quantity", item.getQuantity());
                order.put("hsn", "");
                order.put("unit", item.getQuantity());
                order.put("saleRate", "");
                order.put("purchaseRate", "");

                orderDetails.put(order);
            }
            orderData.put("orderDetails", orderDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = orderData.toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(SalesmanCreateOrderActivity.this, "Order placed successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SalesmanCreateOrderActivity.this, SalesmanActivityOrdersActivity.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Toast.makeText(SalesmanCreateOrderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", sharedPreferences.getString("auth", null));
                return headers;
            }
            @Override
            public String getBodyContentType() {
                // Specify the content type of your raw body
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    // Convert your string body to bytes using the specified encoding
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                    return null;
                }
            }

        };
        queue.add(request);
    }

    private void loadData() {
        String baseUrl = properties.getProperty("dairymartServerUrl");
        String url = baseUrl + "/retailorder/get/all/products"; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        updateProductsList(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SalesmanCreateOrderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void updateProductsList(String response) {
        JSONArray jsonArray = null;
        try{
            jsonArray = new JSONArray(response);


            // Update recent transactions
            ArrayList<SalesmanCreateOrderItem> products = new ArrayList<>();
            for(int indx=0; indx<jsonArray.length(); indx++) {
                JSONObject product = jsonArray.getJSONObject(indx);

                int productId = product.getInt("productId");
                String productName = product.getString("productName");
                String productCode = product.getString("productCode");
                String productImageUrl = product.getString("productPictureUrl");
                String productHsn = product.getString("hsn");
                String productPurchaseRate = product.getString("productPurchaseRate");
                String productSaleRate = product.getString("productSaleRate");
                String productMrp = product.getString("mrp");
                products.add(new SalesmanCreateOrderItem(productId, productName, productCode, productImageUrl, productHsn, productPurchaseRate, productSaleRate, productMrp));


            }

            createOrderAdapter = new SalesmanCreateOrderAdapter(this, products);
            productsListView.setAdapter(createOrderAdapter);


        }
        catch(JSONException e) {
            Toast.makeText(SalesmanCreateOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
