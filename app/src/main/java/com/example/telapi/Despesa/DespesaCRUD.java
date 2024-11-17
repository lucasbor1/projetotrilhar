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

    public DespesaCRUD(Context context, String userId) {
        dbHelper = new DBHelper(context, userId);
        database = dbHelper.openDatabase();
        this.context = context;
    }

    public void adicionarDespesa(Despesa despesa) {
        ContentValues values = new ContentValues();
        values.put("categoria", despesa.getCategoria());
        values.put("descricao", despesa.getDescricao());
        values.put("valor", despesa.getValor());
        values.put("vencimento", despesa.getVencimento());
        values.put("pago", despesa.isPago() ? 1 : 0);

        long resultado = database.insert("despesas", null, values);
        if (resultado == -1) {
            Toast.makeText(context, "Erro ao adicionar despesa", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Despesa adicionada com sucesso", Toast.LENGTH_SHORT).show();
        }
    }

    public void alterarDespesa(Despesa despesa) {
        ContentValues values = new ContentValues();
        values.put("categoria", despesa.getCategoria());
        values.put("descricao", despesa.getDescricao());
        values.put("valor", despesa.getValor());
        values.put("vencimento", despesa.getVencimento());
        values.put("pago", despesa.isPago() ? 1 : 0);

        int resultado = database.update("despesas", values, "id = ?", new String[]{String.valueOf(despesa.getId())});
        if (resultado > 0) {
            Toast.makeText(context, "Despesa atualizada com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erro ao atualizar despesa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void removerDespesa(int id) {
        database.delete("despesas", "id = ?", new String[]{String.valueOf(id)});
    }

    @Override
    public List<Despesa> listarDespesas() {
        List<Despesa> despesas = new ArrayList<>();

        if (database == null || !database.isOpen()) {
            database = dbHelper.openDatabase();
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
            } while (cursor.moveToNext());

            cursor.close();
        }

        return despesas;
    }

}
