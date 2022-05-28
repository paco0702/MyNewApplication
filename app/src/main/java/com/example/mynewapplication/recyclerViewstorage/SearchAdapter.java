package com.example.mynewapplication.recyclerViewstorage;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mynewapplication.entities.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends ArrayAdapter<User> {
    private static final String TAG = "UserListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    public static class FriendListViewHolder {
        CircleImageView friend_image;
        TextView friend_name;
    }

    public SearchAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


}
