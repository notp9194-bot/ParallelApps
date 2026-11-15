package com.parallelapps.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/** Answers the system's GET_PROVISIONING_MODE with "managed profile". */
public class ProvisioningModeActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        Intent r = new Intent();
        r.putExtra("android.app.extra.PROVISIONING_MODE", 2); // PROVISIONING_MODE_MANAGED_PROFILE
        setResult(RESULT_OK, r);
        finish();
    }
}
