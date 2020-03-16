package com.tronix.kwakvoca;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;
import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {

    final String TAG = "UpdateActivity";
    String updateUrl = "https://github.com/Kwak04/KwakVoca/releases";

    TextView newVersion, currentVersion, description, improvements, date;
    Button update;
    ImageButton help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        newVersion = findViewById(R.id.text_new_version);
        currentVersion = findViewById(R.id.text_current_version);
        update = findViewById(R.id.btn_update);
        help = findViewById(R.id.btn_help);
        description = findViewById(R.id.text_description);
        improvements = findViewById(R.id.text_improvements);
        date = findViewById(R.id.text_update_date);

        getWindow().setStatusBarColor(getColor(R.color.colorBackground));

        initializeUI();

        checkForUpdates();

        // Help button
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogContents contents = new DialogContents();
                contents.title = getString(R.string.action_help);
                contents.text = getString(R.string.dialog_text_help_update);
                contents.yes = getString(R.string.action_ok);
                contents.iconResId = R.drawable.ic_help_white;

                DefaultDialog dialog = new DefaultDialog(contents, DefaultDialog.DEFAULT,
                        UpdateActivity.this);
                dialog.show();
            }
        });

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
        help.setBackground(null);
        help.setEnabled(false);

        String descriptionText = getString(R.string.text_current_version_description);
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

            // Show help button
            help.setBackground(getDrawable(R.drawable.button_help));
            help.setEnabled(true);

            // Set updateUrl
            updateUrl = "https://github.com/Kwak04/KwakVoca/releases/tag/" + newVersionData.version;

            // Set different color and text
            currentVersion.setTextColor(getColor(R.color.colorImportantRed));
            update.setBackground(getDrawable(R.drawable.button_update_available));
            update.setText(R.string.action_update);

            String language = Locale.getDefault().getLanguage();

            // New version description
            String descriptionText;
            if (language.equals("en")) {
                descriptionText =
                        "These are the features added in KwakVoca " + newVersionData.version;
            } else {
                descriptionText = "콱보카 " + newVersionData.version + "에서 추가된 기능들이에요.";
            }
            description.setText(descriptionText);

            // Version improvements
            List<String> improvements;
            if (language.equals("en")) {
                improvements = newVersionData.improvements_en;
            } else {
                improvements = newVersionData.improvements;
            }
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
            String dateText;
            if (language.equals("en")) {
                dateText = "Release date: " + newVersionData.date;
            } else {
                dateText = "배포일: " + newVersionData.date;
            }
            date.setText(dateText);
        }
    }
}
