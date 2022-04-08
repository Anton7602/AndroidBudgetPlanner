package com.example.budgetplanning;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TransactionDAO {

    @Insert
    void insert(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("Select * From `transaction`")
    List<Transaction> getAllTransactions();

    @Query("Select MAX(dbKey) From `transaction`")
    String getCurrentLastKey();

    @Query("DELETE FROM `transaction`")
    void clearTable();
}
