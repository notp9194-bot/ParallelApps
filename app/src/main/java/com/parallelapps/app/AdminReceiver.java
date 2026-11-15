package com.parallelapps.app;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

public class AdminReceiver extends DeviceAdminReceiver {
    /** Runs inside the NEW work profile, as profile owner. */
    @Override
    public void onProfileProvisioningComplete(Context ctx, Intent intent) {
        DevicePolicyManager dpm = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName admin = ProfileHelper.admin(ctx);
        dpm.setProfileName(admin, "Parallel Space");

        PackageManager pm = ctx.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(ctx, ProfileActionActivity.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        // no launcher icon of our own inside the work profile
        pm.setComponentEnabledSetting(new ComponentName(ctx, SplashActivity.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        IntentFilter f = new IntentFilter();
        f.addAction(ProfileHelper.A_INSTALL);
        f.addAction(ProfileHelper.A_LAUNCH);
        f.addAction(ProfileHelper.A_HIDE);
        f.addCategory(Intent.CATEGORY_DEFAULT);
        dpm.addCrossProfileIntentFilter(admin, f, DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_CHILD);

        dpm.setProfileEnabled(admin);
    }
}
