package com.example.grafico;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.telapi.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;

public class MensalFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensal, container, false);

        PieChart pieChart = view.findViewById(R.id.pieChartMensal);
        setupPieChart(pieChart);

        return view;
    }

    private void setupPieChart(PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Alimentação"));
        entries.add(new PieEntry(30f, "Transporte"));
        entries.add(new PieEntry(20f, "Lazer"));
        entries.add(new PieEntry(10f, "Outros"));

        PieDataSet dataSet = new PieDataSet(entries, "Despesas Mensais");
        dataSet.setColors(
                new int[]{R.color.verde, R.color.vermelho, R.color.laranja, R.color.cinza},
                getContext()
        );
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        pieChart.setData(data);
        pieChart.invalidate();     }
}
