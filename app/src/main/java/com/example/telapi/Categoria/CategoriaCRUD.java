package com.example.telapi.Categoria;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class CategoriaCRUD {
    private FirebaseFirestore db;

    public CategoriaCRUD() {
        db = FirebaseFirestore.getInstance();
    }

    public void adicionarCategoria(String categoria) {
        db.collection("categorias").document(categoria)
                .set(new Categoria(categoria))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("CategoriaCRUD", "Categoria adicionada: " + categoria);
                        } else {
                            Log.e("CategoriaCRUD", "Erro ao adicionar categoria", task.getException());
                        }
                    }
                });
    }

    public void removerCategoria(String nomeCategoria) {
        db.collection("categorias").document(nomeCategoria)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("CategoriaCRUD", "Categoria removida: " + nomeCategoria);

                        } else {
                            Log.e("CategoriaCRUD", "Erro ao remover categoria", task.getException());
                        }
                    }
                });
    }


}
