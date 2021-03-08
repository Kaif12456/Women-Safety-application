package com.a.womensafety;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SafetyThreat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_threat);

        getSupportActionBar().setTitle("Avoiding Threat");
    }
}
