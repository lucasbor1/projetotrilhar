package com.example.telapi.Despesa;

import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.telapi.MyApp;
import com.example.telapi.R;
import com.example.telapi.Categoria.Categoria;
import com.example.telapi.Categoria.CategoriaCRUD;
import com.example.telapi.Categoria.modal_categoria;
import com.example.telapi.calendario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class atv_cadastro extends AppCompatActivity implements View.OnClickListener {
    private AutoCompleteTextView autoCompleteCategoria;
    private EditText edtDescricao, edtValor, edtVencimento;
    private Button btnGravar, btnExcluir;
    private SwitchMaterial switchDespesaPaga;

    private ImageView imgDespesaStatus;
    private List<Categoria> categorias;

    private DespesaCRUD despesaCRUD;
    private CategoriaCRUD categoriaCRUD;
    private Despesa despesa;
    private String acao;
    private boolean isFormatting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_cadastro);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtValor = findViewById(R.id.edtValor);
        edtVencimento = findViewById(R.id.edtVencimento);
        switchDespesaPaga = findViewById(R.id.switchDespesaPaga);
        imgDespesaStatus = findViewById(R.id.imgDespesaStatus);
        btnGravar = findViewById(R.id.btnGravar);
        btnExcluir = findViewById(R.id.btnExcluir);
        FloatingActionButton btnAddCategoria = findViewById(R.id.btnAddCategoria);

        String userId = MyApp.getInstance().getUserId();
        despesaCRUD = new DespesaCRUD(this, userId);
        categoriaCRUD = new CategoriaCRUD(this, userId);

        categorias = new ArrayList<>();
        carregarCategorias();

        acao = getIntent().getStringExtra("acao");
        despesa = (Despesa) getIntent().getSerializableExtra("obj");

        if ("ALTERAR".equals(acao) && despesa != null) {
            preencherCamposDespesa();
            btnExcluir.setVisibility(View.VISIBLE);
        } else {
            btnExcluir.setVisibility(View.GONE);
        }

        btnGravar.setOnClickListener(this);
        btnExcluir.setOnClickListener(this);
        btnAddCategoria.setOnClickListener(this);
        findViewById(R.id.imgCalendario).setOnClickListener(this);

        edtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                formatarValor();
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
            abrirCalendario();
        }
    }

    private void salvarDespesa() {
        Despesa novaDespesa = criarDespesa();
        if (novaDespesa == null) {
            Toast.makeText(this, "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (despesa == null || despesa.getId() == -1) {
            despesaCRUD.adicionarDespesa(novaDespesa);
            Toast.makeText(this, "Despesa adicionada", Toast.LENGTH_SHORT).show();
        } else {
            novaDespesa.setId(despesa.getId());
            despesaCRUD.alterarDespesa(novaDespesa);
            Toast.makeText(this, "Despesa atualizada", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void excluirDespesa() {
        if (despesa != null) {
            despesaCRUD.removerDespesa(despesa.getId());
            Toast.makeText(this, "Despesa removida", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private Despesa criarDespesa() {
        String categoria = autoCompleteCategoria.getText().toString().trim();
        String descricao = edtDescricao.getText().toString().trim();

        String valorStr = edtValor.getText().toString()
                .replace("R$", "")
                .replace("\u00A0", "")
                .replaceAll("[^\\d.,]", "")
                .replace(",", ".");
        double valor = valorStr.isEmpty() ? 0.0 : Double.parseDouble(valorStr);

        String vencimento = edtVencimento.getText().toString().trim();
        boolean isPaga = switchDespesaPaga.isChecked();

        if (descricao.isEmpty() || valor <= 0 || vencimento.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show();
            return null;
        }

        int id = (despesa != null) ? despesa.getId() : -1;
        return new Despesa(id, categoria, descricao, valor, vencimento, isPaga);
    }

    private void preencherCamposDespesa() {
        if (despesa != null) {
            autoCompleteCategoria.setText(despesa.getCategoria());
            edtDescricao.setText(despesa.getDescricao());
            edtValor.setText(String.format(Locale.getDefault(), "R$%.2f", despesa.getValor()));
            edtVencimento.setText(despesa.getVencimento());
            switchDespesaPaga.setChecked(despesa.isPago());
            imgDespesaStatus.setImageResource(despesa.isPago() ? R.drawable.pago : R.drawable.naopago);
        }
    }

    private void abrirCalendario() {
        Calendar calendario = Calendar.getInstance();
        new calendario(this, (view, year, month, dayOfMonth) -> {
            String dataSelecionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
            edtVencimento.setText(dataSelecionada);
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void carregarCategorias() {
        List<String> nomesCategorias = categoriaCRUD.listarCategorias();
        if (nomesCategorias == null) {
            nomesCategorias = new ArrayList<>();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nomesCategorias);
        autoCompleteCategoria.setAdapter(adapter);
    }

    private void formatarValor() {
        if (isFormatting) return;
        isFormatting = true;

        String valorStr = edtValor.getText().toString().replace("R$", "").replaceAll("[^\\d,]", "").replace(",", ".");
        if (valorStr.isEmpty()) {
            edtValor.setText("R$0,00");
            edtValor.setSelection(4);
            isFormatting = false;
            return;
        }

        double valor = Double.parseDouble(valorStr);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = currencyFormat.format(valor);

        edtValor.setText(valorFormatado);
        edtValor.setSelection(valorFormatado.length());
        isFormatting = false;
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
            public void onCategoriaRemovida(String nomeCategoria) {
            }
            @Override
            public void onCategoriaAdicionada(String nomeCategoria) {
                if (nomeCategoria != null && !nomeCategoria.trim().isEmpty()) {
                    autoCompleteCategoria.setText(nomeCategoria);
                }
            }
        });
        modal.show(getSupportFragmentManager(), "modal_categoria");
    }


}
