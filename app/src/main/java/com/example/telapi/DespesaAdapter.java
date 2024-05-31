package com.example.telapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DespesaAdapter extends ArrayAdapter<Despesa> {

    private Context context;
    private List<Despesa> despesas;

    public DespesaAdapter(Context context, List<Despesa> despesas) {
        super(context, 0, despesas);
        this.context = context;
        this.despesas = despesas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        }

        Despesa despesa = despesas.get(position);

        TextView textViewDescricao = listItem.findViewById(R.id.textViewDescricao);
        textViewDescricao.setText(despesa.getDescricao());

        TextView textViewValor = listItem.findViewById(R.id.textViewValor);
        textViewValor.setText(String.valueOf(despesa.getValor()));

        return listItem;
    }

    public void atualizarDespesas(List<Despesa> despesas) {
        this.despesas.clear();
        this.despesas.addAll(despesas);
        notifyDataSetChanged();
    }
}
