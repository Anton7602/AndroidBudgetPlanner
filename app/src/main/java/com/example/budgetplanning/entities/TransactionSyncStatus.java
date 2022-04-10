package com.example.budgetplanning.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TransactionSyncStatus {
    @PrimaryKey (autoGenerate = true)
    private int syncID;
    private String firebaseKey;
    private int syncStatusCode;

    public TransactionSyncStatus(String firebaseKey, int syncStatusCode) {
        this.firebaseKey = firebaseKey;
        this.syncStatusCode = syncStatusCode;
    }

    public int getSyncID() {
        return syncID;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public int getSyncStatusCode() {
        return syncStatusCode;
    }

    public void setSyncID(int syncID) {
        this.syncID = syncID;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public void setSyncStatusCode(int syncStatusCode) {
        this.syncStatusCode = syncStatusCode;
    }
}
