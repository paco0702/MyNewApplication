package com.example.mynewapplication.SerachExchange;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.example.mynewapplication.GroupDetailActivity;
import com.example.mynewapplication.LLS;
import com.example.mynewapplication.R;
import com.google.firebase.database.DatabaseReference;
import static com.example.mynewapplication.IndexActivity.FLAT_FOR_GroupDetail;
import static com.example.mynewapplication.IndexActivity.exchangeGroup;
import static com.example.mynewapplication.IndexActivity.selectedGroup;
import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
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

public class GroupDetailSearchExchangeActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_group_detail_search_exchange);
        mProgressDialog = new ProgressDialog(this);

        back_toPrevious = (Button) findViewById(R.id.back_to_previous_detailedPage_SE);

        submit_request_btn = (Button) findViewById(R.id.submit_request_btn_SE);
        mListView = findViewById(R.id.Group_information_view_group_details_SE);

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

        if(FLAT_FOR_GroupDetail.compareTo("Processing")==0 || FLAT_FOR_GroupDetail.compareTo("Pending")==0 || FLAT_FOR_GroupDetail.compareTo("Notification")==0){
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
                    if(node.getExchange_item().getStatus().compareTo("free")==0
                            && node.getNext().getExchange_item().getStatus().compareTo("free")==0){
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

    private void UploadExchangeEvent(){
        String event_id = getEventID();

        LLS.LLSNode node =  chosenGroupToUpLoad.getHead();
        int count = 0;

        //seperate pending and processing (event starter is in processin)
        ArrayList<String> participate_userID = new ArrayList<>(); // for pending list user ID
        String initiate = node.getProvider().getUserID();
        while(node!=null) {  // processing for who start the evnet , already accpet becasue they start the event

            //add starter to the processing list
            realTimeDatabaseForUploadingExchangeEventProcessing.child(initiate).child(event_id).child("" + (count + 1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
            realTimeDatabaseForUploadingExchangeEventProcessing.child(initiate).child(event_id).child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
            realTimeDatabaseForUploadingExchangeEventProcessing.child(initiate).child(event_id).child("" + (count + 1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());

            //initiate the items need to lock

            if (count > 0) {
                participate_userID.add(node.getProvider().getUserID());
                // storing details toall event dataset
                realTimeDatabaseForAllEventRecord.child(event_id).child("participants").child(node.getProvider().getUserID()).setValue(new Participant(node.getProvider().getEmail(), node.getProvider().getPassword(), node.getProvider().getUserName(),
                        node.getProvider().getPhoneNum(), node.getProvider().getBirthday(), node.getProvider().getUserID(), "Waiting", node.getProvider().getRating()));

                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());

            } else {
                //count == 0 , transaction starter
                realTimeDatabaseForAllEventRecord.child(event_id).child("participants").child(node.getProvider().getUserID()).setValue(new Participant(node.getProvider().getEmail(), node.getProvider().getPassword(), node.getProvider().getUserName(),
                        node.getProvider().getPhoneNum(), node.getProvider().getBirthday(), node.getProvider().getUserID(),"Accepted", node.getProvider().getRating()));
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (count + 1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());

                //lock the transaction starter items but not the other users' item
                realTimeDatabaseForUploadingExchangeEventProcessing.child(initiate).child(event_id).child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).child("status").setValue("locked");
                realTimeDatabaseForItems.child(node.getProvider().getUserID() + "_" + node.getExchange_item().getItemsID()).child("status").setValue("locked");
                realTimeDatabaseForItemsUser.child(node.getProvider().getUserID()).child(node.getExchange_item().getItemsID()).child("status").setValue("locked");

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
            while (node != null) {
                // uploading as pending (for other participate ) each participate need group details
                realTimeDatabaseForUploadingExchangeEvent.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                realTimeDatabaseForUploadingExchangeEvent.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                realTimeDatabaseForUploadingExchangeEvent.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());

                // realTimeDatabaseForNotification.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                // realTimeDatabaseForNotification.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                // realTimeDatabaseForNotification.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());
                // realTimeDatabaseForNotification.child(participate_userID.get(i)).child(event_id).child("status").setValue("Waiting");
                realTimeDatabaseForNotification.child(participate_userID.get(i)).child(event_id).child("status").setValue("Waiting");
                count++;
                node = node.getNext();
            }
        }
        finishUpload();
    }
    private void initGroupDetailViewList(ListView mListView, ArrayList<LLS.LLSNode> list_of_processing){
        GroupDetailsAdapter adapter = new GroupDetailsAdapter (this, R.layout.detailsgroupinform_show_list,  list_of_processing);
        mListView.setAdapter(adapter);
    }

    private void finishUpload(){
        // show message that upload finish
        System.out.println("transaction uploaded");
        mProgressDialog.dismiss();
        Toast.makeText(this,"Exchange event uploaded.",Toast.LENGTH_SHORT).show();
        GroupDetailSearchExchangeActivity.super.onBackPressed();
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