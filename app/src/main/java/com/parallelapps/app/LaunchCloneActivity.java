package com.parallelapps.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import top.niunaijun.blackbox.BlackBoxCore;

/** Trampoline for home-screen shortcuts: starts the clone inside the engine. */
public class LaunchCloneActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        String pkg = getIntent().getStringExtra("pkg");
        int userId = getIntent().getIntExtra("userId", 0);
        boolean ok = false;
        try { ok = pkg != null && BlackBoxCore.get().launchApk(pkg, userId); } catch (Throwable ignored) { }
        if (!ok) Toast.makeText(this, "Clone open nahi hua", Toast.LENGTH_LONG).show();
        finish();
    }
}
