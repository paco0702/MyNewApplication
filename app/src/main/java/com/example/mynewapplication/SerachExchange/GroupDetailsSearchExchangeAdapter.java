package com.example.mynewapplication.SerachExchange;

import static com.example.mynewapplication.IndexActivity.FLAT_FOR_ItemsDetail;
import static com.example.mynewapplication.IndexActivity.selectedItem;

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

import com.example.mynewapplication.LLS;
import com.example.mynewapplication.R;
import com.example.mynewapplication.ShowItems.showItemDetailActivity;

import java.util.List;

public class GroupDetailsSearchExchangeAdapter extends ArrayAdapter<LLS.LLSNode> {
    private static final String TAG = "Search exchange Adapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    public static class GroupDetailsSEListViewHolder {
        TextView provider_name;
        TextView provider_email;
        TextView provider_phone;
        TextView receiver_name;
        TextView receiver_email;
        TextView receiver_phone;
        TextView item_name;
        TextView item_ID;
        TextView item_category;
        Button groupInformation;
    }

    public GroupDetailsSearchExchangeAdapter(@NonNull Context context, int resource, @NonNull List<LLS.LLSNode> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        GroupDetailsSEListViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new GroupDetailsSearchExchangeAdapter.GroupDetailsSEListViewHolder();

            holder.provider_name = (TextView)convertView.findViewById(R.id.Provider_name);
            holder.provider_email = (TextView)convertView.findViewById(R.id.Provider_email);
            holder.provider_phone = (TextView)convertView.findViewById(R.id.Provider_number);
            holder.receiver_name= (TextView)convertView.findViewById(R.id.Receiver_Name);
            holder.receiver_email= (TextView)convertView.findViewById(R.id.Receiver_Email);
            holder.receiver_phone= (TextView)convertView.findViewById(R.id.Receiver_Phone);
            holder.item_name= (TextView)convertView.findViewById(R.id.Item_name);
            holder.item_ID= (TextView)convertView.findViewById(R.id.Item_ID);
            holder.item_category= (TextView)convertView.findViewById(R.id.Item_Category);
            holder.groupInformation =(Button)convertView.findViewById(R.id.item_detail_button);

            result = convertView;
            convertView.setTag(holder);
        }
        else{
            holder = (GroupDetailsSEListViewHolder) convertView.getTag();
            result = convertView;
        }



        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.fui_slide_in_right:R.anim.fui_slide_in_right );

        result.startAnimation(animation);
        lastPosition = position;


        holder.provider_name.setText(getItem(position).getProvider().getUserName());
        holder.provider_email.setText(getItem(position).getProvider().getEmail());
        holder.provider_phone.setText(getItem(position).getProvider().getPhoneNum());

        holder.receiver_name.setText(getItem(position).getReceiver().getUserName());
        holder.receiver_email.setText(getItem(position).getReceiver().getEmail());
        holder.receiver_phone.setText(getItem(position).getReceiver().getPhoneNum());

        holder.item_name.setText(getItem(position).getExchange_item().getName());
        holder.item_ID.setText(getItem(position).getExchange_item().getItemsID());
        holder.item_category.setText(getItem(position).getExchange_item().getCategory());

        holder.groupInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAT_FOR_ItemsDetail = "Non_Item_Pool";
                selectedItem = getItem(position).getExchange_item();
                Intent intent = new Intent(mContext, showItemDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }
}
