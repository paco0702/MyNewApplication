package com.example.mynewapplication.FragmentClass;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mynewapplication.LLS;
import com.example.mynewapplication.SerachExchange.MatchCategorySystem;
import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.ChooseCategorySuggestionAdapter;
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
 * Use the {@link SearchExchangeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchExchangeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchExchangeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchExchangeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchExchangeFragment newInstance(String param1, String param2) {
        SearchExchangeFragment fragment = new SearchExchangeFragment();
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
    View view;

    private HashMap<String, Items> allItemsButLogin;
    private HashMap<String, Items> allItems;
    private ArrayList<Items> itemsList;
    private HashMap<String, User> allUsers;
    private HashMap<String, ArrayList<String>> allWishList;
    ArrayList<String> itemsKeyButLogin;
    ArrayList<String> itemsKey;
    ArrayList<String> itemsName;
    ArrayList <Items> loginUserUploadedItems;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference realTimeDatabaseForRetrievingAllItems;
    private DatabaseReference realTimeDatabaseForRetrievingAllUsers;
    private DatabaseReference realTimeDatabaseForRetrievingAllWishList;
    Spinner items_spinner;
    String clicked_items_key; // store chosen categories
    Items chosenItem;
    MatchCategorySystem m; // for new matching system

    private ValueEventListener getItemsList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            allItemsButLogin = new HashMap<String, Items>();
            allItems = new  HashMap<String, Items>();

            itemsName = new ArrayList<>();
            itemsKeyButLogin = new ArrayList<>();
            itemsKey = new ArrayList<>();

            itemsName.add("select an items"); // set the dummy value to the list
            itemsKeyButLogin.add(""); // set the dummy value to the list

            for(DataSnapshot ds: snapshot.getChildren()){
                Items dummy = ds.getValue(Items.class);
                String str = ds.getKey().toString();
                String[] arrOfStr = str.split("_", 2);
                if(dummy.getOwnerID().compareTo(currentUser.getUid())!=0){
                    //store all items but the login user items
                    dummy.setItemsID(arrOfStr[1]);
                    itemsKeyButLogin.add(dummy.getItemsID().toString()); // the itemskey is for get back the item from hash map
                    itemsName.add(dummy.getName()+": "+dummy.getCategory()); // for user to click only, is not for return items' information
                    allItemsButLogin.put(dummy.getItemsID().toString(), dummy); // put the information into hash map for retreive
                }
                //store all items
                dummy.setItemsID(arrOfStr[1]);
                itemsKey.add(dummy.getItemsID().toString()); // the itemskey is for get back the item from hash map
                allItems.put(dummy.getItemsID().toString(), dummy); // put the information into hash map for retreive

            }
            items_spinner.setAdapter(new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_dropdown_item, itemsName));

            items_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==0){
                        Toast.makeText(getActivity(),
                                "Please Select an Items ", Toast.LENGTH_SHORT).show();
                    }else{
                        // Get select value
                        //String clicked_items_key = parent.getItemAtPosition(position).toString();
                        String clicked_items_key = itemsKeyButLogin.get(position);
                        chosenItem = allItemsButLogin.get(clicked_items_key);

                        System.out.println("Chosen items is "+chosenItem.getName()+" "+chosenItem.getItemsID());
                        System.out.println("chose items inform "+itemsName.get(position));
                        realTimeDatabaseForRetrievingAllUsers.addValueEventListener(getUser);
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

    private ValueEventListener getUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot ds: snapshot.getChildren()){
                String userID = ds.getKey();
                User dummy = ds.getValue(User.class);
                dummy.setUserID(userID);
                allUsers.put(userID, dummy);
            }
            realTimeDatabaseForRetrievingAllWishList.addValueEventListener(getAllWishList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private ValueEventListener getAllWishList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            allWishList = new HashMap<String, ArrayList<String>>();
            for(DataSnapshot ds: snapshot.getChildren()){
                String userID = ds.getKey();
                ArrayList<String> wishList = new ArrayList<>();

                for (DataSnapshot category: ds.getChildren()){
                    wishList.add(category.getValue().toString());
                    System.out.println(userID+": "+category.getValue().toString());
                }
                allWishList.put(userID, wishList);
            }
            realTimeDatabaseForRetrievingAllItems.addValueEventListener(getAllItem);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private ValueEventListener getAllItem = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            allItems = new HashMap<String, Items>(); //set string, get string based on categories
            itemsList = new ArrayList<>();
            loginUserUploadedItems = new ArrayList<>();
            for(DataSnapshot ds: snapshot.getChildren()){
                Items dummy = ds.getValue(Items.class);
                String str = ds.getKey().toString();
                String[] arrOfStr = str.split("_", 2);
                dummy.setItemsID(arrOfStr[1]); // arrOfstr [1]is the items id , arrOfstr[0] is user ID

                if (arrOfStr[0].compareTo(currentUser.getUid())==0) {
                    //allItems.put(dummy.getCategory(), dummy);
                    allItems.put(dummy.getItemsID(), dummy);
                    loginUserUploadedItems.add(dummy);
                }else{
                    //allItems.put(dummy.getCategory(), dummy);
                    allItems.put(dummy.getItemsID(), dummy);
                    itemsList.add(dummy);
                }
            }
            m = new MatchCategorySystem(clicked_items_key, allUsers, allWishList, allItems, currentUser.getUid(), getActivity(), view, itemsList, loginUserUploadedItems);
            m.startSystem();

            ArrayList<LLS> outputGroup = m.getOutputGroup();
            String clicked_category = m.getClicked_category();
            initListView(view, outputGroup, clicked_category);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void initListView(View view, ArrayList<LLS> outputGroup, String clicked_category){      // outputview list is provider and reciever
        ChooseCategorySuggestionAdapter adapter = new ChooseCategorySuggestionAdapter(getActivity(), R.layout.group_list_layout, outputGroup, clicked_category);
        ListView mListView = (ListView) view.findViewById(R.id.Group_information_view_group_details_search_exchange);   // to view list is wishlist and ite
        mListView.setAdapter(adapter);
    }

    ArrayList<String> category = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("search exchange fragment page: ");
        System.out.println("Capture list of user... ");

        if(getParentFragmentManager().findFragmentByTag("fragGroup")!=null) {
            getParentFragmentManager().findFragmentByTag("fragGroup").onDestroy();
            getParentFragmentManager().findFragmentByTag("fragGroup").onDetach();
        }

        mAuth = FirebaseAuth.getInstance();
        getCurrentUser();
        allItemsButLogin = new HashMap<String, Items>();
        allUsers = new HashMap<String, User>();
        itemsKeyButLogin = new ArrayList<>();
        itemsName = new ArrayList<>();
        chosenItem = new Items();


        view = inflater.inflate(R.layout.fragment_search_exchange, container, false);
        m = new MatchCategorySystem();
        //get list of the items
        //realTimeDatabaseForRetrievingAllItems = FirebaseDatabase.getInstance().getReference("Items");
        //realTimeDatabaseForRetrievingAllItems.addValueEventListener(getItemsList);

        realTimeDatabaseForRetrievingAllItems = FirebaseDatabase.getInstance().getReference("Items");
        realTimeDatabaseForRetrievingAllUsers = FirebaseDatabase.getInstance().getReference("user");
        realTimeDatabaseForRetrievingAllWishList = FirebaseDatabase.getInstance().getReference("Wish_list");
        //spinner for the items
        items_spinner = view.findViewById(R.id.all_item_search_spinner);
        clicked_items_key = "";
        initiateCategory();
        return view;
    }

    private void initiateCategory(){
        category.add("Select a category");
        category.add("Clothes");
        category.add("Accessory");
        category.add("Sport item");
        category.add("Instrument");
        category.add("Fashion");
        category.add("Electronic product");
        category.add("Stationery");
        category.add("Furniture");
        category.add("Daily product");

        items_spinner.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, category));

        items_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Toast.makeText(getActivity(),
                            "Please Select an Items ", Toast.LENGTH_SHORT).show();
                }else{

                    clicked_items_key = parent.getItemAtPosition(position).toString();
                    System.out.println("Chosen items category is "+ clicked_items_key);
                    realTimeDatabaseForRetrievingAllUsers.addValueEventListener(getUser);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getCurrentUser(){
        currentUser = mAuth.getCurrentUser();
    }

    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == Activity.RESULT_OK)){
            System.out.println("Recreate the fragment now");
            getActivity().getSupportFragmentManager().beginTransaction().detach(SearchExchangeFragment.this).attach(SearchExchangeFragment.this).commit();
            //ft.detach(frag).attach(frag).commit();
        }

    }

     */

}