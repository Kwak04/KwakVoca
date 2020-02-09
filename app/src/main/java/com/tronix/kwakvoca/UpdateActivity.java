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

import java.util.List;

public class UpdateActivity extends AppCompatActivity {

    final String TAG = "UpdateActivity";
    String updateUrl = "https://github.com/Kwak04/KwakVoca/releases";

    TextView newVersion, currentVersion, description, improvements, date;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        newVersion = findViewById(R.id.text_new_version);
        currentVersion = findViewById(R.id.text_current_version);
        update = findViewById(R.id.btn_update);
        description = findViewById(R.id.text_description);
        improvements = findViewById(R.id.text_improvements);
        date = findViewById(R.id.text_update_date);

        getWindow().setStatusBarColor(getColor(R.color.colorBackground));

        initializeUI();

        checkForUpdates();

        // Update button
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
                startActivity(intent);
            }
        });
    }

    private void initializeUI() {
        newVersion.setText(R.string.sample_no_text);
        currentVersion.setTextColor(getColor(R.color.colorOrange));
        update.setBackground(getDrawable(R.drawable.button_update_unavailable));
        update.setText(R.string.action_see_version_logs);

        String descriptionText = "야호! 현재 콱보카의 최신 버전을 사용하고 있어요!";
        description.setText(descriptionText);
        improvements.setText(null);
        date.setText(null);
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

        // Current & New version
        currentVersion.setText(Versions.CURRENT_VERSION);
        newVersion.setText(newVersionData.version);


        if (Versions.CURRENT_VERSION_CODE < newVersionData.versionInt) {

            // Set updateUrl
            updateUrl = "https://github.com/Kwak04/KwakVoca/releases/tag/" + newVersionData.version;

            // Set different color and text
            currentVersion.setTextColor(getColor(R.color.colorImportantRed));
            update.setBackground(getDrawable(R.drawable.button_update_available));
            update.setText(R.string.action_update);

            // New version description
            String descriptionText =
                    "콱보카 "+ newVersionData.version + "에서 추가된 기능들이에요.";
            description.setText(descriptionText);

            // Version improvements
            List<String> improvements = newVersionData.improvements;
            StringBuilder improvementsText = new StringBuilder();
            for (int featureNumber = 0; featureNumber < improvements.size(); featureNumber++) {
                String improvement = improvements.get(featureNumber);
                improvementsText.append("● ");
                improvementsText.append(improvement);
                if (featureNumber != improvements.size() - 1) {  // Doesn't add "\n" in the last line
                    improvementsText.append("\n");
                }
            }
            this.improvements.setText(improvementsText);

            // Release date
            String dateText = "배포일: " + newVersionData.date;
            date.setText(dateText);
        }
    }
}
