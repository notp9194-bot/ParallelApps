package com.parallelapps.app;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.List;

/** Work-profile (managed profile) helpers. Clone = real second copy inside the work profile. */
public class ProfileHelper {
    public static final String A_INSTALL = "com.parallelapps.app.action.INSTALL";
    public static final String A_LAUNCH = "com.parallelapps.app.action.LAUNCH";
    public static final String A_HIDE = "com.parallelapps.app.action.HIDE";

    public static ComponentName admin(Context c) {
        return new ComponentName(c, AdminReceiver.class);
    }

    public static boolean hasProfile(Context c) {
        UserManager um = (UserManager) c.getSystemService(Context.USER_SERVICE);
        List<UserHandle> l = um.getUserProfiles();
        return l != null && l.size() > 1;
    }

    public static boolean canProvision(Context c) {
        DevicePolicyManager dpm = (DevicePolicyManager) c.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dpm.isProvisioningAllowed(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);
    }

    public static boolean createProfile(Activity a, int req) {
        if (!canProvision(a)) return false;
        Intent i = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);
        i.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME, admin(a));
        try {
            a.startActivityForResult(i, req);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    /** Sends an action to the copy of this app running inside the work profile. */
    public static boolean send(Context c, String action, String pkg) {
        try {
            c.startActivity(new Intent(action).putExtra("pkg", pkg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public static boolean inProfile(Context c) {
        DevicePolicyManager dpm = (DevicePolicyManager) c.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dpm.isProfileOwnerApp(c.getPackageName());
    }

    /** Idempotent. Runs inside the work profile: enables the action activity and links personal -> profile. */
    public static boolean setupInsideProfile(Context ctx) {
        DevicePolicyManager dpm = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!dpm.isProfileOwnerApp(ctx.getPackageName())) return false;
        ComponentName admin = admin(ctx);
        try { dpm.setProfileName(admin, "Parallel Space"); } catch (Exception ignored) { }
        try {
            ctx.getPackageManager().setComponentEnabledSetting(new ComponentName(ctx, ProfileActionActivity.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } catch (Exception ignored) { }
        try {
            dpm.clearCrossProfileIntentFilters(admin);
            IntentFilter f = new IntentFilter();
            f.addAction(A_INSTALL);
            f.addAction(A_LAUNCH);
            f.addAction(A_HIDE);
            f.addCategory(Intent.CATEGORY_DEFAULT);
            dpm.addCrossProfileIntentFilter(admin, f, DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED);
        } catch (Exception e) {
            return false;
        }
        try { dpm.setProfileEnabled(admin); } catch (Exception ignored) { }
        return true;
    }
}
