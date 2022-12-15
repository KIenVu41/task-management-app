package com.kma.taskmanagement.data.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.Objects;

public class Transaction {

    private long userId;
    private long secrete;

    public Transaction(long userId, long secrete) {
        this.userId = userId;
        this.secrete = secrete;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getSecrete() {
        return secrete;
    }

    public void setSecrete(long secrete) {
        this.secrete = secrete;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeLong(userId);
            dataOutputStream.writeLong(secrete);
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
        return  Objects.equals(userId, that.userId) &&
                Objects.equals(secrete, that.secrete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, secrete);
    }
}
