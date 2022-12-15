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
    public static PublicKey publicKey = null;
    public static PrivateKey key = null;
    public static KeyStore keyStore = null;
    public static Signature signature = null;

    public static void generateKeyPair() {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyPairGenerator.initialize(
                        new KeyGenParameterSpec.Builder(KEY_NAME,
                                KeyProperties.PURPOSE_SIGN)
                                .setDigests(KeyProperties.DIGEST_SHA256)
                                .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                                .setUserAuthenticationRequired(true)
                                .build());
            }
            keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public void genPubKey() {
        try {
            getKeyStore();
            keyStore.load(null);
            publicKey = keyStore.getCertificate(KEY_NAME).getPublicKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void genPrivateKey() {
        try {
            getKeyStore();
            keyStore.load(null);
            key = (PrivateKey) keyStore.getKey(KEY_NAME, null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }

    public static KeyStore getKeyStore() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            return keyStore;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Signature getSignature() {
        try {
            signature = Signature.getInstance("SHA256withECDSA");
            return signature;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getKey() {
        return key;
    }
}
