package com.example.telapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.telapi.R;
import com.example.telapi.auth.GoogleAuthManager;
import com.example.telapi.auth.AuthResultListener;
import com.example.telapi.firebase.FirebaseAuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class atv_login extends AppCompatActivity implements AuthResultListener {

    private GoogleAuthManager googleAuthManager;
    private FirebaseAuthManager firebaseAuthManager;

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

        googleAuthManager = new GoogleAuthManager(this, this);
        firebaseAuthManager = new FirebaseAuthManager(this, this);
    }

    public void signInWithGoogle(View view) {
        googleAuthManager.signIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleAuthManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAuthSuccess(String displayName) {
        // Continue com a lógica de navegação ou obtenção/criação de usuário aqui
        Toast.makeText(this, "Autenticado com sucesso: " + displayName, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(atv_login.this, atv_menu.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthFailure(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
