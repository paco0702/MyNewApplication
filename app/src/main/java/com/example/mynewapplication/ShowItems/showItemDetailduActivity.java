package com.example.mynewapplication.ShowItems;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.Participant;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.RecyclerViewListAllItemPictureAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mynewapplication.IndexActivity.FLAT_FOR_ItemsDetail;
import static com.example.mynewapplication.IndexActivity.allUsers;
import static com.example.mynewapplication.IndexActivity.selectedItem;
import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;


public class showItemDetailduActivity extends AppCompatActivity {

    private TextView itemName;
    private TextView ownerName;
    private TextView category;
    private TextView itemDescription;
    private TextView itemId;
    private CircleImageView profileItemImage;
    private Button addToWantedListButton;
    private Button startExchangeButton;
    private Button back;
    private Button deleteItem;

    // variable for item's image information
    private ArrayList<String> itemsNames;
    private ArrayList<String> itemsImageUrls;
    private static final String TAG = "RecyclerViewAdapter";

    private Items gottenItem;

    private StorageReference mStorageRef;
    private StorageReference imageRef;

    // for upload event
    private DatabaseReference realTimeDatabaseForUploadingExchangeEvent;
    private DatabaseReference realTimeDatabaseForUploadingExchangeEventProcessing;
    private DatabaseReference realTimeDatabaseForItems;
    private DatabaseReference realTimeDatabaseForItemsUser;
    private DatabaseReference realTimeDatabaseForAllEventRecord;
    private DatabaseReference realTimeDatabaseForAllUser;
    // spinner chosen items
    Items selectedItems;
    // for notification
    private DatabaseReference realTimeDatabaseForNotification;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item_detail);

        mProgressDialog = new ProgressDialog(this);
        mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference();

        itemName = (TextView) findViewById(R.id.item_detail_page_item_name);
        ownerName = (TextView) findViewById(R.id.item_detail_page_owner_name);
        category = (TextView) findViewById(R.id.item_detail_page_categories);
        itemDescription = (TextView) findViewById(R.id.item_detail_page_item_description);
        itemId = (TextView) findViewById(R.id.item_detail_page_item_id);
        profileItemImage = (CircleImageView) findViewById(R.id.item_image_view);

        // get the chosen Items
        gottenItem = selectedItem;
        setItemInformation(gottenItem);

        addToWantedListButton = (Button) findViewById(R.id.button_add_to_wanted_list);
        startExchangeButton = (Button) findViewById(R.id.button_send_request);
        back = (Button) findViewById(R.id.Items_detail_back);
        deleteItem = (Button) findViewById(R.id.button_delete_items);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBox = new AlertDialog.Builder(getApplicationContext());
                alertBox.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userID = gottenItem.getOwnerID();
                        String itemID = gottenItem.getItemsID();
                        System.out.println("Delete owner "+userID+" items "+itemID);
                        FirebaseDatabase.getInstance().getReference("Items_Users").child(userID).child(itemID).removeValue();
                        FirebaseDatabase.getInstance().getReference("Items").child(userID+"_"+itemID).removeValue();
                    }
                });

                alertBox.setPositiveButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = alertBox.create();
                dialog.show();
            }
        });

        if(FLAT_FOR_ItemsDetail.compareTo("Item_Pool") ==0 ){
            setUpForUpload();
        }else if(FLAT_FOR_ItemsDetail.compareTo("Item_Pool")==0 ){
            setUpForOwner();
        }else{
            setUpForView();
        }


    }
    private void setUpForOwner(){
        addToWantedListButton.setEnabled(false);
        startExchangeButton.setEnabled(false);
        addToWantedListButton.setVisibility(View.INVISIBLE);
        startExchangeButton.setVisibility(View.INVISIBLE);

    }

    private void setUpForView(){
        addToWantedListButton.setEnabled(false);
        startExchangeButton.setEnabled(false);
        addToWantedListButton.setVisibility(View.INVISIBLE);
        startExchangeButton.setVisibility(View.INVISIBLE);

    }

    private void setUpForUpload(){
        // for upload location
        realTimeDatabaseForUploadingExchangeEvent = FirebaseDatabase.getInstance().getReference("Exchange_event/pending");
        realTimeDatabaseForUploadingExchangeEventProcessing = FirebaseDatabase.getInstance().getReference("Exchange_event/processing");
        realTimeDatabaseForItems = FirebaseDatabase.getInstance().getReference("Items");;
        realTimeDatabaseForItemsUser = FirebaseDatabase.getInstance().getReference("Items_Users");;
        realTimeDatabaseForAllEventRecord = FirebaseDatabase.getInstance().getReference("All_event");
        realTimeDatabaseForNotification = FirebaseDatabase.getInstance().getReference("Notification");

        addToWantedListButton.setEnabled(true);
        startExchangeButton.setEnabled(true);
        addToWantedListButton.setVisibility(View.VISIBLE);
        startExchangeButton.setVisibility(View.VISIBLE);

        startExchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mProgressDialog.setMessage("Request submitting.");
                //mProgressDialog.show();
                if(gottenItem.getStatus().compareTo("free")==0){
                    ChooseItemsToSend();
                }else{
                    ErrorMessage();
                }
            }
        });
    }

    private void ErrorMessage(){
        System.out.println("Items locked");
        mProgressDialog.dismiss();
        Toast.makeText(this,"The Items is locked for other transaction.",Toast.LENGTH_SHORT).show();
    }

    private void SelectItemsErrorMessage(){
        mProgressDialog.dismiss();
        Toast.makeText(this,"Please select an item to process",Toast.LENGTH_SHORT).show();
    }

    private void ChooseItemsToSend(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(showItemDetailduActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        alertDialog.setTitle("Choose an Item to send");

        //set up the spinner to show login user items for transaction
        Spinner items_spinner = (Spinner) mView.findViewById(R.id.spinner_items_selection);
        ArrayList<Items> ownerItem = theCurrentLoginUser.getOwnsItems();
        // output items name as arraylist
        ArrayList<String> itemsName = new ArrayList<>();
        itemsName.add("Please select an item");


        for(Items items: ownerItem){
            itemsName.add(items.getItemsID()+": "+items.getName());
        }
        items_spinner.setAdapter(new ArrayAdapter<>(showItemDetailduActivity.this,
                android.R.layout.simple_spinner_dropdown_item, itemsName));

        items_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Toast.makeText(getApplication(),
                            "Please Select a user ", Toast.LENGTH_SHORT).show();
                }else{
                    // Get select value
                    int index = position-1;
                    selectedItems = ownerItem.get(index);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // to see if can upload
                System.out.println(items_spinner.getSelectedItemPosition());
                if( items_spinner.getSelectedItemPosition()!=0){
                    System.out.println(items_spinner.getSelectedItem().toString());
                    int choseIndex = items_spinner.getSelectedItemPosition()-1;

                    if(ownerItem.get(choseIndex).getStatus().compareTo("free")==0){
                        UploadExchangeTransaction(theCurrentLoginUser, ownerItem.get(choseIndex), gottenItem);
                    }else{
                        ErrorMessage();
                    }
                }else{
                    SelectItemsErrorMessage();
                }

            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.setView(mView);
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void UploadExchangeTransaction(User loginUser, Items loginUserItem, Items chosenItem){
        String event_id = getEventID();
        // get opposite user
        User oppositeUser = getUser(chosenItem);

        //add starter to the processing list
        if(setProcessingListForStarter(event_id, loginUser, oppositeUser,  loginUserItem, chosenItem)){
            //add the opposite user to pending list
            if(setPendingListForOpposite(event_id, loginUser, oppositeUser,  loginUserItem, chosenItem)){
                //set All event based on event id
                if(setAllEventGroupValue(event_id, loginUser, oppositeUser,  loginUserItem, chosenItem)){
                    SentRequestMessage();
                    showItemDetailduActivity.super.onBackPressed();
                    finish();
                }
            }
        }
    }

    private boolean setPendingListForOpposite(String event_id, User loginUser, User oppositeUser,  Items loginUserItem, Items chosenItem){
        // uploading as pending (for other participate ) each participate need group details
        realTimeDatabaseForUploadingExchangeEvent.child(oppositeUser.getUserID()).child(event_id).child("" + (1)).child("Item provider").child(loginUser.getUserID()).setValue(loginUser);
        realTimeDatabaseForUploadingExchangeEvent.child(oppositeUser.getUserID()).child(event_id).child("" + (1)).child("Item").child(loginUserItem.getItemsID()).setValue(loginUserItem);
        realTimeDatabaseForUploadingExchangeEvent.child(oppositeUser.getUserID()).child(event_id).child("" + (1)).child("Item receiver").child(oppositeUser.getUserID()).setValue(oppositeUser);

        realTimeDatabaseForUploadingExchangeEvent.child(oppositeUser.getUserID()).child(event_id).child("" + (2)).child("Item provider").child(oppositeUser.getUserID()).setValue(oppositeUser);
        realTimeDatabaseForUploadingExchangeEvent.child(oppositeUser.getUserID()).child(event_id).child("" + (2)).child("Item").child(chosenItem.getItemsID()).setValue(chosenItem);
        realTimeDatabaseForUploadingExchangeEvent.child(oppositeUser.getUserID()).child(event_id).child("" + (2)).child("Item receiver").child(loginUser.getUserID()).setValue(loginUser);

        realTimeDatabaseForNotification.child(oppositeUser.getUserID()).child(event_id).child("status").setValue("Waiting");

        return true;
    }

    private boolean setProcessingListForStarter(String event_id, User loginUser, User oppositeUser,  Items loginUserItem, Items chosenItem){
        realTimeDatabaseForUploadingExchangeEventProcessing.child(loginUser.getUserID()).child(event_id).child(""+1).child("Item provider").child(loginUser.getUserID()).setValue(loginUser);
        realTimeDatabaseForUploadingExchangeEventProcessing.child(loginUser.getUserID()).child(event_id).child(""+1).child("Item").child(loginUserItem.getItemsID()).setValue(loginUserItem);
        realTimeDatabaseForUploadingExchangeEventProcessing.child(loginUser.getUserID()).child(event_id).child(""+1).child("Item receiver").child(oppositeUser.getUserID()).setValue(oppositeUser);

        realTimeDatabaseForUploadingExchangeEventProcessing.child(loginUser.getUserID()).child(event_id).child("" + (2)).child("Item provider").child(oppositeUser.getUserID()).setValue(oppositeUser);
        realTimeDatabaseForUploadingExchangeEventProcessing.child(loginUser.getUserID()).child(event_id).child("" + (2)).child("Item").child(chosenItem.getItemsID()).setValue(chosenItem);
        realTimeDatabaseForUploadingExchangeEventProcessing.child(loginUser.getUserID()).child(event_id).child("" + (2)).child("Item receiver").child(loginUser.getUserID()).setValue(loginUser);
        //initiate the items need to lock
        realTimeDatabaseForUploadingExchangeEventProcessing.child(loginUser.getUserID()).child(event_id).child("" +1).child("Item").child(loginUserItem.getItemsID()).child("status").setValue("locked");
        realTimeDatabaseForItems.child(loginUser.getUserID()+ "_" + loginUserItem.getItemsID()).child("status").setValue("locked");
        realTimeDatabaseForItemsUser.child(loginUser.getUserID()).child(loginUserItem.getItemsID()).child("status").setValue("locked");

        return true;
    }

    private boolean setAllEventGroupValue(String event_id, User loginUser, User oppositeUser,  Items loginUserItem, Items chosenItem){

        realTimeDatabaseForAllEventRecord.child(event_id).child("participants").child(loginUser.getUserID()).setValue(new Participant(loginUser.getEmail(), loginUser.getPassword(), loginUser.getUserName(),
                loginUser.getPhoneNum(), loginUser.getBirthday(), loginUser.getUserID(), loginUser.getProfileImagePath(), "Accepted"));

        realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (1)).child("Item provider").child(loginUser.getUserID()).setValue(loginUser);
        realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (1)).child("Item").child(loginUserItem.getItemsID()).setValue(loginUserItem);
        realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (1)).child("Item receiver").child(oppositeUser.getUserID()).setValue(oppositeUser);

        //set for request receiver
        realTimeDatabaseForAllEventRecord.child(event_id).child("participants").child(oppositeUser.getUserID()).setValue(new Participant(oppositeUser.getEmail(), oppositeUser.getPassword(), oppositeUser.getUserName(),
                oppositeUser.getPhoneNum(), oppositeUser.getBirthday(), oppositeUser.getUserID(), oppositeUser.getProfileImagePath(), "Waiting"));

        realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (2)).child("Item provider").child(oppositeUser.getUserID()).setValue(oppositeUser);
        realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (2)).child("Item").child(chosenItem.getItemsID()).setValue(chosenItem);
        realTimeDatabaseForAllEventRecord.child(event_id).child("group").child("" + (2)).child("Item receiver").child(loginUser.getUserID()).setValue(loginUser);

        realTimeDatabaseForAllEventRecord.child(event_id).child("status").setValue("Waiting");

        return true;
    }

    private void SentRequestMessage(){
        Toast.makeText(this,"Request sent to User",Toast.LENGTH_SHORT).show();
    }

    private User getUser(Items choseItems){
        String userID = choseItems.getOwnerID();
        User result = new User();
        for(User ur: allUsers){
            if(userID.compareTo(ur.getUserID())==0){
                System.out.println("found the user "+ur.getUserName()+" "+ur.getUserID());
                result = ur;
                return result;
            }
        }
        return result;
    }

    private void setItemInformation(Items gottenItem){
        if(gottenItem.getPathForImagesPictures()!=null) {
            imageRef = mStorageRef.child(gottenItem.getPathForImagesPictures().get(0)); // get the first picture to show in the  profile
            try {
                File localfile = File.createTempFile("tempfile", ".jpg");
                imageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        profileItemImage.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            itemsImageUrls = gottenItem.getPathForImagesPictures();
            for (int i=0; i<itemsImageUrls.size(); i++){
                System.out.println(itemsImageUrls.get(i));
            }

            initRecyclerViewForOneItem();
            //itemDescription.setText() // not available yet
            //getItemImageInformation();
        }
        itemName.setText(gottenItem.getName());
        ownerName.setText(gottenItem.getOwner());
        category.setText(gottenItem.getCategory());

        itemId.setText(gottenItem.getItemsID());

    }

    private void getItemImageInformation(){
        initRecyclerViewForOneItem();
    }


    private void initRecyclerViewForOneItem(){
        Log.d(TAG, "initRecyclcerView: init Recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_show_items_all_picture); // android:id="@+id/recyclerView"

        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewListAllItemPictureAdapter adapter = new RecyclerViewListAllItemPictureAdapter(this, itemsImageUrls); // set the images as adapter
        recyclerView.setAdapter(adapter);

    }

    private String getEventID(){
        int lengthOfTheID = 10;
        String id = "";
        for (int i =0; i<lengthOfTheID; i++){
            id = id + randomChooseAChar();
        }
        return id;
    }

    private char randomChooseAChar(){
        char [] valueRange= "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        int upperBound = valueRange.length;
        int n = (int)(Math.random() * upperBound );
        return valueRange[n];
    }
}