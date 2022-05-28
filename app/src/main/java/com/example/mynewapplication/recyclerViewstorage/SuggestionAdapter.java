package com.example.mynewapplication.recyclerViewstorage;

import static com.example.mynewapplication.FragmentClass.ProfileFragment.selectedUser;
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
import androidx.fragment.app.FragmentActivity;

import com.example.mynewapplication.FragmentClass.ProfileFragment;
import com.example.mynewapplication.GroupDetailActivity;
import com.example.mynewapplication.LLS;
import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestionAdapter extends ArrayAdapter<LLS> {
    private static final String TAG = "SuggestionListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private ArrayList<LLS> outputGroupList;

    public static class SuggestionListViewHolder {
        TextView category_name;
        TextView involvedUser1;
        TextView involvedUser2;
        TextView involvedUser3;
        TextView involvedUser4;
        TextView involvedUser5;
        TextView involvedUser6;
        Button detailsBtn;
    }

    public SuggestionAdapter(@NonNull Context context, int resource, @NonNull ArrayList<LLS> objects, ArrayList<LLS> outputGroupList) {
        super(context, resource, objects);
        mContext =context;
        mResource = resource;
        this.outputGroupList =outputGroupList;
    }


    public View getView(int position, View convertView, ViewGroup parent){

        //get the persons information
        String category = getItem(position).getTargetCategory();
        // String imgUrl = getItem(position).getImgURL();

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        SuggestionListViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new SuggestionListViewHolder();
            holder.category_name = (TextView)convertView.findViewById(R.id.name_of_the_wish_item);
            holder.involvedUser1 = (TextView)convertView.findViewById(R.id.involvedUser1);
            holder.involvedUser2 = (TextView)convertView.findViewById(R.id.involvedUser2);
            holder.involvedUser3 = (TextView)convertView.findViewById(R.id.involvedUser3);
            holder.involvedUser4 = (TextView)convertView.findViewById(R.id.involvedUser4);
            holder.involvedUser5 = (TextView)convertView.findViewById(R.id.involvedUser5);
            holder.involvedUser6 = (TextView)convertView.findViewById(R.id.involvedUser6);
            holder.detailsBtn = (Button)convertView.findViewById(R.id.group_details_button);
            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (SuggestionListViewHolder) convertView.getTag();
            result = convertView;
        }



        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.fui_slide_in_right:R.anim.fui_slide_in_right );

        result.startAnimation(animation);
        lastPosition = position;

        holder.category_name.setText(category);
        String [] storeUserID = new String[6];
        //LLS.LLSNode node = getItem(position).getHead();
        LLS.LLSNode node = this.outputGroupList.get(position).getHead();
        int i =0;
        while(node!=null){
            //storeUserID[i] =node.getWishUser();
            storeUserID[i] =node.getProvider().getUserName();
            System.out.println(storeUserID[i]);
            node = node.getNext();
            i++;
        }
        if(storeUserID[0]!=null){
            holder.involvedUser1.setText(storeUserID[0]);
        }
        if(storeUserID[1]!=null){
            holder.involvedUser2.setText(storeUserID[1]);
        }
        if(storeUserID[2]!=null){
            holder.involvedUser3.setText(storeUserID[2]);
        }
        if(storeUserID[3]!=null){
            holder.involvedUser4.setText(storeUserID[3]);
        }
        if(storeUserID[4]!=null){
            holder.involvedUser5.setText(storeUserID[4]);
        }
        if(storeUserID[5]!=null){
            holder.involvedUser6.setText(storeUserID[5]);
        }

        holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGroup = outputGroupList.get(position);
                FLAT_FOR_GroupDetail = "Suggestion";
                Intent intent = new Intent(mContext, GroupDetailActivity.class);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

}
