package com.tronix.kwakvoca;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddWordsActivity extends AppCompatActivity {

    final String TAG = "AddWordsActivity";

    ImageButton done;
    EditText word, meaning;

    FirebaseFirestore db;
    CollectionReference reference;
    WordData wordData;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_words);

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
                wordData.word = word.getText().toString();
                wordData.meaning = meaning.getText().toString();
                wordData.user = currentUser.getEmail();
                wordData.group = "my group";  // Group feature will be added.
                wordData.uid = currentUser.getUid();

                reference.document().set(wordData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "Adding words completed.");
                                setResult(ActivityCodes.RESULT_ADDED_WORD);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Adding words failed", e);
                                setResult(ActivityCodes.RESULT_FAILED_ADDING_WORD);
                                finish();
                            }
                        });
            }
        });
    }
}
