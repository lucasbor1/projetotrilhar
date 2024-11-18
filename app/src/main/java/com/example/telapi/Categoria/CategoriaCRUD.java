package com.example.telapi.Categoria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import com.example.telapi.DBHelper;
import java.util.ArrayList;
import java.util.List;

public class CategoriaCRUD implements CategoriaRepository {
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;

    public CategoriaCRUD(Context context, String userId) {
        this.context = context;
        dbHelper = new DBHelper(context, userId);
        database = dbHelper.openDatabase();
    }

    public List<String> listarCategorias() {
        List<String> categorias = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT nome FROM categorias", null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("nome");
            if (columnIndex >= 0) {
                do {
                    String nomeCategoria = cursor.getString(columnIndex);
                    categorias.add(nomeCategoria);
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(context, "Coluna 'nome' não encontrada!", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }
        return categorias;
    }

    public void adicionarCategoria(String nomeCategoria) {
        ContentValues values = new ContentValues();
        values.put("nome", nomeCategoria);
        database.insert("categorias", null, values);
    }

    public void removerCategoria(String nomeCategoria) {
        database.delete("categorias", "nome = ?", new String[]{nomeCategoria});
    }

    public boolean categoriaJaExiste(String nomeCategoria) {
        Cursor cursor = database.rawQuery("SELECT nome FROM categorias WHERE nome = ?", new String[]{nomeCategoria});
        boolean existe = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        return existe;
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
