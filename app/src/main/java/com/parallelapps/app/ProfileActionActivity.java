package com.parallelapps.app;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/** Runs INSIDE the work profile (enabled only there). Does the privileged work for the main app. */
public class ProfileActionActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        String action = getIntent().getAction();
        String pkg = getIntent().getStringExtra("pkg");
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName admin = ProfileHelper.admin(this);
        try {
            if (pkg == null) {
                Toast.makeText(this, "Package missing", Toast.LENGTH_SHORT).show();
            } else if (!dpm.isProfileOwnerApp(getPackageName())) {
                Toast.makeText(this, "Ye copy profile owner nahi hai", Toast.LENGTH_LONG).show();
            } else if (ProfileHelper.A_INSTALL.equals(action)) {
                boolean ok = dpm.installExistingPackage(admin, pkg);
                dpm.setApplicationHidden(admin, pkg, false);
                Toast.makeText(this, ok ? "Clone installed" : "Clone install fail", Toast.LENGTH_SHORT).show();
            } else if (ProfileHelper.A_LAUNCH.equals(action)) {
                Intent li = getPackageManager().getLaunchIntentForPackage(pkg);
                if (li == null) {
                    dpm.installExistingPackage(admin, pkg);
                    dpm.setApplicationHidden(admin, pkg, false);
                    li = getPackageManager().getLaunchIntentForPackage(pkg);
                }
                if (li != null) startActivity(li.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                else Toast.makeText(this, "Clone open nahi hua", Toast.LENGTH_SHORT).show();
            } else if (ProfileHelper.A_HIDE.equals(action)) {
                dpm.setApplicationHidden(admin, pkg, true);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
