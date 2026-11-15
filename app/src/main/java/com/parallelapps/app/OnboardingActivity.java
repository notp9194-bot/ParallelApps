package com.parallelapps.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {
    private static final String[] EMOJI = {"📱", "🚀", "🛡️"};
    private static final String[] TITLE = {"Clone & Run Apps\nin Parallel", "Lightweight & Fast", "Safe & Secure"};
    private static final String[] DESC = {
            "Use multiple accounts of\nWhatsApp, Instagram, Telegram\nand more...",
            "No lag, no hassle.\nRun parallel apps smoothly.",
            "Your data is always\nprotected and private."};

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_onboarding);
        ViewPager2 pager = findViewById(R.id.pager);
        TabLayout dots = findViewById(R.id.dots);
        MaterialButton next = findViewById(R.id.btnNext);
        pager.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
                View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_onboarding, p, false);
                return new RecyclerView.ViewHolder(v) { };
            }
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int i) {
                ((TextView) h.itemView.findViewById(R.id.emoji)).setText(EMOJI[i]);
                ((TextView) h.itemView.findViewById(R.id.title)).setText(TITLE[i]);
                ((TextView) h.itemView.findViewById(R.id.desc)).setText(DESC[i]);
            }
            @Override public int getItemCount() { return 3; }
        });
        new TabLayoutMediator(dots, pager, (tab, pos) -> { }).attach();
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int pos) { next.setText(pos == 2 ? "Get Started" : "Next"); }
        });
        next.setOnClickListener(v -> {
            if (pager.getCurrentItem() < 2) pager.setCurrentItem(pager.getCurrentItem() + 1);
            else finishOnboarding();
        });
        findViewById(R.id.btnSkip).setOnClickListener(v -> finishOnboarding());
    }

    private void finishOnboarding() {
        Store.setOnboarded(this);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
