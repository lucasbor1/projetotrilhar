package com.example.telapi;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.icu.text.DecimalFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.telapi.CRUDDespesas;
import com.example.telapi.CategoriaDialogFragment;
import com.example.telapi.Despesa;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class atv_cadastro extends AppCompatActivity implements View.OnClickListener, CategoriaDialogFragment.CategoriaDialogListener {
    Button btnGravar, btnExcluir;
    ImageButton btnVoltar;
    TextView edtVencimento;
    EditText edtDescricao, edtValor;
    String acao;
    Despesa d;
    CRUDDespesas crudDespesas;
    private FirebaseFirestore db;
    private AutoCompleteTextView autoCompleteCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cadastro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();

        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        autoCompleteCategoria.setAdapter(adapter);
        atualizarAutoCompleteCategoria();

        FloatingActionButton btnAddCategoria = findViewById(R.id.btnAddCategoria);
        btnAddCategoria.setOnClickListener(v -> {
            CategoriaDialogFragment dialog = new CategoriaDialogFragment();
            dialog.setCategoriaDialogListener(atv_cadastro.this);
            dialog.show(getSupportFragmentManager(), "CategoriaDialogFragment");
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            acao = extras.getString("acao");
            d = (Despesa) extras.getSerializable("obj");
        } else {
            acao = "Inserir";
            d = null;
        }

        crudDespesas = new CRUDDespesas();
        criarComponentes();

        if ("Alterar".equals(acao) && d != null) {
            edtDescricao.setText(d.getDescricao());
            edtValor.setText(formatarValor(d.getValor()));
            edtVencimento.setText(d.getVencimento());
        } else {
            edtValor.setText("R$0,00");
        }
    }

    private void criarComponentes() {
        btnGravar = findViewById(R.id.btnGravar);
        btnGravar.setOnClickListener(this);
        btnGravar.setText(acao);
        btnExcluir = findViewById(R.id.btnExcluir);
        btnExcluir.setOnClickListener(this);

        if ("Inserir".equals(acao)) {
            btnExcluir.setVisibility(View.INVISIBLE);
        } else {
            btnExcluir.setVisibility(View.VISIBLE);
        }

        edtDescricao = findViewById(R.id.edtDescricao);
        edtValor = findViewById(R.id.edtValor);
        edtVencimento = findViewById(R.id.txtDataSelecionada);
        ImageView imgCalendario = findViewById(R.id.imgCalendario);
        imgCalendario.setOnClickListener(this);

        configurarTecladoNumerico();
    }

    private void configurarTecladoNumerico() {
        TextView button0 = findViewById(R.id.button0);
        TextView button1 = findViewById(R.id.button1);
        TextView button2 = findViewById(R.id.button2);
        TextView button3 = findViewById(R.id.button3);
        TextView button4 = findViewById(R.id.button4);
        TextView button5 = findViewById(R.id.button5);
        TextView button6 = findViewById(R.id.button6);
        TextView button7 = findViewById(R.id.button7);
        TextView button8 = findViewById(R.id.button8);
        TextView button9 = findViewById(R.id.button9);
        TextView buttonErase = findViewById(R.id.buttonErase);

        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        buttonErase.setOnClickListener(this);
    }

    private void apagarUltimoCaractere() {
        String currentValue = edtValor.getText().toString();
        if (currentValue.length() > 4) { // para garantir que sempre temos "R$0,00"
            currentValue = currentValue.substring(0, currentValue.length() - 1);
            if (currentValue.length() == 3) {
                currentValue = "R$0,00";
            } else {
                currentValue = formatarValor(Double.parseDouble(currentValue.replace("R$", "").replace(",", ".")) / 10);
            }
            edtValor.setText(currentValue);
        }
    }

    private void onNumberClick(View v) {
        TextView button = (TextView) v;
        String buttonText = button.getText().toString();
        String currentValue = edtValor.getText().toString().replace("R$", "").replace(",", "");

        if (currentValue.equals("0")) {
            currentValue = buttonText;
        } else {
            // Remove os zeros à esquerda
            currentValue = currentValue.replaceAll("^0+(?!$)", "");
            currentValue += buttonText;
        }

        // Formatando o valor para adicionar a vírgula
        StringBuilder formattedValue = new StringBuilder(currentValue);
        if (formattedValue.length() >= 3) {
            formattedValue.insert(formattedValue.length() - 2, ",");
        } else {
            formattedValue.insert(0, "0,");
        }

        // Adicionando o "R$" novamente ao texto
        edtValor.setText("R$" + formattedValue.toString());
    }


    private String formatarValor(double valor) {
        DecimalFormat df = new DecimalFormat("R$#,##0.00");
        return df.format(valor);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnGravar) {
            if ("Inserir".equals(acao)) {
                adicionarDespesa();
            } else if ("Alterar".equals(acao)) {
                atualizarDespesa();
            }
        } else if (id == R.id.btnExcluir) {
            excluirDespesa();
        } else if (id == R.id.imgCalendario) {
            abrirDatePicker();
        } else if (id == R.id.buttonErase) {
            apagarUltimoCaractere();
        } else {
            onNumberClick(v);
        }
    }

    private void abrirDatePicker() {
        Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> edtVencimento.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), ano, mes, dia);
        dialog.show();
    }

    private void adicionarDespesa() {
        String descricao = edtDescricao.getText().toString();
        double valor = Double.parseDouble(edtValor.getText().toString().replace("R$", "").replace(",", "."));
        String vencimento = edtVencimento.getText().toString();

        Despesa despesa = new Despesa();
        despesa.setDescricao(descricao);
        despesa.setValor(valor);
        despesa.setVencimento(vencimento);

        db.collection("despesas")
                .add(despesa)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Despesa adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao adicionar despesa", Toast.LENGTH_SHORT).show());
    }

    private void atualizarDespesa() {
        d.setDescricao(edtDescricao.getText().toString());
        d.setValor(Double.parseDouble(edtValor.getText().toString().replace("R$", "").replace(",", ".")));
        d.setVencimento(edtVencimento.getText().toString());

        db.collection("despesas")
                .document(d.getId())
                .set(d)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Despesa atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao atualizar despesa", Toast.LENGTH_SHORT).show());
    }

    private void excluirDespesa() {
        db.collection("despesas")
                .document(d.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Despesa excluída com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao excluir despesa", Toast.LENGTH_SHORT).show());
    }

    private void atualizarAutoCompleteCategoria() {
        db.collection("categorias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> categorias = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String nomeCategoria = documentSnapshot.getString("nome");
                        categorias.add(nomeCategoria);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categorias);
                    autoCompleteCategoria.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Toast.makeText(atv_cadastro.this, "Erro ao obter categorias", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onCategoriaAdicionada(String categoria) {
        Map<String, Object> data = new HashMap<>();
        data.put("nome", categoria);

        db.collection("categorias").add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(atv_cadastro.this, "Categoria adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                    atualizarAutoCompleteCategoria();
                })
                .addOnFailureListener(e -> Toast.makeText(atv_cadastro.this, "Erro ao adicionar categoria", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onCategoriaRemovida(String categoriaId) {
        db.collection("categorias").document(categoriaId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(atv_cadastro.this, "Categoria removida com sucesso!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(atv_cadastro.this, "Erro ao remover categoria", Toast.LENGTH_SHORT).show());
    }
}
