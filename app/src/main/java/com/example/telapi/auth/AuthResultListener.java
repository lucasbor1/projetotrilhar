package com.example.telapi.auth;

public interface AuthResultListener {
    void onAuthSuccess(String displayName);
    void onAuthFailure(String errorMessage);
}
