package com.tronix.kwakvoca;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {

    final String UPDATE_URL = "https://github.com/Kwak04/KwakVoca/releases";
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        update = findViewById(R.id.btn_update);

        getWindow().setStatusBarColor(getColor(R.color.colorBackground));

        // Update button
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(UPDATE_URL));
                startActivity(intent);
            }
        });
    }
}
