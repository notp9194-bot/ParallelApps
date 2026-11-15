package com.parallelapps.app;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
}
