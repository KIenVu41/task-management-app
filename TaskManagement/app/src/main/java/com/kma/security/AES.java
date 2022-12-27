package com.kma.security;


import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static final String SECRET_KEY = "my_super_secret_key";
    private static final String SALT = "ssshhhhhhhhhhh!!!!";
    public static SecretKey secretKey = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void generateSecretKey(String keyAlias) {
        try {
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder("key1",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
            KeyGenParameterSpec keySpec = builder
                    .setKeySize(256)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setRandomizedEncryptionRequired(true)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(5 * 60)
                    .build();

            KeyGenerator kg = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            kg.init(keySpec);
            secretKey = kg.generateKey();
        }catch (Exception e) {

        }
    }

    public static SecretKey getSecretKey() {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);

            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry)ks.getEntry("key1", null);
            secretKey = entry.getSecretKey();
            return secretKey;
        } catch (Exception e) {

        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String strToEncrypt) {
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            //KeySpec spec = new PBEKeySpec(Base64.getEncoder().encodeToString(getSecretKey().getEncoded()).toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String strToDecrypt) {
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            //KeySpec spec = new PBEKeySpec(Base64.getEncoder().encodeToString(getSecretKey().getEncoded()).toCharArray(), SALT.getBytes(), 65536, 256);
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
        }
        return null;
    }
}
