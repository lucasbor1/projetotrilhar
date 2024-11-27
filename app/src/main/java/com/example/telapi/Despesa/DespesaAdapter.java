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

    private final List<Despesa> despesas;

    public DespesaAdapter(@NonNull Context context, @NonNull List<Despesa> despesas) {
        super(context, 0, despesas);
        this.despesas = new ArrayList<>(despesas);
    }

    public void setDespesas(List<Despesa> novasDespesas) {
        despesas.clear();
        if (novasDespesas != null) {
            despesas.addAll(novasDespesas);
        }
        notifyDataSetChanged();
    }

    public void atualizarDespesa(Despesa despesaAtualizada) {
        for (int i = 0; i < despesas.size(); i++) {
            if (despesas.get(i).getId() == despesaAtualizada.getId()) {
                despesas.set(i, despesaAtualizada);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public void removerDespesa(Despesa despesaRemovida) {
        despesas.removeIf(d -> d.getId() == despesaRemovida.getId());
        notifyDataSetChanged();
    }

    public Despesa getDespesa(int position) {
        return despesas.get(position);
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

        txtDescricao.setText(despesa.getDescricao());
        txtValor.setText(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(despesa.getValor()));
        txtVencimento.setText(despesa.getVencimento());

        int color = despesa.isPago()
                ? R.color.verde_destaque
                : (despesa.isAtrasada()
                ? R.color.vermelho_claro
                : R.color.cinza_claro);

        cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), color));


        return convertView;
    }
}
