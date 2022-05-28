package com.example.mynewapplication.FragmentClass;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.mynewapplication.LLS;
import com.example.mynewapplication.R;
import com.example.mynewapplication.SuggestionSystem;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.SuggestionAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupSuggestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupSuggestionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupSuggestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupSuggestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupSuggestionFragment newInstance(String param1, String param2) {
        GroupSuggestionFragment fragment = new GroupSuggestionFragment();
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
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference wishListSuggestion;
    private DatabaseReference realTimeDatabaseForItem;
    private DatabaseReference userInform;

    public ArrayList<String>  loginUserWishlist;
    public ArrayList<Items>  loginUseruploadedItemlist;
    public ArrayList<String> allItemsKey;
    public ArrayList<String> allWishListKey;
    public HashMap<String, ArrayList<Items>> allItems;
    public HashMap<String, ArrayList<String>> allWishList;
    public HashMap<String, User> allUserInform;
    public SuggestionSystem s;

    private void getSuggestion(){
        realTimeDatabaseForItem .addValueEventListener(getAllItemList);
    }

    private ValueEventListener getAllItemList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            for (DataSnapshot ds : snapshot.getChildren()) {
                ArrayList<Items> dummy = new ArrayList<>();
                // System.out.println(ds.getKey());
                if(ds.getKey().compareTo(currentUser.getUid())==0){    // if userid is login user id
                    for (DataSnapshot uploadedItem :ds.getChildren()){
                        Items userItem = uploadedItem.getValue(Items.class);
                        userItem.setItemsID(uploadedItem.getKey().toString());
                        //System.out.println("Login user items id "+ userItem.getItemsID());
                        loginUseruploadedItemlist.add(userItem);
                    }
                    allItemsKey.add(ds.getKey().toString());
                    allItems.put(currentUser.getUid().toString(), loginUseruploadedItemlist);
                }else{
                    for (DataSnapshot uploadedItem :ds.getChildren()){
                        Items userItem = uploadedItem.getValue(Items.class);
                        userItem.setItemsID(uploadedItem.getKey().toString());
                        dummy.add(userItem);
                    }
                    allItemsKey.add(ds.getKey().toString());
                    allItems.put(ds.getKey().toString(), dummy);
                }
            }
            System.out.println("Finished capture item data");
            userInform.addValueEventListener(getUserInform);
            //wishListSuggestion.addValueEventListener(getUserWishList);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }

    };

    private ValueEventListener getUserInform = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot ds : snapshot.getChildren()){
                User dummyuser =  ds.getValue(User.class);
                System.out.println(dummyuser.toString());
                allUserInform.put(ds.getKey().toString(), dummyuser);
            }
            wishListSuggestion.addValueEventListener(getUserWishList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private ValueEventListener getUserWishList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot ds : snapshot.getChildren()) {
                ArrayList<String> dummy = new ArrayList<>();
                if(ds.getKey().compareTo(currentUser.getUid())==0){    // if userid is login user id, add into login user wish list
                    System.out.println(ds.getKey());
                    for (DataSnapshot wishitemds :ds.getChildren()){
                        loginUserWishlist.add(wishitemds.getValue().toString());
                    }
                    allWishListKey.add(ds.getKey().toString());
                    allWishList.put(currentUser.getUid().toString(), loginUserWishlist);
                }else{
                    for (DataSnapshot wishitemds :ds.getChildren()){
                        dummy.add(wishitemds.getValue().toString());
                    }
                    allWishListKey.add(ds.getKey().toString());
                    allWishList.put(ds.getKey().toString(), dummy);
                }
            }
            System.out.println("Finished capture wish list data");

            s = new SuggestionSystem(currentUser.getUid(), allItems, allItemsKey, allWishList, allWishListKey, loginUserWishlist, loginUseruploadedItemlist, getActivity(), view, allUserInform);
            s.start();

            ArrayList<LLS> toViewList = s.getToViewList();
            ArrayList<LLS> outputViewList = s.getOutputViewList();

            initListView(view, toViewList, outputViewList);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }

    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group_suggestion, container, false);
        mAuth = FirebaseAuth.getInstance();
        getCurrentUser();
        System.out.println("Suggestion user fragment page: the current user: "+ currentUser.getUid());


        if(getParentFragmentManager().findFragmentByTag("fragSearchExchnage")!=null){
            getParentFragmentManager().findFragmentByTag("fragSearchExchange").onDestroy();
            getParentFragmentManager().findFragmentByTag("fragSearchExchange").onDetach();
        }


        loginUserWishlist = new ArrayList<>();
        loginUseruploadedItemlist = new ArrayList<>();
        allItemsKey = new ArrayList<>();
        allWishListKey = new ArrayList<>();
        allItems = new HashMap<String, ArrayList<Items>>();
        allWishList = new HashMap<String, ArrayList<String>>();
        allUserInform = new HashMap<String, User>();

        wishListSuggestion = FirebaseDatabase.getInstance().getReference("Wish_list");
        realTimeDatabaseForItem = FirebaseDatabase.getInstance().getReference("Items_Users");

        userInform = FirebaseDatabase.getInstance().getReference("user");

        getSuggestion();
        return view;
    }


    private void getCurrentUser(){
        currentUser = mAuth.getCurrentUser();
    }


    private void initListView(View view, ArrayList<LLS> toViewList,  ArrayList<LLS> outputViewList){
        // outputview list is provider and reciever
        if(getActivity()!=null) { //handle null pointer
            SuggestionAdapter adapter = new SuggestionAdapter(getActivity(), R.layout.group_list_layout, toViewList, outputViewList);
            ListView mListView = (ListView) view.findViewById(R.id.Group_list);   // to view list is wishlist and ite
            mListView.setAdapter(adapter);
        }
    }


}