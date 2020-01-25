package com.tronix.kwakvoca;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AddWordsActivity extends AppCompatActivity {

    LinearLayout background;
    ImageButton done;
    EditText word, meaning;

    FirebaseFirestore db;
    CollectionReference reference;
    WordData wordData;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_words);

        background = findViewById(R.id.layout_background);
        done = findViewById(R.id.btn_done);
        word = findViewById(R.id.edit_word);
        meaning = findViewById(R.id.edit_meaning);

        getWindow().setStatusBarColor(getApplicationContext().getColor(R.color.colorBackground));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();
        reference = db.collection("words");
        wordData = new WordData();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Prevent from adding document with no word data
                boolean isTyped = false;
                boolean isWordBlank = word.getText().toString().equals("");
                boolean isMeaningBlank = meaning.getText().toString().equals("");
                if (isWordBlank && isMeaningBlank) {
                    Snackbar.make(background, R.string.error_type_word_and_meaning, Snackbar.LENGTH_LONG).show();
                } else if (isWordBlank) {
                    Snackbar.make(background, R.string.error_type_word, Snackbar.LENGTH_LONG).show();
                } else if (isMeaningBlank) {
                    Snackbar.make(background, R.string.error_type_meaning, Snackbar.LENGTH_LONG).show();
                } else {  // Word and meaning are typed
                    isTyped = true;
                }

                wordData.word = word.getText().toString();
                wordData.meaning = meaning.getText().toString();
                wordData.user = currentUser.getEmail();
                wordData.group = "my group";  // Group feature will be added.
                wordData.uid = currentUser.getUid();

                if (isTyped) {
                    Intent wordDataIntent = new Intent();
                    Gson gson = new GsonBuilder().create();
                    String stringWordData = gson.toJson(wordData, WordData.class);
                    wordDataIntent.putExtra("wordData", stringWordData);
                    setResult(ActivityCodes.RESULT_ADD_WORD, wordDataIntent);
                    finish();
                }
            }
        });
    }
}
