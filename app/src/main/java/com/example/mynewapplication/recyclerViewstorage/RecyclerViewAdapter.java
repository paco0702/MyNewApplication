package com.example.mynewapplication.recyclerViewstorage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.Items;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerView list items ";


    // vars
    //two arrays list
    private ArrayList<Items> itemsList = new ArrayList<>();
    private Context mContext;
    private StorageReference mStorageRef;

    public RecyclerViewAdapter( Context mContext, ArrayList<String> mNames, ArrayList<String> mImageUrls) {
        this.mContext = mContext;
    }

    public RecyclerViewAdapter( Context mContext, ArrayList <Items> itemsList) {
        this.itemsList = itemsList;
        //this.mNames = mNames;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");

        // get the view and return it
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent,false);
        // return the new viewholder object
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //bind the data to individual list of items
        Log.d(TAG, "onBindViewHolder: called.");
        //do the image
       // System.out.println(holder.image.getId());
        System.out.println("Position "+position+" items ID "+itemsList.get(position).getItemsID());


        if(itemsList.get(position).getPathForImagesPictures()!=null){
            mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference()// always get the first picture
                    .child(itemsList.get(position).getPathForImagesPictures().get(0));
            try {
                File localfile = File.createTempFile("tempfile", ".jpg");
                mStorageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        holder.image.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            //Glide.with(mContext)
                  //  .asBitmap()
                  //  .load(itemsList.get(position).getPathForImagesPictures().get(1))
                  //  .into(holder.image); // if reference to the image, we need to reference the view holder
        }else{
            holder.image.setImageResource(R.drawable.resource_default);
        }

        holder.name.setText(itemsList.get(position).getName());

        holder.image.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on an image: "+itemsList.get(position).getName());
                Toast.makeText(mContext, itemsList.get(position).getName(), Toast.LENGTH_SHORT).show();

                // after clicking, get detail of the items
            }  // enable to click
        });
    }

    @Override
    public int getItemCount() {
        //return the number of items
        return itemsList.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView image;
        TextView name;

        public ViewHolder (View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.image01);
            System.out.println("In view holder constructor " +image.getId());
            name = itemView.findViewById(R.id.name);
        }
    }
}

