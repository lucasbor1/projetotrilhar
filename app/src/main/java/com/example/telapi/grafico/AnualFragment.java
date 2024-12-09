package com.example.telapi.grafico;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.telapi.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AnualFragment extends Fragment {

    private Spinner spinnerAno;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anual, container, false);

        spinnerAno = view.findViewById(R.id.spinnerAno);

        configurarSpinnerAno();

        return view;
    }

    private void configurarSpinnerAno() {
        // Gerar anos dinâmicos
        List<String> anos = gerarListaAnos();

        // Configurar Adapter com estilo personalizado
        ArrayAdapter<String> adapterAnos = new ArrayAdapter<>(getContext(), R.layout.item_spinner, anos);
        adapterAnos.setDropDownViewResource(R.layout.item_spinner);
        spinnerAno.setAdapter(adapterAnos);

        // Listener para alterações no Spinner
        spinnerAno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String anoSelecionado = parent.getItemAtPosition(position).toString();
                atualizarGrafico(anoSelecionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Selecionar o ano atual como padrão
        Calendar calendar = Calendar.getInstance();
        int anoAtual = calendar.get(Calendar.YEAR);
        spinnerAno.setSelection(anos.indexOf(String.valueOf(anoAtual)));
    }

    private List<String> gerarListaAnos() {
        List<String> anos = new ArrayList<>();
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = anoAtual - 10; i <= anoAtual + 10; i++) {
            anos.add(String.valueOf(i));
        }
        return anos;
    }

    private void atualizarGrafico(String ano) {
        // Atualize o gráfico com base no ano selecionado
        Toast.makeText(getContext(), "Ano selecionado: " + ano, Toast.LENGTH_SHORT).show();
    }
}
