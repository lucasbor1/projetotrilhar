package com.example.telapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class atv_login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "atv_login";
    private FirebaseAuth autenticacaoFirebase;
    private GoogleSignInClient clienteGoogleSignIn;

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

        GoogleSignInOptions configuracaoGoogleSignIn = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        clienteGoogleSignIn = GoogleSignIn.getClient(this, configuracaoGoogleSignIn);
        autenticacaoFirebase = FirebaseAuth.getInstance();

        findViewById(R.id.btnGoogleSignIn).setOnClickListener(v -> signInComGoogle());
    }

    private void signInComGoogle() {
        Intent signInIntent = clienteGoogleSignIn.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount conta = task.getResult(ApiException.class);
                autenticarNoFirebase(conta);
            } catch (ApiException e) {

                Log.w(TAG, "Autenticação Google falhou", e);
                Toast.makeText(atv_login.this, "Falha ao autenticar com o Google.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void autenticarNoFirebase(GoogleSignInAccount contaGoogle) {
        AuthCredential credencial = GoogleAuthProvider.getCredential(contaGoogle.getIdToken(), null);
        autenticacaoFirebase.signInWithCredential(credencial)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser usuarioFirebase = autenticacaoFirebase.getCurrentUser();
                        Toast.makeText(atv_login.this, "Autenticado com sucesso: " + usuarioFirebase.getDisplayName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(atv_login.this, atv_menu.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w(TAG, "Falha na autenticação com o Firebase", task.getException());
                        Toast.makeText(atv_login.this, "Falha na autenticação com o Firebase.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
