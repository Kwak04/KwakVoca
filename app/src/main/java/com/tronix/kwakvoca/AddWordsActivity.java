package com.tronix.kwakvoca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class AddWordsActivity extends AppCompatActivity {

    final String TAG = "AddWordsActivity";

    LinearLayout background, inputLayout;
    ImageButton done;
    EditText word;

    FirebaseFirestore db;
    CollectionReference reference;
    WordData wordData;

    FirebaseUser currentUser;

    List<EditText> meanings = new ArrayList<>();
    int fieldCount = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_words);

        background = findViewById(R.id.layout_background);
        inputLayout = findViewById(R.id.layout_input);
        done = findViewById(R.id.btn_done);
        word = findViewById(R.id.edit_word);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();
        reference = db.collection("words");
        wordData = new WordData();

        getWindow().setStatusBarColor(getApplicationContext().getColor(R.color.colorBackground));

        addMeaningField();  // Add first meaning field

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addWord(meanings);
            }
        });
    }

    private void finishActivityWithData() {
        Intent wordDataIntent = new Intent();
        Gson gson = new GsonBuilder().create();

        // Convert WordData to Json String
        String stringWordData = gson.toJson(wordData, WordData.class);

        wordDataIntent.putExtra("wordData", stringWordData);
        setResult(ActivityCodes.RESULT_ADD_WORD, wordDataIntent);
        finish();
    }

    private void addMeaningField() {
        LayoutInflater inflater = getLayoutInflater();
        View meaningField = inflater.inflate(R.layout.layout_add_meaning, inputLayout, false);
        EditText meaning = meaningField.findViewById(R.id.edit_meaning);

        meaning.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        meaning.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    addMeaningField();
                    // TODO Move focus to new field
                    return true;
                }
                return false;
            }
        });
        fieldCount += 1;
        meanings.add(meaning);
        inputLayout.addView(meaningField);

        Log.d(TAG, "added a meaning field: fieldCount=" + fieldCount);
    }

    public void removeMeaningField(View v) {
        if (fieldCount > 1) {
            inputLayout.removeView((View) v.getParent());
            fieldCount -= 1;
            EditText meaning = ((View) v.getParent()).findViewById(R.id.edit_meaning);
            meanings.remove(meaning);
        } else {
            Snackbar.make(background, R.string.error_minimum_meaning_field, Snackbar.LENGTH_LONG).show();
        }

        Log.d(TAG, "removed a meaning field: fieldCount=" + fieldCount);
    }

    private void addWord(List<EditText> meanings) {
        String meaningData;

        // Prevent from adding document with no word data
        boolean isTyped = false;
        boolean isWordBlank = word.getText().toString().equals("");
        boolean isMeaningBlank = false;
        for (EditText meaning : meanings) {
            String meaningText = meaning.getText().toString();
            if (meaningText.equals("")) {
                isMeaningBlank = true;
            }
        }

        if (isWordBlank && isMeaningBlank) {
            Snackbar.make(background, R.string.error_type_word_and_meaning, Snackbar.LENGTH_LONG).show();
        } else if (isWordBlank) {
            Snackbar.make(background, R.string.error_type_word, Snackbar.LENGTH_LONG).show();
        } else if (isMeaningBlank) {
            Snackbar.make(background, R.string.error_type_meaning, Snackbar.LENGTH_LONG).show();
        } else {  // Word and meaning are typed
            isTyped = true;
        }

        // Convert multisense meaning to text
        if (meanings.size() > 1) {
            String space = "";
            StringBuilder meaningAll = new StringBuilder();
            String meaningText;
            int count = 1;
            for (EditText meaning : meanings) {
                meaningText = space + count + ". " + meaning.getText().toString();
                meaningAll.append(meaningText);
                space = "\n";
                count += 1;
            }
            meaningData = meaningAll.toString();
        } else {
            meaningData = meanings.get(0).getText().toString();
        }

        // Adding word information to data
        wordData.word = word.getText().toString();
        wordData.meaning = meaningData;
        wordData.user = currentUser.getEmail();
        wordData.group = "my group";  // Group feature will be added.
        wordData.uid = currentUser.getUid();

        if (isTyped) {
            finishActivityWithData();
        }
    }
}
