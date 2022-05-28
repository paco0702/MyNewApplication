package com.example.mynewapplication;

import static com.example.mynewapplication.IndexActivity.FLAT_FOR_GroupDetail;
import static com.example.mynewapplication.IndexActivity.exchangeGroup;
import static com.example.mynewapplication.IndexActivity.selectedGroup;
import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynewapplication.FragmentClass.HomeFragment;
import com.example.mynewapplication.FragmentClass.MessageFragment;
import com.example.mynewapplication.entities.Participant;
import com.example.mynewapplication.recyclerViewstorage.GroupDetailsAdapter;
import com.example.mynewapplication.recyclerViewstorage.ProcessingListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GroupDetailActivity extends AppCompatActivity {


    Button back_toPrevious;
    Button submit_request_btn;
    ListView mListView;

    private ProgressDialog mProgressDialog;

    //upload the group the database
    // for upload event
    private DatabaseReference realTimeDatabaseForUploadingExchangeEvent;
    private DatabaseReference realTimeDatabaseForUploadingExchangeEventProcessing;
    private DatabaseReference realTimeDatabaseForItems;
    private DatabaseReference realTimeDatabaseForItemsUser;
    private DatabaseReference databaseUploadLocation;
    private DatabaseReference realTimeDatabaseForAllEventRecord;
        // for notification
    private DatabaseReference realTimeDatabaseForNotification;
    // the chosen group
    LLS chosenGroupToUpLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                //At this point you should start the login activity and finish this one
                finish();
            }
        }, intentFilter);

        setContentView(R.layout.activity_group_detail);
        mProgressDialog = new ProgressDialog(this);

        back_toPrevious = (Button) findViewById(R.id.back_to_previous_detailedPage);
        submit_request_btn = (Button) findViewById(R.id.submit_request_btn);
        mListView = findViewById(R.id.Group_information_view_group_details);

        //the chose group
        chosenGroupToUpLoad = selectedGroup;

        // for upload location
        realTimeDatabaseForUploadingExchangeEvent = FirebaseDatabase.getInstance().getReference("Exchange_event/pending");
        realTimeDatabaseForUploadingExchangeEventProcessing = FirebaseDatabase.getInstance().getReference("Exchange_event/processing");
        realTimeDatabaseForItems = FirebaseDatabase.getInstance().getReference("Items");;
        realTimeDatabaseForItemsUser = FirebaseDatabase.getInstance().getReference("Items_Users");;
        realTimeDatabaseForAllEventRecord = FirebaseDatabase.getInstance().getReference("All_event");
        realTimeDatabaseForNotification = FirebaseDatabase.getInstance().getReference("Notification");

        back_toPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGroup = new LLS();
                finish();
            }
        });
        ShowInformation();

        if(FLAT_FOR_GroupDetail.compareTo("Processing")==0 || FLAT_FOR_GroupDetail.compareTo("Pending")==0 ||FLAT_FOR_GroupDetail.compareTo("History")==0|| FLAT_FOR_GroupDetail.compareTo("Notification")==0){
            submit_request_btn.setEnabled(false);
            submit_request_btn.setVisibility(View.INVISIBLE);
        }else{
            submit_request_btn.setEnabled(true);
            submit_request_btn.setVisibility(View.VISIBLE);
            submit_request_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProgressDialog.setMessage("Request submitting.");
                    mProgressDialog.show();

                    LLS.LLSNode node = chosenGroupToUpLoad.getHead();

                    int countFree = 0;
                    while (node!=null){
                        if(node.getExchange_item().getStatus().compareTo("free")==0){
                            countFree++;
                        }
                        node=node.getNext();
                    }
                    //all item is free  = size of the node
                    System.out.println(chosenGroupToUpLoad.getSize()+" "+countFree );
                    if(countFree==chosenGroupToUpLoad.getSize()){
                        UploadExchangeEvent();
                    }else{
                        ErrorMessage();
                    }
                }
            });
        }

    }
    private void ErrorMessage(){
        System.out.println("Items locked");
        mProgressDialog.dismiss();
        Toast.makeText(this,"The Items is locked for other transaction.",Toast.LENGTH_SHORT).show();
    }

    private void ShowInformation(){
        ArrayList<LLS.LLSNode> nodelist = new ArrayList<>();
        LLS.LLSNode node = chosenGroupToUpLoad.getHead();
        while(node!=null){
            nodelist.add(node);
            node = node.getNext();
        }

        initGroupDetailViewList(mListView,  nodelist);
    }


    private void initGroupDetailViewList(ListView mListView, ArrayList<LLS.LLSNode> list_of_processing){
        if(this!=null){ // handle the null pointer
            GroupDetailsAdapter adapter = new GroupDetailsAdapter (this, R.layout.detailsgroupinform_show_list,  list_of_processing);
            mListView.setAdapter(adapter);
        }
    }

    private void UploadExchangeEvent(){
        String event_id = getEventID();

        LLS.LLSNode node =  chosenGroupToUpLoad.getHead();
        int count = 0;

        //seperate pending and processing (event starter is in processin)
        ArrayList<String> participate_userID = new ArrayList<>(); // for pending list user ID

        while(node!=null) {  // processing for who start the evnet , already accpet becasue they start the event
            if(node.getProvider().getUserID().compareTo(theCurrentLoginUser.getUserID())==0){
                //initiate the items need to lock
                realTimeDatabaseForUploadingExchangeEventProcessing.child(theCurrentLoginUser.getUserID()).child(event_id).child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).child("status").setValue("locked");
                realTimeDatabaseForItems.child(node.getProvider().getUserID() + "_" + node.getExchange_item().getItemsID()).child("status").setValue("locked");
                realTimeDatabaseForItemsUser.child(node.getProvider().getUserID()).child(node.getExchange_item().getItemsID()).child("status").setValue("locked");


                //realTimeDatabaseForAllEventRecord.child(event_id).child("participants").child(node.getProvider().getUserID()).setValue(new Participant(node.getProvider().getEmail(), node.getProvider().getPassword(), node.getProvider().getUserName(),
                        //node.getProvider().getPhoneNum(), node.getProvider().getBirthday(), node.getProvider().getUserID(), "Accepted", node.getProvider().getRating(), node.getProvider().getProfileImagePath()));
                realTimeDatabaseForAllEventRecord.child(event_id).child("participants").child(node.getProvider().getUserID()).setValue(new Participant(node.getProvider().getEmail(), node.getProvider().getPassword(), node.getProvider().getUserName(),
                        node.getProvider().getPhoneNum(), node.getProvider().getBirthday(), node.getProvider().getUserID(), "Accepted", node.getProvider().getRating()));
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());
            }else{
                participate_userID.add(node.getProvider().getUserID());
                // storing details to all event dataset
                realTimeDatabaseForAllEventRecord.child(event_id).child("participants").child(node.getProvider().getUserID()).setValue(new Participant(node.getProvider().getEmail(), node.getProvider().getPassword(), node.getProvider().getUserName(),
                        node.getProvider().getPhoneNum(), node.getProvider().getBirthday(), node.getProvider().getUserID(),"Waiting",  node.getProvider().getRating()));
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());

                //add starter to the processing list
                realTimeDatabaseForUploadingExchangeEventProcessing.child(theCurrentLoginUser.getUserID()).child(event_id).child("" + (count + 1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                realTimeDatabaseForUploadingExchangeEventProcessing.child(theCurrentLoginUser.getUserID()).child(event_id).child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                realTimeDatabaseForUploadingExchangeEventProcessing.child(theCurrentLoginUser.getUserID()).child(event_id).child("" + (count + 1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());

            }
            count++;
            node = node.getNext();
        }
            //then
            //add to all event dataset
        realTimeDatabaseForAllEventRecord.child(event_id).child("status").setValue("Waiting");
        realTimeDatabaseForAllEventRecord.child(event_id).child("Sender").setValue(theCurrentLoginUser.getUserName());

        //pending is rest of the user
        for (int i = 0; i < participate_userID.size(); i++) {
            count = 0;
            node = chosenGroupToUpLoad.getHead();
            while (node != null) { // uploading as pending (for other participate ) each participate need group details
                realTimeDatabaseForUploadingExchangeEvent.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                realTimeDatabaseForUploadingExchangeEvent.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                realTimeDatabaseForUploadingExchangeEvent.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());

                realTimeDatabaseForNotification.child(participate_userID.get(i)).child(event_id).child("status").setValue("Waiting");
                count++;
                node = node.getNext();
            }
        }
       finishUpload();
    }

    private void finishUpload(){
        // show message that upload finish
        mProgressDialog.dismiss();
        Toast.makeText(this,"Exchange event uploaded.",Toast.LENGTH_SHORT).show();
        GroupDetailActivity.super.onBackPressed();
        finish();
    }

    private String getEventID(){
        int lengthOfTheID = 10;
        String id = "";
        for (int i =0; i<lengthOfTheID; i++){
            id = id + randomChooseAChar();
        }
        return id;
    }

    private char randomChooseAChar(){
        char [] valueRange= "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        int upperBound = valueRange.length;
        int n = (int)(Math.random() * upperBound );
        return valueRange[n];
    }
}