package com.example.budgetplanning;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Transaction.class}, version = 2)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract TransactionDAO getTransactionDAO();
}
