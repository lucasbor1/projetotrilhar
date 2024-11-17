package com.example.telapi.auth;

public interface AuthResultListener {
    void onAuthSuccess(String userId);
    void onAuthFailure(String errorMessage);
}
