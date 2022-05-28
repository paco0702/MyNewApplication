package com.example.mynewapplication.FragmentClass;

import static com.example.mynewapplication.IndexActivity.FLAT_FOR_ItemsPOOL;
import static com.example.mynewapplication.IndexActivity.fullList;
import static com.example.mynewapplication.IndexActivity.preferCategoryList;
import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


//import com.chaquo.python.PyObject;
//import com.chaquo.python.Python;
//import com.chaquo.python.android.AndroidPlatform;
import com.example.mynewapplication.FullList.ItemsPoolActivity;
import com.example.mynewapplication.R;
import com.example.mynewapplication.Sorting.BucketSort;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //two arrays list pass these two to the adapters
    private ArrayList<String > mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private static final String TAG = "RecyclerViewAdapter";
    private TextView display;
    // upload picture
    private FloatingActionButton uploadPictureButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference realTimeDatabaseForUsers;
    private DatabaseReference realTimeDatabaseForAllItems;
    //private DatabaseReference allUploadedItems;

    private String nameOfUser;
    private String emailOfUser;
    private String phoneNumberOfUser;
    private String idOfUser;
    private String birthdayOfUser;

    //return items list
    RecyclerView recyclerView_popular_list;
    Button enter_popular_list;
    RecyclerView recyclerView_preference_list;
    Button enter_preference_list;

    private ArrayList<Items> itemsList = new ArrayList<>();
    private ArrayList<Items> preferedItems = new ArrayList<>();
    private HashMap<String, User > usersList = new HashMap<>();

    private ValueEventListener getUserInfoEventListener = new  ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            //System.out.println("Index page");
            for(DataSnapshot ds: snapshot.getChildren()){
                User theUser = ds.getValue(User.class);
                // System.out.println("User id: "+theUser.getUserID()+"login in id: "+currentUser.getUid().toString()); // show returned user id 5
                usersList.put(ds.getKey(), theUser);

                if(theUser.getUserID().compareTo(currentUser.getUid().toString())==0) {
                    nameOfUser = theUser.getUserName();
                    emailOfUser = theUser.getEmail();
                    phoneNumberOfUser = theUser.getPhoneNum();
                    idOfUser = theUser.getUserID();
                    birthdayOfUser = theUser.getBirthday();
                    display.setText(nameOfUser);
                    theCurrentLoginUser.setUserName(nameOfUser);
                    theCurrentLoginUser.setEmail(emailOfUser);
                    theCurrentLoginUser.setPhoneNum(phoneNumberOfUser);
                    theCurrentLoginUser.setUserID(idOfUser);
                    theCurrentLoginUser.setBirthday(birthdayOfUser);

                }
            }
            realTimeDatabaseForAllItems.addValueEventListener(getItemsList);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    private ValueEventListener getItemsList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            for(DataSnapshot ds: snapshot.getChildren()){
                String [] token = ds.getKey().split("_");
                String ownerID = token[0];
                String itemsID = token[1];
                if(ownerID.compareTo(theCurrentLoginUser.getUserID())!=0){
                    Items dummyItem = ds.getValue(Items.class);
                    dummyItem.setItemsID(itemsID);
                    itemsList.add(dummyItem);
                }
            }


            BucketSort s = new BucketSort();
            for(int i=0; i<itemsList.size(); i++){
                User user = usersList.get(itemsList.get(i).getOwnerID().toString());
                String owner_rating = user.getRating();
                if(!user.getRating().equals("")){
                    s.insert(owner_rating, itemsList.get(i));
                }
            }
            // based on the rating of the user, sort it out


            ArrayList<Items> sortedList = new ArrayList<>();
            sortedList = s.getOutput();
            itemsList = sortedList;

            // get the prefered Items
            String preferCategories = theCurrentLoginUser.getPreferCategories();
            String [] categoriesToken = {"", "", ""};
            if(preferCategories.compareTo("")!=0 && preferCategories!=null){
                categoriesToken = preferCategories.split(",");
            }
            System.out.println("category token 0:"+categoriesToken[0]+" 1:"+categoriesToken[1]+" 2:"+categoriesToken[2]);
            preferedItems = new ArrayList<>();

            for(int j=0; j<sortedList.size(); j++){
                if(inPreferedCategory(sortedList.get(j), categoriesToken)){
                    preferedItems.add(sortedList.get(j));
                }
            }

            enableEnterPreferList();
            enableEnterPopularList();
            initPreferenceRecyclerView(view, preferedItems);
            initRecyclerView(view, sortedList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private boolean inPreferedCategory(Items item, String[] categoriesToken){

        if(item.getCategory().compareTo(categoriesToken[0])==0||
                item.getCategory().compareTo(categoriesToken[1])==0||
                item.getCategory().compareTo(categoriesToken[2])==0){
            return true;
        }else{
            return false;
        }
    }

    private void enableEnterPopularList(){
        enter_popular_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAT_FOR_ItemsPOOL = "popularList";
                fullList = itemsList;
                Intent intent = new Intent(getActivity(), ItemsPoolActivity.class);
                startActivity(intent);
            }
        });
    }

    private void enableEnterPreferList(){
        enter_preference_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAT_FOR_ItemsPOOL = "preferList";
                preferCategoryList = preferedItems;
                Intent intent = new Intent(getActivity(), ItemsPoolActivity.class);
                startActivity(intent);
            }
        });
    }


    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        // get the login user information
        mAuth = FirebaseAuth.getInstance();
        realTimeDatabaseForUsers = FirebaseDatabase.getInstance().getReference("user");
        realTimeDatabaseForAllItems = FirebaseDatabase.getInstance().getReference("Items");
        //allUploadedItems = FirebaseDatabase.getInstance().getReference("Items_Users");
        System.out.println("Get the current user in home page fragment... ");
        getCurrentUser();
        System.out.println("Index page home page fragment: the current user: "+ currentUser.getUid());
        System.out.println("Capture the user information ... ");

        display = view.findViewById(R.id.display_user_name_home_fragment);
        //display.setText(nameOfUser);
        realTimeDatabaseForUsers.addValueEventListener(getUserInfoEventListener);

        enter_popular_list = view.findViewById(R.id.button_enter_full_popular_list);

        recyclerView_preference_list = view.findViewById(R.id.recyclerView_preference_item);;
        enter_preference_list = view.findViewById(R.id.button_enter_full_preference_item_list);;


        // pass the users id did the exchange with login user
        //PyObject pyobj = py.getModule("collaborative_filtering_recommendation"); // give python script name
        //PyObject obj = pyobj.callAttr("prepare_system");
        //System.out.println("the current login user "+ theCurrentLoginUser.getUserID());

        return view ;
    }



    public void getCurrentUser(){
        currentUser = mAuth.getCurrentUser();
    }

    private void getImages(View view){
        // get image should be something from the clients
        // add the image
        // TODO　add the user to the recycle view instead
        Log.d(TAG, "initImageBitmaps: preparing bitmaps:");

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

        //initRecyclerView(view);
    }
    // locate where to put the picture into which view
    private void initPreferenceRecyclerView(View view, ArrayList<Items> itemsList){
        Log.d(TAG, "init RecyclcerView: init Recyclerview");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity() , LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_preference_item); // android:id="@+id/recyclerView"roid:id="@+id/recyclerView"

        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), itemsList); // set the images as adapter
        recyclerView.setAdapter(adapter);
    }

    // locate where to put the picture into which view
    private void initRecyclerView(View view, ArrayList<Items> itemsList){
        Log.d(TAG, "init RecyclcerView: init Recyclerview");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity() , LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_popular_List); // android:id="@+id/recyclerView"roid:id="@+id/recyclerView"

        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), itemsList); // set the images as adapter
        recyclerView.setAdapter(adapter);
    }




}