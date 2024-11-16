package com.example.telapi.auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.telapi.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleAuthManager implements AuthManager {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleAuthManager";
    private GoogleSignInClient googleSignInClient;
    private AuthResultListener authResultListener;
    private Activity activity;

    public GoogleAuthManager(Activity activity, AuthResultListener listener) {
        this.activity = activity;
        this.authResultListener = listener;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    @Override
    public void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void handleSignInResult(GoogleSignInAccount account) {
        if (account != null) {
            authResultListener.onAuthSuccess(account.getDisplayName());
        } else {
            authResultListener.onAuthFailure("Falha ao autenticar com o Google.");
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
