package com.example.telapi.grafico;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.telapi.grafico.MensalFragment;

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
        return new MensalFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
