package com.example.mynewapplication.recyclerViewstorage;

import static com.example.mynewapplication.IndexActivity.FLAT_FOR_GroupDetail;
import static com.example.mynewapplication.IndexActivity.allUsers;
import static com.example.mynewapplication.IndexActivity.selectedGroup;
import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;

import com.example.mynewapplication.GroupDetailActivity;
import com.example.mynewapplication.IndexActivity;
import com.example.mynewapplication.LLS;
import com.example.mynewapplication.MainActivity;
import com.example.mynewapplication.R;
import com.example.mynewapplication.UploadActivity;
import com.example.mynewapplication.entities.Participant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsalf.smileyrating.SmileyRating;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationAdapter extends ArrayAdapter<LLS> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private ArrayList<String> eventKeyList;
    private ArrayList<String> allGroupInformStatus;
    private DatabaseReference realTimeDatabaseForUploadingExchangeEventProcessing =
            FirebaseDatabase.getInstance().getReference("Exchange_event/processing");

    private DatabaseReference realTimeDatabaseForUploadingExchangeEventPending =
            FirebaseDatabase.getInstance().getReference("Exchange_event/pending");
    private DatabaseReference realTimeDatabasePathForAllEvent = FirebaseDatabase.getInstance().getReference("All_event");

    public String onCheckingEventID;  // let checkEvent function can use

    public ArrayList<Participant> loginParticipants;
    // check all user agree


    // for connect to the server for comment session
    private OkHttpClient okhttpclient = new OkHttpClient();
    private String postBodyString = "";
    private MediaType mediaType;
    private RequestBody requestBody;

    private ProgressDialog mProgressDialog;

    // for using for updating record
    LLS onClickEvent = new LLS();

    @RequiresApi(api = Build.VERSION_CODES.M)

    ValueEventListener checkEvent = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int AllAgree = 0;
            int count = 0;
            ArrayList<String> userID = new ArrayList<>();
            for(DataSnapshot ds: snapshot.getChildren()){
                System.out.println("User id is" + ds.getKey()+" eventID "+onCheckingEventID);
                userID.add(ds.getKey());
                if(ds.child("status").getValue().toString().compareTo("Accepted")==0) {
                    AllAgree ++;
                    System.out.println("Changed to true");
                }else{

                    System.out.println("Changed to false");
                }
                count++;
            }
            System.out.println("Eventually it is "+AllAgree);
            if(AllAgree==count){ // update the notification
                FirebaseDatabase.getInstance().getReference("All_event").child(onCheckingEventID).child("status").setValue("Waiting to send");
                for(int i=0; i<userID.size(); i++){
                    FirebaseDatabase.getInstance().getReference("Notification").child(userID.get(i)).child(onCheckingEventID).child("status").setValue("Waiting to send");
                }
                return;
            }else{
                return;
            }


        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    // check if all user send items
    ValueEventListener checkEventAllSent = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int AllSent = 0;
            int count = 0;
            ArrayList<String> userID = new ArrayList<>();
            for(DataSnapshot ds: snapshot.getChildren()){
                System.out.println("User id is " + ds.getKey());
                userID.add(ds.getKey());
                if(ds.child("status").getValue().toString().compareTo("Sent Item")==0) {
                    AllSent++;
                    System.out.println("Changed to true");
                }else{
                    System.out.println("Changed to false");
                }
                count ++;
            }
            System.out.println("Eventually it is All sent "+AllSent+" count "+count);
            if(AllSent==count){ // update the notification
                //change
                FirebaseDatabase.getInstance().getReference("All_event").child(onCheckingEventID).child("status").setValue("Items Sent");
                for(int i=0; i<userID.size(); i++){
                    FirebaseDatabase.getInstance().getReference("Notification").child(userID.get(i)).child(onCheckingEventID).child("status").setValue("Items Sent");
                }
                return;
            }else{
                return;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    // check if all user received items
    ValueEventListener checkEventConfirmReceived = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int AllReceived = 0;
            int count = 0;
            ArrayList<String> userID = new ArrayList<>();
            for(DataSnapshot ds: snapshot.getChildren()){
                System.out.println("User id is" + ds.getKey());
                userID.add(ds.getKey());
                if(ds.child("status").getValue().toString().compareTo("Received Item")==0) {
                    AllReceived++;
                    System.out.println("Changed to true");
                }else{
                    System.out.println("Changed to false");
                }
                count++;
            }
            System.out.println("Eventually it is "+AllReceived);
            if(AllReceived==count){ // update the notification
                //change
               FirebaseDatabase.getInstance().getReference("All_event").child(onCheckingEventID).child("status").setValue("Transaction finish");
                for(int i=0; i<userID.size(); i++){
                    FirebaseDatabase.getInstance().getReference("Notification").child(userID.get(i)).child(onCheckingEventID).child("status").setValue("Transaction finish");
                }
                return;
            }else{
                return;
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener sendRejection = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ArrayList<String> userID = new ArrayList<>();
            for(DataSnapshot ds: snapshot.getChildren()){
                System.out.println("User id is" + ds.getKey());
                userID.add(ds.getKey());
                //send the notification to other user that user rejected
                FirebaseDatabase.getInstance().getReference("Notification").child(ds.getKey()).child(onCheckingEventID).child("status").setValue("Users rejected");
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    ValueEventListener checkRejection = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int allRejected = 0;
            int count = 0;
            for(DataSnapshot ds: snapshot.child("participants").getChildren()){
                if(ds.child("status").getValue(String.class).compareTo("Rejected")==0){
                    allRejected ++;
                }
                count++;
            }

            if(allRejected==count){
                //user user noticed the rejection and rejected
                // unloack items of all user
                for(DataSnapshot ds: snapshot.child("group").getChildren()){
                    String userID = "";
                    String itemID = "";
                    for(DataSnapshot uD: ds.child("Item provider").getChildren()){
                        userID = uD.getKey();
                    }
                    for(DataSnapshot iD: ds.child("Item").getChildren()){
                        itemID = iD.getKey();
                    }
                    FirebaseDatabase.getInstance().getReference("Items").child(userID+"_"+itemID).child("status").setValue("free");
                    FirebaseDatabase.getInstance().getReference("Items_Users").child(userID).child(itemID).child("status").setValue("free");
                }
                return;
            }else{
                return;
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    ValueEventListener checkGivenRating = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int AllGivenRating = 0;
            int count = 0;
            ArrayList<String> userID = new ArrayList<>();
            for(DataSnapshot ds: snapshot.getChildren()){
                System.out.println("User id is" + ds.getKey());
                userID.add(ds.getKey());
                if(ds.child("status").getValue().toString().compareTo("Gave rating")==0) {
                    AllGivenRating++;
                    System.out.println("Changed to true");
                }else{
                    System.out.println("Changed to false");
                }
                count++;
            }

            if(AllGivenRating==count){ // update the notification
                //change
                FirebaseDatabase.getInstance().getReference("All_event").child(onCheckingEventID).child("status").setValue("Transaction finish_Gave_rating");
                for(int i=0; i<userID.size(); i++){
                    //add to history
                    LLS.LLSNode node =onClickEvent.getHead();
                    int nodeNum = 1;
                    while(node!=null){
                        FirebaseDatabase.getInstance().getReference("Exchange_event").child("history").child(userID.get(i)).child(onCheckingEventID).child(nodeNum+"").child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                        FirebaseDatabase.getInstance().getReference("Exchange_event").child("history").child(userID.get(i)).child(onCheckingEventID).child(nodeNum+"").child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                        FirebaseDatabase.getInstance().getReference("Exchange_event").child("history").child(userID.get(i)).child(onCheckingEventID).child(nodeNum+"").child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());

                        //set the item to traded
                        FirebaseDatabase.getInstance().getReference("Items_Users").child(node.getProvider().getUserID()).child(node.getExchange_item().getItemsID()).child("status").setValue("traded");
                        FirebaseDatabase.getInstance().getReference("Items").child(node.getProvider().getUserID()+"_"+node.getExchange_item().getItemsID()).child("status").setValue("traded");
                        nodeNum++;
                        node = node.getNext();
                    }
                    //remove on processing
                    FirebaseDatabase.getInstance().getReference("Exchange_event").child("processing").child(userID.get(i)).child(onCheckingEventID).removeValue();
                }
                return;
            }else{
                return;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    int Offer_rating = 0;

    public static class NotificationListViewHolder {
        TextView sender_user_name;
        TextView eventID;
        Button accept_request;
        Button reject_request;
        Button send_item_confirm;
        Button confirm_receive_item;
        Button groupInformation;
        FloatingActionButton notice;
    }

    public NotificationAdapter (@NonNull Context context, int resource, @NonNull List<LLS> objects, ArrayList<String> allGroupInformStatus, ArrayList<String> eventKeyList, ArrayList<Participant> loginParticipants) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.eventKeyList = eventKeyList;
        this.allGroupInformStatus = allGroupInformStatus;
        this.loginParticipants = loginParticipants; //mainly store the status of login user status for each event
        mProgressDialog = new ProgressDialog(mContext);
        okhttpclient.setConnectTimeout(15, TimeUnit.SECONDS);
        okhttpclient.setReadTimeout(3, TimeUnit.MINUTES);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        //get the persons information
        String eventID = this.eventKeyList.get(position);
        String eventStatus = this.allGroupInformStatus.get(position);

        String senderUserName = getItem(position).getHead().getReceiver().getUserName();
        String senderUserID = getItem(position).getHead().getReceiver().getUserID();

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        NotificationListViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new NotificationListViewHolder();
            holder.sender_user_name = (TextView)convertView.findViewById(R.id.sender_user_name);
            holder.eventID = (TextView)convertView.findViewById(R.id.event_id);
            holder.accept_request = (Button) convertView.findViewById(R.id.notification_accept_request);
            holder.reject_request = (Button) convertView.findViewById(R.id.notification_reject_request);
            holder.send_item_confirm = (Button) convertView.findViewById(R.id.notification_send_item_confirm);
            holder.confirm_receive_item = (Button) convertView.findViewById(R.id.notification_receive_item_confirm);
            holder.groupInformation = (Button) convertView.findViewById(R.id.group_information_notification);
            holder.notice = (FloatingActionButton) convertView.findViewById(R.id.floatingActionButton_notice);
            result = convertView;
            convertView.setTag(holder);
        }else{
            holder = (NotificationListViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.fui_slide_in_right:R.anim.fui_slide_in_right );

        result.startAnimation(animation);
        lastPosition = position;

        holder.sender_user_name.setText(senderUserName);
        holder.eventID.setText(eventID);

        checkStatus(holder, eventStatus, eventID, position);


        holder.groupInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGroup =  getItem(position);
                FLAT_FOR_GroupDetail = "Notification";
                Intent intent = new Intent(mContext, GroupDetailActivity.class);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    private void checkStatus(NotificationListViewHolder holder, String eventStatus, String eventID, int position){
        System.out.println("participant status "+ loginParticipants.get(position).getStatus()+" event "+eventID);

        if(eventStatus.compareTo("Waiting")==0){
            // the event need to be accepted or rejected
            holder.accept_request.setVisibility(View.VISIBLE);
            holder.reject_request.setVisibility(View.VISIBLE);

            holder.accept_request.setEnabled(true);
            holder.reject_request.setEnabled(true);

            holder.reject_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // TODO remove from pending list, or put it into history status reject
                            FirebaseDatabase.getInstance().getReference("Notification").child(theCurrentLoginUser.getUserID()).child(eventID).removeValue();
                            FirebaseDatabase.getInstance().getReference("All_event").child("eventID").child("participants").child(theCurrentLoginUser.getUserID()).child("status").setValue("Rejected");

                            String event_id = eventID;
                            selectedGroup =  getItem(position); // in case all user accepted
                            onCheckingEventID = event_id;
                            // after clicking check if the event cyclic
                            realTimeDatabasePathForAllEvent.child(event_id).child("participants").addValueEventListener(sendRejection);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setTitle("Confirm to reject?").create();
                    builder.show();

                }
            });

            holder.accept_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // remove the original notificaiton
                            FirebaseDatabase.getInstance().getReference("Notification").child(theCurrentLoginUser.getUserID()).child(eventID).child("status").removeValue();

                            //after accept , lock the items
                            LLS.LLSNode loopNode  = getItem(position).getHead();
                            while(loopNode!=null){
                                // if providerID matches the current login userID , the that item is belong to provider and need to be lock
                                if(loopNode.getProvider().getUserID().compareTo(theCurrentLoginUser.getUserID())==0){
                                    FirebaseDatabase.getInstance().getReference("Items").child(theCurrentLoginUser.getUserID()+"_"+loopNode.getExchange_item().getItemsID()).child("status").setValue("locked");
                                    FirebaseDatabase.getInstance().getReference("Items_Users").child(theCurrentLoginUser.getUserID()).child(loopNode.getExchange_item().getItemsID()).child("status").setValue("locked");
                                    break;
                                }else{
                                    loopNode = loopNode.getNext();
                                }
                            }

                            // transaction remove from pending list
                            realTimeDatabaseForUploadingExchangeEventProcessing.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    selectedGroup =  getItem(position); // in case all user accepted
                                    String event_id = eventID;
                                    onCheckingEventID = event_id;
                                    LLS.LLSNode node = getItem(position).getHead();
                                    int count = 0;
                                    while(node!=null){  // processing for who start the event , already accept becasue they start the event
                                        realTimeDatabaseForUploadingExchangeEventProcessing.child(theCurrentLoginUser.getUserID()).child(event_id).child(""+(count+1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                                        realTimeDatabaseForUploadingExchangeEventProcessing.child(theCurrentLoginUser.getUserID()).child(event_id).child(""+(count+1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                                        realTimeDatabaseForUploadingExchangeEventProcessing.child(theCurrentLoginUser.getUserID()).child(event_id).child(""+(count+1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());
                                        count++;
                                        node = node.getNext();
                                    }
                                    // then remove the same event in pending
                                    FirebaseDatabase.getInstance().getReference("Exchange_event/pending").child(theCurrentLoginUser.getUserID()).child(event_id).removeValue();

                                    // modify the record after user click agree
                                    realTimeDatabasePathForAllEvent.child(event_id).child("participants").child(theCurrentLoginUser.getUserID()).child("status").setValue("Accepted");

                                    // after clicking check if the event cyclic
                                    realTimeDatabasePathForAllEvent.child(event_id).child("participants").addValueEventListener(checkEvent);


                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setTitle("Confirm to accept?").create();
                    builder.show();

                }
            });

            holder.send_item_confirm.setEnabled(false);
            holder.send_item_confirm.setVisibility(View.INVISIBLE);
            holder.confirm_receive_item.setEnabled(false);
            holder.confirm_receive_item.setVisibility(View.INVISIBLE);

            holder.notice.setEnabled(false);
            holder.notice.setVisibility(View.INVISIBLE);
        }

        if(eventStatus.compareTo("Waiting to send")==0 && loginParticipants.get(position).getStatus().compareTo("Accepted")==0){
            // the event need to be accepted or rejected
            holder.accept_request.setVisibility(View.INVISIBLE);
            holder.reject_request.setVisibility(View.INVISIBLE);
            holder.accept_request.setEnabled(false);
            holder.reject_request.setEnabled(false);


            holder.send_item_confirm.setEnabled(true);
            holder.send_item_confirm.setVisibility(View.VISIBLE);

            // show button that user confirm sent item
            holder.send_item_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.out.println(theCurrentLoginUser.getUserID());
                            System.out.println(eventID);
                            System.out.println(FirebaseDatabase.getInstance().getReference("Notification").child(theCurrentLoginUser.getUserID()).child(eventID).child("status").toString());
                            FirebaseDatabase.getInstance().getReference("Notification").child(theCurrentLoginUser.getUserID()).child(eventID).child("status").removeValue();

                            String event_id = eventID;
                            selectedGroup =  getItem(position); // in case all user accepted
                            onCheckingEventID = event_id;


                            // modify the record after user click agree
                            // change the status in participants
                            realTimeDatabasePathForAllEvent.child(event_id).child("participants").child(theCurrentLoginUser.getUserID()).child("status").setValue("Sent Item");

                            // after clicking check if the event cyclic
                            realTimeDatabasePathForAllEvent.child(event_id).child("participants").addValueEventListener(checkEventAllSent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setTitle("Confirm sent the item?").create();
                    builder.show();

                }
            });

            holder.confirm_receive_item.setEnabled(false);
            holder.confirm_receive_item.setVisibility(View.INVISIBLE);

            holder.notice.setEnabled(false);
            holder.notice.setVisibility(View.INVISIBLE);
        }

        if(eventStatus.compareTo("Items Sent")==0 && loginParticipants.get(position).getStatus().compareTo("Sent Item")==0){
            // the event need to be accepted or rejected
            holder.accept_request.setVisibility(View.INVISIBLE);
            holder.reject_request.setVisibility(View.INVISIBLE);
            holder.accept_request.setEnabled(false);
            holder.reject_request.setEnabled(false);


            holder.send_item_confirm.setEnabled(false);
            holder.send_item_confirm.setVisibility(View.INVISIBLE);

            holder.confirm_receive_item.setEnabled(true);
            holder.confirm_receive_item.setVisibility(View.VISIBLE);
            holder.confirm_receive_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseDatabase.getInstance().getReference("Notification").child(theCurrentLoginUser.getUserID()).child(eventID).child("status").removeValue();
                            String event_id = eventID;
                            selectedGroup =  getItem(position); // in case all user accepted
                            onCheckingEventID = event_id;

                            // modify the record after user click agree
                            // change the status in participants
                            realTimeDatabasePathForAllEvent.child(event_id).child("participants").child(theCurrentLoginUser.getUserID()).child("status").setValue("Received Item");

                            // after clicking check if the event cyclic
                            realTimeDatabasePathForAllEvent.child(event_id).child("participants").addValueEventListener(checkEventConfirmReceived);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setTitle("Confirm received the item?").create();
                    builder.show();


                }
            });


            holder.notice.setEnabled(false);
            holder.notice.setVisibility(View.INVISIBLE);
        }

        if(eventStatus.compareTo("Transaction finish")==0){
            holder.accept_request.setVisibility(View.INVISIBLE);
            holder.reject_request.setVisibility(View.INVISIBLE);
            holder.accept_request.setEnabled(false);
            holder.reject_request.setEnabled(false);


            holder.send_item_confirm.setEnabled(false);
            holder.send_item_confirm.setVisibility(View.INVISIBLE);

            holder.confirm_receive_item.setEnabled(false);
            holder.confirm_receive_item.setVisibility(View.INVISIBLE);

            holder.notice.setEnabled(true);
            holder.notice.setVisibility(View.VISIBLE);

            //notice button is used to give comment
            //after giving comment, the transaction can be put into history
            //items status is sent and not output to the items pool
            holder.notice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("The position is "+position);
                    onCheckingEventID = eventID;
                    onClickEvent = getItem(position);
                    System.out.println("Clicked node is "+onClickEvent.getHead().getReceiver().getUserID());
                    showCommentDialog();
                }
            });
        }

        if(eventStatus.compareTo("Users rejected")==0){
            // the event need to be accepted or rejected
            holder.accept_request.setVisibility(View.INVISIBLE);
            holder.reject_request.setVisibility(View.INVISIBLE);
            holder.accept_request.setEnabled(false);
            holder.reject_request.setEnabled(false);


            holder.send_item_confirm.setEnabled(false);
            holder.send_item_confirm.setVisibility(View.INVISIBLE);
            holder.confirm_receive_item.setEnabled(false);
            holder.confirm_receive_item.setVisibility(View.INVISIBLE);

            holder.notice.setEnabled(true);
            holder.notice.setVisibility(View.VISIBLE);
            holder.notice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            FirebaseDatabase.getInstance().getReference("Notification").child(theCurrentLoginUser.getUserID()).child(eventID).removeValue();
                            realTimeDatabasePathForAllEvent.child("eventID").child("participants").child(theCurrentLoginUser.getUserID()).child("status").setValue("Rejected");

                            realTimeDatabasePathForAllEvent.child("eventID").addValueEventListener(checkRejection);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setTitle("Confirm rejection message").create();
                    builder.show();

                }
            });

        }
    }

    private void showCommentDialog(){
        Dialog commentDialog = new Dialog(mContext);
        commentDialog.setContentView(R.layout.comment_dialog_layout);
        commentDialog.getWindow().setLayout(getWindowWidth()-50, ViewGroup.LayoutParams.WRAP_CONTENT);

        SmileyRating rating = commentDialog.findViewById(R.id.smileyRating);
        EditText comment = commentDialog.findViewById(R.id.comment_area_MultiLine);
        Button submit = commentDialog.findViewById(R.id.commentSumbit);
        Button cancel = commentDialog.findViewById(R.id.commentCancel);

        rating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                switch(type){
                    case TERRIBLE:
                        Offer_rating = 1;
                        break;
                    case BAD:
                        Offer_rating = 2;
                        break;
                    case OKAY:
                        Offer_rating = 3;
                        break;
                    case GOOD:
                        Offer_rating = 4;
                        break;
                    case GREAT:
                        Offer_rating = 5;
                        break;
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(comment.getText().toString());
                //submit the comment to the server
                mProgressDialog.setMessage("Processing");
                mProgressDialog.show();


                postComment(comment.getText().toString(),"http://192.168.0.157:5000/perform_comment_analysis");
                commentDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                commentDialog.dismiss();
            }
        });

        commentDialog.show();
    }

    //server
    private void postComment(String comment, String URL) {
        mediaType = MediaType.parse("text/plain");
        requestBody = RequestBody.create(mediaType, comment);

        Request user_request = new Request
                .Builder()
                .post(requestBody).addHeader("comment", comment)
                .url(URL)
                .build();

        okhttpclient.newCall(user_request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }
            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
                System.out.println(result);
                // update the rating to item provider
                // remove this notification
                // save the comment to the item provider
                mProgressDialog.dismiss();
                FirebaseDatabase.getInstance().getReference("Notification").child(theCurrentLoginUser.getUserID()).child(onCheckingEventID).child("status").removeValue();
                updateTransaction(result, comment);
            }
        });
    }


    private int getWindowWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
      return metrics.widthPixels;
    };

    private void updateTransaction(String result, String comment){
        System.out.println("Clicked node is "+onClickEvent.getHead().getReceiver().getUserID());

        LLS.LLSNode node = onClickEvent.getHead();
        float convertedResult =  Float.parseFloat(result);
        float scaled_result = convertedResult*5;
        float received_rating = (scaled_result+Offer_rating)/2;

        while(node!=null){
            System.out.println(node.getReceiver().getUserID()+" "+theCurrentLoginUser.getUserID() );

            if(node.getReceiver().getUserID().compareTo(theCurrentLoginUser.getUserID())==0){
                String comment_receiverID = node.getProvider().getUserID();

                FirebaseDatabase.getInstance().getReference("All_event").child(onCheckingEventID).child("received_comment").child(comment_receiverID).child("comment").setValue(comment);
                FirebaseDatabase.getInstance().getReference("All_event").child(onCheckingEventID).child("received_comment").child(comment_receiverID).child("received_rating").setValue(received_rating);
                //for user to retrieve
                FirebaseDatabase.getInstance().getReference("Comment_rating").child(comment_receiverID).child(onCheckingEventID).child("comment").setValue(comment);
                FirebaseDatabase.getInstance().getReference("Comment_rating").child(comment_receiverID).child(onCheckingEventID).child("received_rating").setValue(received_rating);

                //update the rating in user dataset
                for(int i=0; i< allUsers.size();i++){
                    if(comment_receiverID.compareTo(allUsers.get(i).getUserID())==0){
                        System.out.println("Item provider "+comment_receiverID+" received "+received_rating);
                        String rating = allUsers.get(i).getRating();
                        System.out.println(allUsers.get(i).getUserID()+ " original rating is "+rating);
                        int newRating = Math.round((received_rating + Integer.parseInt(rating))/2);
                        System.out.println("New Rating "+newRating);
                        FirebaseDatabase.getInstance().getReference("user").child(comment_receiverID).child("rating").setValue(newRating+"");
                        FirebaseDatabase.getInstance().getReference("All_event").child(onCheckingEventID).child("participants").child(node.getReceiver().getUserID()).child("status").setValue("Gave rating");
                        FirebaseDatabase.getInstance().getReference("All_event").child(onCheckingEventID).child("participants").addValueEventListener(checkGivenRating);
                        break;
                    }
                }
                break;
            }
            node = node.getNext();
        }

    }


}
