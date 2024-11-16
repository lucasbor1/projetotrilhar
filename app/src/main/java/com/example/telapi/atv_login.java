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
import com.example.telapi.firebase.UserManager;

public class atv_login extends AppCompatActivity implements AuthResultListener {

    private GoogleAuthManager googleAuthManager;
    private UserManager userManager;

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
        userManager = new UserManager(this);
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

        userManager.getOrCreateUser(displayName);
        Intent intent = new Intent(atv_login.this, atv_menu.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthFailure(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
