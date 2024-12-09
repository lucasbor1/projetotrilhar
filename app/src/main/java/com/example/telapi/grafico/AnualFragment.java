package com.example.telapi.grafico;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.telapi.Despesa.Despesa;
import com.example.telapi.Despesa.DespesaCRUD;
import com.example.telapi.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class AnualFragment extends Fragment {

    private static final String TAG = "AnualFragment";

    private Spinner spinnerAno;
    private PieChart pieChart;
    private DespesaCRUD despesaCRUD;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anual, container, false);

        spinnerAno = view.findViewById(R.id.spinnerAno);
        pieChart = view.findViewById(R.id.pieChartAnual);

        inicializarFirebase();
        configurarSpinnerAno();
        configurarGrafico();

        return view;
    }

    private void inicializarFirebase() {
        userId = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(getContext(), "Erro ao obter ID do usuário", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "FirebaseAuth.getUid() retornou null.");
            return;
        }
        despesaCRUD = new DespesaCRUD(requireContext(), userId);
        Log.d(TAG, "ID do usuário obtido com sucesso: " + userId);
    }

    private void configurarSpinnerAno() {
        List<String> anos = gerarListaAnos();
        ArrayAdapter<String> adapterAnos = new ArrayAdapter<>(getContext(), R.layout.item_spinner, anos);
        spinnerAno.setAdapter(adapterAnos);

        spinnerAno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                atualizarDespesas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Calendar calendar = Calendar.getInstance();
        spinnerAno.setSelection(anos.indexOf(String.valueOf(calendar.get(Calendar.YEAR))));
    }

    private List<String> gerarListaAnos() {
        List<String> anos = new ArrayList<>();
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = anoAtual - 10; i <= anoAtual + 10; i++) {
            anos.add(String.valueOf(i));
        }
        return anos;
    }

    private void configurarGrafico() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.getLegend().setEnabled(false);
    }

    private void atualizarDespesas() {
        String anoSelecionado = spinnerAno.getSelectedItem().toString();
        List<Despesa> despesas = despesaCRUD.listarDespesasPorAno(Integer.parseInt(anoSelecionado));

        if (despesas.isEmpty()) {
            pieChart.clear();
            Toast.makeText(getContext(), "Nenhuma despesa encontrada para o ano selecionado.", Toast.LENGTH_SHORT).show();
        } else {
            processarDespesas(despesas);
        }
    }

    private void processarDespesas(List<Despesa> despesas) {
        Map<String, Float> totaisPorCategoria = new HashMap<>();
        float totalGeral = 0f;

        // Soma os valores por categoria
        for (Despesa despesa : despesas) {
            String categoria = despesa.getCategoria();
            float valor = (float) despesa.getValor();
            totaisPorCategoria.put(categoria, totaisPorCategoria.getOrDefault(categoria, 0f) + valor);
            totalGeral += valor;
        }

        // Prepara os dados para o gráfico
        List<PieEntry> entries = new ArrayList<>();
        List<String> categorias = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        List<Float> valores = new ArrayList<>();

        for (Map.Entry<String, Float> entry : totaisPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            float valor = entry.getValue();
            float porcentagem = (valor / totalGeral) * 100;

            entries.add(new PieEntry(porcentagem, categoria));
            categorias.add(categoria);
            valores.add(valor);
            colors.add(gerarCorAleatoria());
        }

        exibirGrafico(entries, colors, categorias, valores);
    }


    private void exibirGrafico(List<PieEntry> entries, List<Integer> colors, List<String> categorias, List<Float> valores) {
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        // Define um tamanho de texto padrão
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(getResources().getColor(R.color.white));

        // Customiza o ValueFormatter para ajustar o texto dinamicamente
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.1f%%", value);
            }
        });

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate();

        configurarLegendasPersonalizadas(categorias, valores, colors);
    }

    private void configurarLegendasPersonalizadas(List<String> categorias, List<Float> valores, List<Integer> colors) {
        LinearLayout legendContainer = requireView().findViewById(R.id.legendContainer);
        legendContainer.removeAllViews();

        for (int i = 0; i < categorias.size(); i++) {
            LinearLayout itemLayout = new LinearLayout(getContext());
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            itemLayout.setPadding(0, 8, 0, 8);
            itemLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

            View colorIndicator = new View(getContext());
            LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(32, 32);
            colorParams.setMargins(0, 0, 16, 0);
            colorIndicator.setLayoutParams(colorParams);
            colorIndicator.setBackgroundColor(colors.get(i));

            String legendaTexto = categorias.get(i).toUpperCase() + " - TOTAL R$ " +
                    String.format(Locale.getDefault(), "%.2f", valores.get(i));
            TextView categoryText = new TextView(getContext());
            categoryText.setText(legendaTexto);
            categoryText.setTextColor(getResources().getColor(R.color.white));
            categoryText.setTextSize(14f);

            itemLayout.addView(colorIndicator);
            itemLayout.addView(categoryText);

            legendContainer.addView(itemLayout);
        }
    }

    private int gerarCorAleatoria() {
        Random random = new Random();
        return 0xff000000 | random.nextInt(0xffffff);
    }
}
