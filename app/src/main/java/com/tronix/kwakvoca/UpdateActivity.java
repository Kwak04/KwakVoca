package com.tronix.kwakvoca;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UpdateActivity extends AppCompatActivity {

    final String UPDATE_URL = "https://github.com/Kwak04/KwakVoca/releases";
    Button update;
    final String TAG = "UpdateActivity";
    TextView newVersion, currentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        newVersion = findViewById(R.id.text_new_version);
        currentVersion = findViewById(R.id.text_current_version);
        update = findViewById(R.id.btn_update);

        getWindow().setStatusBarColor(getColor(R.color.colorBackground));

        checkForUpdates();
        currentVersion.setText(Versions.CURRENT_VERSION);

        // Update button
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(UPDATE_URL));
                startActivity(intent);
            }
        });
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
                        newVersion.setText(data.version);
                    }
                } else {
                    Log.d(TAG, "Snapshot: null");
                }
            }
        });
    }
}
