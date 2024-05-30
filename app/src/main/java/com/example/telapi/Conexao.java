package com.example.telapi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Conexao extends SQLiteOpenHelper {
    private static final String NAME = "banco.db";
    private static final int VERSION = 1;

    private static final String SQL_CREATE = "create table despesa(" +
            "id integer primary key autoincrement, " +
            "descricao varchar(50), " +
            "valor varchar(20), " +
            "vencimento date);";

    public Conexao(@Nullable Context context){
        super(context, NAME, null,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE);
    };
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}



}
