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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MensalFragment extends Fragment {

    private static final String TAG = "MensalFragment";

    private Spinner spinnerMes, spinnerAno;
    private PieChart pieChart;
    private DespesaCRUD despesaCRUD;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensal, container, false);

        // Inicializar componentes
        spinnerMes = view.findViewById(R.id.spinnerMes);
        spinnerAno = view.findViewById(R.id.spinnerAno);
        pieChart = view.findViewById(R.id.pieChartMensal);

        // Inicializar Firebase e banco de dados
        inicializarFirebase();

        // Configurações
        configurarSpinners();
        configurarGrafico();

        return view;
    }

    private void inicializarFirebase() {
        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(getContext(), "Erro ao obter ID do usuário", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "FirebaseAuth.getUid() retornou null.");
            return;
        }
        despesaCRUD = new DespesaCRUD(requireContext(), userId);
        Log.d(TAG, "ID do usuário obtido com sucesso: " + userId);
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> adapterMeses = ArrayAdapter.createFromResource(
                getContext(),
                R.array.meses,
                R.layout.item_spinner
        );
        spinnerMes.setAdapter(adapterMeses);

        List<String> anos = gerarListaAnos();
        ArrayAdapter<String> adapterAnos = new ArrayAdapter<>(getContext(), R.layout.item_spinner, anos);
        spinnerAno.setAdapter(adapterAnos);

        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                atualizarDespesas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerAno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                atualizarDespesas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Calendar calendar = Calendar.getInstance();
        spinnerMes.setSelection(calendar.get(Calendar.MONTH));
        spinnerAno.setSelection(anos.indexOf(String.valueOf(calendar.get(Calendar.YEAR))));
    }

    private List<String> gerarListaAnos() {
        List<String> anos = new ArrayList<>();
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = anoAtual - 5; i <= anoAtual + 5; i++) {
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
        String mesSelecionado = String.format("%02d", spinnerMes.getSelectedItemPosition() + 1);
        String anoSelecionado = spinnerAno.getSelectedItem().toString();

        List<Despesa> despesas = despesaCRUD.listarDespesasPorMesAno(mesSelecionado, Integer.parseInt(anoSelecionado));

        if (despesas.isEmpty()) {
            pieChart.clear();
            Toast.makeText(getContext(), "Nenhuma despesa encontrada.", Toast.LENGTH_SHORT).show();
        } else {
            List<PieEntry> entries = new ArrayList<>();
            List<String> categorias = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();

            for (Despesa despesa : despesas) {
                entries.add(new PieEntry((float) despesa.getValor(), despesa.getDescricao()));
                categorias.add(despesa.getDescricao());
                colors.add(gerarCorAleatoria());
            }

            exibirGrafico(entries, colors, categorias);
        }
    }

    private void exibirGrafico(List<PieEntry> entries, List<Integer> colors, List<String> categorias) {
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(getResources().getColor(R.color.white));

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();

        configurarLegendasPersonalizadas(categorias, colors);
    }

    private void configurarLegendasPersonalizadas(List<String> categorias, List<Integer> colors) {
        LinearLayout legendContainer = requireView().findViewById(R.id.legendContainer);
        legendContainer.removeAllViews();

        for (int i = 0; i < categorias.size(); i++) {
            LinearLayout itemLayout = new LinearLayout(getContext());
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);

            View colorIndicator = new View(getContext());
            LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(32, 32);
            colorParams.setMargins(0, 0, 16, 0);
            colorIndicator.setLayoutParams(colorParams);
            colorIndicator.setBackgroundColor(colors.get(i));

            TextView categoryText = new TextView(getContext());
            categoryText.setText(categorias.get(i));
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
