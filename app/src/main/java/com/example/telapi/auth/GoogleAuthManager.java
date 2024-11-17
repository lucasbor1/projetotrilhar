package com.example.telapi.auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.telapi.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GoogleAuthManager implements AuthManager {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleAuthManager";
    private GoogleSignInClient googleSignInClient;
    private AuthResultListener authResultListener;
    private Activity activity;
    private FirebaseAuth mAuth;

    public GoogleAuthManager(Activity activity, AuthResultListener listener) {
        this.activity = activity;
        this.authResultListener = listener;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void handleSignInResult(GoogleSignInAccount account) {
        if (account != null) {
            firebaseAuthWithGoogle(account);
        } else {
            authResultListener.onAuthFailure("Falha ao autenticar com o Google.");
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        String idToken = account.getIdToken();

        if (idToken != null) {
            mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    authResultListener.onAuthSuccess(userId);
                                } else {
                                    authResultListener.onAuthFailure("Erro ao obter o UID do usuário.");
                                }
                            } else {
                                authResultListener.onAuthFailure("Falha ao autenticar com o Firebase.");
                            }
                        }
                    });
        } else {
            authResultListener.onAuthFailure("Token de autenticação inválido.");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleSignInResult(account);
            } catch (ApiException e) {
                Log.e(TAG, "Falha ao autenticar com o Google: " + e.getStatusCode(), e);
                authResultListener.onAuthFailure("Falha ao autenticar com o Google.");
            }
        }
    }
}
