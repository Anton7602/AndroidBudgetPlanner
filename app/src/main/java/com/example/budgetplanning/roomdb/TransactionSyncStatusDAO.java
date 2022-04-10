package com.example.budgetplanning.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.budgetplanning.entities.TransactionSyncStatus;

import java.util.List;

@Dao
public interface TransactionSyncStatusDAO {
    @Insert
    void insert(TransactionSyncStatus transactionSyncStatus);

    @Delete
    void delete(TransactionSyncStatus transactionSyncStatus);

    @Query("Select * From transactionsyncstatus")
    List<TransactionSyncStatus> getListOfSyncStatuses();
}
