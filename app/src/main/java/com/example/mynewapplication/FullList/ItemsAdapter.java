package com.example.mynewapplication.FullList;

import static com.example.mynewapplication.IndexActivity.FLAT_FOR_ItemsDetail;
import static com.example.mynewapplication.IndexActivity.selectedItem;

import android.content.Context;
import android.content.Intent;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemsAdapter  extends ArrayAdapter<Items> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private StorageReference mStorageRef;

    //item_of_user_info_layout
    public static class ItemsListViewHolder {
        CircleImageView itemsImage;
        TextView category;
        TextView owner;
        TextView name;
    }

    public ItemsAdapter (@NonNull Context context, int resource, @NonNull List<Items> objects){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        ItemsListViewHolder holder;
        //create the view result for showing the animation
        final View result;

        String itemsName = getItem(position).getName();
        String category = getItem(position).getCategory();
        String ownerName = getItem(position).getOwner();

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ItemsListViewHolder();
            holder.itemsImage = (CircleImageView)convertView.findViewById(R.id.item_image_view);
            holder.category = (TextView)convertView.findViewById(R.id.name_of_category);
            holder.owner = (TextView) convertView.findViewById(R.id.owner_name);
            holder.name = (TextView) convertView.findViewById(R.id.name_of_the_item);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ItemsListViewHolder) convertView.getTag();
            result = convertView;
        }



        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.fui_slide_in_right:R.anim.fui_slide_in_right );

        result.startAnimation(animation);
        lastPosition = position;

        holder.category.setText(category);
        holder.name.setText(itemsName);
        holder.owner.setText(ownerName );
        //retreiving the image
        if(getItem(position).getPathForImagesPictures()!=null){
            //System.out.println("itemsImageUrls.get("+position+") "+getItem(position).getPathForImagesPictures().get(0));
            mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference().child(getItem(position).getPathForImagesPictures().get(0)); // always get the first picture
            try {
                File localfile = File.createTempFile("tempfile", ".jpg");
                mStorageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        holder.itemsImage.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            holder.itemsImage.setImageResource(R.drawable.resource_default);
        }


        holder.itemsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: clicked on an image: "+itemsNames.get(position));
                // TODO click into details page
                //Toast.makeText(mContext, itemsNames.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, showItemDetailActivity.class);
                //Items localSelectedItems = new Items();
                Items localSelectedItems = getItem(position);
                FLAT_FOR_ItemsDetail = "Item_Pool";
                //localSelectedItems.setName(itemsNames.get(position));
                //localSelectedItems.setCategory(itemsCategories.get(position));
                //localSelectedItems.setOwner(ownerNames.get(position));
                //localSelectedItems.setPathForImagesPictures(itemsImageUrls);
                //localSelectedItems.setItemsID(itemsId.get(position));
                //localSelectedItems.setOwnerID(ownerId.get(position));
                // System.out.println("select items information is item id "+ itemsId.get(position)+" owner id "+ ownerId.get(position));
                System.out.println("select items information is item id "+ getItem(position).getItemsID()+" owner id "+ getItem(position).getOwnerID());
                selectedItem = localSelectedItems; //assign selected Items
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
