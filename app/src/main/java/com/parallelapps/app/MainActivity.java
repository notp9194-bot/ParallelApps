package com.parallelapps.app;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView nav;
    private boolean unlocked = false;

    private final ActivityResultLauncher<Intent> unlock = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), r -> {
                if (r.getResultCode() == RESULT_OK) unlocked = true; else finish();
            });

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment f = id == R.id.nav_home ? new HomeFragment()
                    : id == R.id.nav_apps ? new AppsFragment() : new SettingsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
            return true;
        });
        if (b == null) nav.setSelectedItemId(R.id.nav_home);

        if (Store.appLock(this)) {
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null && km.isDeviceSecure()) {
                Intent i = km.createConfirmDeviceCredentialIntent("Parallel Apps", "Unlock to continue");
                if (i != null && !unlocked) unlock.launch(i);
            }
        }
    }

    public void select(int menuId) { nav.setSelectedItemId(menuId); }
}
