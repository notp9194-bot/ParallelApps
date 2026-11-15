package com.parallelapps.app;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreatingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_creating);
        String name = getIntent().getStringExtra("name");
        String pkg = getIntent().getStringExtra("pkg");
        try {
            ((ImageView) findViewById(R.id.icon)).setImageDrawable(getPackageManager().getApplicationIcon(pkg));
        } catch (Exception ignored) { }
        if (!ProfileHelper.send(this, ProfileHelper.A_INSTALL, pkg))
            Toast.makeText(this, "Work profile link nahi mila. Work apps (briefcase) mein \"Parallel Apps\" icon ek baar kholo, phir try karo.", Toast.LENGTH_LONG).show();
        ProgressBar bar = findViewById(R.id.progress);
        ValueAnimator a = ValueAnimator.ofInt(0, 100);
        a.setDuration(3500);
        a.addUpdateListener(v -> bar.setProgress((int) v.getAnimatedValue()));
        a.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(android.animation.Animator an) {
                Store.add(CreatingActivity.this, name, pkg);
                Toast.makeText(CreatingActivity.this, "Parallel app ready!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreatingActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });
        a.start();
    }
}
