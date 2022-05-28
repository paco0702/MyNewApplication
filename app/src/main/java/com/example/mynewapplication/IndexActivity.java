package com.example.mynewapplication;


import static com.example.mynewapplication.MainActivity.csvDictionary;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynewapplication.FragmentClass.ExchangeFragment;
import com.example.mynewapplication.FragmentClass.FriendListFragment;
import com.example.mynewapplication.FragmentClass.GroupSuggestionFragment;
import com.example.mynewapplication.FragmentClass.HomeFragment;
import com.example.mynewapplication.FragmentClass.MessageFragment;
import com.example.mynewapplication.FragmentClass.StatusFragment;
import com.example.mynewapplication.FragmentClass.ProfileFragment;
import com.example.mynewapplication.FragmentClass.SearchExchangeFragment;
import com.example.mynewapplication.FragmentClass.SearchFragment;
import com.example.mynewapplication.FragmentClass.SettingFragment;
import com.example.mynewapplication.FragmentClass.StarItemsFragment;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;

import com.example.mynewapplication.recyclerViewstorage.RecyclerViewAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class IndexActivity  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //for navigation side bar
    private DrawerLayout drawer;

    //two arrays list pass these two to the adapters
    private ArrayList<String > mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private static final String TAG = "RecyclerViewAdapter";

    // upload picture button
    private FloatingActionButton uploadPictureButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference realTimeDatabaseForUsers;
    private DatabaseReference allUploadedItems;


    private String nameOfUser;
    private String emailOfUser;
    private String phoneNumberOfUser;
    private String idOfUser;
    private String birthdayOfUser;
    private String rating;
    private String preferCategories;
    private String profileImagePath;

    //store the login user
    public static User theCurrentLoginUser;
    public static ArrayList<User> userAllUploadedItem; // User class item

    public static LLS  suggestGroup; // stand for LinkedList Suggestion system
    public static LLS exchangeGroup = new LLS();
    // stand for LinkedList for form group in exchange fragment, stored the gorup all enter by user

    public static boolean boxFlag = false;

    // for pending and processing to store group for achieving detailed information activity
    //selected Group, stored the selected group from:
    // pending , processing , suggestion adapter, choose category suggestion adapter
    public static LLS selectedGroup; // processing list page to retreive detail information

    public static String FLAT_FOR_GroupDetail= ""; // to indicate if the group detail request is from suggestion system from processing list
    public static String FLAT_FOR_ItemsDetail = "";
    public static String FLAT_FOR_ItemsPOOL = "";

    public static String selectEventKey;
    public static Items selectedItem; // return from where select an Item to Itemsdetail page, from show item activity or
                                       // or from items suggestion group activty


    public static ArrayList<Items> fullList = new ArrayList<>(); // stroe from home fragment return to items pool activity
    public static ArrayList<Items> preferCategoryList = new ArrayList<>();
    public static ArrayList<User> allUsers = new ArrayList<>();

    private ValueEventListener getUploadedItemEventListener = new  ValueEventListener(){ // all uploaded items
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    //manage the fragment
    public FragmentManager fragment = getSupportFragmentManager();

    // check fragment is added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                //At this point you should start the login activity and finish this one
                finish();
            }
        }, intentFilter);

        setContentView(R.layout.nagivation_page); // means related to the activity main layer

        // get the login user information
        mAuth = FirebaseAuth.getInstance();
        realTimeDatabaseForUsers = FirebaseDatabase.getInstance().getReference("user");
        realTimeDatabaseForUsers.addValueEventListener(new  ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //System.out.println("Index page");
                for(DataSnapshot ds: snapshot.getChildren()){
                    User theUser = ds.getValue(User.class);
                    if(theUser.getUserID().compareTo(currentUser.getUid().toString())==0) {
                        nameOfUser = theUser.getUserName();
                        emailOfUser = theUser.getEmail();
                        phoneNumberOfUser = theUser.getPhoneNum();
                        idOfUser = theUser.getUserID();
                        birthdayOfUser = theUser.getBirthday();
                        rating = theUser.getRating();
                        preferCategories = theUser.getPreferCategories();
                        if(theUser.getProfileImagePath()!=null && !theUser.getProfileImagePath().equals("")){
                            profileImagePath = theUser.getProfileImagePath();
                        }
                        theCurrentLoginUser = theUser;
                        //break;
                    }
                    allUsers.add(theUser);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        allUploadedItems = FirebaseDatabase.getInstance().getReference("Items_Users");
        allUploadedItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //for each user
                System.out.println("get uploaded items");

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String userID = ds.getKey();
                    //System.out.print(userID+" ");
                    ArrayList <Items> ownedItems = new ArrayList<>();
                    // get the user's items
                    for (DataSnapshot itemds :ds.getChildren()){
                        Items token = itemds.getValue(Items.class);
                        token.setItemsID(itemds.getKey());
                        ownedItems.add(token);
                    }
                    User newUser = new User(userID, ownedItems);
                    userAllUploadedItem.add(newUser); // from index page variable
                    if(userID.compareTo(theCurrentLoginUser.getUserID())==0){
                        theCurrentLoginUser.setOwnsItems(ownedItems);
                        System.out.println("current uploaded items: "+theCurrentLoginUser.getOwnsItems().size());
                    }
                }
                //printUserUploadedItemsList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        userAllUploadedItem = new ArrayList<>();
        System.out.println("Get the current user ... ");
        getCurrentUser();
        System.out.println("Index page: the current user: "+ currentUser.getUid());
        System.out.println("Capture the user information ... ");
        // store all users owned items

        //allUploadedItems.addValueEventListener(getUploadedItemEventListener);

        // realTimeDatabaseForItem.addValueEventListener(getItemsList);
        // control the navigation bar
        //                                            id is bottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setBackground(null);
        bottomNav.setOnNavigationItemSelectedListener(bottomNavListener); // refer to the function set below
        // floating circle button
        uploadPictureButton = (FloatingActionButton) findViewById(R.id.add_item_button_shortCut);
        uploadPictureButton.setOnClickListener(enterUploadPicturesPage);

        // return the view the recommendation cycle
        //getImages();
        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout)findViewById(R.id.nav_main_layout);
        NavigationView navigationSideView = findViewById(R.id.nav_view_side);
        navigationSideView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.index_page_navigation_drawer_open, R.string.index_page_navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState==null){
            Fragment selectedFragment =  new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        }

        //getSentiment();
        //s.setTargetUserIDItemList(currentUser.getUid(), currentUser.getUid());
        /*
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("CLOSE_ALL");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // close activity
                finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
        */
    }


    // for navigation side view
    private float [] getSentiment(){
        String review = "This is very good";
        String [] token = review.split(" ");
        float [] tokenizedReview = new float [87];
        for (int i =0; i<token.length; i++){
            tokenizedReview[i] = getToken(token[i]);
            System.out.println(token[i]+" tokenized "+ tokenizedReview[i]+" ");
        }
        System.out.println();
        return tokenizedReview;
    }

    private float getToken(String tokenWord){
        int index = 0;
        int count = 0;
        for(int k=0; k<csvDictionary.size(); k++){
            if(tokenWord.compareTo(csvDictionary.get(k).getValue())==0){
                System.out.println(csvDictionary.get(k).getValue()+ " and "+ tokenWord+" is a match");
                index = csvDictionary.get(k).getIndex();
                break;
            }else{
                count++;
            }
        }
        if(count==csvDictionary.size()){
            System.out.println("Token is not found");
            return 1F;
        }else{
            return index;
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_side_bar_exchange:
                System.out.println("Into the exchange fragment");
                if(fragment.findFragmentByTag("fragExchange")==null) {
                    FragmentTransaction ft = fragment.beginTransaction();
                    ft.replace(R.id.fragment_container, new ExchangeFragment(), "fragExchange");
                    ft.addToBackStack(null);
                    ft.commit();
                }else{
                    if( boxFlag ==true){
                        boxFlag = false;
                        FragmentTransaction ft = fragment.beginTransaction();
                        ft.replace(R.id.fragment_container, new ExchangeFragment(), "fragExchange");
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                    }
                    System.out.println("Fragment Tag is exchange: "+fragment.findFragmentByTag("fragExchange"));
                    System.out.println("Exist");
                }
                break;
                /*
            case R.id.nav_side_bar_friendList:
                if(fragment.findFragmentByTag("fragExchange")!=null) {
                    System.out.println("pervious fragment exchange");
                    Fragment exfragment = fragment.findFragmentByTag("fragExchange");
                    fragment.beginTransaction().remove(exfragment).addToBackStack(null).commit();
                    fragment.popBackStack();
                }
                fragment.beginTransaction().replace(R.id.fragment_container,
                        new FriendListFragment(), "fragFriendList").addToBackStack(null).commit();
                break;*/
            case R.id.nav_side_bar_view_status:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new StatusFragment(), "fragStatus").addToBackStack(null).commit();
                break;
            case R.id.nav_side_bar_message:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MessageFragment(), "fragMessage").addToBackStack(null).commit();

                break;
                /*
            case R.id.nav_side_bar_startItem:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new StarItemsFragment(), "fragStartItem").commit();
                break;*/
            case R.id.nav_side_bar_group:

                if(fragment.findFragmentByTag("fragSearchExchange")!=null) {

                    Fragment SEFragment = fragment.findFragmentByTag("fragSearchExchange");
                    fragment.beginTransaction().remove(SEFragment).commit();
                    getSupportFragmentManager().beginTransaction().remove(SEFragment).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new GroupSuggestionFragment(), "fragGroup").commit();

                    System.out.println(getSupportFragmentManager().findFragmentByTag("fragSearchExchange"));

                }else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new GroupSuggestionFragment(), "fragGroup").addToBackStack(null).commit();
                }
                break;

            case R.id.nav_side_bar_search_exchange:
                if(fragment.findFragmentByTag("fragGroup")!=null) {
                    Fragment GFragment = fragment.findFragmentByTag("fragGroup");
                    getSupportFragmentManager().beginTransaction().remove(GFragment).commit();
                    fragment.beginTransaction().remove(GFragment).commit();
                    System.out.println(getSupportFragmentManager().findFragmentByTag("fragGroup"));

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new SearchExchangeFragment(), "fragSearchExchange").commit();

                }else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new SearchExchangeFragment(), "fragSearchExchange").addToBackStack(null).commit();

                }

                break;
            case R.id.nav_side_bar_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(IndexActivity.this);
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth loginUser = FirebaseAuth.getInstance();
                        loginUser.signOut();

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                        sendBroadcast(broadcastIntent);
                        finish();

                        Intent homeIntent = new Intent(IndexActivity.this, MainActivity.class);
                        startActivity(homeIntent);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setTitle("Logout").create();
                builder.show();
                break;

        }
        //return false mean there no item selected ,
        // if return false, there will be no action
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    //control the bottom navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment =  new HomeFragment(); // default display home page
                    switch (item.getItemId()){ // case id is refered to the menu id
                        case R.id.bar_Home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.bar_Profile:
                            selectedFragment = new ProfileFragment();
                            break;
                        case R.id.bar_Search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.bar_setting:
                            selectedFragment = new SettingFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    //get the user information


    public void getCurrentUser(){
        currentUser = mAuth.getCurrentUser();
    }



    View.OnClickListener enterUploadPicturesPage = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v==uploadPictureButton){
                setEnterUploadActivityPage();
            }
        }
    };

    private void setEnterUploadActivityPage(){
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }

    //Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    //startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    // get back the image that the image user selected
    private void getImages(){
        // get image should be something from the clients
        // add the image
        // TODO　add the user to the recycle view instead
        Log.d(TAG, "initImageBitmaps: preparing bitmaps:");
        //mImageUrls.add("https://cl.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        //mNames.add("Havasu Falls");

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



