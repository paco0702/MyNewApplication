package com.example.mynewapplication.recyclerViewstorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.mynewapplication.FragmentClass.ProfileFragment;
import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mynewapplication.FragmentClass.ProfileFragment.selectedUser;

public class UserListAdapter extends ArrayAdapter<User> {
    private static final String TAG = "FriendListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private StorageReference mStorageRef; // to store the upload picture

    public static class FriendListViewHolder {
        CircleImageView friend_image;
        TextView friend_name;
    }
    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        User currentUser = getItem(position);
        //get the persons information
        String name = getItem(position).getUserName();
       // String imgUrl = getItem(position).getImgURL();

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        FriendListViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new FriendListViewHolder();
            holder.friend_image = (CircleImageView)convertView.findViewById(R.id.user_image_view);
            holder.friend_name = (TextView)convertView.findViewById(R.id.name_of_the_friend);
            //holder.image = (ImageView) convertView.findViewById(R.id.image);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (FriendListViewHolder ) convertView.getTag();
            result = convertView;
        }



        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.fui_slide_in_right:R.anim.fui_slide_in_right );

        result.startAnimation(animation);
        lastPosition = position;

        holder.friend_name.setText(name);
        if(currentUser.getProfileImagePath()!=null && !currentUser.getProfileImagePath().equals("")){
            System.out.println("position "+position+" path "+getItem(position).getProfileImagePath());
            mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference().child(getItem(position).getProfileImagePath()); // always get the first picture
            try {
                File localfile = File.createTempFile("tempfile", ".jpg");
                mStorageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        holder.friend_image.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            holder.friend_image.setImageResource(R.drawable.profile_default);

        }
        /*
        else{
            mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference().child("/profile_images/default/profile_default.jpg"); // always get the first picture
            try {
                File localfile = File.createTempFile("tempfile", ".jpg");
                mStorageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        holder.friend_image.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
*/
        holder.friend_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: clicked on an image: "+getItem(position).getUserName());
                // TODO click into details page
                //Toast.makeText(mContext, getItem(position).getUserName(), Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(mContext, ProfileFragment.class);
                selectedUser = new User (
                        getItem(position).getEmail(), getItem(position).getUserName() , getItem(position).getPhoneNum(), getItem(position).getBirthday(), getItem(position).getUserID());
                System.out.println("selected user id "+ getItem(position).getRating());
                selectedUser.setRating(getItem(position).getRating());
                selectedUser.setPreferCategories(getItem(position).getPreferCategories());
                selectedUser.setProfileImagePath(getItem(position).getProfileImagePath());
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).addToBackStack(null).commit();
            }
        });

        return convertView;
    }




}
