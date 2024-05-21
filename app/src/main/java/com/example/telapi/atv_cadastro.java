package com.example.telapi;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class atv_cadastro extends AppCompatActivity implements View.OnClickListener {

    Button btnGravar, btnExcluir;
    ImageButton btnVoltar;
    EditText edtDescricao, edtValor, edtVencimento;
    String acao;
    Despesa d;
    DespesaDao dao;

    private void criarComponentes(){
        btnGravar = findViewById(R.id.btnGravar);
        btnGravar.setOnClickListener(this);
        btnGravar.setText(acao);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(this);

        btnExcluir = findViewById(R.id.btnExcluir);
        btnExcluir.setOnClickListener(this);

        if(acao.equals("Inserir"))
            btnExcluir.setVisibility(View.INVISIBLE);
        else btnExcluir.setVisibility(View.VISIBLE);

        edtDescricao = findViewById(R.id.edtDescricao);
        edtValor = findViewById(R.id.edtValor);
        edtVencimento = findViewById(R.id.edtVencimento);
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

        if(getIntent().getExtras().getSerializable("obj")!= null){
            d = (Despesa) getIntent().getExtras().getSerializable("obj");
            edtDescricao.setText(d.getDescricao());
            edtValor.setText(String.valueOf(d.getValor()));
            edtVencimento.setText(d.getVencimento());
        }
    }
    @Override
    public void onClick(View v){
        if (v==btnVoltar){
            finish();
        } else if (v == btnExcluir) {
            long id = dao.excluir(d);
            Toast.makeText(this,"Despesa"+ d.getDescricao()+" foi excluido com sucesso!", Toast.LENGTH_LONG).show();
            finish();
        } else if (v == btnGravar) {
            d.setDescricao((edtDescricao.getText().toString()));
            d.setValor(Double.parseDouble(edtValor.getText().toString()));
            d.setVencimento(edtVencimento.getText().toString());

            if (acao.equals("Inserir")){
                long id = dao.inserir(d);
                Toast.makeText(this,"Despesa"+ d.getDescricao()+" foi inserido com sucesso"+ id,Toast.LENGTH_LONG).show();
                 }
            else {
                long id = dao.alterar(d);
                Toast.makeText(this,"Despesa"+d.getDescricao()+" foi alterado com sucesso!",Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}