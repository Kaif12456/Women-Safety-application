package com.a.womensafety;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Tips extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        getSupportActionBar().setTitle("Safety Tips");
    }

}
