package com.example.telapi.Despesa;

import static android.text.method.TextKeyListener.clear;

import static java.util.Collections.addAll;

import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telapi.AdpSpinner;
import com.example.telapi.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class atv_despesa extends AppCompatActivity {

    private ImageButton btnAdicionar;
    private Spinner spnMeses;
    private ListView lstDespesas;
    private TextView edtTotal;
    private TextView edtAberto;
    public static final int REQUEST_CODE = 1;

    private DespesaAdapter despesasAdapter;


    private Map<String, List<Despesa>> despesasPorMes;

    private void carregarDespesas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("despesas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Despesa despesa = document.toObject(Despesa.class);
                    despesa.setId(document.getId());
                    Log.d("atv_despesa", "Despesa carregada: " + despesa.toString());
                    adicionarDespesaNova(despesa);
                }

                String mesSelecionado = spnMeses.getSelectedItem().toString();
                exibirDespesasPorMes(mesSelecionado);
                atualizarTotalMensal();
            } else {
                Log.w("atv_despesa", "Erro ao carregar despesas.", task.getException());
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_despesa);


        btnAdicionar = findViewById(R.id.btnAdicionar);
        spnMeses = findViewById(R.id.spnMeses);
        lstDespesas = findViewById(R.id.lstDespesas);
        edtTotal = findViewById(R.id.edtTotal);
        edtAberto = findViewById(R.id.edtAberto);
        despesasPorMes = new HashMap<>();


        despesasAdapter = new DespesaAdapter(this, new ArrayList<>());
        lstDespesas.setAdapter(despesasAdapter);

        carregarDespesas();


        AdpSpinner adapter = new AdpSpinner(this, R.layout.item_spinner, getResources().getTextArray(R.array.meses));
        spnMeses.setAdapter(adapter);

        spnMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mesSelecionado = parent.getItemAtPosition(position).toString();
                exibirDespesasPorMesOrdenadas(mesSelecionado);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(atv_despesa.this, atv_cadastro.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        lstDespesas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Despesa despesaSelecionada = despesasAdapter.getDespesa(position);
                if (despesaSelecionada != null) {
                    abrirTelaCadastroComDespesa(despesaSelecionada);
                }
            }
        });
        configurarSpinnerMesAtual();
    }

    private void configurarSpinnerMesAtual() {
        Calendar cal = Calendar.getInstance();
        int mesAtual = cal.get(Calendar.MONTH);
        spnMeses.setSelection(mesAtual);
    }

    private void abrirTelaCadastroComDespesa(Despesa despesa) {
        Intent intent = new Intent(atv_despesa.this, atv_cadastro.class);
        intent.putExtra("acao", "ALTERAR");
        intent.putExtra("obj", despesa);
        startActivity(intent);
    }


    private void exibirDespesasPorMesOrdenadas(String mes) {
        List<Despesa> despesas = despesasPorMes.get(mes);
        if (despesas != null) {
            ordenarDespesasPorDia(despesas);

            double totalMensal = 0;
            double despesasEmAberto = 0;
            for (Despesa despesa : despesas) {
                totalMensal += despesa.getValor();
                if (!despesa.isPago()) {
                    despesasEmAberto += despesa.getValor();
                }
            }

            despesasAdapter.clear();
            despesasAdapter.addAll(despesas);
            despesasAdapter.notifyDataSetChanged();

            edtTotal.setText(String.valueOf(totalMensal));
            edtAberto.setText(String.valueOf(despesasEmAberto));


        } else {
            edtTotal.setText("0");
            edtAberto.setText("0");
            despesasAdapter.clear();
            despesasAdapter.notifyDataSetChanged();

        }
    }

    private void ordenarDespesasPorDia(List<Despesa> despesas) {
        Collections.sort(despesas, (d1, d2) -> {
            String[] data1 = d1.getVencimento().split("/");
            String[] data2 = d2.getVencimento().split("/");
            int dia1 = Integer.parseInt(data1[0]);
            int dia2 = Integer.parseInt(data2[0]);
            return Integer.compare(dia1, dia2);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String mesSelecionado = spnMeses.getSelectedItem().toString();

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra("nova_despesa")) {
                Despesa novaDespesa = (Despesa) data.getSerializableExtra("nova_despesa");
                if (novaDespesa.getVencimento() == null) {
                    Log.d("atv_despesa", "Nova despesa sem data de vencimento: " + novaDespesa.toString());
                } else {
                    adicionarDespesaNova(novaDespesa);
                }
            } else if (data.hasExtra("despesa_atualizada")) {
                Despesa despesaAtualizada = (Despesa) data.getSerializableExtra("despesa_atualizada");
                if (despesaAtualizada.getVencimento() == null) {
                    Log.d("atv_despesa", "Despesa atualizada sem data de vencimento: " + despesaAtualizada.toString());
                } else {
                    atualizarDespesaExistente(despesaAtualizada);
                }
            } else if (data.hasExtra("despesa_removida")) {
                Despesa despesaRemovida = (Despesa) data.getSerializableExtra("despesa_removida");
                removerDespesaExistente(despesaRemovida);
            }

            exibirDespesasPorMes(mesSelecionado);
            atualizarTotalMensal();
            despesasAdapter.notifyDataSetChanged();
        }
    }

    private void atualizarTotalMensal() {
        String mesSelecionado = spnMeses.getSelectedItem().toString();
        List<Despesa> despesas = despesasPorMes.get(mesSelecionado);
        if (despesas != null) {
            double totalMensal = 0;
            double despesasEmAberto = 0;

            for (Despesa despesa : despesas) {
                totalMensal += despesa.getValor();
                if (!despesa.isPago()) {
                    despesasEmAberto += despesa.getValor();
                }
            }

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            edtTotal.setText(currencyFormat.format(totalMensal));
            edtAberto.setText(currencyFormat.format(despesasEmAberto));
        } else {
            edtTotal.setText("R$0,00");
            edtAberto.setText("R$0,00");
        }
    }

    private void adicionarDespesaNova(Despesa novaDespesa) {
        String mes = obterMesDaDespesa(novaDespesa);
        List<Despesa> despesasDoMes = despesasPorMes.getOrDefault(mes, new ArrayList<>());
        despesasDoMes.add(novaDespesa);
        despesasPorMes.put(mes, despesasDoMes);

        double totalDespesasEmAberto = 0;
        for (List<Despesa> despesas : despesasPorMes.values()) {
            for (Despesa despesa : despesas) {
                if (!despesa.isPago()) {
                    totalDespesasEmAberto += despesa.getValor();
                }
            }
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        edtAberto.setText(currencyFormat.format(totalDespesasEmAberto));
        despesasAdapter.add(novaDespesa);
        despesasAdapter.notifyDataSetChanged();
    }

    private void removerDespesaExistente(Despesa despesaRemovida) {
        String mes = obterMesDaDespesa(despesaRemovida);
        if (despesasPorMes.containsKey(mes)) {
            List<Despesa> despesasDoMes = despesasPorMes.get(mes);
            if (despesasDoMes.remove(despesaRemovida)) {


                despesasAdapter.remove(despesaRemovida);


                despesasAdapter.notifyDataSetChanged();


                exibirDespesasPorMes(mes);
                atualizarTotalMensal();
            }
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
            despesasPorMes.put(mes, despesasDoMes);
            exibirDespesasPorMes(mes);
            despesasAdapter.notifyDataSetChanged();
        }
    }

    private void ordenarDespesasPorMes() {
        for (Map.Entry<String, List<Despesa>> entry : despesasPorMes.entrySet()) {
            List<Despesa> despesasDoMes = entry.getValue();
            Collections.sort(despesasDoMes, (d1, d2) -> {
                String[] data1 = d1.getVencimento().split("/");
                String[] data2 = d2.getVencimento().split("/");
                int dia1 = Integer.parseInt(data1[0]);
                int dia2 = Integer.parseInt(data2[0]);
                return Integer.compare(dia1, dia2);
            });
        }
    }


    private String obterMesDaDespesa(Despesa despesa) {
        String vencimento = despesa.getVencimento();
        if (vencimento == null || vencimento.isEmpty()) {

            return "Mês Desconhecido";
        }
        String[] partesData = vencimento.split("/");
        if (partesData.length < 2) {

            return "Mês Desconhecido";
        }
        int mes = Integer.parseInt(partesData[1]);
        String[] nomesMeses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        return nomesMeses[mes - 1];
    }

    private void exibirDespesasPorMes(String mes) {
        ordenarDespesasPorMes();
        List<Despesa> despesas = despesasPorMes.get(mes);
        if (despesas != null) {
            despesasAdapter.clear();
            despesasAdapter.addAll(despesas);
            despesasAdapter.notifyDataSetChanged();

        } else {
            despesasAdapter.clear();
            despesasAdapter.notifyDataSetChanged();

        }
    }
}





