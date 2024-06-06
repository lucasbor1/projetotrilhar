package com.example.telapi.Despesa;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DespesaCRUD {
    private FirebaseFirestore db;

    public DespesaCRUD() {
        db = FirebaseFirestore.getInstance();
    }

    public void adicionarDespesa(Despesa despesa) {
        Log.d("DespesaCRUD", "Adicionando despesa: " + despesa.toString());
        db.collection("despesas").add(despesa)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("DespesaCRUD", "Despesa adicionada com ID: " + task.getResult().getId());
                            // Ação adicional após adicionar a despesa, se necessário
                        } else {
                            Log.e("DespesaCRUD", "Erro ao adicionar despesa", task.getException());
                        }
                    }
                });
    }

    public void alterarDespesa(Despesa despesa) {
        Log.d("DespesaCRUD", "Alterando despesa: " + despesa.toString());
        DocumentReference docRef = db.collection("despesas").document(despesa.getId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("DespesaCRUD", "Documento existe, atualizando...");
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

    public void removerDespesa(String idDespesa) {
        Log.d("DespesaCRUD", "Tentando remover despesa com ID: " + idDespesa);

        if (idDespesa == null || idDespesa.isEmpty()) {
            Log.e("DespesaCRUD", "ID da despesa inválido.");
            return;
        }

        DocumentReference docRef = db.collection("despesas").document(idDespesa);
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


    public void listarDespesas() {
        db.collection("despesas").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                Despesa despesa = document.toObject(Despesa.class);
                                Log.d("DespesaCRUD", "ID: " + id + " -> " + despesa.toString());
                            }
                        } else {
                            Log.e("DespesaCRUD", "Erro ao listar despesas", task.getException());
                        }
                    }
                });
    }


    public interface DespesaListListener {
        void onDespesasRetrieved(List<Despesa> despesas);

        void onDespesasFailed(Exception e);
    }


}
