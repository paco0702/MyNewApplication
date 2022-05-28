package com.example.mynewapplication.recyclerViewstorage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.ShowItems.showItemDetailActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mynewapplication.IndexActivity.FLAT_FOR_ItemsDetail;
import static com.example.mynewapplication.IndexActivity.selectedItem;

public class RecyclerViewListItemAdapter extends RecyclerView.Adapter<RecyclerViewListItemAdapter.NewViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String > itemsNames = new ArrayList<>();
    private ArrayList<String > ownerNames = new ArrayList<>();
    private ArrayList<String > itemsCategories = new ArrayList<>();
    private ArrayList<String> itemsImageUrls = new ArrayList<>();
    private ArrayList<String> ownerId = new ArrayList<>();
    private ArrayList<String> itemsId = new ArrayList<>();
    private ArrayList<Items> items = new ArrayList<>();
    private Context mContext;
    private StorageReference mStorageRef;

    public RecyclerViewListItemAdapter(Context mContext,  ArrayList<String > itemsNames,  ArrayList<String > ownerNames, ArrayList<String>ownerId, ArrayList<String>itemsId, ArrayList<String> itemsCategories, ArrayList<String> itemsImageUrls){
        this.itemsNames =  itemsNames;
        this.ownerNames = ownerNames;
        this.ownerId = ownerId;
        this.itemsCategories = itemsCategories;
        this.itemsImageUrls = itemsImageUrls;
        this.itemsId = itemsId;
        this.mContext = mContext;
    }

    public RecyclerViewListItemAdapter(Context mContext,  ArrayList<Items> itemsList ){
        this.items = itemsList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //## locate where is the card view ****
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_user_info_layout, parent,false);

        return new NewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        //do the image
        System.out.println("position "+ position);
        //System.out.println("owner id: "+ ownerId.get(position));
        System.out.println("owner id: "+ items.get(position).getOwnerID());
        //mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference().child(itemsImageUrls.get(0)); // always get the first picture

        //retreiving the image
        if(items.get(position).getPathForImagesPictures()!=null){
            System.out.println("itemsImageUrls.get(position) "+items.get(position).getPathForImagesPictures().get(0));
            mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference().child(items.get(position).getPathForImagesPictures().get(0)); // always get the first picture
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
        }


        //String fileName = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference().child(itemsImageUrls.get(position)).getName();
        //System.out.println("file name is "+ fileName);
        /*
        Glide.with(mContext)
                .asBitmap()
                .load(itemsImageUrls.get(position))
                .into(holder.item_image); // if reference to the image, we need to reference the view holder
        */
        holder.item_name.setText(items.get(position).getName());
        holder.name_of_category.setText(items.get(position).getCategory());
        holder.owner_name.setText(items.get(position).getOwner());
        holder.item_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(mContext, showItemDetailActivity.class);
               //Items localSelectedItems = new Items();
                Items localSelectedItems = items.get(position);
               //localSelectedItems.setName(itemsNames.get(position));
               //localSelectedItems.setCategory(itemsCategories.get(position));
               //localSelectedItems.setOwner(ownerNames.get(position));
               //localSelectedItems.setPathForImagesPictures(itemsImageUrls);
               //localSelectedItems.setItemsID(itemsId.get(position));
               //localSelectedItems.setOwnerID(ownerId.get(position));
              // System.out.println("select items information is item id "+ itemsId.get(position)+" owner id "+ ownerId.get(position));
                FLAT_FOR_ItemsDetail = "Item_profile";
                System.out.println("select items information is item id "+ items.get(position).getItemsID()+" owner id "+ items.get(position).getOwnerID());
               selectedItem = localSelectedItems; //assign selected Items
               mContext.startActivity(intent);
            }  // enable to click

        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class NewViewHolder extends RecyclerView.ViewHolder{
        CircleImageView item_image;
        TextView item_name;
        TextView name_of_category;
        TextView owner_name;
                   // that is the xml view in the layout
        public NewViewHolder (View itemView){
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image_view);
            System.out.println("In view holder constructor " +item_image.getId());
            item_name = itemView.findViewById(R.id.name_of_the_item);
            name_of_category =itemView.findViewById(R.id.name_of_category);
            owner_name = itemView.findViewById(R.id.owner_name);
        }
    }
}
