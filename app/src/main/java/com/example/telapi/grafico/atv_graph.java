package com.example.telapi.grafico;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telapi.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class atv_graph extends AppCompatActivity {

    private PieChart pieChartMensal, pieChartAnual;
    private Spinner spinnerMes, spinnerCategoria;
    private TextView tvSomatorioMensal, tvSomatorioAnual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_graph);

        spinnerMes = findViewById(R.id.spinnerMes);
        spinnerCategoria = findViewById(R.id.spinnerAnual);
        tvSomatorioMensal = findViewById(R.id.tvSomatorioMensal);
        tvSomatorioAnual = findViewById(R.id.tvSomatorioAnual);
        pieChartMensal = findViewById(R.id.pieChartMensal);
        pieChartAnual = findViewById(R.id.pieChartAnual);

        // Adiciona os dados e chama o invalidate depois de configurar o gráfico
        setupGraphMensal();
        setupGraphAnual();
    }

    private void setupGraphMensal() {
        ArrayList<PieEntry> entriesMensal = new ArrayList<>();
        float somaMensal = 0f;

        // Dados de exemplo
        entriesMensal.add(new PieEntry(200f, "Alimentação"));
        entriesMensal.add(new PieEntry(150f, "Transporte"));
        entriesMensal.add(new PieEntry(50f, "Lazer"));

        // Calculando o somatório mensal
        for (PieEntry entry : entriesMensal) {
            somaMensal += entry.getValue();
        }

        PieDataSet dataSetMensal = new PieDataSet(entriesMensal, "Despesas Mensais");
        dataSetMensal.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieDataMensal = new PieData(dataSetMensal);

        // Configuração do gráfico
        pieChartMensal.setData(pieDataMensal);
        pieChartMensal.setUsePercentValues(true); // Habilitar valores em porcentagem
        pieChartMensal.setDrawHoleEnabled(true); // Habilitar o buraco central
        pieChartMensal.setHoleColor(android.graphics.Color.WHITE); // Cor do buraco central
        pieChartMensal.setTransparentCircleColor(android.graphics.Color.WHITE); // Cor do círculo transparente
        pieChartMensal.setTransparentCircleAlpha(110); // Opacidade do círculo transparente
        pieChartAnual.getLegend().setTextColor(android.graphics.Color.WHITE); // Legenda em branco
        pieChartAnual.setEntryLabelColor(android.graphics.Color.BLACK);

        pieChartMensal.invalidate();  // Atualizando o gráfico

        tvSomatorioMensal.setText("Somatório Mensal: R$ " + String.format("%.2f", somaMensal));
    }

    private void setupGraphAnual() {
        ArrayList<PieEntry> entriesAnual = new ArrayList<>();
        float somaAnual = 0f;

        // Dados de exemplo
        entriesAnual.add(new PieEntry(2500f, "Alimentação"));
        entriesAnual.add(new PieEntry(1800f, "Transporte"));
        entriesAnual.add(new PieEntry(700f, "Lazer"));

        // Calculando o somatório anual
        for (PieEntry entry : entriesAnual) {
            somaAnual += entry.getValue();
        }

        PieDataSet dataSetAnual = new PieDataSet(entriesAnual, "Despesas Anuais");
        dataSetAnual.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieDataAnual = new PieData(dataSetAnual);

        // Configuração do gráfico
        pieChartAnual.setData(pieDataAnual);
        pieChartAnual.setUsePercentValues(true); // Habilitar valores em porcentagem
        pieChartAnual.setDrawHoleEnabled(true); // Habilitar o buraco central
        pieChartAnual.setHoleColor(android.graphics.Color.WHITE); // Cor do buraco central
        pieChartAnual.setTransparentCircleColor(android.graphics.Color.WHITE); // Cor do círculo transparente
        pieChartAnual.setTransparentCircleAlpha(110); // Opacidade do círculo transparente
        pieChartAnual.getLegend().setTextColor(android.graphics.Color.WHITE); // Legenda em branco
        pieChartAnual.setEntryLabelColor(android.graphics.Color.BLACK);

        pieChartAnual.invalidate();  // Atualizando o gráfico

        tvSomatorioAnual.setText("Somatório Anual: R$ " + String.format("%.2f", somaAnual));
    }
}
