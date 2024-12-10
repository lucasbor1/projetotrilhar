package com.example.telapi.Despesa;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.telapi.Categoria.CategoriaCRUD;
import com.example.telapi.Categoria.CategoriaService;
import com.example.telapi.Categoria.modal_categoria;
import com.example.telapi.MyApp;
import com.example.telapi.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class atv_cadastro extends AppCompatActivity implements View.OnClickListener, DespesaUpdateListener {

    // === Variáveis de Instância ===
    private AutoCompleteTextView autoCompleteCategoria;
    private EditText edtDescricao, edtValor, edtVencimento, edtNumeroParcelas;
    private SwitchMaterial switchDespesaPaga;
    private CheckBox checkboxDespesaPermanente, checkboxDespesaParcelada;
    private LinearLayout layoutParcelas;
    private FloatingActionButton btnAddCategoria;
    private View btnExcluir;

    private DespesaFormHandler formHandler;
    private DespesaService despesaService;
    private CategoriaService categoriaService;

    private Despesa despesa;
    private String acao;

    // === Ciclo de Vida ===
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_cadastro);

        inicializarToolbar();
        inicializarComponentes();
        configurarFormHandler();
        configurarListeners();

        carregarCategorias();
        verificarAcao();
    }

    // === Inicialização ===
    private void inicializarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_botao_back_small);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void inicializarComponentes() {
        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtValor = findViewById(R.id.edtValor);
        edtVencimento = findViewById(R.id.edtVencimento);
        switchDespesaPaga = findViewById(R.id.switchDespesaPaga);
        checkboxDespesaPermanente = findViewById(R.id.checkboxDespesaPermanente);
        checkboxDespesaParcelada = findViewById(R.id.checkboxDespesaParcelada);
        layoutParcelas = findViewById(R.id.layoutParcelas);
        edtNumeroParcelas = findViewById(R.id.edtNumeroParcelas);
        btnAddCategoria = findViewById(R.id.btnAddCategoria);
        btnExcluir = findViewById(R.id.btnExcluir);

        formHandler = new DespesaFormHandler(
                autoCompleteCategoria, edtDescricao, edtValor,
                edtVencimento, switchDespesaPaga,
                checkboxDespesaPermanente, checkboxDespesaParcelada, edtNumeroParcelas
        );
        despesaService = new DespesaService(new DespesaCRUD(this, MyApp.getInstance().getUserId()));
        categoriaService = new CategoriaService(new CategoriaCRUD(this, MyApp.getInstance().getUserId()));
    }

    private void configurarFormHandler() {
        edtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                formHandler.formatarValor();
            }
        });

        checkboxDespesaParcelada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutParcelas.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) edtNumeroParcelas.setText("");
        });

        checkboxDespesaPermanente.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxDespesaParcelada.setChecked(false);
                layoutParcelas.setVisibility(View.GONE);
                edtNumeroParcelas.setText("");
            }
        });
    }

    private void configurarListeners() {
        btnAddCategoria.setOnClickListener(this);
        findViewById(R.id.imgCalendario).setOnClickListener(this);
        findViewById(R.id.btnGravar).setOnClickListener(this);
        btnExcluir.setOnClickListener(this);
    }

    private void verificarAcao() {
        acao = getIntent().getStringExtra("acao");
        despesa = (Despesa) getIntent().getSerializableExtra("obj");

        if ("ALTERAR".equals(acao) && despesa != null) {
            preencherCamposDespesa();
            btnExcluir.setVisibility(View.VISIBLE);
        } else {
            btnExcluir.setVisibility(View.GONE);
        }
    }

    // === Métodos do Formulário ===
    private void preencherCamposDespesa() {
        if (despesa != null) {
            formHandler.setCategoria(despesa.getCategoria());
            formHandler.setDescricao(despesa.getDescricao());
            formHandler.setValor(despesa.getValor());
            formHandler.setVencimento(despesa.getVencimento());
            formHandler.setPago(despesa.isPago());
            formHandler.setPermanente(despesa.isPermanente());
            formHandler.setParcelada(despesa.isParcelada(), despesa.getNumeroParcelas(), despesa.getParcelaAtual());
        }
    }

    private boolean validarVencimento(String vencimento) {
        if (vencimento.isEmpty()) return false;

        String regex = "\\d{2}/\\d{2}/\\d{4}";
        return vencimento.matches(regex);
    }

    private void salvarDespesa() {
        Despesa novaDespesa = formHandler.obterDespesa(despesa != null ? despesa.getId() : -1);

        if (novaDespesa == null || !validarVencimento(novaDespesa.getVencimento())) {
            Toast.makeText(this, "Preencha todos os campos corretamente, incluindo o vencimento.", Toast.LENGTH_SHORT).show();
            return;
        }

        despesaService.salvarOuAtualizarDespesa(novaDespesa, despesa != null);
        setResult(RESULT_OK);
        finish();
    }

    private void excluirDespesa() {
        if (despesa != null) {
            despesaService.removerDespesa(despesa.getId());
            setResult(RESULT_OK);
            finish();
        }
    }

    // === Categorias ===
    private void carregarCategorias() {
        List<String> nomesCategorias = categoriaService.listarCategorias();
        if (nomesCategorias == null) {
            nomesCategorias = new ArrayList<>();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nomesCategorias);
        autoCompleteCategoria.setAdapter(adapter);
    }

    private void abrirModalCategoria() {
        modal_categoria modal = new modal_categoria();
        modal.setCategoriaDialogListener(new modal_categoria.CategoriaDialogListener() {
            @Override
            public void onCategoriaSelecionada(String nomeCategoria) {
                if (nomeCategoria != null && !nomeCategoria.trim().isEmpty()) {
                    autoCompleteCategoria.setText(nomeCategoria);
                }
            }

            @Override
            public void onCategoriaAdicionada(String nomeCategoria) {
                if (nomeCategoria != null && !nomeCategoria.trim().isEmpty()) {
                    autoCompleteCategoria.setText(nomeCategoria);
                    carregarCategorias();
                }
            }

            @Override
            public void onCategoriaRemovida(String nomeCategoria) {
                carregarCategorias();
            }
        });
        modal.show(getSupportFragmentManager(), "modal_categoria");
    }

    // === Listener de Clicks ===
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnGravar) {
            salvarDespesa();
        } else if (id == R.id.btnExcluir) {
            excluirDespesa();
        } else if (id == R.id.btnAddCategoria) {
            abrirModalCategoria();
        } else if (id == R.id.imgCalendario) {
            formHandler.abrirCalendario(this);
        }
    }
    // === Atualizações de Despesas ===
    @Override
    public void onDespesaAtualizada(List<Despesa> despesas) {
        Log.d("atv_cadastro", "onDespesaAtualizada chamado");

        if (despesas != null && !despesas.isEmpty()) {
            Log.d("atv_cadastro", "Despesas atualizadas: " + despesas.size() + " despesas.");

            Intent intent = new Intent();
            intent.putExtra("despesas", (ArrayList<Despesa>) despesas);
            setResult(RESULT_OK, intent);

            Log.d("atv_cadastro", "Resultado OK enviado.");
            finish();
        } else {
            Log.d("atv_cadastro", "Lista de despesas vazia ou nula.");
        }
    }
}
