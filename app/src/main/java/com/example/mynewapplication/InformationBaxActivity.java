package com.example.mynewapplication;

import static com.example.mynewapplication.IndexActivity.boxFlag;
import static com.example.mynewapplication.IndexActivity.exchangeGroup;
import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.UserListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;

public class InformationBaxActivity extends AppCompatActivity{

    ListView mListView;

    //retrieve the User information
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference realTimeDatabaseForRetrievingUsers;
    private DatabaseReference realTimeDatabaseForTargetUserUploadedItems;
    private DatabaseReference realTimeDatabaseForRetrievingReceiver;
    //for retrieving item
    private StorageReference mStorageRef; // to store the upload picture
    ArrayList<User> UserList;
    ArrayList<String> UserName;
    ArrayList<Items> targetUserItemsList;
    ArrayList<String> itemsName;
    ArrayList<User> ReceiverList;
    ArrayList<String>ReceiverName;

    Spinner spinner;
    TextView show_selectedUser;
    Spinner spinner_uploadedItems;
    TextView show_selectedItems;
    Spinner spinner_receiver;
    TextView show_receiver;

    String chosenUser_Name;
    User chosenUser;
    String choseItems_ID;
    Items chosenItems;
    String chosenReceiver_ID;
    User chosenReceiver;

    Button submit;

    private ValueEventListener getUserList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println("Function of get User list with the login user id: "+currentUser.getUid());
            UserName.add("select a user");
            UserList.add(new User()); // dummy user
            if(exchangeGroup.isEmpty()){  // if it is the first button
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.getKey().compareTo(theCurrentLoginUser.getUserID())==0){
                        User eachUser = ds.getValue(User.class);
                        User insertLoginUserToList;
                        System.out.println("retrieved Login user id "+ ds.getKey());
                        insertLoginUserToList = new User(eachUser.getEmail(), eachUser.getPassword(),
                                eachUser.getUserName(), eachUser.getPhoneNum(), eachUser.getBirthday(), ds.getKey(), eachUser.getRating(), eachUser.getPreferCategories());
                        UserList.add(insertLoginUserToList);
                        UserName.add(insertLoginUserToList.getUserName());
                        break;
                    }
                }
            }else{
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User eachUser = ds.getValue(User.class);
                    System.out.println("Ds.getKey() " +ds.getKey()+" current user id "+ currentUser.getUid());
                    User insertToList;
                    System.out.println("retrieved user id "+ ds.getKey());
                    insertToList = new User(eachUser.getEmail(), eachUser.getPassword(),
                            eachUser.getUserName(), eachUser.getPhoneNum(), eachUser.getBirthday(), eachUser.getRating(), ds.getKey());
                    UserList.add(insertToList);
                    UserName.add(insertToList.getUserName());
                }
            }

            spinner.setAdapter(new ArrayAdapter<>(InformationBaxActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, UserName));

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==0){
                        Toast.makeText(getApplication(),
                                "Please Select a user ", Toast.LENGTH_SHORT).show();
                        show_selectedUser.setText("");

                    }else{
                        // Get select value
                        chosenUser_Name = parent.getItemAtPosition(position).toString();
                        chosenUser = UserList.get(position);
                        System.out.println("Chosen user is "+chosenUser.getUserID());
                        //set select value on TextView
                        show_selectedUser.setText(chosenUser_Name);
                        realTimeDatabaseForTargetUserUploadedItems.addValueEventListener(getUploadedItem);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private ValueEventListener getUploadedItem = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            itemsName = new ArrayList<>();
            for (DataSnapshot ds : snapshot.getChildren()) {
                System.out.println("Chosen user ID "+chosenUser.getUserID());
                if(ds.getKey().compareTo(chosenUser.getUserID())==0){    //
                    targetUserItemsList = new ArrayList<>();
                    targetUserItemsList.add(new Items()); // dummy items
                    itemsName.add("Select an item");
                    for (DataSnapshot items : ds.getChildren()) {
                        Items dummy = items.getValue(Items.class);
                        dummy.setItemsID(items.getKey());
                        System.out.println(dummy.getItemsID());
                        targetUserItemsList.add(dummy);
                        itemsName.add(dummy.getName()+" "+dummy.getItemsID());
                    }
                    break;
                }
            }
            spinner_uploadedItems.setEnabled(true);

            spinner_uploadedItems.setAdapter(new ArrayAdapter<>(InformationBaxActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, itemsName));

            spinner_uploadedItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==0){
                        Toast.makeText(getApplication(),
                                "Please Select a item ", Toast.LENGTH_SHORT).show();
                        show_selectedItems.setText("");

                    }else{
                        // Get select value
                        choseItems_ID = parent.getItemAtPosition(position).toString();
                        chosenItems = targetUserItemsList.get(position);
                        System.out.println("The chosenItems is "+chosenItems.getItemsID());
                        //set select value on TextView
                        show_selectedItems.setText(chosenItems.toString());
                        realTimeDatabaseForRetrievingReceiver.addValueEventListener(getReceiverList);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private ValueEventListener getReceiverList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println("Receiver list  ");
            ReceiverName = new ArrayList<>();
            ReceiverList = new ArrayList<>();
            ReceiverName.add("select a user");
            ReceiverList.add(new User()); // dummy user
            for (DataSnapshot ds : snapshot.getChildren()) {
                User eachUser = ds.getValue(User.class);
                System.out.println("Ds.getKey() " +ds.getKey()+" chosen user Name "+ chosenUser_Name +" user ID "+chosenUser.getUserID());
                if(ds.getKey().compareTo(chosenUser.getUserID())!=0){// don't return the chosen provider
                    User insertToList;
                    insertToList = new User(eachUser.getEmail(), eachUser.getPassword(),
                            eachUser.getUserName(), eachUser.getPhoneNum(), eachUser.getBirthday(), eachUser.getRating(), ds.getKey());
                    ReceiverList.add(insertToList);
                    ReceiverName.add(insertToList.getUserName());
                }
            }

            spinner_receiver.setEnabled(true);
            spinner_receiver.setAdapter(new ArrayAdapter<>(InformationBaxActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, ReceiverName));

            spinner_receiver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==0){
                        Toast.makeText(getApplication(),
                                "Please Select a user ", Toast.LENGTH_SHORT).show();
                        show_receiver.setText("");

                    }else{
                        // Get select value
                        chosenReceiver_ID = parent.getItemAtPosition(position).toString();
                        chosenReceiver =ReceiverList.get(position);
                        System.out.println("Chosen receiver is "+chosenReceiver.getUserID());
                        //set select value on TextView
                        show_receiver.setText(chosenReceiver_ID);
                        submit.setEnabled(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_box);

        //access database
        mAuth = FirebaseAuth.getInstance();
        getCurrentUser();


        System.out.println("search user fragment page: the current user: "+ currentUser.getUid());
        System.out.println("Capture list of user... ");


        // Inflate the layout for this fragment
        mListView = (ListView) findViewById(R.id.list_user_view);

        UserList = new ArrayList<>();
        UserName = new ArrayList<>();
        //itemsName = new ArrayList<>();


        realTimeDatabaseForRetrievingUsers = FirebaseDatabase.getInstance().getReference("user");
        realTimeDatabaseForRetrievingUsers.addValueEventListener(getUserList);
        realTimeDatabaseForTargetUserUploadedItems = FirebaseDatabase.getInstance().getReference("Items_Users");
        realTimeDatabaseForRetrievingReceiver = FirebaseDatabase.getInstance().getReference("user");

        mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference();

        //  searchable spinner
        spinner = findViewById(R.id.spinner);
        show_selectedUser = findViewById(R.id.selected_user);

        spinner_uploadedItems = findViewById(R.id.spinner_uploaded_item);
        show_selectedItems = findViewById(R.id.show_items);
        spinner_uploadedItems.setEnabled(false);

        spinner_receiver = findViewById(R.id.spinner_receiver);
        show_receiver = findViewById(R.id.selected_receiver);
        spinner_receiver.setEnabled(false);

        submit = findViewById(R.id.button_submit_information_exchange);
        submit.setEnabled(false);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chosenUser != null && chosenItems!=null && chosenReceiver!=null){
                    System.out.println("Chosen provider "+chosenUser.getUserName() +" chosenItems "+ chosenItems.getName()+" Chosen receiver "+chosenReceiver.getUserName());
                    exchangeGroup.insert(chosenUser, chosenReceiver, chosenItems);
                    System.out.println(getSupportFragmentManager().findFragmentByTag("fragExchange"));
                    boxFlag = true;
                    setResult(Activity.RESULT_OK);
                    InformationBaxActivity.super.onBackPressed();
                }
            }
        });
    }



    private void getCurrentUser(){
        currentUser = mAuth.getCurrentUser();
    }

}