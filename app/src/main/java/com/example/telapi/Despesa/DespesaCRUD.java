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
        db.collection("despesas").document(despesa.getId())
                .set(despesa)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DespesaCRUD", "Despesa alterada com ID: " + despesa.getId());
                        } else {
                            Log.e("DespesaCRUD", "Erro ao alterar despesa", task.getException());
                        }
                    }
                });
    }

    public void removerDespesa(String idDespesa) {
        Log.d("DespesaCRUD", "Removendo despesa com ID: " + idDespesa);
        db.collection("despesas").document(idDespesa)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DespesaCRUD", "Despesa removida com ID: " + idDespesa);
                            // Ação adicional após remover a despesa, se necessário
                        } else {
                            Log.e("DespesaCRUD", "Erro ao remover despesa", task.getException());
                        }
                    }
                });
    }

    public void listarDespesas(final DespesaListListener listener) {
        db.collection("despesas").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Despesa> despesas = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Despesa despesa = document.toObject(Despesa.class);
                                despesas.add(despesa);
                            }
                            listener.onDespesasRetrieved(despesas);
                        } else {
                            Log.e("DespesaCRUD", "Erro ao listar despesas", task.getException());
                            listener.onDespesasFailed(task.getException());
                        }
                    }
                });
    }

    public interface DespesaListListener {
        void onDespesasRetrieved(List<Despesa> despesas);

        void onDespesasFailed(Exception e);
    }


}
