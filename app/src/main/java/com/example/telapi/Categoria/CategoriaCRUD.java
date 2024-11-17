package com.example.telapi.Categoria;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoriaCRUD {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public CategoriaCRUD() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void adicionarCategoria(String categoria) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios")
                .document(userId)
                .collection("categorias")
                .document(categoria)
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


    public void removerCategoria(String categoria) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId)
                .collection("categorias").document(categoria)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("CategoriaCRUD", "Categoria removida: " + categoria);
                        } else {
                            Log.e("CategoriaCRUD", "Erro ao remover categoria", task.getException());
                        }
                    }
                });
    }

    public void listarCategorias(CategoriasCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("usuarios").document(userId)
                .collection("categorias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> categorias = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String categoria = document.getString("categoria");
                        categorias.add(categoria);
                    }
                    callback.onCategoriasLoaded(categorias);
                })
                .addOnFailureListener(e -> {
                    Log.e("CategoriaCRUD", "Erro ao listar categorias", e);
                    callback.onCategoriasFailed("Erro ao carregar categorias. Tente novamente.");
                });
    }

    public interface CategoriasCallback {
        void onCategoriasLoaded(List<String> categorias);
        void onCategoriasFailed(String errorMessage);
    }
}
