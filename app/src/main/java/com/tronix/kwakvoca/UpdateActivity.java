package com.tronix.kwakvoca;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        getWindow().setStatusBarColor(getColor(R.color.colorBackground));
    }
}
