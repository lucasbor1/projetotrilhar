package com.example.telapi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DespesaDao {

    private final String TABELA = "cliente";
    private final String[]CAMPOS = {"id, descricao, valor, vencimento"};
    private Conexao conexao;
    private SQLiteDatabase banco;

    public DespesaDao(Context context){
        conexao = new Conexao(context);
        banco = conexao.getWritableDatabase();
    }
    public float somaValores(){
        Cursor c = banco.rawQuery("select sum (valor) from cliente", null);
        c.moveToNext();
        float total = c.getFloat(0);
        return total;
    }
    private ContentValues preencherValores(Despesa despesa){
        ContentValues values = new ContentValues();

        values.put("descricao", despesa.getDescricao());
        values.put("valor", despesa.getValor());
        values.put("vencimento", despesa.getVencimento());
        return values;
    }

    public long inserir(Despesa despesa){
        ContentValues values = preencherValores(despesa);
        return banco.insert(TABELA, null,values);
    }

    public long alterar (Despesa despesa){
        ContentValues values = preencherValores(despesa);
        return banco.update(TABELA, values,"id = ?", new String[]{despesa.getId().toString()});
    }

    public long excluir (Despesa despesa){
        return  banco.delete(TABELA,"id = ?",new String[]{despesa.getId().toString()});
    }
    public List<Despesa> listar(){
        Cursor c = banco.query(TABELA,CAMPOS,null,null,null,null,null);

        List<Despesa>lista = new ArrayList<>();
        while(c.moveToNext()){
            Despesa despesa = new Despesa();
            despesa.setId(c.getLong(0));
            despesa.setDescricao(c.getString(1));
            despesa.setValor(c.getDouble(2));
            despesa.setVencimento(c.getString(3));
            lista.add(despesa);
        }
        return lista;
    }
}
