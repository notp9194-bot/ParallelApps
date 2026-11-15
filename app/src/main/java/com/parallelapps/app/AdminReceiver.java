package com.parallelapps.app;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class AdminReceiver extends DeviceAdminReceiver {
    /** Runs inside the NEW work profile, as profile owner. */
    @Override
    public void onProfileProvisioningComplete(Context ctx, Intent intent) {
        ProfileHelper.setupInsideProfile(ctx);
    }
}
