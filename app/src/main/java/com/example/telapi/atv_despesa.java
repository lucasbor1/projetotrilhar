package com.example.telapi;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class atv_despesa extends AppCompatActivity {

    private ImageButton btnAdicionar;
    private Spinner spnMeses;
    private ListView lstDespesas;
    private EditText edtTotal;
    private EditText edtAberto;
    private static final int REQUEST_CODE = 1;
    private Despesa despesaAtual;

    private Map<String, List<String>> despesasPorMes;

    // Carrega as despesas do banco de dados Firebase
    // Carrega as despesas do banco de dados Firebase
    // Carrega as despesas do banco de dados Firebase
    private void carregarDespesas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("despesas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Despesa despesa = document.toObject(Despesa.class);
                    adicionarDespesaNova(despesa); // Adiciona a despesa à lista geral
                }
                // Após carregar todas as despesas, exibe as despesas do mês atual selecionado no Spinner
                String mesSelecionado = spnMeses.getSelectedItem().toString();
                exibirDespesasPorMes(mesSelecionado);
            } else {
                Log.w("atv_despesa", "Erro ao carregar despesas.", task.getException());
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_despesa);

        // Inicialização dos componentes da UI
        btnAdicionar = findViewById(R.id.btnAdicionar);
        spnMeses = findViewById(R.id.spnMeses);
        lstDespesas = findViewById(R.id.lstDespesas);
        edtTotal = findViewById(R.id.edtTotal);
        edtAberto = findViewById(R.id.edtAberto);
        despesasPorMes = new HashMap<>();

        // Carregar despesas do banco de dados Firebase
        carregarDespesas();

        // Configuração do Spinner para selecionar meses
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMeses.setAdapter(adapter);
        spnMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mesSelecionado = parent.getItemAtPosition(position).toString();
                exibirDespesasPorMes(mesSelecionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Configuração do botão para adicionar nova despesa
        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(atv_despesa.this, atv_cadastro.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }


    // Manipula o resultado da atividade de cadastro de despesa
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.hasExtra("nova_despesa")) {
                    Despesa novaDespesa = (Despesa) data.getSerializableExtra("nova_despesa");
                    if (novaDespesa.getVencimento() == null) {
                        Log.d("atv_despesa", "Nova despesa sem data de vencimento: " + novaDespesa.toString());
                    } else {
                        adicionarDespesaNova(novaDespesa);
                        // Atualiza a exibição para mostrar a nova despesa
                        String mesSelecionado = spnMeses.getSelectedItem().toString();
                        exibirDespesasPorMes(mesSelecionado);
                    }
                } else if (data.hasExtra("despesa_atualizada")) {
                    Despesa despesaAtualizada = (Despesa) data.getSerializableExtra("despesa_atualizada");
                    if (despesaAtualizada.getVencimento() == null) {
                        Log.d("atv_despesa", "Despesa atualizada sem data de vencimento: " + despesaAtualizada.toString());
                    } else {
                        atualizarDespesaExistente(despesaAtualizada);
                    }
                }
            }
        }
    }



    private void adicionarDespesaNova(Despesa novaDespesa) {
        String mes = obterMesDaDespesa(novaDespesa);
        List<String> despesasDoMes = despesasPorMes.getOrDefault(mes, new ArrayList<>());
        despesasDoMes.add(novaDespesa.toString());
        despesasPorMes.put(mes, despesasDoMes);
        exibirDespesasPorMes(mes);

        // Adiciona a despesa diretamente ao mês correto no mapa despesasPorMes
        List<String> despesasGeral = despesasPorMes.getOrDefault("Geral", new ArrayList<>());
        despesasGeral.add(novaDespesa.toString());
        despesasPorMes.put("Geral", despesasGeral);
    }



    private void removerDespesaExistente(Despesa despesaRemovida) {
        String mes = obterMesDaDespesa(despesaRemovida);
        if (despesasPorMes.containsKey(mes)) {
            List<String> despesasDoMes = despesasPorMes.get(mes);
            despesasDoMes.remove(despesaRemovida.toString());
            exibirDespesasPorMes(mes); // Atualiza a exibição da lista
        }
    }

    private void atualizarDespesaExistente(Despesa despesaAtualizada) {
        String mes = obterMesDaDespesa(despesaAtualizada);
        if (despesasPorMes.containsKey(mes)) {
            List<String> despesasDoMes = despesasPorMes.get(mes);
            for (int i = 0; i < despesasDoMes.size(); i++) {
                if (despesasDoMes.get(i).equals(despesaAtualizada.toString())) {
                    despesasDoMes.set(i, despesaAtualizada.toString());
                    break;
                }
            }
            exibirDespesasPorMes(spnMeses.getSelectedItem().toString());
        }
    }

    // Obtém o mês da despesa a partir do Timestamp
// Obtém o mês da despesa a partir do Timestamp
    private String obterMesDaDespesa(Despesa despesa) {
        String vencimento = despesa.getVencimento();
        if (vencimento == null || vencimento.isEmpty()) {
            Log.d("atv_despesa", "Vencimento é nulo para a despesa: " + despesa.toString());
            return "Mês Desconhecido";
        }
        String[] partesData = vencimento.split("/");
        if (partesData.length < 2) {
            Log.d("atv_despesa", "Formato de data inválido para a despesa: " + despesa.toString());
            return "Mês Desconhecido";
        }
        int mes = Integer.parseInt(partesData[1]);
        String[] nomesMeses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        return nomesMeses[mes - 1];
    }


    private void exibirDespesasPorMes(String mes) {
        List<String> despesas = despesasPorMes.get(mes);
        if (despesas != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, despesas);
            lstDespesas.setAdapter(adapter);
        } else {
            lstDespesas.setAdapter(null);
        }
    }


    // Converte a despesa atual em uma representação de string formatada
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String vencimento = despesaAtual != null ? despesaAtual.getVencimento() : null;
        String dataFormatada = vencimento != null ? sdf.format(vencimento) : "Sem Data";
        return despesaAtual != null ? despesaAtual.getCategoria() + ": " + despesaAtual.getDescricao() + " - R$" + String.format("%.2f", despesaAtual.getValor()) + " (Vencimento: " + dataFormatada + ")" : "";
    }
}

