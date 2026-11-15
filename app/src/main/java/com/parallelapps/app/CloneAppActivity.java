package com.parallelapps.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import java.util.List;

public class CloneAppActivity extends AppCompatActivity {
    private int index;
    private Store.Clone clone;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_clone_app);
        index = getIntent().getIntExtra("index", -1);
        List<Store.Clone> list = Store.load(this);
        if (index < 0 || index >= list.size()) { finish(); return; }
        clone = list.get(index);
        bind();

        findViewById(R.id.btnLaunch).setOnClickListener(v -> {
            boolean ok = false;
            try { ok = top.niunaijun.blackbox.BlackBoxCore.get().launchApk(clone.pkg, clone.userId); }
            catch (Throwable t) { Toast.makeText(this, "Error: " + t, Toast.LENGTH_LONG).show(); }
            if (!ok) Toast.makeText(this, "Clone open nahi hua", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnShortcut).setOnClickListener(v -> addShortcut());
        findViewById(R.id.btnRename).setOnClickListener(v -> {
            EditText et = new EditText(this);
            et.setText(clone.name);
            new AlertDialog.Builder(this).setTitle("Rename").setView(et)
                    .setPositiveButton("Save", (d, w) -> {
                        String n = et.getText().toString().trim();
                        if (!n.isEmpty()) { Store.rename(this, index, n); clone.name = n; bind(); }
                    }).setNegativeButton("Cancel", null).show();
        });
        findViewById(R.id.btnDelete).setOnClickListener(v ->
                new AlertDialog.Builder(this).setTitle("Delete clone?")
                        .setMessage(clone.name + " list se hat jayega.")
                        .setPositiveButton("Delete", (d, w) -> {
                            try {
                                top.niunaijun.blackbox.BlackBoxCore.get().uninstallPackageAsUser(clone.pkg, clone.userId);
                            } catch (Throwable ignored) { }
                            Store.remove(this, index); finish(); })
                        .setNegativeButton("Cancel", null).show());
    }

    private void bind() {
        ((TextView) findViewById(R.id.title)).setText(clone.name);
        ((TextView) findViewById(R.id.account)).setText(clone.label);
        ((TextView) findViewById(R.id.pkg)).setText(clone.pkg);
        try {
            ((ImageView) findViewById(R.id.icon)).setImageDrawable(getPackageManager().getApplicationIcon(clone.pkg));
        } catch (Exception ignored) { }
    }

    private void addShortcut() {
        Intent launch = new Intent(this, LaunchCloneActivity.class)
                .setAction(Intent.ACTION_VIEW).putExtra("pkg", clone.pkg).putExtra("userId", clone.userId);
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
            Toast.makeText(this, "Shortcut supported nahi", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Drawable d = getPackageManager().getApplicationIcon(clone.pkg);
            Bitmap bmp = Bitmap.createBitmap(Math.max(1, d.getIntrinsicWidth()), Math.max(1, d.getIntrinsicHeight()), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmp);
            d.setBounds(0, 0, c.getWidth(), c.getHeight());
            d.draw(c);
            ShortcutInfoCompat info = new ShortcutInfoCompat.Builder(this, "clone_" + clone.pkg + "_" + index)
                    .setShortLabel(clone.name)
                    .setIcon(IconCompat.createWithBitmap(bmp))
                    .setIntent(launch)
                    .build();
            ShortcutManagerCompat.requestPinShortcut(this, info, null);
        } catch (Exception e) {
            Toast.makeText(this, "Shortcut fail hua", Toast.LENGTH_SHORT).show();
        }
    }
}
