package com.example.budgetplanning.roomdb;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class App extends Application {
    public static App instance;

    private LocalDatabase localDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        localDatabase = Room.databaseBuilder(this, LocalDatabase.class, "localStorage").build();
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Toast.makeText(getApplicationContext(), "Firebase is Online", Toast.LENGTH_SHORT).show();
                    DatabaseReference transactionDatabase = FirebaseDatabase.getInstance().getReference().child("Транзакции");
                    transactionDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int numOfTransactionsInFirebase = (int) snapshot.getChildrenCount();
                            Toast.makeText(getApplicationContext(), String.valueOf(numOfTransactionsInFirebase), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    transactionDatabase.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                                String keyOfLastTransactionInFirebase = currentSnapshot.getKey();
                                Toast.makeText(getApplicationContext(), keyOfLastTransactionInFirebase, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Firebase is Offline", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public static  App getInstance() {
        return instance;
    }

    public LocalDatabase getLocalDatabase() {
        return  localDatabase;
    }
}
