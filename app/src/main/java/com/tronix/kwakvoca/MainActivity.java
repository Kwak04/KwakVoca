package com.tronix.kwakvoca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    LinearLayout background;
    RecyclerView wordList;

    final String TAG = "MainActivity";

    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById(R.id.layout_background);
        wordList = findViewById(R.id.word_list);

        getWindow().setStatusBarColor(getApplicationContext().getColor(R.color.colorBackground));

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (account != null) {
            firebaseAuthWithGoogle(account);
        }

        // Word list
        wordList.setLayoutManager(new LinearLayoutManager(this));
        wordList.setHasFixedSize(true);
        wordList.setAdapter(new WordListAdapter());
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential: success");
                            FirebaseUser currentUser = auth.getCurrentUser();
                            updateUI(currentUser);
                        } else {
                            Log.w(TAG, "signInWithCredential: failure", task.getException());
                            Snackbar.make(background, "로그인에 실패하였습니다.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Snackbar.make(background, "로그인에 실패하였습니다.", Snackbar.LENGTH_LONG).show();
        }
    }
}
