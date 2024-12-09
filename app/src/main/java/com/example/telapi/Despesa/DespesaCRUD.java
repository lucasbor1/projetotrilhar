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
        if (despesa.isPermanente()) {
            salvarDespesaPermanente(despesa);
        } else if (despesa.isParcelada()) {
            salvarDespesaParcelada(despesa);
        } else {
            salvarDespesaUnica(despesa);
        }
    }

    private void salvarDespesaUnica(Despesa despesa) {
        ContentValues values = criarContentValues(despesa);
        long resultado = database.insert("despesas", null, values);
        if (resultado == -1) {
            Log.e(TAG, "Erro ao adicionar despesa única");
            Toast.makeText(context, "Erro ao adicionar despesa", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Despesa única adicionada com sucesso");
            Toast.makeText(context, "Despesa adicionada com sucesso", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarDespesaPermanente(Despesa despesa) {
        Calendar calendario = Calendar.getInstance();
        int mesAtual = calendario.get(Calendar.MONTH) + 1;  // Meses começam em 0, por isso somamos 1
        int anoAtual = calendario.get(Calendar.YEAR);
        int diaVencimento = Integer.parseInt(despesa.getVencimento().split("/")[0]);  // Obtemos o dia da data de vencimento fornecida

        for (int i = 0; i < 12; i++) { // Adicionar para os próximos 12 meses
            ContentValues values = criarContentValues(despesa);

            // Configura o vencimento para a despesa
            values.put("vencimento", String.format("%02d/%02d/%d", diaVencimento, mesAtual, anoAtual));
            values.put("ano", anoAtual);
            values.put("permanente", 1); // Marca como permanente

            // A primeira despesa permanente é paga, as demais ficam em aberto
            boolean isPago = (i == 0) ? true : false;  // Somente o primeiro mês será pago
            values.put("pago", isPago ? 1 : 0);

            // Insere a despesa na tabela
            long resultado = database.insert("despesas", null, values);
            if (resultado == -1) {
                Log.e(TAG, "Erro ao adicionar despesa permanente");
            }

            // Incrementar o mês, se o mês ultrapassar 12, reinicia o mês para 1 e incrementa o ano
            mesAtual++;
            if (mesAtual > 12) {
                mesAtual = 1;
                anoAtual++;
            }
        }

        Log.d(TAG, "Despesa permanente adicionada com sucesso");
        Toast.makeText(context, "Despesa permanente adicionada com sucesso", Toast.LENGTH_SHORT).show();
    }


    private void salvarDespesaParcelada(Despesa despesa) {
        Calendar calendario = Calendar.getInstance();
        int mesAtual = calendario.get(Calendar.MONTH) + 1;
        int anoAtual = calendario.get(Calendar.YEAR);
        int parcela = 1;

        for (int i = 0; i < despesa.getNumeroParcelas(); i++) {
            ContentValues values = criarContentValues(despesa);
            values.put("vencimento", String.format("%02d/%02d/%d", despesa.getDiaVencimento(), mesAtual, anoAtual));
            values.put("ano", anoAtual);
            values.put("parcelada", 1);
            values.put("numeroParcelas", despesa.getNumeroParcelas());
            values.put("parcelaAtual", parcela);

            boolean isPago = (parcela == 1) ? true : false;
            values.put("pago", isPago ? 1 : 0);

            long resultado = database.insert("despesas", null, values);
            if (resultado == -1) {
                Log.e(TAG, "Erro ao adicionar despesa parcelada");
            }

            parcela++;
            mesAtual++;
            if (mesAtual > 12) {
                mesAtual = 1;
                anoAtual++;
            }
        }

        Log.d(TAG, "Despesa parcelada adicionada com sucesso");
        Toast.makeText(context, "Despesa parcelada adicionada com sucesso", Toast.LENGTH_SHORT).show();
    }

    // Método para alterar uma despesa
    public void alterarDespesa(Despesa despesa) {
        ContentValues values = criarContentValues(despesa);

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
            do {
                despesas.add(criarDespesa(cursor));
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

    // Métodos auxiliares
    private ContentValues criarContentValues(Despesa despesa) {
        ContentValues values = new ContentValues();
        values.put("descricao", despesa.getDescricao());
        values.put("valor", despesa.getValor());
        values.put("vencimento", despesa.getVencimento());
        values.put("ano", despesa.getAno());
        values.put("pago", despesa.isPago() ? 1 : 0);
        values.put("categoria", despesa.getCategoria());
        values.put("permanente", despesa.isPermanente() ? 1 : 0);
        values.put("parcelada", despesa.isParcelada() ? 1 : 0);
        values.put("numeroParcelas", despesa.getNumeroParcelas());
        values.put("parcelaAtual", despesa.getParcelaAtual());
        return values;
    }

    private Despesa criarDespesa(Cursor cursor) {
        int id = obterValorSeguro(cursor, "id", 0);
        String categoria = obterValorSeguro(cursor, "categoria", "");
        String descricao = obterValorSeguro(cursor, "descricao", "");
        double valor = obterValorSeguro(cursor, "valor", 0.0);
        String vencimento = obterValorSeguro(cursor, "vencimento", "");
        int ano = obterValorSeguro(cursor, "ano", 0);
        boolean pago = obterValorSeguro(cursor, "pago", 0) == 1;
        boolean permanente = obterValorSeguro(cursor, "permanente", 0) == 1;
        boolean parcelada = obterValorSeguro(cursor, "parcelada", 0) == 1;
        int numeroParcelas = obterValorSeguro(cursor, "numeroParcelas", 0);
        int parcelaAtual = obterValorSeguro(cursor, "parcelaAtual", 0);

        return new Despesa(id, categoria, descricao, valor, vencimento, ano, pago, permanente, parcelada, numeroParcelas, parcelaAtual);
    }

    private int obterIndiceSeguro(Cursor cursor, String coluna) {
        int index = cursor.getColumnIndex(coluna);
        return index >= 0 ? index : -1;
    }

    private <T> T obterValorSeguro(Cursor cursor, String coluna, T valorPadrao) {
        int index = obterIndiceSeguro(cursor, coluna);
        if (index == -1) {
            return valorPadrao;
        }

        try {
            if (valorPadrao instanceof String) {
                return (T) cursor.getString(index);
            } else if (valorPadrao instanceof Integer) {
                return (T) Integer.valueOf(cursor.getInt(index));
            } else if (valorPadrao instanceof Double) {
                return (T) Double.valueOf(cursor.getDouble(index));
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter valor da coluna " + coluna + ": " + e.getMessage());
        }

        return valorPadrao;
    }

    public List<Despesa> listarDespesasPorMesAno(String mes, int ano) {
        List<Despesa> despesas = new ArrayList<>();
        String mesFormatado = String.format(Locale.getDefault(), "%02d", Integer.parseInt(mes));
        String query = "SELECT * FROM despesas WHERE substr(vencimento, 4, 2) = ? AND ano = ?";
        Cursor cursor = database.rawQuery(query, new String[]{mesFormatado, String.valueOf(ano)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                despesas.add(criarDespesa(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return despesas;
    }

    public List<Despesa> listarDespesasPorAno(int ano) {
        List<Despesa> despesas = new ArrayList<>();
        String query = "SELECT * FROM despesas WHERE ano = ?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(ano)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                despesas.add(criarDespesa(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return despesas;
    }

}
