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
import androidx.core.content.ContextCompat;

import com.example.telapi.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DespesaAdapter extends ArrayAdapter<Despesa> {
    private List<Despesa> despesas;

    public DespesaAdapter(@NonNull Context context, @NonNull List<Despesa> despesas) {
        super(context, 0, despesas);
        this.despesas = new ArrayList<>(despesas);
    }

    public void setDespesas(List<Despesa> novasDespesas) {
        if (novasDespesas != null) {
            this.despesas.clear();
            this.despesas.addAll(novasDespesas);
            notifyDataSetChanged();
        }
    }

    public Despesa getDespesa(int position) {
        return despesas.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_despesa, parent, false);
        }

        Despesa despesa = getDespesa(position);

        TextView txtDescricao = convertView.findViewById(R.id.txtDescricao);
        TextView txtValor = convertView.findViewById(R.id.txtValor);
        TextView txtVencimento = convertView.findViewById(R.id.txtVencimento);
        CardView cardView = (CardView) convertView;

        if (despesa != null) {
            txtDescricao.setText(despesa.getDescricao());

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            txtValor.setText(currencyFormat.format(despesa.getValor()));
            txtVencimento.setText(despesa.getVencimento());

            if (despesa.isPago()) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.azul_escuro));
            } else if (despesa.isAtrasada()) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.vermelho_claro));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.cinza_claro));
            }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return despesas.size();
    }

    @Nullable
    @Override
    public Despesa getItem(int position) {
        return despesas.get(position);
    }
}
