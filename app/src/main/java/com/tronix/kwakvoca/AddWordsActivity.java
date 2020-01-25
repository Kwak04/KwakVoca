package com.tronix.kwakvoca;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddWordsActivity extends AppCompatActivity {

    final String TAG = "AddWordsActivity";

    LinearLayout background;
    ImageButton done;
    EditText word, meaning;

    FirebaseFirestore db;
    CollectionReference reference;
    WordData wordData;

    FirebaseUser currentUser;

    LinearLayout mainActivityBackground;

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
                    Snackbar.make(background, getApplicationContext().getString(R.string.error_type_word_and_meaning), Snackbar.LENGTH_LONG).show();
                } else if (isWordBlank) {
                    Snackbar.make(background, getApplicationContext().getString(R.string.error_type_word), Snackbar.LENGTH_LONG).show();
                } else if (isMeaningBlank) {
                    Snackbar.make(background, getApplicationContext().getString(R.string.error_type_meaning), Snackbar.LENGTH_LONG).show();
                } else {  // Word and meaning are typed
                    isTyped = true;
                }

                wordData.word = word.getText().toString();
                wordData.meaning = meaning.getText().toString();
                wordData.user = currentUser.getEmail();
                wordData.group = "my group";  // Group feature will be added.
                wordData.uid = currentUser.getUid();

                if (isTyped) {
                    finish();
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.addWord(wordData);
                }
            }
        });
    }

    public void setMainActivityBackground(LinearLayout background) {
        mainActivityBackground = background;
    }
}
