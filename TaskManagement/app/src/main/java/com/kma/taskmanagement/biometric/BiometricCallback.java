package com.kma.taskmanagement.biometric;

import android.hardware.biometrics.BiometricPrompt;

public interface BiometricCallback {

    void onSdkVersionNotSupported();

    void onBiometricAuthenticationNotSupported();

    void onBiometricAuthenticationNotAvailable();

    void onBiometricAuthenticationPermissionNotGranted();

    void onBiometricAuthenticationInternalError(String error);


    void onAuthenticationFailed();

    void onAuthenticationCancelled();

    void onAuthenticationSuccessful(BiometricPrompt.AuthenticationResult result);

    void onAuthenticationHelp(int helpCode, CharSequence helpString);

    void onAuthenticationError(int errorCode, CharSequence errString);
}