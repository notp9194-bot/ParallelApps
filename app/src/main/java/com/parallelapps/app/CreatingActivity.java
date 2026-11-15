package com.parallelapps.app;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.entity.pm.InstallResult;

public class CreatingActivity extends AppCompatActivity {
    private volatile String error = null;
    private volatile boolean done = false;
    private int userId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_creating);
        String name = getIntent().getStringExtra("name");
        String pkg = getIntent().getStringExtra("pkg");
        userId = Store.nextUserId(this, pkg);
        try {
            ((ImageView) findViewById(R.id.icon)).setImageDrawable(getPackageManager().getApplicationIcon(pkg));
        } catch (Exception ignored) { }
        ProgressBar bar = findViewById(R.id.progress);

        // real work: create virtual user + install the app into it
        new Thread(() -> {
            try {
                BlackBoxCore core = BlackBoxCore.get();
                core.createUser(userId);
                InstallResult r = core.installPackageAsUser(pkg, userId);
                if (r == null || !r.success) error = (r == null ? "install failed" : r.msg);
            } catch (Throwable t) {
                error = String.valueOf(t);
            }
            done = true;
        }, "clone-install").start();

        ValueAnimator a = ValueAnimator.ofInt(0, 90);
        a.setDuration(2500);
        a.addUpdateListener(v -> bar.setProgress((int) v.getAnimatedValue()));
        a.start();

        Handler h = new Handler(Looper.getMainLooper());
        Runnable poll = new Runnable() {
            @Override public void run() {
                if (!done) { h.postDelayed(this, 200); return; }
                bar.setProgress(100);
                if (error != null) {
                    ((TextView) findViewById(R.id.status)).setText("Fail: " + error);
                    Toast.makeText(CreatingActivity.this, "Clone fail: " + error, Toast.LENGTH_LONG).show();
                    h.postDelayed(() -> finish(), 2500);
                    return;
                }
                Store.add(CreatingActivity.this, name, pkg, userId);
                Toast.makeText(CreatingActivity.this, "Parallel app ready!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreatingActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        };
        h.postDelayed(poll, 500);
    }
}
