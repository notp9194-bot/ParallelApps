package com.parallelapps.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
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
            try {
                java.io.File apk = new java.io.File(pm.getApplicationInfo(pkg, 0).sourceDir);
                if (!top.niunaijun.blackbox.utils.AbiUtils.isSupport(apk)) continue;
            } catch (Throwable ignored) { }
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
            startActivity(new Intent(this, CreatingActivity.class)
                    .putExtra("name", selected.name).putExtra("pkg", selected.pkg));
            finish();
        });
    }
}
