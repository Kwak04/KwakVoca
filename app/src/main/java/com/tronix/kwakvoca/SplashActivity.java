package com.tronix.kwakvoca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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
            SharedPreferences preferences = getSharedPreferences("DATA", MODE_PRIVATE);
            boolean isSignedIn = preferences.getBoolean("isSignedIn", false);
            Log.d(TAG, "isSignedIn=" + isSignedIn);
            startActivity(setActivityToMove(isSignedIn));
            overridePendingTransition(0, R.anim.splash_fade_out);
            SplashActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() { }

    private Intent setActivityToMove(boolean isSignedIn) {
        Intent intentSignInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        Intent intentMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        if (isSignedIn) {
            return intentMainActivity;
        } else {
            return intentSignInActivity;
        }
    }
}
