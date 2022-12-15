package com.kma.security;

import android.util.Log;

import com.kma.taskmanagement.data.model.Transaction;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StoreBackend {

    public static final Map<Long, PublicKey> mPublicKeys = new HashMap<>();
    public static final Set<Transaction> mReceivedTransactions = new HashSet<>();
    static Signature verificationFunction;

    public static boolean verify(Transaction transaction, byte[] transactionSignature) {
        try {
            if (mReceivedTransactions.contains(transaction)) {
                // It verifies the equality of the transaction including the client nonce
                // So attackers can't do replay attacks.
                return false;
            }
            Log.d("TAG", "server tran " + Arrays.toString(transactionSignature));
            mReceivedTransactions.add(transaction);
            PublicKey publicKey = mPublicKeys.get(transaction.getUserId());
            Log.d("TAG", "server pub " + publicKey.toString());
            Signature verificationFunction = Signature.getInstance("SHA256withECDSA");
            //Signature verificationFunction = KeyPair.providesSignature();
            verificationFunction.initVerify(publicKey);
            verificationFunction.update(transaction.toByteArray());
            if (verificationFunction.verify(transactionSignature)) {
                // Transaction is verified with the public key associated with the user
                // Do some post purchase processing in the server
                return true;
            }
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            // In a real world, better to send some error message to the user
        }
        return false;
    }

    public boolean verify(Transaction transaction, String password) {
        // As this is just a sample, we always assume that the password is right.
        return true;
    }

    public static boolean enroll(long userId, PublicKey publicKey) {
        if (publicKey != null) {
            mPublicKeys.put(userId, publicKey);
        }
        return true;
    }
}
