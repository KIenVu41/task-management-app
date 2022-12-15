package com.kma.taskmanagement.data.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.Objects;

public class Transaction {

    /** The unique ID of the item of the purchase */
    private final Long mItemId;

    /** The unique user ID who made the transaction */
    private final Long mUserId;

    /**
     * The random long value that will be also signed by the private key and verified in the server
     * that the same nonce can't be reused to prevent replay attacks.
     */
    private final Long mClientNonce;

    public Transaction(long userId, long itemId, long clientNonce) {
        mItemId = itemId;
        mUserId = userId;
        mClientNonce = clientNonce;
    }

    public Long getUserId() {
        return mUserId;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeLong(mItemId);
            dataOutputStream.writeLong(mUserId);
            dataOutputStream.writeLong(mClientNonce);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
            } catch (IOException ignore) {
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException ignore) {
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Transaction that = (Transaction) o;
        return Objects.equals(mItemId, that.mItemId) && Objects.equals(mUserId, that.mUserId) &&
                Objects.equals(mClientNonce, that.mClientNonce);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mItemId, mUserId, mClientNonce);
    }
}
