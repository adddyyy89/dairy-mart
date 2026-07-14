package com.dairymart.android.dairymartapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.dairymart.android.dairymartapplication.adapters.SalesmanDashboardRecentTransactionAdapter;
import com.dairymart.android.dairymartapplication.adapters.SalesmanOrderItem;
import com.dairymart.android.dairymartapplication.adapters.SalesmanOrderItemAdapter;
import com.dairymart.android.dairymartapplication.util.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SalesmanActivityOrdersActivity extends AppCompatActivity {

    Properties properties;

    TextView errorTextView;

    TextView activeOrders;

    TextView totalActiveCrates;

    LinearLayout nav_home;
    LinearLayout nav_activity;
    LinearLayout nav_delivery;
    LinearLayout nav_ledger;

    ListView ordersListView;

    Spinner retailerOptionsSpinner;
    String selectedRetailerId;
    String selectedRetailerName;

    SharedPreferences sharedPreferences;
    int userId;
    String userTypeId;


    Button createOrderBtn;

    ArrayAdapter<String> retailerOptionsAdapter;

    SalesmanOrderItemAdapter orderItemAdapter;

    SearchView searchView;


    JSONArray orders;

    Context context;


    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesman_activity_orders); // Create activity_welcome.xml
        properties = PropertiesUtil.loadConfig(this);
        searchView = findViewById(R.id.searchView);

        sharedPreferences = getSharedPreferences("dairymart", Context.MODE_PRIVATE);
        userId = Integer.parseInt(sharedPreferences.getString("userid", null));
        userTypeId = sharedPreferences.getString("usertypeid", null);

        activeOrders = findViewById(R.id.salesman_order_activity_activeorder);
        totalActiveCrates = findViewById(R.id.salesman_order_activity_totalactivecrates);
        boolean isValidUser = validateUserRole(userTypeId);

        selectedRetailerId = "";
        selectedRetailerName = "";

        //loadData();
        if(isValidUser) {
            nav_home = findViewById(R.id.nav_home);
            nav_activity = findViewById(R.id.nav_activity);
            nav_delivery = findViewById(R.id.nav_delivery);
            nav_ledger = findViewById(R.id.nav_ledger);
            createOrderBtn = findViewById(R.id.createOrder);
            retailerOptionsSpinner = findViewById(R.id.retailerSpinner);

            ordersListView = findViewById(R.id.ordersListView);


            nav_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(SalesmanActivityOrdersActivity.this, SalesmanDashboardActivity.class));

                }
            });

            nav_activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(SalesmanActivityOrdersActivity.this, SalesmanActivityOrdersActivity.class));

                }
            });

            nav_delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(SalesmanActivityOrdersActivity.this, SalesmanDeliveryPendingActivity.class));

                }
            });

            nav_ledger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(SalesmanActivityOrdersActivity.this, SalesmanLedgerDashboardActivity.class));

                }
            });

            createOrderBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SalesmanActivityOrdersActivity.this, SalesmanCreateOrderActivity.class);
                    if(selectedRetailerId != null && !selectedRetailerId.isEmpty()) {
                        intent.putExtra("create_order_for_shop_id", selectedRetailerId);
                        intent.putExtra("create_order_for_shop_name", selectedRetailerName);
                        startActivity(intent);
                    }
                }
            });

            retailerOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    selectedRetailerId = selectedItem.split(" - ")[0];
                    selectedRetailerName = selectedItem.split(" - ")[1];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            loadSalesmanCrates();

            //
            loadDataRetailerList();

            //
            loadOrderList(context);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //orderItemAdapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //orderItemAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
        else {
            Toast.makeText(this, "Not a valid user", Toast.LENGTH_SHORT);
        }


    }

    private void loadOrderList(Context context) {
        String baseUrl = properties.getProperty("dairymartServerUrl");
        String url = baseUrl + "/retailorder/get/salesman/" + userId; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            int activeOrderCnt = 0;
                            orders = new JSONArray(response);
                            ArrayList<SalesmanOrderItem> ordersList = new ArrayList<>();
                            for(int indx=0; indx<orders.length(); indx++) {
                                JSONObject order = orders.getJSONObject(indx);

                                SalesmanOrderItem orderItem = new SalesmanOrderItem();

                                orderItem.setOrderId(order.getInt("orderId"));
                                orderItem.setOrderStatusId(order.getJSONObject("status").getInt("statusId"));
                                if(order.getJSONObject("status").getString("statusDesc") == "NEW"
                                    || order.getJSONObject("status").getString("statusDesc") == "CONFIRMED"
                                    || order.getJSONObject("status").getString("statusDesc") == "DISPATCHED") {
                                    activeOrderCnt++;
                                }
                                orderItem.setOrderTimestamp(order.getString("createdon"));
                                orderItem.setOrderStatusMessage(order.getJSONObject("status").getString("statusDesc"));
                                orderItem.setRetailerName(order.getJSONObject("retailer").getString("shopName"));
                                orderItem.setRetailerId(order.getInt("retailerId"));

                                // Get order amount total = product rate * quantity
                                JSONArray orderItemsJSON = order.getJSONArray("orderDetails");
                                Double orderAmount = 0d;
                                for(int itemNum=0; itemNum<orderItemsJSON.length(); itemNum++) {
                                    JSONObject orderItemProduct = orderItemsJSON.getJSONObject(itemNum);
                                    Double productSaleRate = 0d;
                                    if(!orderItemProduct.getString("saleRate").isEmpty()) {
                                        productSaleRate = Double.parseDouble(orderItemProduct.getString("saleRate"));
                                    }

                                    int productQuantity = Integer.parseInt(orderItemProduct.getString("quantity"));
                                    orderAmount+=productSaleRate * productQuantity;

                                }

                                orderItem.setOrderAmount(orderAmount);

                                ordersList.add(orderItem);
                            }


                            orderItemAdapter = new SalesmanOrderItemAdapter(context, ordersList);
                            ordersListView.setAdapter(orderItemAdapter);

                            activeOrders.setText(String.valueOf(activeOrderCnt));
                        } catch (JSONException e) {
                            Toast.makeText(SalesmanActivityOrdersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SalesmanActivityOrdersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void loadSalesmanCrates() {
        String baseUrl = properties.getProperty("dairymartServerUrl");
        String url = baseUrl + "/crate/assigned/user/" + userId; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String activeCrate = String.valueOf(response);
                            totalActiveCrates.setText(activeCrate);
                        } catch (Exception e) {
                            Toast.makeText(SalesmanActivityOrdersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SalesmanActivityOrdersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private boolean validateUserRole(String userTypeId) {
        if(userTypeId.equalsIgnoreCase("2"))
            return true;
        return false;
    }

    // Update Retailers List to create order
    private void loadDataRetailerList() {
        String baseUrl = properties.getProperty("dairymartServerUrl");
        String url = baseUrl + "/salesmantoretail/get/assignment/salesman/" + userId; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            List<JSONObject> retailerList = new ArrayList<>();
                            List<String> retailerNameList = new ArrayList<>();
                            //retailerNameList.add("Select shop name");
                            for(int indx=0; indx<jsonArray.length(); indx++) {
                                JSONObject retailerJsonObject = jsonArray.getJSONObject(indx);
                                retailerList.add(retailerJsonObject);
                                int retailerId = retailerJsonObject.getInt("retailerId");
                                String retailerName = retailerJsonObject.getJSONObject("retailer").getString("shopName");
                                retailerNameList.add(retailerId + " - " + retailerName);

                            }
                            retailerOptionsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, retailerNameList);
                            retailerOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            retailerOptionsSpinner.setAdapter(retailerOptionsAdapter);


                        } catch (JSONException e) {
                            Toast.makeText(SalesmanActivityOrdersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SalesmanActivityOrdersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
