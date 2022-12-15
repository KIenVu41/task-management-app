package com.kma.security;

import static com.kma.security.utils.Constants.KEY_NAME;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;

public class KeyPair {

    public static KeyPairGenerator keyPairGenerator = null;
    public static KeyStore keyStore = null;
    public static Signature signature = null;

    public static synchronized KeyPairGenerator providesKeyPairGenerator() {
        if(keyPairGenerator == null) {
            try {
                keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
        }
        return keyPairGenerator;
    }

    public static synchronized KeyStore providesKeystore() {
        if(keyStore == null) {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }
        return keyStore;
    }

    public static synchronized Signature providesSignature() {
        if(signature == null) {
            try {
                signature = Signature.getInstance("SHA256withECDSA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return signature;
    }
}
