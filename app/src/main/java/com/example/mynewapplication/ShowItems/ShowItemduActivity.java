package com.example.mynewapplication.ShowItems;

import android.os.Bundle;

import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.recyclerViewstorage.RecyclerViewAdapter;
import com.example.mynewapplication.recyclerViewstorage.RecyclerViewListItemAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import java.util.ArrayList;

import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;


public class ShowItemduActivity extends AppCompatActivity {

    // list for storing items information
    private ArrayList<String > itemsNames = new ArrayList<>();
    private ArrayList<String> itemsImageUrls = new ArrayList<>();
    private ArrayList<String> ownerNames = new ArrayList<>();
    private ArrayList<String> itemsCategories = new ArrayList<>();
    private ArrayList<String> ownerId = new ArrayList<>();
    private ArrayList<String> itemsId = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference realTimeDatabaseForRerievingItems;

    // for example
    private ArrayList<String > mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    //for retrieving item
    private StorageReference mStorageRef; // to store the upload picture
    private ArrayList<Items> listOfUsersItem;
    //for retrieveing item 's first picture
    private static final String TAG = "RecyclerViewAdapter";

    private ValueEventListener getUserInfoEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println("Function of get items information with the login user id: "+currentUser.getUid());
            for (DataSnapshot ds : snapshot.getChildren()) {

                Items items = ds.getValue(Items.class);
                Items insertToList = new Items();
                System.out.println("retrieved items id "+ ds.getKey());
                System.out.println("retrieved items categories "+ items.getCategory());
                for (int i =0; i<items.getPathForImagesPictures().size();i++){
                    System.out.println("retreived items path of picture "+items.getPathForImagesPictures().get(i));
                }
                insertToList.setItemsID(ds.getKey());
                insertToList.setCategory(items.getCategory());
                insertToList.setOwner(items.getOwner());
                insertToList.setName(items.getName());
                insertToList.setPathForImagesPictures(items.getPathForImagesPictures());
                insertToList.setValue(items.getValue());
                insertToList.setOwnerID(items.getOwnerID());
                insertToList.setOwnerEmail(items.getOwnerEmail());
                listOfUsersItem.add(insertToList);
            }
            getItems();
            //getImagesInformation();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item);

        mAuth = FirebaseAuth.getInstance();
        System.out.println("Get the current user ... ");
        getCurrentUser();

        System.out.println("List items page: the current user: "+ currentUser.getUid());
        System.out.println("Capture the uploaded items information of the user... ");

        // for retreived items to stored the return Items
        listOfUsersItem = new ArrayList<>();

        // get the login user information

        realTimeDatabaseForRerievingItems = FirebaseDatabase.getInstance().getReference("Items_Users").child(currentUser.getUid().toString());
        realTimeDatabaseForRerievingItems = realTimeDatabaseForRerievingItems.getRef();
        System.out.println("realTimeDatabaseForRerievingItems "+realTimeDatabaseForRerievingItems.getKey().toString());
        realTimeDatabaseForRerievingItems.addValueEventListener(getUserInfoEventListener);


        // store location reference
        mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference();

    }


    public void getCurrentUser(){
        currentUser = mAuth.getCurrentUser();
    }



    private void getItems(){
        // TODO return the items information for items Arraylist

        // get the storage path for the item images
        //StorageReference storageReference = mStorageRef.child("images/users/" + userID + "/" + name + i + ".jpg");
        String userID = theCurrentLoginUser.getUserID();
        System.out.println("UserId "+userID);

        // return the item information for array list
        System.out.println("insert the information into array list");
        for (int i=0; i<listOfUsersItem.size(); i++){
            System.out.println("The items id is :　" +listOfUsersItem.get(i).getItemsID());
            // there is some error with the value return so change the assignment here
            itemsImageUrls.add(listOfUsersItem.get(i).getPathForImagesPictures().get(0)); // get the frist image path of each item of that user
            itemsNames.add(listOfUsersItem.get(i).getName());
            ownerNames.add(listOfUsersItem.get(i).getOwner());
            ownerId.add(listOfUsersItem.get(i).getOwnerID());
            itemsCategories.add(listOfUsersItem.get(i).getCategory());
            itemsId.add(listOfUsersItem.get(i).getItemsID());

            System.out.println("Items id is "+ itemsId.get(i));
            System.out.println("Items imageUrl "+ itemsImageUrls.get(i));
            System.out.println("Items name "+ itemsNames.get(i)); // wrong took the User name
            System.out.println("owner name "+ ownerNames.get(i)); // wrongly is the owner id
            System.out.println("Items catgories "+ itemsCategories.get(i));
            System.out.println("items owner id "+ ownerId.get(i));
        }

        initRecyclerViewNew();
    }



    private void initRecyclerViewNew(){
        Log.d(TAG, "initRecyclcerView: init Recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_showUserItems);  // that is going to the view content_scrolling xml


        recyclerView.setLayoutManager(layoutManager);
        // new Recycler View Adpater class                   // put the view and name of the image and imageurls
        // the adapter will adpat the xml layout with the card only for item list
        //RecyclerViewListItemAdapter adapter = new RecyclerViewListItemAdapter(this, itemsNames, ownerNames, ownerId,itemsId, itemsCategories, itemsImageUrls); // set the images as adapter
        RecyclerViewListItemAdapter adapter = new RecyclerViewListItemAdapter(this, listOfUsersItem); // set the images as adapter
        recyclerView.setAdapter(adapter);
    }

}