package com.example.telapi.Despesa;

import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.telapi.AdpSpinner;
import com.example.telapi.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class atv_despesa extends AppCompatActivity {

    private ImageButton btnAdicionar;
    private Spinner spnMeses;
    private ListView lstDespesas;
    private TextView edtTotal;
    private TextView edtAberto;
    public static final int REQUEST_CODE = 1;

    private DespesaAdapter despesasAdapter;
    private DespesaCRUD despesaCRUD;
    private String userId;
    private Map<String, List<Despesa>> despesasPorMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_despesa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        btnAdicionar = findViewById(R.id.btnAdicionar);
        spnMeses = findViewById(R.id.spnMeses);
        lstDespesas = findViewById(R.id.lstDespesas);
        edtTotal = findViewById(R.id.edtTotal);
        edtAberto = findViewById(R.id.edtAberto);
        despesasPorMes = new HashMap<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Log.e("atv_despesa", "Erro: User ID é null");
            Toast.makeText(this, "Erro ao obter ID do usuário", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            despesaCRUD = new DespesaCRUD(this, userId);
        } catch (Exception e) {
            Log.e("atv_despesa", "Erro ao inicializar o DespesaCRUD", e);
            Toast.makeText(this, "Erro ao inicializar o banco de dados", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        despesasAdapter = new DespesaAdapter(this, new ArrayList<>());
        lstDespesas.setAdapter(despesasAdapter);

        carregarDespesas();
        String[] mesesArray = getResources().getStringArray(R.array.meses);
        AdpSpinner adapter = new AdpSpinner(this, R.layout.item_spinner, mesesArray);
        spnMeses.setAdapter(adapter);

        spnMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mesSelecionado = (String) parent.getItemAtPosition(position);
                exibirDespesasPorMes(mesSelecionado != null ? mesSelecionado : "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(atv_despesa.this, atv_cadastro.class);
            intent.putExtra("userId", userId);
            startActivityForResult(intent, REQUEST_CODE);
        });

        lstDespesas.setOnItemClickListener((parent, view, position, id) -> {
            Despesa despesaSelecionada = despesasAdapter.getDespesa(position);
            if (despesaSelecionada != null) {
                abrirTelaCadastroComDespesa(despesaSelecionada);
            }
        });

        configurarSpinnerMesAtual();
    }

    private void carregarDespesas() {
        if (despesaCRUD == null) return;

        List<Despesa> todasDespesas = despesaCRUD.listarDespesas();
        despesasPorMes.clear();

        if (todasDespesas != null) {
            for (Despesa despesa : todasDespesas) {
                adicionarDespesaNova(despesa);
            }
        }

        String mesSelecionado = spnMeses.getSelectedItem() != null ? spnMeses.getSelectedItem().toString() : "";
        exibirDespesasPorMes(mesSelecionado);
        atualizarTotalMensal();
    }

    private void abrirTelaCadastroComDespesa(Despesa despesa) {
        Intent intent = new Intent(atv_despesa.this, atv_cadastro.class);
        intent.putExtra("acao", "ALTERAR");
        intent.putExtra("obj", despesa);
        intent.putExtra("userId", userId);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void adicionarDespesaNova(Despesa novaDespesa) {
        String mes = obterMesDaDespesa(novaDespesa);
        List<Despesa> despesasDoMes = despesasPorMes.getOrDefault(mes, new ArrayList<>());
        despesasDoMes.add(novaDespesa);
        despesasPorMes.put(mes, despesasDoMes);
    }

    private String obterMesDaDespesa(Despesa despesa) {
        String vencimento = despesa.getVencimento();
        if (vencimento == null || vencimento.isEmpty()) return "Mês Desconhecido";

        String[] partesData = vencimento.split("/");
        if (partesData.length < 2) return "Mês Desconhecido";

        try {
            int mes = Integer.parseInt(partesData[1]);
            String[] nomesMeses = getResources().getStringArray(R.array.meses);
            return nomesMeses[mes - 1];
        } catch (Exception e) {
            return "Mês Desconhecido";
        }
    }

    private void exibirDespesasPorMes(String mes) {
        List<Despesa> despesas = despesasPorMes.get(mes);
        despesasAdapter.clear();

        if (despesas != null) {
            despesasAdapter.addAll(despesas);
        }

        despesasAdapter.notifyDataSetChanged();
        atualizarTotalMensal();
    }

    private void atualizarTotalMensal() {
        String mesSelecionado = spnMeses.getSelectedItem() != null ? spnMeses.getSelectedItem().toString() : "";
        List<Despesa> despesas = despesasPorMes.get(mesSelecionado);

        double totalMensal = 0;
        double despesasEmAberto = 0;

        if (despesas != null) {
            for (Despesa despesa : despesas) {
                totalMensal += despesa.getValor();
                if (!despesa.isPago()) {
                    despesasEmAberto += despesa.getValor();
                }
            }
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        edtTotal.setText(currencyFormat.format(totalMensal));
        edtAberto.setText(currencyFormat.format(despesasEmAberto));
    }

    private void configurarSpinnerMesAtual() {
        Calendar cal = Calendar.getInstance();
        spnMeses.setSelection(cal.get(Calendar.MONTH));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
