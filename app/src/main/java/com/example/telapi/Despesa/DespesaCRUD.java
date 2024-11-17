package com.example.telapi.Despesa;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DespesaCRUD {
    private FirebaseFirestore db;

    public DespesaCRUD() {
        db = FirebaseFirestore.getInstance();
    }

    public void adicionarDespesa(String userId, String categoriaId, Despesa despesa) {
        db.collection("usuarios")
                .document(userId)
                .collection("categorias")
                .document(categoriaId)
                .collection("despesas")
                .add(despesa)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("DespesaCRUD", "Despesa adicionada com ID: " + task.getResult().getId());
                        } else {
                            Log.e("DespesaCRUD", "Erro ao adicionar despesa", task.getException());
                        }
                    }
                });
    }

    public void alterarDespesa(String userId, String categoriaId, Despesa despesa) {
        DocumentReference docRef = db.collection("usuarios")
                .document(userId)
                .collection("categorias")
                .document(categoriaId)
                .collection("despesas")
                .document(despesa.getId());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        docRef.set(despesa).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("DespesaCRUD", "Despesa alterada com ID: " + despesa.getId());
                                } else {
                                    Log.e("DespesaCRUD", "Erro ao alterar despesa", task.getException());
                                }
                            }
                        });
                    } else {
                        Log.d("DespesaCRUD", "Documento não existe, não é possível atualizar.");
                    }
                } else {
                    Log.e("DespesaCRUD", "Erro ao verificar existência do documento", task.getException());
                }
            }
        });
    }

    public void removerDespesa(String userId, String categoriaId, String idDespesa) {
        if (idDespesa == null || idDespesa.isEmpty()) {
            Log.e("DespesaCRUD", "ID da despesa inválido.");
            return;
        }

        DocumentReference docRef = db.collection("usuarios")
                .document(userId)
                .collection("categorias")
                .document(categoriaId)
                .collection("despesas")
                .document(idDespesa);

        docRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DespesaCRUD", "Despesa removida com ID: " + idDespesa);
                        } else {
                            Log.e("DespesaCRUD", "Erro ao remover despesa", task.getException());
                        }
                    }
                });
    }
}
