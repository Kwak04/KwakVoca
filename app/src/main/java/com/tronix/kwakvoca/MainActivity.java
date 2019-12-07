package com.tronix.kwakvoca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    RecyclerView wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordList = findViewById(R.id.word_list);

        getWindow().setStatusBarColor(getApplicationContext().getColor(R.color.colorBackground));

        // Word list
        wordList.setLayoutManager(new LinearLayoutManager(this));
        wordList.setHasFixedSize(true);
        wordList.setAdapter(new WordListAdapter());
    }
}
