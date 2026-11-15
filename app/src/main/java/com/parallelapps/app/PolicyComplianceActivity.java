package com.parallelapps.app;

import android.app.Activity;
import android.os.Bundle;

public class PolicyComplianceActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setResult(RESULT_OK);
        finish();
    }
}
