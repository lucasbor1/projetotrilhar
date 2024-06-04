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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
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
    public static final int REQUEST_CODE = 1;
    private Despesa despesaAtual;
    private ArrayAdapter<String> despesasAdapter;


    private Map<String, List<Despesa>> despesasPorMes;

    // Carrega as despesas do banco de dados Firebase
    private void carregarDespesas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("despesas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Despesa despesa = document.toObject(Despesa.class);
                    Log.d("atv_despesa", "Despesa carregada: " + despesa.toString());
                    adicionarDespesaNova(despesa); // Adiciona a despesa ao mês correspondente
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

        despesasAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);
        lstDespesas.setAdapter(despesasAdapter);

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
                        // Atualize a exibição para mostrar a nova despesa
                        String mesSelecionado = spnMeses.getSelectedItem().toString();
                        exibirDespesasPorMes(mesSelecionado);
                        // Notifique o adapter sobre as mudanças nos dados
                        despesasAdapter.notifyDataSetChanged();
                    }
                } else if (data.hasExtra("despesa_atualizada")) {
                    Despesa despesaAtualizada = (Despesa) data.getSerializableExtra("despesa_atualizada");
                    if (despesaAtualizada.getVencimento() == null) {
                        Log.d("atv_despesa", "Despesa atualizada sem data de vencimento: " + despesaAtualizada.toString());
                    } else {
                        atualizarDespesaExistente(despesaAtualizada);
                        // Atualize a exibição para refletir a despesa atualizada
                        String mesSelecionado = spnMeses.getSelectedItem().toString();
                        exibirDespesasPorMes(mesSelecionado);
                        // Notifique o adapter sobre as mudanças nos dados
                        despesasAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }




    private void adicionarDespesaNova(Despesa novaDespesa) {
        String mes = obterMesDaDespesa(novaDespesa);
        List<Despesa> despesasDoMes = despesasPorMes.getOrDefault(mes, new ArrayList<>());
        despesasDoMes.add(novaDespesa);
        despesasPorMes.put(mes, despesasDoMes); // Atualize a entrada correspondente no mapa
        Log.d("atv_despesa", "Despesa adicionada ao mês " + mes + ": " + novaDespesa.toString());

        // Adicione a nova despesa ao adapter
        despesasAdapter.add(novaDespesa.toString());
        despesasAdapter.notifyDataSetChanged();

    }


    private void removerDespesaExistente(Despesa despesaRemovida) {
        String mes = obterMesDaDespesa(despesaRemovida);
        if (despesasPorMes.containsKey(mes)) {
            List<Despesa> despesasDoMes = despesasPorMes.get(mes);
            despesasDoMes.remove(despesaRemovida);
            exibirDespesasPorMes(mes); // Atualiza a exibição da lista
        }
    }

    private void atualizarDespesaExistente(Despesa despesaAtualizada) {
        String mes = obterMesDaDespesa(despesaAtualizada);
        if (despesasPorMes.containsKey(mes)) {
            List<Despesa> despesasDoMes = despesasPorMes.get(mes);
            for (int i = 0; i < despesasDoMes.size(); i++) {
                if (despesasDoMes.get(i).getId().equals(despesaAtualizada.getId())) {
                    despesasDoMes.set(i, despesaAtualizada);
                    break;
                }
            }
            exibirDespesasPorMes(spnMeses.getSelectedItem().toString());
        }
    }

    private String obterMesDaDespesa(Despesa despesa) {
        String vencimento = despesa.getVencimento();
        if (vencimento == null || vencimento.isEmpty()) {
            Log.d("atv_despesa", "Vencimento é nulo para a despesa: "        + despesa.toString());
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
        List<Despesa> despesas = despesasPorMes.get(mes);
        if (despesas != null) {
            List<String> despesasString = new ArrayList<>();
            for (Despesa despesa : despesas) {
                despesasString.add(despesa.toString());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, despesasString);
            lstDespesas.setAdapter(adapter);

            // Notifica o adapter sobre as mudanças nos dados
            adapter.notifyDataSetChanged();
            Log.d("atv_despesa", "Lista de despesas atualizada para o mês " + mes);
        } else {
            lstDespesas.setAdapter(null);
            Log.d("atv_despesa", "Nenhuma despesa encontrada para o mês " + mes);
        }
    }


}

