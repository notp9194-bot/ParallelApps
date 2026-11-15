package com.parallelapps.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddAppActivity extends AppCompatActivity {
    private final List<TileAdapter.Item> all = new ArrayList<>();
    private final List<TileAdapter.Item> shown = new ArrayList<>();
    private TileAdapter adapter;
    private TileAdapter.Item selected;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_app);
        findViewById(R.id.back).setOnClickListener(v -> finish());

        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        for (ResolveInfo ri : pm.queryIntentActivities(main, 0)) {
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(getPackageName())) continue;
            all.add(new TileAdapter.Item(ri.loadLabel(pm).toString(), pkg, null, false));
        }
        Collections.sort(all, (a, c) -> a.name.compareToIgnoreCase(c.name));
        shown.addAll(all);

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new TileAdapter(shown, (item, pos) -> {
            selected = item;
            adapter.selectedPkg = item.pkg;
            adapter.notifyDataSetChanged();
        });
        list.setAdapter(adapter);

        ((EditText) findViewById(R.id.search)).addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void afterTextChanged(Editable e) {
                String q = e.toString().trim().toLowerCase();
                shown.clear();
                for (TileAdapter.Item it : all) if (it.name.toLowerCase().contains(q)) shown.add(it);
                adapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btnSelect).setOnClickListener(v -> {
            if (selected == null) {
                Toast.makeText(this, "Pehle app select karo", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Store.has(this, selected.pkg)) {
                Toast.makeText(this, "Is app ka clone pehle se hai", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!ProfileHelper.hasProfile(this)) {
                new AlertDialog.Builder(this)
                        .setTitle("Parallel Space setup")
                        .setMessage("Clone chalane ke liye ek baar work profile banana hoga. "
                                + "Agle screen par Accept dabao. Clone isi profile mein alag copy ki tarah chalega.")
                        .setPositiveButton("Setup", (d, w) -> {
                            if (!ProfileHelper.createProfile(this, REQ_PROFILE))
                                Toast.makeText(this, "Is phone par work profile allowed nahi hai", Toast.LENGTH_LONG).show();
                        })
                        .setNegativeButton("Cancel", null).show();
                return;
            }
            startActivity(new Intent(this, CreatingActivity.class)
                    .putExtra("name", selected.name).putExtra("pkg", selected.pkg));
            finish();
        });
    }

    private static final int REQ_PROFILE = 77;

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == REQ_PROFILE) {
            Toast.makeText(this, res == RESULT_OK
                    ? "Profile ban gaya. Ab Select App dubara dabao."
                    : "Profile setup cancel hua", Toast.LENGTH_LONG).show();
        }
    }
}
