package com.example.telapi.Despesa;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.telapi.AdpSpinner;
import com.example.telapi.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class atv_despesa extends AppCompatActivity implements DespesaUpdateListener {

    private static final String TAG = "atv_despesa";

    private ImageButton btnAdicionar;
    private Spinner spnMeses;
    private ListView lstDespesas;
    private TextView edtTotal, edtAberto;

    private DespesaAdapter despesasAdapter;
    private DespesaCRUD despesaCRUD;
    private String userId;
    private Map<String, List<Despesa>> despesasPorMes;

    private ActivityResultLauncher<Intent> cadastroLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_despesa);

        inicializarFirebase();
        inicializarUI();
        configurarToolbar();
        configurarSpinner();
        configurarBotaoAdicionar();
        configurarCadastroLauncher();

        carregarDespesas();
        configurarSpinnerMesAtual();
        atualizarTotalMensal();
    }

    private void inicializarFirebase() {
        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "Erro ao obter ID do usuário", Toast.LENGTH_SHORT).show();
            finish();
        }
        despesaCRUD = new DespesaCRUD(this, userId, this);
        despesasPorMes = new HashMap<>();
    }

    private void inicializarUI() {
        btnAdicionar = findViewById(R.id.btnAdicionar);
        spnMeses = findViewById(R.id.spnMeses);
        lstDespesas = findViewById(R.id.lstDespesas);
        edtTotal = findViewById(R.id.edtTotal);
        edtAberto = findViewById(R.id.edtAberto);

        despesasAdapter = new DespesaAdapter(this, new ArrayList<>());
        lstDespesas.setAdapter(despesasAdapter);

        lstDespesas.setOnItemClickListener((parent, view, position, id) -> {
            Despesa despesaSelecionada = despesasAdapter.getDespesa(position);
            if (despesaSelecionada != null) {
                abrirTelaCadastro(despesaSelecionada);
            }
        });
    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void configurarSpinner() {
        AdpSpinner adapter = new AdpSpinner(this, R.layout.item_spinner, getResources().getStringArray(R.array.meses));
        spnMeses.setAdapter(adapter);

        spnMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mesSelecionado = (String) parent.getItemAtPosition(position);
                exibirDespesasPorMes(mesSelecionado);
                atualizarTotalMensal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void configurarBotaoAdicionar() {
        btnAdicionar.setOnClickListener(v -> abrirTelaCadastro(null));
    }

    private void configurarCadastroLauncher() {
        cadastroLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        carregarDespesas();
                        atualizarTotalMensal();
                    }
                }
        );
    }

    private void carregarDespesas() {
        List<Despesa> todasDespesas = despesaCRUD.listarDespesas();
        despesasPorMes.clear();

        if (todasDespesas != null) {
            for (Despesa despesa : todasDespesas) {
                adicionarDespesaNova(despesa);
            }
            despesasAdapter.setDespesas(todasDespesas);
        } else {
            despesasAdapter.setDespesas(new ArrayList<>());
        }

        despesasAdapter.notifyDataSetChanged();
    }

    private void adicionarDespesaNova(Despesa novaDespesa) {
        String mes = obterMesDaDespesa(novaDespesa);
        despesasPorMes.computeIfAbsent(mes, k -> new ArrayList<>()).add(novaDespesa);
    }

    private void exibirDespesasPorMes(String mesSelecionado) {
        List<Despesa> despesas = despesasPorMes.getOrDefault(mesSelecionado, new ArrayList<>());
        despesasAdapter.setDespesas(despesas);
    }

    private void atualizarTotalMensal() {
        String mesSelecionado = spnMeses.getSelectedItem().toString();
        List<Despesa> despesas = despesasPorMes.getOrDefault(mesSelecionado, new ArrayList<>());

        double totalMensal = 0;
        double despesasEmAberto = 0;

        for (Despesa despesa : despesas) {
            totalMensal += despesa.getValor();
            if (!despesa.isPago()) {
                despesasEmAberto += despesa.getValor();
            }
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        edtTotal.setText("Total: " + currencyFormat.format(totalMensal));
        edtAberto.setText("Em Aberto: " + currencyFormat.format(despesasEmAberto));
    }

    private void abrirTelaCadastro(Despesa despesa) {
        Intent intent = new Intent(this, atv_cadastro.class);
        if (despesa != null) {
            intent.putExtra("acao", "ALTERAR");
            intent.putExtra("obj", despesa);
        }
        cadastroLauncher.launch(intent);
    }

    private String obterMesDaDespesa(Despesa despesa) {
        String vencimento = despesa.getVencimento();
        String[] partesData = vencimento.split("/");
        int mes = Integer.parseInt(partesData[1]);
        String[] nomesMeses = getResources().getStringArray(R.array.meses);
        return nomesMeses[mes - 1];
    }

    private void configurarSpinnerMesAtual() {
        Calendar calendar = Calendar.getInstance();
        int mesAtual = calendar.get(Calendar.MONTH);
        spnMeses.setSelection(mesAtual);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDespesaAtualizada(List<Despesa> despesas) {
        carregarDespesas();
        atualizarTotalMensal();
    }
}
