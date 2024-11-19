package com.example.telapi.Despesa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.telapi.DBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DespesaCRUD implements DespesaRepository {
    private static final String TAG = "DespesaCRUD";
    private final SQLiteDatabase database;
    private final Context context;

    public DespesaCRUD(Context context, String userId) {
        DBHelper dbHelper = new DBHelper(context, userId);
        this.database = dbHelper.openDatabase();
        this.context = context;
    }

    // Método para adicionar uma despesa
    public void adicionarDespesa(Despesa despesa) {
        ContentValues values = new ContentValues();
        values.put("categoria", despesa.getCategoria());
        values.put("descricao", despesa.getDescricao());
        values.put("valor", despesa.getValor());
        values.put("vencimento", despesa.getVencimento());
        values.put("ano", obterAnoDoVencimento(despesa.getVencimento())); // Derivando o ano
        values.put("pago", despesa.isPago() ? 1 : 0);

        long resultado = database.insert("despesas", null, values);
        if (resultado == -1) {
            Log.e(TAG, "Erro ao adicionar despesa");
            Toast.makeText(context, "Erro ao adicionar despesa", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Despesa adicionada com sucesso");
            Toast.makeText(context, "Despesa adicionada com sucesso", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para alterar uma despesa
    public void alterarDespesa(Despesa despesa) {
        ContentValues values = new ContentValues();
        values.put("categoria", despesa.getCategoria());
        values.put("descricao", despesa.getDescricao());
        values.put("valor", despesa.getValor());
        values.put("vencimento", despesa.getVencimento());
        values.put("ano", obterAnoDoVencimento(despesa.getVencimento())); // Derivando o ano
        values.put("pago", despesa.isPago() ? 1 : 0);

        int resultado = database.update("despesas", values, "id = ?", new String[]{String.valueOf(despesa.getId())});
        if (resultado > 0) {
            Log.d(TAG, "Despesa atualizada com sucesso");
            Toast.makeText(context, "Despesa atualizada com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Erro ao atualizar despesa");
            Toast.makeText(context, "Erro ao atualizar despesa", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para remover uma despesa
    public void removerDespesa(int id) {
        int rowsDeleted = database.delete("despesas", "id = ?", new String[]{String.valueOf(id)});
        if (rowsDeleted > 0) {
            Log.d(TAG, "Despesa removida com sucesso.");
            Toast.makeText(context, "Despesa removida com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Erro ao remover despesa.");
            Toast.makeText(context, "Erro ao remover despesa", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para listar todas as despesas
    public List<Despesa> listarDespesas() {
        List<Despesa> despesas = new ArrayList<>();
        Cursor cursor = database.query("despesas", null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int categoriaIndex = cursor.getColumnIndex("categoria");
            int descricaoIndex = cursor.getColumnIndex("descricao");
            int valorIndex = cursor.getColumnIndex("valor");
            int vencimentoIndex = cursor.getColumnIndex("vencimento");
            int anoIndex = cursor.getColumnIndex("ano");
            int pagoIndex = cursor.getColumnIndex("pago");

            do {
                int id = cursor.getInt(idIndex);
                String categoria = cursor.getString(categoriaIndex);
                String descricao = cursor.getString(descricaoIndex);
                double valor = cursor.getDouble(valorIndex);
                String vencimento = cursor.getString(vencimentoIndex);
                int ano = cursor.getInt(anoIndex);
                boolean pago = cursor.getInt(pagoIndex) == 1;

                despesas.add(new Despesa(id, categoria, descricao, valor, vencimento, ano, pago));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return despesas;
    }

    // Método para obter o total mensal (filtro por mês e ano)
    public double obterTotalMensal(String mes, int ano) {
        double totalMensal = 0;
        Cursor cursor = database.rawQuery(
                "SELECT SUM(valor) FROM despesas WHERE substr(vencimento, 4, 2) = ? AND ano = ?",
                new String[]{mes, String.valueOf(ano)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            totalMensal = cursor.getDouble(0);
            cursor.close();
        }

        return totalMensal;
    }

    // Método para obter despesas em aberto (não pagas)
    public double obterTotalEmAberto(String mes, int ano) {
        double totalAberto = 0;
        Cursor cursor = database.rawQuery(
                "SELECT SUM(valor) FROM despesas WHERE substr(vencimento, 4, 2) = ? AND ano = ? AND pago = 0",
                new String[]{mes, String.valueOf(ano)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            totalAberto = cursor.getDouble(0);
            cursor.close();
        }

        return totalAberto;
    }

    // Método auxiliar para obter o ano do campo vencimento
    private int obterAnoDoVencimento(String vencimento) {
        try {
            String[] partesData = vencimento.split("/");
            if (partesData.length == 3) {
                return Integer.parseInt(partesData[2]);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter o ano do vencimento: " + e.getMessage());
        }
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}
