package com.example.telapi;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;

public class atv_cadastro extends AppCompatActivity implements View.OnClickListener {

    Button btnGravar, btnExcluir;
    ImageButton btnVoltar;
    TextView edtVencimento;
    EditText edtDescricao, edtValor;
    String acao;
    Despesa d;
    DespesaDao dao;

    private void criarComponentes() {
        btnGravar = findViewById(R.id.btnGravar);
        btnGravar.setOnClickListener(this);
        btnGravar.setText(acao);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(this);

        btnExcluir = findViewById(R.id.btnExcluir);
        btnExcluir.setOnClickListener(this);

        if (acao.equals("Inserir"))
            btnExcluir.setVisibility(View.INVISIBLE);
        else
            btnExcluir.setVisibility(View.VISIBLE);

        edtDescricao = findViewById(R.id.edtDescricao);
        edtValor = findViewById(R.id.edtValor);
        edtVencimento = findViewById(R.id.txtDataSelecionada);
        ImageView imgCalendario = findViewById(R.id.imgCalendario);
        imgCalendario.setOnClickListener(this);
    }

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

        acao = getIntent().getExtras().getString("acao");
        dao = new DespesaDao(this);
        criarComponentes();

        // Definir o valor padrão para o campo de valor
        edtValor.setText("R$0,00");

        // Adicionar TextWatcher para formatar o campo de valor enquanto o usuário digita
        edtValor.addTextChangedListener(new TextWatcher() {
            DecimalFormat decFormat = new DecimalFormat("#,##0.00");
            private String valorAnterior = "0.00";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                valorAnterior = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(valorAnterior)) {
                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    double parsed = Double.parseDouble(cleanString) / 100;
                    String formatted = decFormat.format(parsed);
                    valorAnterior = formatted;
                    edtValor.setText(formatted);
                    edtValor.setSelection(formatted.length());
                }
            }
        });

        if (getIntent().getExtras().getSerializable("obj") != null) {
            d = (Despesa) getIntent().getExtras().getSerializable("obj");
            edtDescricao.setText(d.getDescricao());
            edtValor.setText(String.valueOf(d.getValor())); // Apenas define o valor diretamente
            edtVencimento.setText(d.getVencimento());
        }
    }



    @Override
    public void onClick(View v) {
        if (v == btnVoltar) {
            finish();
        } else if (v == btnExcluir) {
            long id = dao.excluir(d);
            Toast.makeText(this, "Despesa " + d.getDescricao() + " foi excluída com sucesso!", Toast.LENGTH_LONG).show();
            finish();
        } else if (v == btnGravar) {
            d.setDescricao(edtDescricao.getText().toString());
            d.setValor(Double.parseDouble(edtValor.getText().toString()));
            d.setVencimento(edtVencimento.getText().toString()); // Mudança aqui

            if (acao.equals("Inserir")) {
                long id = dao.inserir(d);
                Toast.makeText(this, "Despesa " + d.getDescricao() + " foi inserida com sucesso! ID: " + id, Toast.LENGTH_LONG).show();
            } else {
                long id = dao.alterar(d);
                Toast.makeText(this, "Despesa " + d.getDescricao() + " foi alterada com sucesso!", Toast.LENGTH_LONG).show();
            }
            finish();
        } else if (v.getId() == R.id.imgCalendario) { // Verifique o ID do ImageView do calendário
            abrirCalendario();
        }
    }

    private void abrirCalendario() {
        final Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialogCustom datePickerDialog = new DatePickerDialogCustom(this, (view, year, monthOfYear, dayOfMonth) -> {
            String dataSelecionada = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
            edtVencimento.setText(dataSelecionada);
        }, ano, mes, dia);

        datePickerDialog.show();
    }}
