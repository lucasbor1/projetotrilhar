package com.example.telapi;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CRUDDespesas {
    private FirebaseFirestore db;

    public CRUDDespesas() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void adicionarDespesa(Despesa despesa, Consumer<Boolean> callback) {
        Map<String, Object> despesaMap = new HashMap<>();
        despesaMap.put("descricao", despesa.getDescricao());
        despesaMap.put("valor", despesa.getValor());
        despesaMap.put("vencimento", despesa.getVencimento());

        db.collection("despesas")
                .add(despesaMap)
                .addOnSuccessListener(documentReference -> {
                    despesa.setId(documentReference.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void atualizarDespesa(Despesa despesa, Consumer<Boolean> callback) {
        Map<String, Object> despesaMap = new HashMap<>();
        despesaMap.put("descricao", despesa.getDescricao());
        despesaMap.put("valor", despesa.getValor());
        despesaMap.put("vencimento", despesa.getVencimento());

        db.collection("despesas").document(despesa.getId())
                .set(despesaMap)
                .addOnSuccessListener(aVoid -> callback.accept(true))
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void excluirDespesa(String id, Consumer<Boolean> callback) {
        db.collection("despesas").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> callback.accept(true))
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void listarDespesas(Consumer<List<Despesa>> callback) {
        db.collection("despesas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Despesa> listaDespesas = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Despesa despesa = document.toObject(Despesa.class);
                            despesa.setId(document.getId());
                            listaDespesas.add(despesa);
                        }
                        callback.accept(listaDespesas);
                    } else {
                        callback.accept(null);
                    }
                });
    }
}
