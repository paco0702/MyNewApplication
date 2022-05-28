package com.example.mynewapplication.recyclerViewstorage;

import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.mynewapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NameDisplayAdapter extends ArrayAdapter<String> {
    private static final String TAG = "ListNameAdapter";
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wish_list").child(theCurrentLoginUser.getUserID());

    public static class  NameDisplayViewHolder {
        TextView name_of_wish_item;
        FloatingActionButton remove_item_button;

    }

    public NameDisplayAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        //get the persons information
        String name = getItem(position);
        // String imgUrl = getItem(position).getImgURL();

        //create the view result for showing the animation
        final View result;
        //ViewHolder object
        NameDisplayViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new  NameDisplayViewHolder();
            holder.name_of_wish_item = (TextView)convertView.findViewById(R.id.name_of_item);

            //holder.image = (ImageView) convertView.findViewById(R.id.image);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (NameDisplayViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.fui_slide_in_right:R.anim.fui_slide_in_right );

        result.startAnimation(animation);
        lastPosition = position;

        holder.name_of_wish_item.setText(name);

        return convertView;
    }


}
