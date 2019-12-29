package com.tronix.kwakvoca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    LinearLayout background;
    RecyclerView wordList;
    ImageButton addWords;

    final String TAG = "MainActivity";

    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById(R.id.layout_background);
        wordList = findViewById(R.id.word_list);
        addWords = findViewById(R.id.btn_add_words);

        getWindow().setStatusBarColor(getApplicationContext().getColor(R.color.colorBackground));

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Word list
        wordList.setLayoutManager(new LinearLayoutManager(this));
        wordList.setHasFixedSize(true);
        wordList.setAdapter(new WordListAdapter());

        // Button - Add words
        addWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddWordsActivity.class);
                startActivity(intent);
            }
        });
    }
}
