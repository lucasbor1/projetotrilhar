package com.example.telapi.firebase;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.example.telapi.firebase.User;

public class UserManager {
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public UserManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void getOrCreateUser(String displayName) {
        String userId = auth.getCurrentUser().getUid();
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

        User user = new User(displayName, auth.getCurrentUser().getEmail(), userId);
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.set(user).addOnSuccessListener(aVoid -> {

            Toast.makeText(context, "Usuário criado com sucesso: " + displayName, Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Erro ao criar usuário", Toast.LENGTH_SHORT).show();
        });
    }
}
