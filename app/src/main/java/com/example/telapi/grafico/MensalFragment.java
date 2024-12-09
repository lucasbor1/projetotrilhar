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
import androidx.fragment.app.Fragment;

import com.example.telapi.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class MensalFragment extends Fragment {

    private Spinner spinnerMes, spinnerAno;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensal, container, false);

        spinnerMes = view.findViewById(R.id.spinnerMes);
        spinnerAno = view.findViewById(R.id.spinnerAno);

        configurarSpinners();

        return view;
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> adapterMeses = ArrayAdapter.createFromResource(
                getContext(),
                R.array.meses,
                R.layout.item_spinner
        );
        adapterMeses.setDropDownViewResource(R.layout.item_spinner);
        spinnerMes.setAdapter(adapterMeses);

        // Configurar Spinner de Anos
        List<String> anos = gerarListaAnos();
        ArrayAdapter<String> adapterAnos = new ArrayAdapter<>(getContext(), R.layout.item_spinner, anos);
        adapterAnos.setDropDownViewResource(R.layout.item_spinner);
        spinnerAno.setAdapter(adapterAnos);

        // Listener para o Spinner de Meses
        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                atualizarConteudoPorMesEAno();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener para o Spinner de Anos
        spinnerAno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                atualizarConteudoPorMesEAno();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Configurar mês e ano atuais como selecionados
        Calendar calendar = Calendar.getInstance();
        spinnerMes.setSelection(calendar.get(Calendar.MONTH));
        spinnerAno.setSelection(anos.indexOf(String.valueOf(calendar.get(Calendar.YEAR))));
    }

    private List<String> gerarListaAnos() {
        List<String> anos = new ArrayList<>();
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = anoAtual - 10; i <= anoAtual + 10; i++) {
            anos.add(String.valueOf(i));
        }
        return anos;
    }

    private void atualizarConteudoPorMesEAno() {
        String mesSelecionado = spinnerMes.getSelectedItem().toString();
        String anoSelecionado = spinnerAno.getSelectedItem().toString();

        // Atualize o conteúdo do Fragment (gráfico ou outros elementos) com base no mês e ano selecionados
        Toast.makeText(getContext(), "Mês: " + mesSelecionado + ", Ano: " + anoSelecionado, Toast.LENGTH_SHORT).show();
    }
}
