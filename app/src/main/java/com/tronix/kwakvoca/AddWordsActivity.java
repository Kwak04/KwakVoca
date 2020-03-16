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
import java.util.Objects;

public class AddWordsActivity extends AppCompatActivity {

    static final int MODE_ADD_WORD = 1;
    static final int MODE_EDIT_WORD = 2;

    int mode;

    final String TAG = "AddWordsActivity";

    TextView title;
    LinearLayout background, inputLayout;
    ImageButton done, help;
    EditText word;

    FirebaseFirestore db;
    CollectionReference reference;

    String documentId;

    FirebaseUser currentUser;

    List<EditText> meanings = new ArrayList<>();
    int fieldCount = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_words);

        title = findViewById(R.id.tv_mode_title);
        background = findViewById(R.id.layout_background);
        inputLayout = findViewById(R.id.layout_input);
        done = findViewById(R.id.btn_done);
        help = findViewById(R.id.btn_help);
        word = findViewById(R.id.edit_word);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();
        reference = db.collection("words");

        getWindow().setStatusBarColor(getApplicationContext().getColor(R.color.colorBackground));

        // Modes
        mode = Objects.requireNonNull(getIntent().getExtras()).getInt("mode");
        startByMode(mode);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addWord(meanings);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogContents contents = new DialogContents();
                contents.title = getString(R.string.action_help);
                contents.text = getString(R.string.dialog_text_help_multisense);
                contents.yes = getString(R.string.action_ok);
                contents.iconResId = R.drawable.ic_help_white;

                DefaultDialog dialog = new DefaultDialog(contents, DefaultDialog.DEFAULT,
                        AddWordsActivity.this);
                dialog.show();
            }
        });
    }

    private void startByMode(int mode) {
        setTitleByMode(mode);

        if (mode == MODE_ADD_WORD) {
            addMeaningField();  // Add first meaning field
        } else if (mode == MODE_EDIT_WORD) {
            String stringWordData =
                    Objects.requireNonNull(getIntent().getExtras()).getString("data");
            Gson gson = new GsonBuilder().create();
            WordData wordData = gson.fromJson(stringWordData, WordData.class);

            showExistingData(wordData);
        }
    }

    private void setTitleByMode(int mode) {
        if (mode == MODE_ADD_WORD) {
            title.setText(R.string.title_add_word);
        } else if (mode == MODE_EDIT_WORD) {
            title.setText(R.string.title_edit_word);
        }
    }

    private void showExistingData(WordData data) {
        word.setText(data.word);
        documentId = data.documentId;

        for (int i = 0; i < data.meanings.size(); i++) {
            addMeaningField();
            meanings.get(i).setText(data.meanings.get(i));
        }
    }

    private void finishActivityWithData(WordData wordData) {
        Intent wordDataIntent = new Intent();
        Gson gson = new GsonBuilder().create();

        // Convert WordData to Json String
        String stringWordData = gson.toJson(wordData, WordData.class);

        wordDataIntent.putExtra("wordData", stringWordData);

        if (mode == MODE_ADD_WORD) {
            setResult(ActivityCodes.RESULT_ADD_WORD, wordDataIntent);
        } else if (mode == MODE_EDIT_WORD) {
            setResult(ActivityCodes.RESULT_EDIT_WORD, wordDataIntent);
        }
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
        List<String> meaningData = new ArrayList<>();

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
            for (EditText meaning : meanings) {
                meaningData.add(meaning.getText().toString());
            }
        } else {
            meaningData.add(meanings.get(0).getText().toString());
        }

        // Adding word information to data
        WordData wordData = new WordData();
        wordData.word = word.getText().toString();
        wordData.meanings = meaningData;
        wordData.user = currentUser.getEmail();
        wordData.group = "my group";  // Group feature will be added.
        wordData.uid = currentUser.getUid();
        if (mode == MODE_EDIT_WORD) {  // Set document id for existing word
            wordData.documentId = documentId;
        }

        if (isTyped) {
            finishActivityWithData(wordData);
        }
    }
}
