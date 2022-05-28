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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PendListAdapter extends ArrayAdapter<LLS> {
    private static final String TAG = "PendingListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private ArrayList<String> eventKeyList;
    private DatabaseReference realTimeDatabaseForUploadingExchangeEventProcessing =
            FirebaseDatabase.getInstance().getReference("Exchange_event/processing");
    private DatabaseReference realTimeDatabasePathForAllEvent = FirebaseDatabase.getInstance().getReference("All_event");


    public String onCheckingEventID;  // let checkEvent function can use

    // check all user agree


    public static class PendingListViewHolder {
        TextView sender_user_name;
        TextView eventID;
        Button groupInformation;
    }
    public PendListAdapter(@NonNull Context context, int resource, @NonNull List<LLS> objects, ArrayList<String> eventKeyList) {
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
        PendingListViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new PendingListViewHolder();
            holder.sender_user_name = (TextView)convertView.findViewById(R.id.sender_user_name);
            holder.eventID = (TextView)convertView.findViewById(R.id.event_id);
            holder.groupInformation = (Button) convertView.findViewById(R.id.group_information_pending_button);
            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (PendingListViewHolder) convertView.getTag();
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
                FLAT_FOR_GroupDetail = "Pending";
                Intent intent = new Intent(mContext, GroupDetailActivity.class);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

}
