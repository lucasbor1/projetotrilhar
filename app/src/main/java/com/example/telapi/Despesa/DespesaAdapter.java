package com.example.telapi.Despesa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.telapi.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DespesaAdapter extends ArrayAdapter<Despesa> {

    public DespesaAdapter(@NonNull Context context, @NonNull List<Despesa> despesas) {
        super(context, 0, despesas);
    }

    public Despesa getDespesa(int position) {
        return getItem(position);
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
        CardView cardView = (CardView) convertView;

        if (despesa != null) {
            txtDescricao.setText(despesa.getDescricao());

            // Formata o valor para moeda
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            txtValor.setText(currencyFormat.format(despesa.getValor()));

            txtVencimento.setText(despesa.getVencimento());

            if (despesa.isPago()) {
                cardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.verde_claro));
            } else if (despesa.isAtrasada()) {
                cardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.vermelho_claro));
            } else {
                cardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.cinza_claro));
            }
        }

        return convertView;
    }
}
