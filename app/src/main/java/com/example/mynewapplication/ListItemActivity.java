package com.example.mynewapplication;

import android.os.Bundle;

import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.recyclerViewstorage.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import android.view.View;

import java.util.ArrayList;

import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;


public class ListItemActivity extends AppCompatActivity {

    private ArrayList<String > itemsNames = new ArrayList<>();
    private ArrayList<String> itemsImageUrls = new ArrayList<>();
    private ArrayList<String> ownerNames = new ArrayList<>();
    private ArrayList<String> itemsCategories = new ArrayList<>();

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
                insertToList.setOwner(items.getOwnerID());
                insertToList.setName(items.getOwner());
                insertToList.setPathForImagesPictures(items.getPathForImagesPictures());
                insertToList.setValue(items.getValue());
                insertToList.setOwnerEmail(items.getOwnerEmail());
                insertToList.setOwnerID(items.getOwnerID());
                insertToList.setStatus(items.getStatus());
                listOfUsersItem.add(insertToList);
            }

            //getItems();
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
        //setContentView(R.layout.);

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


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        //toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //getImages();

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
            itemsImageUrls.add(listOfUsersItem.get(i).getPathForImagesPictures().get(0)); // get the frist image path of each item of that user
            itemsNames.add(listOfUsersItem.get(i).getName());
            ownerNames.add(listOfUsersItem.get(i).getOwner());
            itemsCategories.add(listOfUsersItem.get(i).getCategory());
            System.out.println("Items imageUrl "+ itemsImageUrls.get(i));
            System.out.println("Items name "+ itemsNames.get(i));
            System.out.println("owner name "+ ownerNames.get(i));
            System.out.println("Items catgories "+ itemsCategories.get(i));
        }

        initRecyclerViewNew();
    }



    private void initRecyclerViewNew(){
        Log.d(TAG, "initRecyclcerView: init Recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);  // that is going to the view content_scrolling xml
        recyclerView.setLayoutManager(layoutManager);
        // new Recycler View Adpater class                   // put the view and name of the image and imageurls
        // the adapter will adpat the xml layout with the card only for item list
        //RecyclerViewListItemAdapter adapter = new RecyclerViewListItemAdapter(this, itemsNames, ownerNames, itemsCategories, itemsImageUrls); // set the images as adapter
        //recyclerView.setAdapter(adapter);

    }

    private void getImagesInformation(){ // get image should be something from the clients

        // add the image
        // TODO　add the user to the recycle view instead

        //Log.d(TAG, "initImageBitmaps: preparing bitmaps:");

        //  mImageUrls.add("https://cl.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        //   mNames.add("Havasu Falls");

        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mNames.add("Tronheim");

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mNames.add("Portugal");

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        mNames.add("Rocky Mountain National Park");

        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add("Mahahual");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mNames.add("Frozen Lake");

        mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg");
        mNames.add("White Sands Desert");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
        mNames.add("Austrailia");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mNames.add("Washington");

        initRecyclerView();
    }

    // locate where to put the picture into which view
    private void initRecyclerView(){
        Log.d(TAG, "initRecyclcerView: init Recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView); // android:id="@+id/recyclerView"

        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImageUrls); // set the images as adapter
        recyclerView.setAdapter(adapter);
    }



}