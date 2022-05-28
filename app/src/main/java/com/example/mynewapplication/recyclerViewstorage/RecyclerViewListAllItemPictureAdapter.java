package com.example.mynewapplication.recyclerViewstorage;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewListAllItemPictureAdapter extends RecyclerView.Adapter<RecyclerViewListAllItemPictureAdapter.ViewHolder_showItemDetails>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> itemsImageUrls = new ArrayList<>();
    private Context mContext;
    private StorageReference mStorageRef;


    public RecyclerViewListAllItemPictureAdapter( Context mContext, ArrayList<String> mImageUrls) {
        this.itemsImageUrls = mImageUrls;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder_showItemDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent,false);
        return new ViewHolder_showItemDetails(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_showItemDetails holder, int position) {
        //bind the data to individual list of items
        Log.d(TAG, "onBindViewHolder: called.");
        //do the image

        mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference().child(itemsImageUrls.get(position)); // always get the first picture
        try {
            File localfile = File.createTempFile("tempfile", ".jpg");
            mStorageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    holder.item_image.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        Glide.with(mContext)
                .asBitmap()
                .load(itemsImageUrls.get(position))
                .into(holder.item_image); // if reference to the image, we need to reference the view holder
        */

        holder.item_image.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on an image: "+itemsImageUrls.get(position));
                Toast.makeText(mContext, itemsImageUrls.get(position), Toast.LENGTH_SHORT).show();
            }  // enable to click
        });
    }


    @Override
    public int getItemCount() {
        //return the number of items
        return itemsImageUrls.size();
    }

    public static class ViewHolder_showItemDetails extends  RecyclerView.ViewHolder{
        CircleImageView item_image;


        public ViewHolder_showItemDetails (View itemView){
            super(itemView);
            item_image = itemView.findViewById(R.id.image01);
            System.out.println("In view holder constructor " +item_image.getId());

        }
    }
}

