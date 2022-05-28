package com.example.mynewapplication.SerachExchange;

import android.view.View;
import android.widget.ListView;

import androidx.fragment.app.FragmentActivity;

import com.example.mynewapplication.LLS;
import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.ChooseCategorySuggestionAdapter;
import com.example.mynewapplication.recyclerViewstorage.SuggestionAdapter;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchCategorySystem {

    String clicked_category;
    private HashMap<String, User>  userList;
    private HashMap<String, ArrayList<String>> getAllWishList;
    private HashMap<String, Items>  getAllItem;
    private String loginUserID;
    private ArrayList<Items> itemsList; // without login user (target user)
    private ArrayList<Items> loginUserUploadedItems;
    FragmentActivity content;
    View view;

    // system
    private ArrayList<String> matchedItem;
    private ArrayList<LLS> outputGroup;

    public MatchCategorySystem(String  clicked_category, HashMap<String, User>  userList,
                        HashMap<String, ArrayList<String>> getAllWishList, HashMap<String, Items>  getAllItem,
                        String loginUserID, FragmentActivity content, View view, ArrayList<Items> itemsList,
                               ArrayList<Items> loginUserUploadedItems){
        this.clicked_category = clicked_category;
        this.userList = userList;
        this.getAllWishList = getAllWishList;
        this.getAllItem = getAllItem;
        this.loginUserID = loginUserID;
        this.content = content;
        this.view = view;
        this.itemsList = itemsList;
        this.loginUserUploadedItems = loginUserUploadedItems;
        this.outputGroup = new ArrayList<LLS>(); // for the output
    }

    public MatchCategorySystem(){
        this.clicked_category = "";
        this.userList = new HashMap<String, User>();
        this.getAllWishList =new  HashMap<String, ArrayList<String>>();
        this.getAllItem = new HashMap<String, Items>();
        this.loginUserID = "";
    }

    public void startSystem(){
        this.matchedItem = new ArrayList<>();
        for(int i=0; i<this.itemsList.size(); i++){
            if(this.itemsList.get(i).getCategory().compareTo(this.clicked_category)==0){
                this.matchedItem.add(this.itemsList.get(i).getItemsID());
               // System.out.println("Status of the items is "+this.itemsList.get(i).getStatus());
                //System.out.println("Matched items "+ this.itemsList.get(i).getItemsID());
            }
        }
        //store matched items Id int o matchedItems arraylist
        // then loop through user uploaded items match user wish list
        findMatch();
    }

    private void findMatch(){
        for(int i=0; i<this.matchedItem.size(); i++){ // based on the owner of matched items, see onwer wish list has items match user uploaded item
            //TODO error here
            String matchedUserID = getAllItem.get(this.matchedItem.get(i)).getOwnerID(); // matchItem is hash
            Items matchedItems = getAllItem.get(this.matchedItem.get(i));
            //System.out.println("Owner ID is "+matchedUserID);
            // if user has a wish list, loop the list
            ArrayList<String> matchedUserWishlist = getAllWishList.get(matchedUserID); // see wish list

            if(matchedUserWishlist!=null && matchedItems.getStatus()!="free"){
                for(int j=0 ; j<matchedUserWishlist.size(); j++){
                    for(int k=0; k<this.loginUserUploadedItems.size(); k++){
                        String [] loginUserUploadedItemsgetCategory = this.loginUserUploadedItems.get(k).getCategory().split(",");
                        System.out.println("loginUserUploadedItemsgetCategory "+loginUserUploadedItemsgetCategory[0]);
                        if (loginUserUploadedItemsgetCategory[0].compareTo(matchedUserWishlist.get(j)) == 0) {
                            // login user uploaed items category match user wish list
                            // the there is match
                            User loginUser = userList.get(this.loginUserID);
                            User matchUser = userList.get(matchedUserID);
                            LLS group = new LLS();
                                      // provider  , reciever ,
                            //System.out.println("MatchedItems "+matchedItems.getItemsID());
                            //System.out.println("Login user Items "+this.loginUserUploadedItems.get(k).getItemsID());
                            //System.out.println("Matched Items status "+ matchedItems.getStatus());
                            //System.out.println("Login user uploaedItems "+this.loginUserUploadedItems.get(k).getStatus());
                            // initiate is loginuser
                            group.insert(loginUser, matchUser, this.loginUserUploadedItems.get(k));
                            group.insert(matchUser, loginUser, matchedItems);


                            group.printExchangeNode();
                            outputGroup.add(group);
                        }
                    }
                }
            }
        }

    }

    public String getClicked_category() {
        return this.clicked_category;
    }

    public ArrayList<LLS> getOutputGroup() {
        return this.outputGroup;
    }

    private void initListView(View view, FragmentActivity content, ArrayList<LLS> outputGroup, String clicked_category){      // outputview list is provider and reciever
        ChooseCategorySuggestionAdapter adapter = new ChooseCategorySuggestionAdapter(content, R.layout.group_list_layout, outputGroup, clicked_category);
        ListView mListView = (ListView) view.findViewById(R.id.Group_information_view_group_details_search_exchange);   // to view list is wishlist and ite
        mListView.setAdapter(adapter);
    }
}
