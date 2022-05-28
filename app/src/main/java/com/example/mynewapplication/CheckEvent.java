package com.example.mynewapplication;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckEvent {
    private DatabaseReference realTimeDatabasePathForAllEvent = FirebaseDatabase.getInstance().getReference("All_event");
    ValueEventListener checkEvent = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot ds: snapshot.getChildren()){

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void startChecking(){
        realTimeDatabasePathForAllEvent.addValueEventListener(checkEvent);
    }
}
