package com.parallelapps.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppsFragment extends Fragment {
    private RecyclerView list;
    private View empty;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        View v = i.inflate(R.layout.fragment_apps, c, false);
        list = v.findViewById(R.id.list);
        empty = v.findViewById(R.id.empty);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        v.findViewById(R.id.btnAnother).setOnClickListener(x ->
                startActivity(new Intent(requireContext(), AddAppActivity.class)));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Store.Clone> clones = Store.load(requireContext());
        empty.setVisibility(clones.isEmpty() ? View.VISIBLE : View.GONE);
        list.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
                return new RecyclerView.ViewHolder(
                        LayoutInflater.from(p.getContext()).inflate(R.layout.item_clone_row, p, false)) { };
            }
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
                Store.Clone k = clones.get(pos);
                ((TextView) h.itemView.findViewById(R.id.name)).setText(k.name);
                ((TextView) h.itemView.findViewById(R.id.label)).setText(k.label);
                try {
                    Drawable d = requireContext().getPackageManager().getApplicationIcon(k.pkg);
                    ((ImageView) h.itemView.findViewById(R.id.icon)).setImageDrawable(d);
                } catch (Exception ignored) { }
                View.OnClickListener open = x -> startActivity(
                        new Intent(requireContext(), CloneAppActivity.class).putExtra("index", h.getBindingAdapterPosition()));
                h.itemView.setOnClickListener(open);
                h.itemView.findViewById(R.id.btnOpen).setOnClickListener(open);
            }
            @Override public int getItemCount() { return clones.size(); }
        });
    }
}
