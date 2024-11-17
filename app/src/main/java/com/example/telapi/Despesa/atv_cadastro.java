package com.example.telapi.Despesa;

import static com.example.telapi.Despesa.atv_despesa.REQUEST_CODE;

import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telapi.Categoria.Categoria;
import com.example.telapi.Categoria.CategoriaCRUD;
import com.example.telapi.Categoria.modal_categoria;
import com.example.telapi.R;
import com.example.telapi.calendario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class atv_cadastro extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView autoCompleteCategoria;
    private EditText edtDescricao, edtValor, edtVencimento;
    private Button btnGravar, btnExcluir;
    private Despesa despesa;
    private String acao;
    private DespesaCRUD despesaCRUD;
    private FirebaseFirestore db;

    private SwitchMaterial switchDespesaPaga;
    private ImageView imgDespesaStatus;
    private List<Categoria> categorias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_cadastro);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_botao_back_small);

        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtValor = findViewById(R.id.edtValor);
        edtVencimento = findViewById(R.id.edtVencimento);
        btnGravar = findViewById(R.id.btnGravar);
        btnExcluir = findViewById(R.id.btnExcluir);
        switchDespesaPaga = findViewById(R.id.switchDespesaPaga);
        imgDespesaStatus = findViewById(R.id.imgDespesaStatus);

        despesaCRUD = new DespesaCRUD();
        db = FirebaseFirestore.getInstance();

        ImageView imgCalendario = findViewById(R.id.imgCalendario);
        imgCalendario.setOnClickListener(v -> abrirCalendario());

        // Certifique-se de inicializar a lista de categorias antes de usar no adapter
        categorias = new ArrayList<>(); // Inicializando a lista
        ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categorias);
        autoCompleteCategoria.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            acao = extras.getString("acao");
            despesa = (Despesa) extras.getSerializable("obj");
        } else {
            acao = "INSERIR";
            despesa = null;
        }

        btnGravar.setText(acao);

        if ("ALTERAR".equals(acao) && despesa != null) {
            preencherCamposDespesa();
            btnExcluir.setVisibility(View.VISIBLE);
        } else {
            btnExcluir.setVisibility(View.GONE);
            edtValor.setText("R$0,00");
        }

        switchDespesaPaga.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                imgDespesaStatus.setImageResource(R.drawable.pago);
            } else {
                imgDespesaStatus.setImageResource(R.drawable.naopago);
            }
        });

        btnGravar.setOnClickListener(v -> {
            Despesa despesaAtualizada = criarDespesa(); // Verifique aqui se despesaAtualizada não é null
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (despesaAtualizada == null) {
                Toast.makeText(atv_cadastro.this, "Erro ao criar despesa. Tente novamente.", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("INSERIR".equals(acao)) {
                String categoriaSelecionada = autoCompleteCategoria.getText().toString();

                // Verifique se a categoria está vazia
                if (categoriaSelecionada.isEmpty()) {
                    Toast.makeText(atv_cadastro.this, "Por favor, selecione uma categoria válida.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Encontre a categoria correspondente no banco de dados
                String categoria = categoriaSelecionada;

                if (categoria == null) {
                    Toast.makeText(atv_cadastro.this, "Categoria não encontrada", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tente adicionar a despesa
                try {
                    despesaCRUD.adicionarDespesa(userId, categoria, despesaAtualizada);
                    Toast.makeText(atv_cadastro.this, "Despesa adicionada com sucesso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("nova_despesa", despesaAtualizada);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(atv_cadastro.this, "Erro ao adicionar despesa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else if ("ALTERAR".equals(acao)) {
                try {
                    despesaCRUD.alterarDespesa(userId, despesa.getCategoria(), despesaAtualizada);
                    Toast.makeText(atv_cadastro.this, "Despesa atualizada com sucesso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("despesa_atualizada", despesaAtualizada);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(atv_cadastro.this, "Erro ao atualizar despesa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnExcluir.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (despesa != null) {
                Log.d("atv_cadastro", "Tentando remover despesa com ID: " + despesa.getId());
                despesaCRUD.removerDespesa(userId, despesa.getCategoria(), despesa.getId());
                Toast.makeText(atv_cadastro.this, "Despesa removida com sucesso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("despesa_removida", despesa);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Log.e("atv_cadastro", "Despesa é null, não é possível remover.");
            }
        });

        edtVencimento.setOnClickListener(v -> abrirCalendario());

        FloatingActionButton btnAddCategoria = findViewById(R.id.btnAddCategoria);
        btnAddCategoria.setOnClickListener(v -> abrirModalCategoria());
        getCategorias();

        edtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {}

            @Override
            public void afterTextChanged(Editable editable) {
                formatarValor();
            }
        });
    }


    private Despesa criarDespesa() {
        String categoriaSelecionada = autoCompleteCategoria.getText().toString();

        if (categoriaSelecionada.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione uma categoria válida.", Toast.LENGTH_SHORT).show();
            return null;
        }

        String descricao = edtDescricao.getText().toString();
        String valorStr = edtValor.getText().toString().replace("R$", "").replaceAll("[^\\d,]", "").replace(",", ".");
        double valor = valorStr.isEmpty() ? 0.0 : Double.parseDouble(valorStr);
        String dataVencimentoStr = edtVencimento.getText().toString();

        boolean isPaga = switchDespesaPaga.isChecked();

        if (descricao.isEmpty() || valor <= 0 || dataVencimentoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show();
            return null;
        }

        String id = (despesa != null && despesa.getId() != null) ? despesa.getId() : UUID.randomUUID().toString();

        return new Despesa(id, categoriaSelecionada, descricao, valor, dataVencimentoStr, isPaga);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("nova_despesa")) {
                Despesa novaDespesa = (Despesa) data.getSerializableExtra("nova_despesa");
            }
        }
    }

    private void preencherCamposDespesa() {
        // Preenche com o nome da categoria
        autoCompleteCategoria.setText(despesa.getCategoria());
        edtDescricao.setText(despesa.getDescricao());
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        edtValor.setText(currencyFormat.format(despesa.getValor()));
        edtVencimento.setText(despesa.getVencimento());
        switchDespesaPaga.setChecked(despesa.isPago());
        if (despesa.isPago()) {
            imgDespesaStatus.setImageResource(R.drawable.pago);
        } else {
            imgDespesaStatus.setImageResource(R.drawable.naopago);
        }
    }


    private void onNumberClick(View v) {
        TextView button = (TextView) v;
        String buttonText = button.getText().toString();
        String currentValue = edtValor.getText().toString().replace("R$", "").replace(",", "");

        if (currentValue.equals("0")) {
            currentValue = buttonText;
        } else {
            currentValue = currentValue.replaceAll("^0+(?!$)", "");
            currentValue += buttonText;
        }

        StringBuilder formattedValue = new StringBuilder(currentValue);
        if (formattedValue.length() >= 3) {
            formattedValue.insert(formattedValue.length() - 2, ",");
        } else {
            formattedValue.insert(0, "0,");
        }

        edtValor.setText("R$" + formattedValue.toString());
    }

    @Override
    public void onClick(View v) {
        onNumberClick(v);
    }

    private void abrirCalendario() {
        final Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        com.example.telapi.calendario datePickerDialog = new calendario(this, (view, year, monthOfYear, dayOfMonth) -> {
            String dataSelecionada = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
            edtVencimento.setText(dataSelecionada);
        }, ano, mes, dia);
        datePickerDialog.show();
    }

    private void getCategorias() {
        CollectionReference categoriasRef = db.collection("categorias");
        categoriasRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categorias.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Categoria categoria = document.toObject(Categoria.class);
                    categorias.add(categoria);
                    Log.d("atv_cadastro", "Categoria carregada: " + categoria.getCategoria());
                }

                ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(atv_cadastro.this, android.R.layout.simple_dropdown_item_1line, categorias);
                autoCompleteCategoria.setAdapter(adapter);
            } else {
                Toast.makeText(atv_cadastro.this, "Falha ao carregar categorias", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private boolean isFormatting = false; // Flag para evitar loop infinito



    private void formatarValor() {
        if (isFormatting) return;  // Se já estamos formatando, não faça nada

        isFormatting = true; // Inicia a formatação
        String valorStr = edtValor.getText().toString().replace("R$", "").replaceAll("[^\\d,]", "").replace(",", ".");

        if (valorStr.isEmpty()) {
            edtValor.setText("R$0,00");  // Garantir que o campo tenha um valor inicial
            edtValor.setSelection(4);  // Manter o cursor na posição correta
            isFormatting = false;
            return;
        }

        double valor = valorStr.isEmpty() ? 0.0 : Double.parseDouble(valorStr);

        // Formatar o valor como moeda
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = currencyFormat.format(valor);

        // Remover temporariamente o TextWatcher
      //  edtValor.removeTextChangedListener(valorTextWatcher);

        // Atualizar o campo de texto com o valor formatado
        edtValor.setText(valorFormatado);
        edtValor.setSelection(valorFormatado.length());  // Manter o cursor no final

        // Adicionar o TextWatcher novamente
       // edtValor.addTextChangedListener(valorTextWatcher);

        isFormatting = false;  // Finaliza a formatação
    }




    private String formatarData(java.util.Date data) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        return sdf.format(data);
    }

    private java.util.Date parseData(String dataString) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        try {
            return sdf.parse(dataString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void abrirModalCategoria() {
        modal_categoria modal = new modal_categoria();

        modal.setCategoriaDialogListener(new modal_categoria.CategoriaDialogListener() {
            @Override
            public void onCategoriaAdicionada(String categoria) {
                autoCompleteCategoria.setText(categoria);
            }

            @Override
            public void onCategoriaRemovida(String categoriaId) {
                CategoriaCRUD categoriaCRUD = new CategoriaCRUD();
                categoriaCRUD.removerCategoria(categoriaId);
            }
        });

        modal.show(getSupportFragmentManager(), "modal_categoria");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
