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
    private static Boolean firebaseConnection;

    private LocalDatabase localDatabase;
    private int numOfTransactionInFirebase;
    private String keyOfLastTransactionInFirebase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        localDatabase = Room.databaseBuilder(this, LocalDatabase.class, "localStorage").allowMainThreadQueries().build();
        App.firebaseConnection = false;
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firebaseConnection = snapshot.getValue(Boolean.class);
                if (firebaseConnection == true) {
                    final DatabaseReference transactionDatabase = FirebaseDatabase.getInstance().getReference().child("Транзакции");
                    transactionDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            numOfTransactionInFirebase = (int) snapshot.getChildrenCount();
                            transactionDatabase.orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                                        keyOfLastTransactionInFirebase = currentSnapshot.getKey();
                                    }
                                    transactionDatabase.removeEventListener(this);
                                    Toast.makeText(getApplicationContext(), LocalDatabase.isDatabaseSynchronizedWithFirebase(numOfTransactionInFirebase,
                                            keyOfLastTransactionInFirebase).toString(), Toast.LENGTH_LONG).show();
                                    if (!LocalDatabase.isDatabaseSynchronizedWithFirebase(numOfTransactionInFirebase,
                                            keyOfLastTransactionInFirebase)) {
                                        LocalDatabase.ReplaceLocalDatabaseWithFirebaseData();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            transactionDatabase.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public static  App getInstance() {return instance;}

    public static Boolean getFirebaseConnection() {return  firebaseConnection;}

    public LocalDatabase getLocalDatabase() {return  localDatabase;    }
}
