// src/main/java/com/example/telapi/Despesa/DespesaAdapter.java
package com.example.telapi.Despesa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.telapi.R;
import java.util.List;

public class DespesaAdapter extends ArrayAdapter<Despesa> {

    public DespesaAdapter(@NonNull Context context, @NonNull List<Despesa> despesas) {
        super(context, 0, despesas);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_despesa, parent, false);
        }

        Despesa despesa = getItem(position);

        TextView txtDescricao = convertView.findViewById(R.id.txtDescricao);
        TextView txtValor = convertView.findViewById(R.id.txtValor);
        TextView txtVencimento = convertView.findViewById(R.id.txtVencimento);

        if (despesa != null) {
            txtDescricao.setText(despesa.getDescricao());
            txtValor.setText(String.valueOf(despesa.getValor()));
            txtVencimento.setText(despesa.getVencimento());
        }

        return convertView;
    }
}
