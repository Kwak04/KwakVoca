package com.tronix.kwakvoca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AddWordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_words);

        getWindow().setStatusBarColor(getApplicationContext().getColor(R.color.colorBackground));
    }
}
