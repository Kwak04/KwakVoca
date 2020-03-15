package com.tronix.kwakvoca;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    ImageButton addWords, settings;

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
        settings = findViewById(R.id.btn_settings);

        getWindow().setStatusBarColor(getApplicationContext().getColor(R.color.colorBackground));

        checkForUpdates();

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
                Intent intent = new Intent(getApplicationContext(), AddWordsActivity.class);
                intent.putExtra("mode", AddWordsActivity.MODE_ADD_WORD);
                startActivityForResult(intent, ActivityCodes.REQUEST_ADD_WORD);
            }
        });

        // Button - Check for updates
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityCodes.REQUEST_ADD_WORD) {  // AddWordsActivity - Add word
            if (resultCode == ActivityCodes.RESULT_ADD_WORD) {
                String stringWordData = Objects.requireNonNull(data).getStringExtra("wordData");
                Gson gson = new GsonBuilder().create();

                // Put String back to WordData
                WordData wordData = gson.fromJson(stringWordData, WordData.class);

                addWord(wordData, background);
            }
        }

        if (requestCode == ActivityCodes.REQUEST_EDIT_WORD) {  // AddWordsActivity - Edit word
            if (resultCode == ActivityCodes.RESULT_EDIT_WORD) {
                String stringWordData = Objects.requireNonNull(data).getStringExtra("wordData");
                Gson gson = new GsonBuilder().create();

                // Put String back to WordData
                WordData wordData = gson.fromJson(stringWordData, WordData.class);

                editWord(wordData, background);
            }
        }
    }


    // MainActivity.onActivityResult will call this function
    public void addWord(final WordData data, final LinearLayout background) {
        reference = FirebaseFirestore.getInstance().collection("words");

        reference.document()
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Word successfully added. word=" + data.word);
                        Snackbar.make(background,
                                R.string.result_main_added_word, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding word", e);
                        Snackbar.make(background,
                                R.string.error_main_adding_word, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    // MainActivity.onActivityResult will call this function
    public void editWord(final WordData data, final LinearLayout background) {
        reference = FirebaseFirestore.getInstance().collection("words");

        reference.document(data.documentId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Word successfully edited. word=" + data.meaning);
                        Snackbar.make(background,
                                R.string.result_main_edited_word, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error editing word", e);
                        Snackbar.make(background,
                                R.string.error_main_editing_word, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    // DeleteWordDialog will call this function
    public void deleteWord(final WordData data, final LinearLayout background) {
        Log.d(TAG, "deleteWord: document id=" + data.documentId);

        reference = FirebaseFirestore.getInstance().collection("words");

        reference.document(data.documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(background, R.string.result_main_deleted_word, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action_main_restore_word, new View.OnClickListener() {
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

    private void restoreWord(WordData data, final LinearLayout background) {
        reference = FirebaseFirestore.getInstance().collection("words");

        reference.document(data.documentId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(background, R.string.result_main_restored_word, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error restoring word document", e);
                    }
                });
    }

    public void copyWord(Context context, LinearLayout background, WordData data) {

        String meaning;

        if (data.meaning.contains("\n")) {
            String[] meanings = data.meaning.split("\n");
            String builderString;
            String space = "";
            StringBuilder meaningBuilder = new StringBuilder();
            for (String string : meanings) {
                builderString = space + string;
                meaningBuilder.append(builderString);
                space = " ";
            }
            meaning = meaningBuilder.toString();
        } else {
            meaning = data.meaning;
        }

        String clipText = data.word + ": " + meaning;
        ClipData clip = ClipData.newPlainText(data.word, clipText);
        ClipboardManager manager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            manager.setPrimaryClip(clip);
        } else {
            Log.d(TAG, "copyWord: ClipboardManager=null");
        }

        Snackbar.make(background, R.string.result_main_copied_word, Snackbar.LENGTH_SHORT).show();
    }

    // WordActionDialog will call this function
    public void moveToEditWord(WordData wordData, Context applicationContext) {
        Gson gson = new GsonBuilder().create();
        String stringWordData = gson.toJson(wordData, WordData.class);

        Intent intent = new Intent(applicationContext, AddWordsActivity.class);
        intent.putExtra("mode", AddWordsActivity.MODE_EDIT_WORD);
        intent.putExtra("data", stringWordData);
        ((Activity) applicationContext)
                .startActivityForResult(intent, ActivityCodes.REQUEST_EDIT_WORD);
    }

    private void checkForUpdates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("versions").document("new");

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Failed to check for updates.", e);
                }

                if (snapshot != null && snapshot.exists()) {
                    VersionData data = snapshot.toObject(VersionData.class);

                    if (data != null) {
                        Log.d(TAG, "new version: " + data.version);
                        updateUI(data);
                    }
                } else {
                    Log.d(TAG, "Snapshot: null");
                }
            }
        });
    }

    private void updateUI(VersionData newVersionData) {
        if (Versions.CURRENT_VERSION_CODE < newVersionData.versionInt) {
            settings.setImageResource(R.drawable.ic_settings_new_update);
        } else {
            settings.setImageResource(R.drawable.ic_settings_white);
        }
    }
}
