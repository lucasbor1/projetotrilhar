package com.example.telapi.Despesa;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telapi.Categoria.CategoriaCRUD;
import com.example.telapi.Categoria.CategoriaService;
import com.example.telapi.Categoria.modal_categoria;
import com.example.telapi.Despesa.Despesa;
import com.example.telapi.Despesa.DespesaCRUD;
import com.example.telapi.Despesa.DespesaFormHandler;
import com.example.telapi.Despesa.DespesaService;
import com.example.telapi.MyApp;
import com.example.telapi.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class atv_cadastro extends AppCompatActivity implements View.OnClickListener, DespesaUpdateListener {
    private AutoCompleteTextView autoCompleteCategoria;
    private EditText edtDescricao, edtValor, edtVencimento;
    private SwitchMaterial switchDespesaPaga;
    private FloatingActionButton btnAddCategoria;
    private View btnExcluir;
    private DespesaFormHandler formHandler;
    private DespesaService despesaService;
    private CategoriaService categoriaService;
    private Despesa despesa;
    private String acao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_cadastro);

        // Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Inicializar os componentes
        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtValor = findViewById(R.id.edtValor);
        edtVencimento = findViewById(R.id.edtVencimento);
        switchDespesaPaga = findViewById(R.id.switchDespesaPaga);
        btnAddCategoria = findViewById(R.id.btnAddCategoria);
        btnExcluir = findViewById(R.id.btnExcluir);

        formHandler = new DespesaFormHandler(autoCompleteCategoria, edtDescricao, edtValor, edtVencimento, switchDespesaPaga);
        despesaService = new DespesaService(new DespesaCRUD(this, MyApp.getInstance().getUserId(), this));
        categoriaService = new CategoriaService(new CategoriaCRUD(this, MyApp.getInstance().getUserId()));

        carregarCategorias();

        acao = getIntent().getStringExtra("acao");
        despesa = (Despesa) getIntent().getSerializableExtra("obj");

        if ("ALTERAR".equals(acao) && despesa != null) {
            preencherCamposDespesa();
            btnExcluir.setVisibility(View.VISIBLE);
        } else {
            btnExcluir.setVisibility(View.GONE);
        }

        // Configurar Listeners
        btnAddCategoria.setOnClickListener(this);
        findViewById(R.id.imgCalendario).setOnClickListener(this);
        findViewById(R.id.btnGravar).setOnClickListener(this);
        btnExcluir.setOnClickListener(this);

        // Listener para formatação do campo de valor
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
    }
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
    private void salvarDespesa() {
        Despesa novaDespesa = formHandler.obterDespesa(despesa != null ? despesa.getId() : -1);
        if (novaDespesa == null) {
            Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show();
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



    private void preencherCamposDespesa() {
        if (despesa != null) {
            formHandler.setCategoria(despesa.getCategoria());
            formHandler.setDescricao(despesa.getDescricao());
            formHandler.setValor(despesa.getValor());
            formHandler.setVencimento(despesa.getVencimento());
            formHandler.setPago(despesa.isPago());
        }
    }

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

    @Override
    public void onDespesaAtualizada(List<Despesa> despesas) {
        Log.d("atv_cadastro", "onDespesaAtualizada chamado");

        if (despesas != null && !despesas.isEmpty()) {
            Log.d("atv_cadastro", "Despesas atualizadas: " + despesas.size() + " despesas.");

            // Passando a lista de despesas atualizada de volta para a atividade pai (atv_despesa)
            Intent intent = new Intent();
            intent.putExtra("despesas", (ArrayList<Despesa>) despesas);  // Adicionando a lista de despesas ao Intent
            setResult(RESULT_OK, intent);  // Definindo o resultado da operação

            Log.d("atv_cadastro", "Resultado OK enviado.");
            finish();  // Finalizando a atividade de cadastro
        } else {
            Log.d("atv_cadastro", "Lista de despesas vazia ou nula.");
        }
    }



}
