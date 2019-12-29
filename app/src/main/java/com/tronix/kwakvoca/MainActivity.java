package com.tronix.kwakvoca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    LinearLayout background;
    RecyclerView wordList;

    final String TAG = "MainActivity";

    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById(R.id.layout_background);
        wordList = findViewById(R.id.word_list);

        getWindow().setStatusBarColor(getApplicationContext().getColor(R.color.colorBackground));

        //
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        updateUI(currentUser);
        Log.d(TAG, "onCreate: currentUser=" + currentUser);

        // Word list
        wordList.setLayoutManager(new LinearLayoutManager(this));
        wordList.setHasFixedSize(true);
        wordList.setAdapter(new WordListAdapter());
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Snackbar.make(background, "로그인에 실패하였습니다.", Snackbar.LENGTH_LONG).show();
        }
    }
}
