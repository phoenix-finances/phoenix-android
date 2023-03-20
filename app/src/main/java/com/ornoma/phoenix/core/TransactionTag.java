package com.ornoma.phoenix.core;

import java.util.UUID;

/**
 * Created by de76 on 5/27/17.
 */

public final class TransactionTag {
    private int id;
    private String uid;
    private final String name;

    public final int getId() {
        return this.id;
    }

    public final void setId(int var1) {
        this.id = var1;
    }

    public final String getUid() {
        return this.uid;
    }

    public final void setUid(String uid) {
        this.uid = uid;
    }

    public final String getName() {
        return this.name;
    }

    public TransactionTag(String name) {
        this.name = name;
        this.uid = UUID.randomUUID().toString();
    }
}

