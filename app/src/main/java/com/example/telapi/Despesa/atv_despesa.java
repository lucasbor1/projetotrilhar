package com.example.telapi.Despesa;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.telapi.MyApp;
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
    private Spinner spnMeses, spnAnos;
    private ListView lstDespesas;
    private TextView edtTotal, edtAberto;

    private DespesaAdapter despesasAdapter;
    private DespesaCRUD despesaCRUD;
    private String userId;
    private Map<String, Map<Integer, List<Despesa>>> despesasPorMesAno;
    private ActivityResultLauncher<Intent> cadastroLauncher;
    private DespesaService despesaService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_despesa);

        despesaService = new DespesaService(new DespesaCRUD(this, MyApp.getInstance().getUserId()));
        inicializarFirebase();
        inicializarUI();
        configurarToolbar();
        configurarSpinners();
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
        despesaCRUD = new DespesaCRUD(this, userId);
        despesasPorMesAno = new HashMap<>();
    }

    private void inicializarUI() {
        btnAdicionar = findViewById(R.id.btnAdicionar);
        spnMeses = findViewById(R.id.spnMeses);
        spnAnos = findViewById(R.id.spnAnos);
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
    private void configurarSpinners() {
        List<String> anos = gerarListaAnos();
        ArrayAdapter<String> adapterAnos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, anos);
        adapterAnos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAnos.setAdapter(adapterAnos);

        ArrayAdapter<CharSequence> adapterMeses = ArrayAdapter.createFromResource(this, R.array.meses, android.R.layout.simple_spinner_item);
        adapterMeses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMeses.setAdapter(adapterMeses);

        spnMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                exibirDespesasPorMesAno();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnAnos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                exibirDespesasPorMesAno();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private List<String> gerarListaAnos() {
        List<String> anos = new ArrayList<>();
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = anoAtual - 10; i <= anoAtual + 10; i++) {
            anos.add(String.valueOf(i));
        }
        return anos;
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
        despesasPorMesAno.clear();

        // Obter despesas do banco
        List<Despesa> todasDespesas = despesaCRUD.listarDespesas();

        if (todasDespesas != null) {
            for (Despesa despesa : todasDespesas) {
                adicionarDespesaPorMesAno(despesa);
            }
        }

        exibirDespesasPorMesAno();
    }


    private void adicionarDespesaPorMesAno(Despesa despesa) {
        String mes = obterMesDaDespesa(despesa);
        int ano = despesa.getAno();
        despesasPorMesAno.computeIfAbsent(mes, k -> new HashMap<>())
                .computeIfAbsent(ano, k -> new ArrayList<>())
                .add(despesa);
    }
    private void exibirDespesasPorMesAno() {
        String mesSelecionado = spnMeses.getSelectedItem().toString();
        int anoSelecionado = Integer.parseInt(spnAnos.getSelectedItem().toString());
        int numeroMes = obterNumeroDoMes(mesSelecionado);
        String mesFormatado = String.format(Locale.getDefault(), "%02d", numeroMes);
        List<Despesa> despesasFiltradas = despesaService.listarDespesasPorMesAno(mesFormatado, anoSelecionado);

        despesasAdapter.setDespesas(despesasFiltradas);
        despesasAdapter.notifyDataSetChanged();
    }

    private void atualizarTotalMensal() {
        String mesSelecionado = spnMeses.getSelectedItem().toString();
        int anoSelecionado = Integer.parseInt(spnAnos.getSelectedItem().toString());

        List<Despesa> despesas = despesasPorMesAno
                .getOrDefault(mesSelecionado, new HashMap<>())
                .getOrDefault(anoSelecionado, new ArrayList<>());

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

    private int obterNumeroDoMes(String nomeMes) {
        String[] nomesMeses = getResources().getStringArray(R.array.meses);
        for (int i = 0; i < nomesMeses.length; i++) {
            if (nomesMeses[i].equalsIgnoreCase(nomeMes)) {
                return i + 1;
            }
        }
        throw new IllegalArgumentException("Mês inválido: " + nomeMes);
    }

    private void configurarSpinnerMesAtual() {
        Calendar calendar = Calendar.getInstance();
        int mesAtual = calendar.get(Calendar.MONTH);
        int anoAtual = calendar.get(Calendar.YEAR);

        spnMeses.setSelection(mesAtual);
        List<String> anos = gerarListaAnos();
        spnAnos.setSelection(anos.indexOf(String.valueOf(anoAtual)));
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
