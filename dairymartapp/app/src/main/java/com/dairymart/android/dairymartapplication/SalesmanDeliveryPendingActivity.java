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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dairymart.android.dairymartapplication.adapters.SalesmanDashboardRecentTransactionAdapter;
import com.dairymart.android.dairymartapplication.adapters.SalesmanPendingOrder;
import com.dairymart.android.dairymartapplication.adapters.SalesmanPendingOrderAdapter;
import com.dairymart.android.dairymartapplication.util.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SalesmanDeliveryPendingActivity extends AppCompatActivity {

    TextView errorTextView;
    TextView salesmanName;

    LinearLayout nav_home;
    LinearLayout nav_activity;
    LinearLayout nav_delivery;
    LinearLayout nav_ledger;

    ListView pendingDeliveryView;

    JSONObject pendingDeliveries;
    int userId;
    String userTypeId;
    SharedPreferences sharedPreferences;
    Properties properties;

    SalesmanPendingOrderAdapter salesmanPendingOrderListAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesman_delivery_pending); // Create activity_welcome.xml

        sharedPreferences = getSharedPreferences("dairymart", Context.MODE_PRIVATE);
        userId = Integer.parseInt(sharedPreferences.getString("userid", null));
        userTypeId = sharedPreferences.getString("usertypeid", null);
        properties = PropertiesUtil.loadConfig(this);
        //loadData();
        nav_home = findViewById(R.id.salesman_delivery_nav_home);
        nav_activity = findViewById(R.id.salesman_delivery_nav_activity);
        nav_delivery = findViewById(R.id.salesman_delivery_nav_delivery);
        nav_ledger = findViewById(R.id.salesman_delivery_nav_ledger);
        TextView pendingTab = findViewById(R.id.pendingTab);
        TextView deliveredTab = findViewById(R.id.deliveredTab);
        salesmanName=findViewById(R.id.salesman_pending_activity_salesmanname);

        pendingDeliveryView = findViewById(R.id.pendingDeliveryView);
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

        loadUserData();

        loadOrderData(this);

    }

    private void loadUserData() {
        String baseUrl = properties.getProperty("dairymartServerUrl");
        String url = baseUrl + "/user/get/" + userId; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject userData = new JSONObject(response);
                            salesmanName.setText(userData.getString("firstName") + " " + userData.getString("lastName"));
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Toast.makeText(SalesmanDashb, error.getMessage() != null ? error.getMessage() : "On Error Response", Toast.LENGTH_SHORT).show();
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

    private void loadOrderData(Context context) {
        String baseUrl = properties.getProperty("dairymartServerUrl");
        String url = baseUrl + "/retailorder/get/salesman/" + userId; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray ordersArr = new JSONArray(response);
                            List<SalesmanPendingOrder> pendingOrderList = new ArrayList<>();
                            for(int i=0; i<ordersArr.length(); i++) {
                                JSONObject order = ordersArr.getJSONObject(i);
                                SalesmanPendingOrder pendingOrder = new SalesmanPendingOrder();
                                pendingOrder.setBranchId(order.getInt("branchId"));


                                pendingOrder.setOrderDate(order.getString("orderDate"));
                                pendingOrder.setOrderId(order.getInt("orderId"));
                                pendingOrder.setOrderStatusId(order.getInt("orderStatusId"));
                                pendingOrder.setCreatedBy(order.getInt("createdBy"));
                                pendingOrder.setRetailerId(order.getInt("retailerId"));
                                pendingOrder.setCreatedOn(order.getString("createdon"));
                                pendingOrder.setLastUpdated(order.getString("lastUpdated"));

                                // OrderStatus

                                SalesmanPendingOrder.OrderStatus orderStatus = new SalesmanPendingOrder.OrderStatus();
                                orderStatus.setStatusId(order.getJSONObject("status").getInt("statusId"));
                                orderStatus.setStatusDesc(order.getJSONObject("status").getString("statusDesc"));
                                orderStatus.setLastUpdated(order.getJSONObject("status").getString("lastUpdated"));
                                pendingOrder.setOrderStatus(orderStatus);

                                // Order Detail
                                List<SalesmanPendingOrder.OrderDetail> orderDetails = new ArrayList<>();
                                JSONArray orderDetailsArr = order.getJSONArray("orderDetails");
                                for(int j=0;j<orderDetailsArr.length();j++) {
                                    JSONObject orderObj = orderDetailsArr.getJSONObject(j);
                                    SalesmanPendingOrder.OrderDetail orderDetail = new SalesmanPendingOrder.OrderDetail();
                                    orderDetail.setHsn(orderObj.getString("hsn"));
                                    orderDetail.setLastUpdated(orderObj.getString("lastUpdated"));
                                    orderDetail.setQuantity(orderObj.getString("quantity"));
                                    orderDetail.setUnit(orderObj.getString("unit"));
                                    orderDetail.setProductCode(orderObj.getString("productCode"));
                                    orderDetail.setPurchaseRate(orderObj.getString("purchaseRate"));
                                    orderDetail.setSaleRate(orderObj.getString("saleRate"));
                                    orderDetail.setOrderId(orderObj.getInt("orderId"));

                                    orderDetails.add(orderDetail);
                                }
                                pendingOrder.setOrderDetails(orderDetails);

                                // Retailer detail
                                SalesmanPendingOrder.RetailerDetail retailerDetail = new SalesmanPendingOrder.RetailerDetail();
                                JSONObject retailerObj = order.getJSONObject("retailer");
                                retailerDetail.setAadharNumber(retailerObj.getJSONObject("gst").getString("aadharNumber"));
                                retailerDetail.setCityName(retailerObj.getJSONObject("address").getJSONObject("city").getString("cityName"));
                                retailerDetail.setCrateCount(retailerObj.getJSONObject("owner").getInt("crateCount"));
                                retailerDetail.setFirstName(retailerObj.getJSONObject("owner").getString("firstName"));
                                retailerDetail.setFullAddress(retailerObj.getJSONObject("address").getString("fullAddress"));
                                retailerDetail.setGstId(retailerObj.getJSONObject("gst").getInt("gstId"));
                                retailerDetail.setGstNumber(retailerObj.getJSONObject("gst").getString("gstNumber"));
                                retailerDetail.setLastName(retailerObj.getJSONObject("owner").getString("lastName"));
                                retailerDetail.setPhoneNumber(retailerObj.getJSONObject("owner").getString("phoneNumber"));
                                retailerDetail.setPanNumber(retailerObj.getJSONObject("gst").getString("panNumber"));
                                retailerDetail.setRetailerId(retailerObj.getJSONObject("owner").getInt("userId"));
                                retailerDetail.setShopId(retailerObj.getInt("shopId"));
                                retailerDetail.setShopName(retailerObj.getString("shopName"));
                                retailerDetail.setStateName(retailerObj.getJSONObject("address").getJSONObject("city").getJSONObject("state").getString("stateName"));
                                retailerDetail.setUserId(retailerObj.getJSONObject("owner").getInt("userId"));

                                pendingOrder.setRetailerDetail(retailerDetail);
                                pendingOrderList.add(pendingOrder);
                            }

                            salesmanPendingOrderListAdapter = new SalesmanPendingOrderAdapter(context, pendingOrderList);
                            pendingDeliveryView.setAdapter(salesmanPendingOrderListAdapter);
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Toast.makeText(SalesmanDashb, error.getMessage() != null ? error.getMessage() : "On Error Response", Toast.LENGTH_SHORT).show();
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

}
