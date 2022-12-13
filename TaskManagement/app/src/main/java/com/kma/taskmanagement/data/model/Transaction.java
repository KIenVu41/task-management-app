package com.kma.taskmanagement.data.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;

public class Transaction {
    private String name;
    private long userId;
    private long secrete;

    public Transaction(String name, long userId, long secrete) {
        this.name = name;
        this.userId = userId;
        this.secrete = secrete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
