package com.parallelapps.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (ProfileHelper.inProfile(this)) {
            boolean ok = ProfileHelper.setupInsideProfile(this);
            android.widget.Toast.makeText(this, ok
                    ? "Parallel Space ready. Ab main Parallel Apps se clone chalao."
                    : "Profile setup fail hua", android.widget.Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        int t = Store.theme(this);
        AppCompatDelegate.setDefaultNightMode(t == 0 ? AppCompatDelegate.MODE_NIGHT_NO
                : t == 1 ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, Store.onboarded(this) ? MainActivity.class : OnboardingActivity.class));
            finish();
        }, 1800);
    }
}
