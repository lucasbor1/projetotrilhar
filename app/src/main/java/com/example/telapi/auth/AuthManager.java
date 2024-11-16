package com.example.telapi.auth;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface AuthManager {
    void signIn();
    void handleSignInResult(GoogleSignInAccount account);
}
