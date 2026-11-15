package com.parallelapps.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    private View rowSecurity, rowTheme;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        View v = i.inflate(R.layout.fragment_settings, c, false);
        row(v, R.id.rowManage, "App Management", "Manage your cloned apps",
                x -> ((MainActivity) requireActivity()).select(R.id.nav_apps));
        rowTheme = row(v, R.id.rowTheme, "Appearance", "", x -> pickTheme());
        row(v, R.id.rowNotif, "Notifications", "App notifications", x -> startActivity(
                new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName())));
        rowSecurity = row(v, R.id.rowSecurity, "Security & Privacy", "", x -> toggleLock());
        row(v, R.id.rowAbout, "About Us", "Version 1.0.0", x -> new AlertDialog.Builder(requireContext())
                .setTitle("Parallel Apps").setMessage("Version 1.0.0").setPositiveButton("OK", null).show());
        v.findViewById(R.id.btnRate).setOnClickListener(x -> {
            String pkg = requireContext().getPackageName();
            try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg))); }
            catch (Exception e) { startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + pkg))); }
        });
        refresh();
        return v;
    }

    private View row(View root, int id, String t, String s, View.OnClickListener l) {
        View r = root.findViewById(id);
        ((TextView) r.findViewById(R.id.rowTitle)).setText(t);
        ((TextView) r.findViewById(R.id.rowSub)).setText(s);
        r.setOnClickListener(l);
        return r;
    }

    private void refresh() {
        String[] names = {"Light", "Dark", "System default"};
        ((TextView) rowTheme.findViewById(R.id.rowSub)).setText(names[Store.theme(requireContext())]);
        ((TextView) rowSecurity.findViewById(R.id.rowSub))
                .setText(Store.appLock(requireContext()) ? "App lock: ON" : "App lock: OFF (tap to enable)");
    }

    private void pickTheme() {
        String[] names = {"Light", "Dark", "System default"};
        new AlertDialog.Builder(requireContext()).setTitle("Appearance")
                .setSingleChoiceItems(names, Store.theme(requireContext()), (d, which) -> {
                    Store.setTheme(requireContext(), which);
                    AppCompatDelegate.setDefaultNightMode(which == 0 ? AppCompatDelegate.MODE_NIGHT_NO
                            : which == 1 ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    d.dismiss();
                }).show();
    }

    private void toggleLock() {
        Store.setAppLock(requireContext(), !Store.appLock(requireContext()));
        refresh();
    }
}
