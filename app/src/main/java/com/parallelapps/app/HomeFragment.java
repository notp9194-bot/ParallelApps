package com.parallelapps.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView grid;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        View v = i.inflate(R.layout.fragment_home, c, false);
        grid = v.findViewById(R.id.grid);
        grid.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        v.findViewById(R.id.cardClone).setOnClickListener(x ->
                startActivity(new Intent(requireContext(), AddAppActivity.class)));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Store.Clone> clones = Store.load(requireContext());
        List<TileAdapter.Item> items = new ArrayList<>();
        for (Store.Clone k : clones) items.add(new TileAdapter.Item(k.name, k.pkg, k.userId > 0 ? String.valueOf(k.userId + 1) : null, false));
        items.add(new TileAdapter.Item("Add App", null, null, true));
        grid.setAdapter(new TileAdapter(items, (item, pos) -> {
            if (item.isAdd) startActivity(new Intent(requireContext(), AddAppActivity.class));
            else startActivity(new Intent(requireContext(), CloneAppActivity.class).putExtra("index", pos));
        }));
    }
}
