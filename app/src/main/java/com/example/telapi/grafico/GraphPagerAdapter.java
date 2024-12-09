package com.example.telapi.grafico;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.telapi.grafico.AnualFragment;

public class GraphPagerAdapter extends FragmentStateAdapter {

    public GraphPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new AnualFragment();
        }
        return new com.example.grafico.MensalFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // Número de abas (Mensal e Anual)
    }
}
