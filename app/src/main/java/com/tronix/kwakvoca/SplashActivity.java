package com.tronix.kwakvoca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setStatusBarColor(getApplicationContext().getColor(R.color.colorBackground));

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 1000);
    }

    private class SplashHandler implements Runnable {
        public void run() {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            startActivity(setActivityToMove(currentUser));

            overridePendingTransition(0, R.anim.splash_fade_out);
            SplashActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() { }

    private Intent setActivityToMove(FirebaseUser currentUser) {
        Intent intentSignInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        Intent intentMainActivity = new Intent(getApplicationContext(), MainActivity.class);

        if (currentUser != null) {
            Log.d(TAG, "setActivityToMove: currentUser exists. Moving to MainActivity.");
            return intentMainActivity;
        } else {
            Log.d(TAG, "setActivityToMove: currentUser does not exist. Moving to SignInActivity.");
            return intentSignInActivity;
        }
    }
}
