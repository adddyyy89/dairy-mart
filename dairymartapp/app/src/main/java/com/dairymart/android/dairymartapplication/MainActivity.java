package com.dairymart.android.dairymartapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.dairymart.android.dairymartapplication.util.PropertiesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    Properties properties;
    EditText phoneNumberEditText, passwordEditText;
    Button loginButton;
    //TextView errorTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        properties = PropertiesUtil.loadConfig(this);

        phoneNumberEditText = findViewById(R.id.phone_number);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        //errorTextView = findViewById(R.id.error_text);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                login(phoneNumber, password);
            }
        });
    }

    private void login(String phoneNumber, String password) {

        String baseUrl = properties.getProperty("dairymartServerUrl");

        String url = baseUrl + "/auth/login"; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;

                        try{
                            jsonObject = new JSONObject(response);
                            SharedPreferences sharedPreferences = getSharedPreferences("dairymart", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String auth = "Basic " + Base64.encodeToString(new StringBuilder(phoneNumber + ":" + password).toString().getBytes(), Base64.NO_WRAP);
                            editor.putString("auth", auth);
                            editor.putString("userid", jsonObject.getString("userId"));
                            editor.putString("usertypeid", String.valueOf(jsonObject.get("role")));
                            editor.apply();

                            // Handle successful login
                            Intent intent = null;
                            String userTypeId = String.valueOf(jsonObject.get("role"));
                            if(userTypeId.equalsIgnoreCase("1")) {
                                Toast.makeText(MainActivity.this, "Admin screen", Toast.LENGTH_SHORT).show();
                                intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                            }
                            else if(userTypeId.equalsIgnoreCase("2")) {
                                Toast.makeText(MainActivity.this, "Salesman screen", Toast.LENGTH_SHORT).show();
                                intent = new Intent(MainActivity.this, SalesmanDashboardActivity.class);
                            }
                            else if(userTypeId.equalsIgnoreCase("3")) {
                                Toast.makeText(MainActivity.this, "Retailer screen", Toast.LENGTH_SHORT).show();
                                intent = new Intent(MainActivity.this, RetailerDashboardActivity.class);
                            }

                            startActivity(intent);
                        }
                        catch(JSONException e){
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Handle login error
                        //errorTextView.setText(error.getMessage());
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        // Error or not open the next page (REMOVE LATER)
                        // Handle successful login
                        Intent intent = new Intent(MainActivity.this, CommonDashboardActivity.class);
                        startActivity(intent);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = phoneNumber + ":" + password;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject reqBody = new JSONObject();
                try{
                    reqBody.put("phoneNumber", phoneNumber);
                    reqBody.put("password", password);
                } catch(JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                return reqBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(request);
    }
}