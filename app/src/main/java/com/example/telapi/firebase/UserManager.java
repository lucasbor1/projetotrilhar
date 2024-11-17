package com.example.telapi.firebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.telapi.DBHelper;

public class UserManager {
    private final Context context;
    private final DBHelper dbHelper;

    public UserManager(Context context, String userId) {
        this.context = context;
        this.dbHelper = new DBHelper(context, userId);
    }

    public void criarOuAtualizarUsuario(String userId, String nome, String email) {
        SQLiteDatabase database = dbHelper.openDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM usuarios WHERE uid = ?", new String[]{userId});
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("email", email);
        values.put("uid", userId);

        if (cursor.moveToFirst()) {
            database.update("usuarios", values, "uid = ?", new String[]{userId});
            String nomeUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            Toast.makeText(context, "Bem-vindo de volta, " + nomeUsuario + "!", Toast.LENGTH_SHORT).show();
        } else {
            database.insert("usuarios", null, values);
            Toast.makeText(context, "Usuário criado: " + nome, Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        dbHelper.closeDatabase();
    }

    public User obterUsuario(String userId) {
        SQLiteDatabase database = dbHelper.openDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM usuarios WHERE uid = ?", new String[]{userId});
        if (cursor.moveToFirst()) {
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            cursor.close();
            dbHelper.closeDatabase();
            return new User(nome, email, userId);
        }
        cursor.close();
        dbHelper.closeDatabase();
        return null;
    }
}
