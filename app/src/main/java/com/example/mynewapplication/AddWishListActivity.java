package com.example.mynewapplication;

import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mynewapplication.FullList.ItemsPoolActivity;
import com.example.mynewapplication.ShowItems.showItemDetailActivity;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.UserListAdapter;
import com.example.mynewapplication.recyclerViewstorage.WishListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AddWishListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener{



    //retrieve the User information
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef; // to store the upload picture
    private ListView wishListView;
    public ArrayList<String> wishList; // change static in the future and use it in add wish list page
    private DatabaseReference realTimeDatabaseForRetrievingWishList;

    public Button addWishList;
    private String selectedAddCategory;


    // login user's wish list
    private ValueEventListener getUserWishList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println("Function of get User Wishlist with the login user id: "+currentUser.getUid());
            wishList = new ArrayList<>();
            for (DataSnapshot ds : snapshot.getChildren()) {
                System.out.println("Ds.getKey() " +ds.getKey()+" current user id "+ currentUser.getUid());
                if(ds.getKey().compareTo(currentUser.getUid())==0){    // dont return the logined user
                    for (DataSnapshot wishitemds :ds.getChildren()){
                        //System.out.println(wishitemds.getValue().toString());
                        wishList.add(wishitemds.getValue().toString());
                    }
                }
            }
            initListView(wishListView, wishList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


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

        setContentView(R.layout.activity_add_wish_list);
        addWishList = findViewById(R.id.button_add_to_wish_list);
        addWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxToadd();
            }
        });
        //access database
        mAuth = FirebaseAuth.getInstance();
        getCurrentUser();
        //System.out.println("add wish list page: the current user: "+ currentUser.getUid());
        System.out.println("add wish list page: the current user: "+  theCurrentLoginUser.getUserID());
        System.out.println("Capture list of user... ");
        wishListView = findViewById(R.id.list_wish_list);


        realTimeDatabaseForRetrievingWishList = FirebaseDatabase.getInstance().getReference("Wish_list");
        realTimeDatabaseForRetrievingWishList.addValueEventListener(getUserWishList);
    }
    private void dialogBoxToadd(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddWishListActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner_category, null);
        alertDialog.setTitle("Choose an category to add");

        //set up the spinner to show login user items for transaction
        Spinner categories_spinner = (Spinner) mView.findViewById(R.id.spinner_category_selection);
        System.out.println(theCurrentLoginUser.getUserID());

        // output items name as arraylist
        ArrayList<String> category = new ArrayList<>();
        category = getCategoryList();

        categories_spinner.setAdapter(new ArrayAdapter<String>(AddWishListActivity.this,
                android.R.layout.simple_spinner_dropdown_item,  category));

        ArrayList<String> finalCategory = category;
        categories_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Toast.makeText(getApplication(),
                            "Please Select a category", Toast.LENGTH_SHORT).show();
                }else{
                    // Get select value
                    selectedAddCategory = finalCategory.get(position);
                    System.out.println("chose "+selectedAddCategory);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //selectedAddCategory
                System.out.println(wishList.size());
                FirebaseDatabase.getInstance().getReference("Wish_list").child(theCurrentLoginUser.getUserID()).child(wishList.size()+"").setValue(selectedAddCategory);
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

    private ArrayList<String> getCategoryList(){
        ArrayList <String> returnList = new ArrayList<>();
        returnList.add("Please choose a category");
        returnList.add("Accessory");
        returnList.add("Sport item");
        returnList.add("Instrument");
        returnList.add("Fashion");
        returnList.add("Electronic product");
        returnList.add("Stationery");
        returnList.add("Furniture");
        returnList.add("Daily product");
        return returnList;
    }
    private void getCurrentUser(){
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.search_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText == null||newText.trim().isEmpty()){
            resetSearch();  // if there is no text, then reset the original friend list to the view
            return false;
        }
        ArrayList<String> returnList = wishList;
        for (int i=0; i<wishList.size(); i++){   // if the friend list doesnt contain the input text
            // then dont show the name in the return list
            if(!returnList.get(i).toLowerCase().contains(newText.toLowerCase())) {
                returnList.remove(returnList.get(i));
            }
        }
        initListView(wishListView,  returnList); // change the adapter

        return true;
    }

    public void resetSearch() {
        initListView(wishListView,  wishList);
    }


    // list the friends
    private void initListView(ListView wishListView, ArrayList<String> wishList){
        // get Friend List
        WishListAdapter adapter  = new WishListAdapter(this, R.layout.wish_list_item_layout, wishList);
        wishListView.setAdapter(adapter);
    }
}