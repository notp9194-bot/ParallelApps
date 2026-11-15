package com.parallelapps.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/** Trampoline used by home-screen shortcuts: opens the clone inside the work profile. */
public class LaunchCloneActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        String pkg = getIntent().getStringExtra("pkg");
        if (pkg == null || !ProfileHelper.send(this, ProfileHelper.A_LAUNCH, pkg))
            Toast.makeText(this, "Work profile ready nahi hai", Toast.LENGTH_SHORT).show();
        finish();
    }
}
