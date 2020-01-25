package com.tronix.kwakvoca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
                                wordData.user = doc.getString("user");
                                wordData.group = doc.getString("group");
                                wordData.uid = doc.getString("uid");
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
                // Send background to show Snackbar
                AddWordsActivity addWordsActivity = new AddWordsActivity();
                addWordsActivity.setMainActivityBackground(background);
                Intent intent = new Intent(getApplicationContext(), AddWordsActivity.class);
                startActivityForResult(intent, ActivityCodes.REQUEST_ADD_WORD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityCodes.REQUEST_ADD_WORD) {  // AddWordsActivity
            if (resultCode == ActivityCodes.RESULT_ADD_WORD) {
                String stringWordData = Objects.requireNonNull(data).getStringExtra("wordData");
                Gson gson = new GsonBuilder().create();
                WordData wordData = gson.fromJson(stringWordData, WordData.class);
                addWord(wordData, background);
            }
        }
    }


    // AddWordsActivity will call this function
    public void addWord(final WordData data, final LinearLayout background) {
        db = FirebaseFirestore.getInstance();
        reference = db.collection("words");

        reference.document()
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Word successfully added. word=" + data.word);
                        Snackbar.make(background, R.string.result_add_word, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding word", e);
                        Snackbar.make(background, R.string.result_failed_add_word, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    // DeleteWordDialog will call this function
    public void deleteWord(final WordData data, final LinearLayout background) {
        Log.d(TAG, "deleteWord: document id=" + data.documentId);

        db = FirebaseFirestore.getInstance();
        reference = db.collection("words");

        reference.document(data.documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(background, R.string.result_delete_word, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action_restore_word, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d(TAG, "Snackbar: Restore button clicked");
                                        restoreWord(data, background);
                                    }
                                })
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting word document", e);
                    }
                });
    }

    public void restoreWord(WordData data, final LinearLayout background) {
        db = FirebaseFirestore.getInstance();
        reference = db.collection("words");

        reference.document(data.documentId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(background, R.string.result_restore_word, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error restoring word document", e);
                    }
                });
    }
}
