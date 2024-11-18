package com.example.telapi.Despesa;

import android.content.Intent;
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
    private ActivityResultLauncher<Intent> cadastroLauncher;

    private static final String TAG = "atv_despesa";

    // Lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_despesa);
        setupUI();
        initializeVariables();
        setupListeners();
        carregarDespesas();
        configurarSpinnerMesAtual();
        atualizarTotalMensal();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume chamado - recarregando despesas");
        carregarDespesas();
        despesasAdapter.notifyDataSetChanged();
        atualizarTotalMensal();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // UI setup
    private void setupUI() {
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
    }

    private void initializeVariables() {
        despesasPorMes = new HashMap<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;
        despesaCRUD = new DespesaCRUD(this, userId, this);

        if (userId == null) {
            Log.e(TAG, "Erro: User ID é null");
            Toast.makeText(this, "Erro ao obter ID do usuário", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        despesasAdapter = new DespesaAdapter(this, new ArrayList<>());
        lstDespesas.setAdapter(despesasAdapter);

        String[] mesesArray = getResources().getStringArray(R.array.meses);
        AdpSpinner adapter = new AdpSpinner(this, R.layout.item_spinner, mesesArray);
        spnMeses.setAdapter(adapter);

        cadastroLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.d(TAG, "Cadastro atualizado com sucesso");
                    }
                }
        );
    }

    private void setupListeners() {
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

        btnAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(atv_despesa.this, atv_cadastro.class);
            intent.putExtra("userId", userId);
            cadastroLauncher.launch(intent);
        });

        lstDespesas.setOnItemClickListener((parent, view, position, id) -> {
            Despesa despesaSelecionada = despesasAdapter.getDespesa(position);
            if (despesaSelecionada != null) {
                abrirTelaCadastroComDespesa(despesaSelecionada);
            }
        });
    }

    // CRUD operations
    private void carregarDespesas() {
        List<Despesa> todasDespesas = despesaCRUD.listarDespesas();
        despesasPorMes.clear();

        if (todasDespesas != null && !todasDespesas.isEmpty()) {
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
        List<Despesa> despesasDoMes = despesasPorMes.getOrDefault(mes, new ArrayList<>());
        despesasDoMes.add(novaDespesa);
        despesasPorMes.put(mes, despesasDoMes);
    }

    private void exibirDespesasPorMes(String mes) {
        List<Despesa> despesas = despesasPorMes.get(mes);
        despesasAdapter.clear();
        if (despesas != null) {
            despesasAdapter.addAll(despesas);
        }
        despesasAdapter.notifyDataSetChanged();
    }

    private void atualizarDespesaNaLista(Despesa despesaAtualizada) {
        for (int i = 0; i < despesasAdapter.getCount(); i++) {
            Despesa despesa = despesasAdapter.getDespesa(i);
            if (despesa.getId() == despesaAtualizada.getId()) {
                despesasAdapter.getDespesa(i).setDescricao(despesaAtualizada.getDescricao());
                despesasAdapter.getDespesa(i).setValor(despesaAtualizada.getValor());
                despesasAdapter.getDespesa(i).setVencimento(despesaAtualizada.getVencimento());
                despesasAdapter.getDespesa(i).setPago(despesaAtualizada.isPago());
                despesasAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private void removerDespesaNaLista(Despesa despesaRemovida) {
        for (int i = 0; i < despesasAdapter.getCount(); i++) {
            Despesa despesa = despesasAdapter.getDespesa(i);
            if (despesa.getId() == despesaRemovida.getId()) {
                despesasAdapter.remove(despesa);
                despesasAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    // Utility methods
    private String obterMesDaDespesa(Despesa despesa) {
        String vencimento = despesa.getVencimento();
        String[] partesData = vencimento.split("/");
        int mes = Integer.parseInt(partesData[1]);
        String[] nomesMeses = getResources().getStringArray(R.array.meses);
        return nomesMeses[mes - 1];
    }

    private void configurarSpinnerMesAtual() {
        Calendar cal = Calendar.getInstance();
        spnMeses.setSelection(cal.get(Calendar.MONTH));
    }

    private void atualizarTotalMensal() {
        String mesSelecionado = spnMeses.getSelectedItem().toString();
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
        edtTotal.setText("Total: " + currencyFormat.format(totalMensal));
        edtAberto.setText("Em Aberto: " + currencyFormat.format(despesasEmAberto));
    }

    // Intent handling
    private void abrirTelaCadastroComDespesa(Despesa despesa) {
        Intent intent = new Intent(atv_despesa.this, atv_cadastro.class);
        intent.putExtra("acao", "ALTERAR");
        intent.putExtra("obj", despesa);
        startActivity(intent);
    }

    private void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String acao = data.getStringExtra("acao");
            Despesa despesaAtualizada = (Despesa) data.getSerializableExtra("despesa");

            if (despesaAtualizada != null) {
                if ("ATUALIZAR".equals(acao)) {
                    atualizarDespesaNaLista(despesaAtualizada);
                } else if ("REMOVER".equals(acao)) {
                    removerDespesaNaLista(despesaAtualizada);
                }
            }

            despesasAdapter.notifyDataSetChanged();
            atualizarTotalMensal();
        }
    }

    @Override
    public void onDespesaAtualizada(List<Despesa> despesas) {
        if (despesas != null && !despesas.isEmpty()) {
            despesasAdapter.clear();
            despesasAdapter.addAll(despesas);
            despesasAdapter.notifyDataSetChanged();
        }

        atualizarTotalMensal();
    }
}
