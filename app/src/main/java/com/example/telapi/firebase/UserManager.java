package com.example.telapi.firebase;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.example.telapi.firebase.User;

import java.util.Objects;

public class UserManager {
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public UserManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void getOrCreateUser(String userId) {
        String displayName = Objects.requireNonNull(auth.getCurrentUser()).getDisplayName();
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String nome = document.getString("nome");
                    Toast.makeText(context, "Bem-vindo de volta, " + nome, Toast.LENGTH_SHORT).show();
                } else {
                    createNewUser(userId, displayName);
                }
            } else {
                Toast.makeText(context, "Erro ao acessar dados do usuário", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewUser(String userId, String displayName) {
        // Verifica se displayName não é nulo ou vazio antes de prosseguir
        if (displayName == null || displayName.isEmpty()) {
            // Exibe uma mensagem de erro (ou não faz nada)
            Toast.makeText(context, "Erro: Nome inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criação do objeto User com o nome correto
        User user = new User(displayName, Objects.requireNonNull(auth.getCurrentUser()).getEmail(), userId);

        // Referência do documento do usuário no Firestore
        DocumentReference userRef = db.collection("usuarios").document(userId);

        // Salva os dados do usuário no Firestore
        userRef.set(user).addOnSuccessListener(aVoid -> {
            // Exibe uma mensagem de sucesso com o nome correto
            Toast.makeText(context, "Usuário criado com sucesso: " + displayName, Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Não exibe mensagem de erro ou qualquer outra ação, pois você não quer mostrar nada
        });
    }

}
