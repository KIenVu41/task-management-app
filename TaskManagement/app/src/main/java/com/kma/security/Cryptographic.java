package com.kma.security;

public class Cryptographic {

//    public void generateSecretKey(KeyGenParameterSpec keyGenParameterSpec) {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance(
//                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
//        keyGenerator.init(keyGenParameterSpec);
//        keyGenerator.generateKey();
//    }
//
//    private SecretKey getSecretKey() {
//        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
//
//        // Before the keystore can be accessed, it must be loaded.
//        keyStore.load(null);
//        return ((SecretKey)keyStore.getKey(KEY_NAME, null));
//    }
//
//    private Cipher getCipher() {
//        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
//                + KeyProperties.BLOCK_MODE_CBC + "/"
//                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
//    }
}
