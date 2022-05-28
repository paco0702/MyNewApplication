package com.example.mynewapplication;

import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.HistoryListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewExchangeRecordActivity extends AppCompatActivity {
    Button back;
    ListView record_list;
    private DatabaseReference realTimeDatabaseForUploadingExchangeHistory;

    ValueEventListener retrieve_historyList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ArrayList<LLS> allGroupInform = new ArrayList<>();
            ArrayList<String> eventKey = new ArrayList<>();
            for (DataSnapshot ds : snapshot.getChildren()) {
                //TODO continue pending event
                if(ds.getKey().compareTo(theCurrentLoginUser.getUserID())==0){
                    for(DataSnapshot event: ds.getChildren()){
                        System.out.println(ds.getKey()+" has event "+event.getKey());
                        eventKey.add(event.getKey());
                        LLS dummyGroup = new LLS();
                        for (DataSnapshot eventNum: event.getChildren()){
                            System.out.println("event Num key is " +eventNum.getKey());
                            Items eventItem = new Items();
                            User provider = new User();
                            User receiver = new User();
                            for(DataSnapshot eachItem: eventNum.child("Item").getChildren()){
                                eventItem = eachItem.getValue(Items.class);
                            }
                            for(DataSnapshot eachProvider: eventNum.child("Item provider").getChildren()){
                                provider = eachProvider.getValue(User.class);
                            }
                            for(DataSnapshot eachReceiver: eventNum.child("Item receiver").getChildren()){
                                receiver = eachReceiver.getValue(User.class);
                            }

                            dummyGroup.insert(provider, receiver, eventItem);
                        }
                        allGroupInform.add(dummyGroup);
                    }
                }
            }
            //after all the loop
            // send dummpyGroup to the Listview adapter to list the event some when user click can view the details
            initRecordViewList(record_list, allGroupInform, eventKey);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exchange_record);
        realTimeDatabaseForUploadingExchangeHistory = FirebaseDatabase.getInstance().getReference("Exchange_event/history");
        realTimeDatabaseForUploadingExchangeHistory.addValueEventListener(retrieve_historyList);

        record_list = findViewById(R.id.exchange_record_profile_view);
        back = findViewById(R.id.return_exchange_record);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
    }


    private void initRecordViewList(ListView mListView, ArrayList<LLS> list_of_processing, ArrayList<String> eventKeyList){
        if(this!=null) {
            HistoryListAdapter adapter = new HistoryListAdapter(this, R.layout.processing_list_showmessage, list_of_processing, eventKeyList);
            mListView.setAdapter(adapter);
        }
    }
}