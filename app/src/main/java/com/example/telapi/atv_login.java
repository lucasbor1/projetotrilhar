package com.example.telapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.telapi.auth.AuthManager;
import com.example.telapi.auth.AuthResultListener;

public class atv_login extends AppCompatActivity implements AuthResultListener {
    private AuthManager authManager;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    authManager.handleSignInResult(result.getData());
                } else {
                    Toast.makeText(this, "Falha na autenticação com Google", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar o AuthManager
        authManager = new AuthManager(this, this, googleSignInLauncher);
    }

    // Método chamado ao clicar no botão de login
    public void signInWithGoogle(View view) {
        authManager.signInWithGoogle();
    }

    // Método chamado após a autenticação bem-sucedida
    @Override
    public void onAuthSuccess(String userId) {
        MyApp.getInstance().setUserId(userId);
        Intent intent = new Intent(this, atv_menu.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthFailure(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
