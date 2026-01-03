package com.dairymart.android.dairymartapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CommonDashboardActivity extends AppCompatActivity {

    Button adminBtn;
    Button salesmanBtn;
    Button retailerBtn;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_dashboard); // Create activity_welcome.xml
        adminBtn = findViewById(R.id.admin_button);
        salesmanBtn = findViewById(R.id.salesman_button);
        retailerBtn = findViewById(R.id.retailer_button);

        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommonDashboardActivity.this, AdminDashboardActivity.class));
            }
        });

        salesmanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommonDashboardActivity.this, SalesmanDashboardActivity.class));
            }
        });

        retailerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommonDashboardActivity.this, RetailerDashboardActivity.class));
            }
        });
    }
}
