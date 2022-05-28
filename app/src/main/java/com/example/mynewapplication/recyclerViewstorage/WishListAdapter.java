package com.example.mynewapplication.recyclerViewstorage;

import static com.example.mynewapplication.FragmentClass.ProfileFragment.selectedUser;
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
import androidx.fragment.app.FragmentActivity;

import com.example.mynewapplication.FragmentClass.ProfileFragment;
import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WishListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "WishListAdapter";
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wish_list").child(theCurrentLoginUser.getUserID());

    public static class WishListViewHolder {
        TextView name_of_wish_item;
        FloatingActionButton remove_item_button;

    }

    public WishListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
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
        WishListViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new WishListViewHolder();
            holder.name_of_wish_item = (TextView)convertView.findViewById(R.id.name_of_the_wish_item);
            holder.remove_item_button = (FloatingActionButton)convertView.findViewById(R.id.floatingActionButton_remove_item);
            //holder.image = (ImageView) convertView.findViewById(R.id.image);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (WishListViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.fui_slide_in_right:R.anim.fui_slide_in_right );

        result.startAnimation(animation);
        lastPosition = position;

        holder.name_of_wish_item.setText(name);
        holder.remove_item_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle("Notification");
                builder.setMessage("Do you confirm to remove this item from wish list");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get the selected category
                                //Query query = ref.orderByChild(position+"").equalTo(name);

                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                           if(ds.getValue().toString().compareTo(name)==0){
                                               ds.getRef().removeValue();
                                               //refresh the page
                                               notifyDataSetChanged();
                                           }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return convertView;
    }


}
