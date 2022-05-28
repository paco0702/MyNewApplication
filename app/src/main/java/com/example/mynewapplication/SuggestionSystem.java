package com.example.mynewapplication;

import static com.example.mynewapplication.IndexActivity.suggestGroup;
import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.SuggestionAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class SuggestionSystem {

    //for suggestion system
    public String targetUserID;

    //new
    HashMap<String, ArrayList<Items>> allUploadedItems;
    HashMap<String, ArrayList<String>> allWishList;
    ArrayList<String> allItemsKey;
    ArrayList<String> allWishListKey ;
    ArrayList<String>  loginUserWishlist;
    ArrayList<Items>  loginUseruploadedItemlist ;
    int currentIndex;
    public LLS [] groupList; // stand for LinkedList
    public ArrayList<LLS> toViewList;
    public ArrayList<LLS> outputViewList;
    FragmentActivity content;
    View view;
    HashMap<String, User> allUserInform;
    public LLS [] outputList;

    public SuggestionSystem(String userID, HashMap<String, ArrayList<Items>> allUploadedItems, ArrayList<String> allItemsKey,
                            HashMap<String, ArrayList<String>> allWishList, ArrayList<String> allWishListKey,
                            ArrayList<String>  loginUserWishlist, ArrayList<Items>  loginUseruploadedItemlist, FragmentActivity content, View view, HashMap<String, User> allUserInform){
        this.targetUserID = userID;
       this.allUploadedItems = allUploadedItems;
       this.allItemsKey = allItemsKey; // user ID
       this.allWishList = allWishList;
       this.allWishListKey = allWishListKey;
       this.loginUserWishlist = loginUserWishlist;
       this.loginUseruploadedItemlist = loginUseruploadedItemlist;
       this.outputList = new LLS[loginUserWishlist.size()];
       this.groupList = new LLS[loginUserWishlist.size()];
        for(int i =0; i<loginUserWishlist.size(); i++){
           this.groupList[i] = new LLS();
           this.outputList[i] = new LLS();
        }
        this.content = content;
        this.view = view;
        this.allUserInform = allUserInform;
    }

    public void start(){
        System.out.println("now start ");
        for(int i =0; i<this.loginUserWishlist.size(); i++){
            this.currentIndex = i;
            this.groupList[this.currentIndex].setTargetCategory(this.loginUserWishlist.get(i));

            //allItemsKey allUploadedItems
            HashMap<String, ArrayList<Items>> dummyUploadedItems = this.allUploadedItems;
            ArrayList<String> dummyItemskey = this.allItemsKey;
            trySuggestion(this.targetUserID, this.loginUserWishlist.get(i), dummyItemskey, dummyUploadedItems);
        }

        //return the group list to the UI
        this.toViewList = new ArrayList<>();
        this.outputViewList = new ArrayList<>();

        //System.out.println("group length "+this.groupList.length);
        for (int j =0; j<this.groupList.length; j++){
            //System.out.println(" index "+j);
            //this.groupList[j].printNode();
            // only return to the View List when the groupList is cyclic
            if(this.groupList[j].isCyclic()){
                this.toViewList.add(this.groupList[j]);
                this.outputViewList.add(this.outputList[j]);
                this.outputList[j].printExchangeNode();
            }
        }
        return;
    }

    public ArrayList<LLS> getToViewList(){
        return this.toViewList;
    }

    public ArrayList<LLS> getOutputViewList(){
        return this.outputViewList;
    }

    public void trySuggestion(String wishUser, String category, ArrayList<String> allItemsKey, HashMap<String, ArrayList<Items>> allUploadedItems){
        System.out.println("suggest category is "+ category+" index number is "+this.currentIndex);
        for(int j=0; j<allItemsKey.size(); j++){
            //System.out.println(allItemsKey.get(j));
            ArrayList<Items> dummyItemsArray = allUploadedItems.get(allItemsKey.get(j));

           for(int k =0; k<dummyItemsArray.size(); k++){
               String[] dummyItemsArray_cateory = dummyItemsArray.get(k).getCategory().split(",");
               System.out.println("dummyItemsArray_cateory  "+dummyItemsArray_cateory[0]);
               if(category.compareTo(dummyItemsArray_cateory[0])==0 &&(allItemsKey.get(j).compareTo(wishUser)!=0)){
                   System.out.println("Found item: uploader is "+allItemsKey.get(j)+" and category is "+ dummyItemsArray_cateory[0]);
                   //get the wish list of that user uploader
                   ArrayList<String> matchedItemUploaderWL = allWishList.get(allItemsKey.get(j)); // user Id is the key
                   ArrayList<Items> uploadedItemOfWishUser = allUploadedItems.get(wishUser);

                   if(matchedItemUploaderWL!=null){
                       // matched item uploader WL (Wish list of provider user
                                                       //wish user is the target user
                       System.out.println("currentIndex "+this.currentIndex);// items key is userID
                                                               //receiver  provider
                       this.groupList[this.currentIndex].insert(wishUser, allItemsKey.get(j), category);
                       User provider = this.allUserInform.get(allItemsKey.get(j));
                       User receiver = this.allUserInform.get(wishUser);
                       Items exchange_item = dummyItemsArray.get(k);
                       System.out.println("show output list "+provider.getUserID()+" "+provider.getUserName()+" receiver "+receiver.getUserID()+" "+receiver.getUserName()+
                               " Items"+exchange_item.getItemsID()+" "+ exchange_item.getName());

                       this.outputList[this.currentIndex].insert(provider, receiver, exchange_item); // store detail
                                                      //provider, reciever, items
                       //System.out.println("after inserted, list has "+this.outputList[this.currentIndex].getSize());
                       dummyItemsArray.remove(k); //remove the inserted item in the array
                       allUploadedItems.put(allItemsKey.get(j), dummyItemsArray);

                       String uploaderUser = allItemsKey.get(j);
                       boolean matchedLoginUserItems = false;
                       for(int o =0; o<matchedItemUploaderWL.size(); o++ ){
                           for(int u =0; u<this.loginUseruploadedItemlist.size(); u++){
                               String [] loginUseruploadedItemlistcategory = this.loginUseruploadedItemlist.get(u).getCategory().split(",");
                               System.out.println("loginUseruploadedItemlistcategory "+loginUseruploadedItemlistcategory[0]);
                               if(matchedItemUploaderWL.get(o).compareTo(loginUseruploadedItemlistcategory[0])==0){
                                   // uploader wish list have match with login user uploaded item
                                   //System.out.println("found match");
                                   this.groupList[this.currentIndex].insert(uploaderUser, targetUserID, loginUseruploadedItemlistcategory[0]);
                                   User newprovider = this.allUserInform.get(targetUserID);
                                   User newreceiver = this.allUserInform.get(uploaderUser);
                                   Items enewxchange_item = this.loginUseruploadedItemlist.get(u);
                                   System.out.println("show output list "+newprovider.getUserID()+" "+newprovider.getUserName()+" receiver "+newreceiver.getUserID()+" "+newreceiver.getUserName()+
                                           " Items"+enewxchange_item.getItemsID()+" "+ enewxchange_item.getName());

                                   this.outputList[this.currentIndex].insert(newprovider, newreceiver,enewxchange_item); // store detail
                                   matchedLoginUserItems = true;
                                   if(this.groupList[this.currentIndex].isCyclic()){
                                       //System.out.println("Output group "+ this.outputList[this.currentIndex].isExchangeCyclic());
                                       //System.out.println("System finish");
                                       return;
                                   }
                               }
                           }
                       }

                       if(matchedLoginUserItems==false){
                           for(int o =0; o<matchedItemUploaderWL.size(); o++ ){
                               for(int u =0; u<uploadedItemOfWishUser.size(); u++){
                                   String [] uploadedItemOfWishUserCategory = uploadedItemOfWishUser.get(u).getCategory().split(",");
                                   System.out.println("uploadedItemOfWishUserCategory "+uploadedItemOfWishUserCategory[0]);
                                   if(matchedItemUploaderWL.get(o).compareTo(uploadedItemOfWishUserCategory[0])==0){
                                       // uploader wish list have match with login user uploaded item
                                       //System.out.println("found match");
                                       this.groupList[this.currentIndex].insert(allItemsKey.get(j), wishUser,  uploadedItemOfWishUserCategory[0]);
                                       User newprovider = this.allUserInform.get(wishUser);
                                       User newreceiver = this.allUserInform.get(allItemsKey.get(j));
                                       Items enewxchange_item = uploadedItemOfWishUser.get(u);
                                       System.out.println("show output list "+newprovider.getUserID()+" "+newprovider.getUserName()+" receiver "+newreceiver.getUserID()+" "+newreceiver.getUserName()+
                                               " Items"+enewxchange_item.getItemsID()+" "+ enewxchange_item.getName());

                                       this.outputList[this.currentIndex].insert(newprovider, newreceiver,enewxchange_item); // store detail

                                       if(this.groupList[this.currentIndex].isCyclic()){
                                           //System.out.println("Output group "+ this.outputList[this.currentIndex].isExchangeCyclic());
                                           //System.out.println("System finish");
                                           return;
                                       }
                                   }
                               }
                           }
                       }


                       if(groupList[this.currentIndex].isCyclic()==false ){
                           // if userB wish category has not match with login user upload item
                           // based on the wish list search again
                           for(int i =0; i<matchedItemUploaderWL.size(); i++){
                               trySuggestion(uploaderUser ,matchedItemUploaderWL.get(i),  allItemsKey, allUploadedItems);
                               if(groupList[this.currentIndex].isCyclic()){
                                   return;
                               }
                           }
                       }
                       if(groupList[this.currentIndex].getSize()>6){
                           //System.out.println("cannot find a gorup");
                           return;
                       }
                       if(groupList[this.currentIndex].isCyclic()){
                           return;
                       }
                   }
               }
           }
        }
    }

}
