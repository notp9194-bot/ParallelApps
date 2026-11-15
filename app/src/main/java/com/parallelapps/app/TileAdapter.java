package com.parallelapps.app;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TileAdapter extends RecyclerView.Adapter<TileAdapter.VH> {
    public static class Item {
        public String name, pkg, badge;
        public boolean isAdd;
        public Item(String name, String pkg, String badge, boolean isAdd) {
            this.name = name; this.pkg = pkg; this.badge = badge; this.isAdd = isAdd;
        }
    }

    public interface Listener { void onClick(Item item, int position); }

    private final List<Item> items;
    private final Listener listener;
    public String selectedPkg = null;

    public TileAdapter(List<Item> items, Listener l) { this.items = items; this.listener = l; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_app_tile, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Item it = items.get(pos);
        h.label.setText(it.name);
        if (it.isAdd) {
            h.icon.setImageResource(R.drawable.ic_add);
            h.icon.setBackgroundResource(R.drawable.bg_tile);
            h.badge.setVisibility(View.GONE);
        } else {
            try {
                Drawable d = h.itemView.getContext().getPackageManager().getApplicationIcon(it.pkg);
                h.icon.setImageDrawable(d);
            } catch (PackageManager.NameNotFoundException e) {
                h.icon.setImageResource(R.drawable.ic_launcher);
            }
            h.icon.setBackground(null);
            if (it.badge != null) { h.badge.setText(it.badge); h.badge.setVisibility(View.VISIBLE); }
            else h.badge.setVisibility(View.GONE);
        }
        boolean sel = it.pkg != null && it.pkg.equals(selectedPkg);
        h.root.setBackgroundResource(sel ? R.drawable.bg_tile_sel : android.R.color.transparent);
        h.itemView.setOnClickListener(v -> {
            int p = h.getBindingAdapterPosition();
            if (p != RecyclerView.NO_POSITION) listener.onClick(items.get(p), p);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon; TextView label, badge; View root;
        VH(View v) {
            super(v);
            icon = v.findViewById(R.id.icon); label = v.findViewById(R.id.label);
            badge = v.findViewById(R.id.badge); root = v.findViewById(R.id.root);
        }
    }
}
