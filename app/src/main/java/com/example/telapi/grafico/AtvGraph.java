package com.example.telapi.grafico;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telapi.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;

public class AtvGraph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_graph);

        TabLayout tabLayout = findViewById(R.id.tabLayoutPeriod);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        GraphPagerAdapter adapter = new GraphPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Mensal");
                    break;
                case 1:
                    tab.setText("Anual");
                    break;
            }
        }).attach();
    }
}
