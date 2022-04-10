package com.example.budgetplanning.roomdb;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.budgetplanning.entities.Transaction;
import com.example.budgetplanning.entities.TransactionSyncStatus;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@Database(entities = {Transaction.class, TransactionSyncStatus.class}, version = 3)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract TransactionDAO getTransactionDAO();
    public  abstract  TransactionSyncStatusDAO getTransactionSyncStatusesDAO();

}
