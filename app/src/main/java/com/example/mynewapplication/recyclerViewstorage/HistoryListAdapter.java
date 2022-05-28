package com.example.mynewapplication.recyclerViewstorage;

import static com.example.mynewapplication.IndexActivity.FLAT_FOR_GroupDetail;
import static com.example.mynewapplication.IndexActivity.selectedGroup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mynewapplication.GroupDetailActivity;
import com.example.mynewapplication.LLS;
import com.example.mynewapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryListAdapter extends ArrayAdapter<LLS> {
    private static final String TAG = "HistoryListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private ArrayList<String> eventKeyList;

    private DatabaseReference realTimeDatabaseForUploadingExchangeEventProcessing =
            FirebaseDatabase.getInstance().getReference("Exchange_event/processing");
    private DatabaseReference realTimeDatabasePathForAllEvent = FirebaseDatabase.getInstance().getReference("All_event");

    ValueEventListener checkEvent = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            boolean AllAgree = false;
            for(DataSnapshot ds: snapshot.getChildren()){
                System.out.println("User id is" + ds.getKey());
                if(ds.child("status").getValue().toString().compareTo("Accepted")==0) {
                    AllAgree = true;
                    System.out.println("Changed to true");
                }else{
                    AllAgree = false;
                    System.out.println("Changed to false");
                }

            }
            System.out.println("Eventually it is "+AllAgree);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public static class ProcessingListViewHolder {
        TextView sender_user_name;
        TextView eventID;
        Button groupInformation;
    }

    public HistoryListAdapter(@NonNull Context context, int resource, @NonNull List<LLS> objects, ArrayList<String> eventKeyList) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.eventKeyList = eventKeyList;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        //get the persons information
        String eventID = this.eventKeyList.get(position);

        String senderUserName = getItem(position).getHead().getReceiver().getUserName();
        String senderUserID = getItem(position).getHead().getReceiver().getUserID();

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ProcessingListViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ProcessingListViewHolder();
            holder.sender_user_name = (TextView)convertView.findViewById(R.id.sender_user_name_processing);
            holder.eventID = (TextView)convertView.findViewById(R.id.event_id_processing);
            holder.groupInformation = (Button)convertView.findViewById(R.id.group_information_processing);
            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ProcessingListViewHolder) convertView.getTag();
            result = convertView;
        }



        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.fui_slide_in_right:R.anim.fui_slide_in_right );

        result.startAnimation(animation);
        lastPosition = position;

        holder.sender_user_name.setText(senderUserName);
        holder.eventID.setText(eventID);
        holder.groupInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGroup =  getItem(position);
                FLAT_FOR_GroupDetail = "History";
                Intent intent = new Intent(mContext, GroupDetailActivity.class);
                mContext.startActivity(intent);
            }
        });

        /*
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO remove from pending list
                realTimeDatabaseForUploadingExchangeEventProcessing.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String event_id = eventID;
                        LLS.LLSNode node = getItem(position).getHead();
                        int count = 0;
                        while(node!=null){  // processing for who start the evnet , already accpet becasue they start the event
                            realTimeDatabaseForUploadingExchangeEventProcessing.child(theCurrentLoginUser.getUserID()).child(event_id).child(""+(count+1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                            realTimeDatabaseForUploadingExchangeEventProcessing.child(theCurrentLoginUser.getUserID()).child(event_id).child(""+(count+1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                            realTimeDatabaseForUploadingExchangeEventProcessing.child(theCurrentLoginUser.getUserID()).child(event_id).child(""+(count+1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());
                            count++;
                            node = node.getNext();
                        }
                        // then remove the same event in pending
                        FirebaseDatabase.getInstance().getReference("Exchange_event/pending").child(theCurrentLoginUser.getUserID()).child(event_id).removeValue();
                        //after remove , out the cord
                        Animation animation = AnimationUtils.loadAnimation(mContext,
                                (position ==position) ? R.anim.fui_slide_in_right:R.anim.fui_slide_in_right );

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
        */
        return convertView;
    }

}
