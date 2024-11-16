package com.example.telapi.firebase;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.telapi.auth.AuthResultListener;
import com.example.telapi.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseAuthManager {

    private static final String TAG = "FirebaseAuthManager";
    private FirebaseAuth firebaseAuth;
    private AuthResultListener authResultListener;
    private Activity activity;

    public FirebaseAuthManager(Activity activity, AuthResultListener listener) {
        this.activity = activity;
        this.authResultListener = listener;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signInWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "Autenticado com sucesso: " + user.getDisplayName());
                        authResultListener.onAuthSuccess(user.getDisplayName());
                    } else {
                        Log.e(TAG, "Falha na autenticação com o Firebase", task.getException());
                        authResultListener.onAuthFailure("Falha na autenticação com o Firebase.");
                    }
                });
    }
}
