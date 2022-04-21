package com.example.budgetplanning.roomdb;

import android.app.Application;
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

    public static Boolean isDatabaseSynchronizedWithFirebase(int numberOfTransactionInFirebase, String keyOfLastTransactionInDatabase) {
        LocalDatabase localDatabase = App.getInstance().getLocalDatabase();
        TransactionDAO transactionDAO = localDatabase.getTransactionDAO();
        return (keyOfLastTransactionInDatabase.equals(transactionDAO.getCurrentLastKey()) &&
        numberOfTransactionInFirebase == transactionDAO.getNumberOfTransactions());
    }

    public static void SynchronizeDatabaseWithFirebase() {

    }

    public static void ReplaceLocalDatabaseWithFirebaseData() {
        final DatabaseReference transactionDatabase = FirebaseDatabase.getInstance().getReference().child("Транзакции");
        LocalDatabase localDatabase = App.getInstance().getLocalDatabase();
        final TransactionDAO transactionDAO = localDatabase.getTransactionDAO();
        transactionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                    Transaction tempTransaction = currentSnapshot.getValue(Transaction.class);
                    tempTransaction.setDbKey(currentSnapshot.getKey());
                    transactionDAO.insert(tempTransaction);
                }
                transactionDatabase.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
