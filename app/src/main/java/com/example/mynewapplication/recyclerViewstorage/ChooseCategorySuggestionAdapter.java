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
import androidx.wear.activity.ConfirmationActivity;

import com.example.mynewapplication.GroupDetailActivity;
import com.example.mynewapplication.LLS;
import com.example.mynewapplication.R;
import com.example.mynewapplication.SerachExchange.GroupDetailSearchExchangeActivity;

import java.util.ArrayList;
import java.util.List;

public class ChooseCategorySuggestionAdapter extends ArrayAdapter<LLS> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private String clicked_category;
    public static class ChooseCategoryListViewHolder {
        TextView category_name;
        TextView involvedUser1;
        TextView involvedUser2;
        Button detailsBtn;
    }

    public ChooseCategorySuggestionAdapter(@NonNull Context context, int resource, @NonNull List<LLS> objects, String clicked_category) {
        super(context, resource, objects);
        mContext =context;
        mResource = resource;
        this.clicked_category = clicked_category;
    }

    public View getView(int position, View convertView, ViewGroup parent){


        //get the persons information
        String category = this.clicked_category;
        // String imgUrl = getItem(position).getImgURL();

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        SuggestionAdapter.SuggestionListViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new SuggestionAdapter.SuggestionListViewHolder();
            holder.category_name = (TextView)convertView.findViewById(R.id.name_of_the_wish_item);
            holder.involvedUser1 = (TextView)convertView.findViewById(R.id.involvedUser1);
            holder.involvedUser2 = (TextView)convertView.findViewById(R.id.involvedUser2);
            holder.detailsBtn = (Button)convertView.findViewById(R.id.group_details_button);
            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (SuggestionAdapter.SuggestionListViewHolder) convertView.getTag();
            result = convertView;
        }



        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.fui_slide_in_right:R.anim.fui_slide_in_right );

        result.startAnimation(animation);
        lastPosition = position;

        holder.category_name.setText(category);
        String [] storeUserID = new String[6];
        //LLS.LLSNode node = getItem(position).getHead();
        LLS.LLSNode node = getItem(position).getHead();
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

        holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGroup = getItem(position);
                FLAT_FOR_GroupDetail = "ChooseCategory";
                Intent intent = new Intent(mContext, GroupDetailSearchExchangeActivity.class);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

}
