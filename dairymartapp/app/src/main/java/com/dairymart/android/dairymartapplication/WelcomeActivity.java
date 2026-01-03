package com.dairymart.android.dairymartapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    TextView errorTextView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome); // Create activity_welcome.xml
        //errorTextView = findViewById(R.id.welcome_text);
        //errorTextView.setText("Welcome !!");
    }
}
