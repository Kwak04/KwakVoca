package com.tronix.kwakvoca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    LinearLayout background;
    RecyclerView wordList;
    ImageButton addWords;

    FirebaseAuth auth;
    FirebaseUser currentUser;

    FirebaseFirestore db;
    CollectionReference reference;

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

        db = FirebaseFirestore.getInstance();
        reference = db.collection("words");


        // Word list

        wordList.setLayoutManager(new LinearLayoutManager(this));
        wordList.setHasFixedSize(true);

        reference.whereEqualTo("uid", currentUser.getUid())  // Get words from documents where uid is same with user's one
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Failed to get words", e);
                            Snackbar.make(background, "단어를 불러오지 못했습니다.", Snackbar.LENGTH_LONG);
                        }

                        List<WordData> wordDataList = new ArrayList<>();

                        if (snapshot != null) {
                            for (QueryDocumentSnapshot doc : snapshot) {
                                Log.d(TAG, "word: " + doc.getString("word") + "  meaning: " + doc.getString("meaning"));

                                WordData wordData = new WordData();
                                wordData.word = doc.getString("word");
                                wordData.meaning = doc.getString("meaning");
                                wordData.documentId = doc.getId();

                                wordDataList.add(wordData);

                                // Sort words in abc order
                                Collections.sort(wordDataList, new Comparator<WordData>() {
                                    @Override
                                    public int compare(WordData o1, WordData o2) {
                                        return Integer.compare(o1.word.compareTo(o2.word), 0);
                                    }
                                });

                                wordList.setAdapter(new WordListAdapter(wordDataList, MainActivity.this, background));
                            }
                        }
                    }
                });


        // Button - Add words
        addWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddWordsActivity.class);
                startActivityForResult(intent, ActivityCodes.REQUEST_ADD_WORD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityCodes.REQUEST_ADD_WORD) {  // AddWordsActivity
            if (resultCode == ActivityCodes.RESULT_ADDED_WORD) {
                Snackbar.make(background, "단어를 추가했습니다.", Snackbar.LENGTH_SHORT).show();
            } else if (resultCode == ActivityCodes.RESULT_FAILED_ADDING_WORD) {
                Snackbar.make(background, "단어를 추가하지 못했습니다.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    // DeleteWordDialog will call this function
    public void deleteWord(String documentId, final LinearLayout background) {
        Log.d(TAG, "deleteWord: document id=" + documentId);

        db = FirebaseFirestore.getInstance();
        reference = db.collection("words");

        reference.document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(background, R.string.result_delete_word, Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting word document", e);
                    }
                });
    }
}
