package com.parallelapps.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.configuration.ClientConfiguration;

public class ParallelApp extends Application {
    private static final String TAG = "ParallelApp";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try { BlackBoxCore.get().closeCodeInit(); } catch (Throwable t) { Log.w(TAG, t); }
        try { BlackBoxCore.get().onBeforeMainApplicationAttach(this, base); } catch (Throwable t) { Log.w(TAG, t); }
        try {
            BlackBoxCore.get().doAttachBaseContext(base, new ClientConfiguration() {
                @Override public String getHostPackageName() { return base.getPackageName(); }
                @Override public boolean isEnableDaemonService() { return false; }
                // The engine's default uploads logcat to a third-party server on crashes. Disabled on purpose.
                @Override public String getLogSenderChatId() { return ""; }
            });
        } catch (Throwable t) { Log.e(TAG, "engine attach failed", t); }
        try { BlackBoxCore.get().onAfterMainApplicationAttach(this, base); } catch (Throwable t) { Log.w(TAG, t); }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try { BlackBoxCore.get().doCreate(); } catch (Throwable t) { Log.e(TAG, "engine create failed", t); }
    }
}
