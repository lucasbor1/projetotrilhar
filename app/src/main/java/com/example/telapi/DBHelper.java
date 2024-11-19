package com.example.telapi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 4;
    private static final String DB_NAME_PREFIX = "despesas_";
    private SQLiteDatabase database;
    private final String userUid;

    public DBHelper(Context context, String uid) {
        super(context, DB_NAME_PREFIX + uid + ".db", null, DB_VERSION);
        this.userUid = uid;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableDespesas = "CREATE TABLE IF NOT EXISTS despesas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "descricao TEXT, " +
                "valor REAL, " +
                "vencimento TEXT, " +
                "ano INTEGER, " +
                "pago INTEGER, " +
                "categoria TEXT, " +
                "permanente INTEGER DEFAULT 0, " + // Nova coluna para despesas permanentes
                "parcelada INTEGER DEFAULT 0, " +  // Nova coluna para despesas parceladas
                "numeroParcelas INTEGER DEFAULT 0, " + // Número total de parcelas
                "parcelaAtual INTEGER DEFAULT 0 " +    // Parcela atual para controle
                ");";
        db.execSQL(createTableDespesas);


        String createTableCategorias = "CREATE TABLE IF NOT EXISTS categorias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT UNIQUE" +
                ");";
        db.execSQL(createTableCategorias);

        String createTableUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT, " +
                "email TEXT, " +
                "uid TEXT UNIQUE" +
                ");";
        db.execSQL(createTableUsuarios);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Atualiza a tabela despesas para adicionar a coluna ano
            db.execSQL("ALTER TABLE despesas ADD COLUMN ano INTEGER DEFAULT 0");
        }
        if (oldVersion < 4) {
            // Adiciona as colunas para despesas permanentes e parceladas
            db.execSQL("ALTER TABLE despesas ADD COLUMN permanente INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE despesas ADD COLUMN parcelada INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE despesas ADD COLUMN numeroParcelas INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE despesas ADD COLUMN parcelaAtual INTEGER DEFAULT 0");
        }
    }

    public SQLiteDatabase openDatabase() {
        if (database == null || !database.isOpen()) {
            database = this.getWritableDatabase();
        }
        return database;
    }

    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public void inserirUsuario(String nome, String email, String uid) {
        SQLiteDatabase db = openDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("email", email);
        values.put("uid", uid);


        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE uid = ?", new String[]{uid});
        if (cursor.getCount() == 0) {
            db.insert("usuarios", null, values);
        }
        cursor.close();
    }
    public Cursor buscarUsuario(String uid) {
        SQLiteDatabase db = openDatabase();
        return db.rawQuery("SELECT * FROM usuarios WHERE uid = ?", new String[]{uid});
    }
}
