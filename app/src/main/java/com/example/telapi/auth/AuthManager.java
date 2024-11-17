package com.example.telapi.auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.example.telapi.MyApp;
import com.example.telapi.R;
import com.example.telapi.firebase.UserManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthManager {
    private static final String TAG = "AuthManager";

    private final GoogleSignInClient googleSignInClient;
    private final FirebaseAuth firebaseAuth;
    private final Activity activity;
    private final ActivityResultLauncher<Intent> googleSignInLauncher;
    private final AuthResultListener authResultListener;

    public AuthManager(Activity activity, AuthResultListener listener, ActivityResultLauncher<Intent> launcher) {
        this.activity = activity;
        this.authResultListener = listener;
        this.googleSignInLauncher = launcher;
        this.firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        this.googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    public void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult();
            if (account != null) {
                firebaseAuthWithGoogle(account);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao fazer login com Google", e);
            authResultListener.onAuthFailure("Falha ao autenticar com o Google");
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            String displayName = user.getDisplayName();
                            String email = user.getEmail();

                            MyApp.getInstance().setUserId(userId);
                            UserManager userManager = new UserManager(activity, userId);
                            userManager.criarOuAtualizarUsuario(userId, displayName, email);
                            authResultListener.onAuthSuccess(userId);
                        }
                    } else {
                        Log.e(TAG, "Falha ao autenticar com Firebase", task.getException());
                        authResultListener.onAuthFailure("Falha ao autenticar com o Firebase");
                    }
                });
    }
}
