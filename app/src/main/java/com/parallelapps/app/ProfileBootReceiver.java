package com.parallelapps.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Re-applies the profile setup after boot / unlock / app update (self-healing). */
public class ProfileBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        ProfileHelper.setupInsideProfile(ctx);
    }
}
