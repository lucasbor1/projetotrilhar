package com.example.telapi.Despesa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.telapi.DBHelper;
import java.util.ArrayList;
import java.util.List;

public class DespesaCRUD implements DespesaRepository {
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;
    private DespesaUpdateListener updateListener;

    private static final String TAG = "DespesaCRUD";  // Adicionado para logs

    public DespesaCRUD(Context context, String userId, DespesaUpdateListener listener) {
        dbHelper = new DBHelper(context, userId);
        database = dbHelper.openDatabase();
        this.context = context;
        this.updateListener = listener;
        Log.d(TAG, "DespesaCRUD inicializado para o usuário: " + userId);
    }

    public void adicionarDespesa(Despesa despesa) {
        Log.d(TAG, "Adicionando despesa: " + despesa.toString());

        ContentValues values = new ContentValues();
        values.put("categoria", despesa.getCategoria());
        values.put("descricao", despesa.getDescricao());
        values.put("valor", despesa.getValor());
        values.put("vencimento", despesa.getVencimento());
        values.put("pago", despesa.isPago() ? 1 : 0);

        long resultado = database.insert("despesas", null, values);
        if (resultado == -1) {
            Log.e(TAG, "Erro ao adicionar despesa");
            atualizarDespesas();
            Toast.makeText(context, "Erro ao adicionar despesa", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Despesa adicionada com sucesso");
            Toast.makeText(context, "Despesa adicionada com sucesso", Toast.LENGTH_SHORT).show();
        }
    }

    public void alterarDespesa(Despesa despesa) {
        Log.d(TAG, "Alterando despesa: " + despesa.toString());

        ContentValues values = new ContentValues();
        values.put("categoria", despesa.getCategoria());
        values.put("descricao", despesa.getDescricao());
        values.put("valor", despesa.getValor());
        values.put("vencimento", despesa.getVencimento());
        values.put("pago", despesa.isPago() ? 1 : 0);

        int resultado = database.update("despesas", values, "id = ?", new String[]{String.valueOf(despesa.getId())});
        if (resultado > 0) {
            Log.d(TAG, "Despesa atualizada com sucesso");
            atualizarDespesas();
            Toast.makeText(context, "Despesa atualizada com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Erro ao atualizar despesa");
            Toast.makeText(context, "Erro ao atualizar despesa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void removerDespesa(int id) {
        Log.d(TAG, "Removendo despesa com ID: " + id);

        int rowsDeleted = database.delete("despesas", "id = ?", new String[]{String.valueOf(id)});
        if (rowsDeleted > 0) {
            Log.d(TAG, "Despesa removida com sucesso.");
            atualizarDespesas();
        } else {
            Log.e(TAG, "Erro ao remover despesa.");
        }
    }

    @Override
    public List<Despesa> listarDespesas() {
        Log.d(TAG, "Listando todas as despesas.");

        List<Despesa> despesas = new ArrayList<>();

        if (database == null || !database.isOpen()) {
            database = dbHelper.openDatabase();
            Log.d(TAG, "Banco de dados reaberto.");
        }

        Cursor cursor = database.query("despesas", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int categoriaIndex = cursor.getColumnIndex("categoria");
            int descricaoIndex = cursor.getColumnIndex("descricao");
            int valorIndex = cursor.getColumnIndex("valor");
            int vencimentoIndex = cursor.getColumnIndex("vencimento");
            int pagoIndex = cursor.getColumnIndex("pago");

            do {
                int id = idIndex != -1 ? cursor.getInt(idIndex) : 0;
                String categoria = categoriaIndex != -1 ? cursor.getString(categoriaIndex) : "";
                String descricao = descricaoIndex != -1 ? cursor.getString(descricaoIndex) : "";
                double valor = valorIndex != -1 ? cursor.getDouble(valorIndex) : 0.0;
                String vencimento = vencimentoIndex != -1 ? cursor.getString(vencimentoIndex) : "";
                boolean pago = pagoIndex != -1 && cursor.getInt(pagoIndex) == 1;

                Despesa despesa = new Despesa(id, categoria, descricao, valor, vencimento, pago);
                despesas.add(despesa);
                Log.d(TAG, "Despesa carregada: " + despesa.toString());
            } while (cursor.moveToNext());

            cursor.close();
        }

        Log.d(TAG, "Total de despesas listadas: " + despesas.size());
        return despesas;
    }

    private void atualizarDespesas() {
        Log.d(TAG, "Atualizando lista de despesas...");
        List<Despesa> despesas = listarDespesas();
        if (updateListener != null) {
            updateListener.onDespesaAtualizada(despesas);
        }
    }

    public double obterTotalMensal(String mes) {
        Log.d(TAG, "Obtendo total mensal para o mês: " + mes);
        double totalMensal = 0;
        Cursor cursor = database.rawQuery(
                "SELECT SUM(valor) FROM despesas WHERE substr(vencimento, 4, 2) = ?",
                new String[]{String.format("%02d", Integer.parseInt(mes))}
        );
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            totalMensal = cursor.getDouble(0);
        }
        cursor.close();
        Log.d(TAG, "Total mensal para o mês " + mes + ": " + totalMensal);
        return totalMensal;
    }

    public double obterTotalEmAberto(String mes) {
        Log.d(TAG, "Obtendo total em aberto para o mês: " + mes);
        double totalAberto = 0;
        Cursor cursor = database.rawQuery(
                "SELECT SUM(valor) FROM despesas WHERE substr(vencimento, 4, 2) = ? AND pago = 0",
                new String[]{String.format("%02d", Integer.parseInt(mes))}
        );
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            totalAberto = cursor.getDouble(0);
        }
        cursor.close();
        Log.d(TAG, "Total em aberto para o mês " + mes + ": " + totalAberto);
        return totalAberto;
    }
}
