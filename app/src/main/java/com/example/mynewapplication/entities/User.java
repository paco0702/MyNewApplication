package com.example.mynewapplication.entities;

import android.net.Uri;

import java.util.ArrayList;

public class User {
    private String email;
    private String UserName;
    private String password;
    private String phoneNum;
    private String birthday;
    private String userID;
    private String realTimeDataBaseID;
    private String profileImagePath;
    private String rating;
    private String preferCategories;
    private ArrayList<Items> ownsItems = new ArrayList<>();
    private ArrayList<String> wishList = new ArrayList<>();

    private Uri profileImage;

    public User(String name){
        this.UserName = name;
    }


    public User(){
        this("", "", "" ,"" ,"", "","");
    }


    public User(String email, String password, String userName, String phoneNum, String birthday, String userID, String rating, String preferedCategories, String profileImagePath){
        this.email = email;
        this.password = password;
        this.UserName =  userName;
        this.phoneNum = phoneNum;
        this.birthday = birthday;
        this.userID = userID;
        this.rating = rating;
        this.profileImagePath = profileImagePath;
        this.preferCategories = preferedCategories;
    }

    public User(String email, String password, String userName, String phoneNum, String birthday, String userID, String rating, String preferedCategories){
        this.preferCategories = preferedCategories;
        this.email = email;
        this.password = password;
        this.UserName =  userName;
        this.phoneNum = phoneNum;
        this.birthday = birthday;
        this.userID = userID;
        this.rating =rating;
    }

    public User(String email, String password, String userName, String phoneNum, String birthday, String userID, String rating){
        this.email = email;
        this.password = password;
        this.UserName =  userName;
        this.phoneNum = phoneNum;
        this.birthday = birthday;
        this.userID = userID;
        this.rating =rating;
    }

    public User(String email, String userName, String phoneNum, String birthday, String userID){
        this.email = email;
        this.UserName =  userName;
        this.phoneNum = phoneNum;
        this.birthday = birthday;
        this.userID = userID;
    }

    public User(String userID, ArrayList<Items> ownsItems){
        this.userID = userID;
        this.ownsItems = ownsItems;
    }


    public String getPreferCategories() {
        return preferCategories;
    }

    public void setPreferCategories(String preferCategories) {
        this.preferCategories = preferCategories;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRealTimeDataBaseID() {
        return realTimeDataBaseID;
    }

    public void setRealTimeDataBaseID(String realTimeDataBaseID) {
        this.realTimeDataBaseID = realTimeDataBaseID;
    }

    public User(String email , String password ){
        this.email = email;
        this.password = password;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getUserName(){
        return this.UserName;
    }

    public void inputItem(Items items){
        ownsItems.add(items);
    }

    public void removeItemByID(String itemsID){
        for(int i =0; i<ownsItems.size(); i++){
            if(ownsItems.get(i).getItemsID().compareTo(itemsID)==0){
                ownsItems.remove(i);
            }
        }
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profilePictureImagePath) {
        this.profileImagePath = profilePictureImagePath;
    }


    public ArrayList<Items> getOwnsItems() {
        return ownsItems;
    }

    public void setOwnsItems(ArrayList<Items> ownsItems) {
        this.ownsItems = ownsItems;
    }

    public ArrayList<String> getWishList() {
        return wishList;
    }

    public void setWishList(ArrayList<String> wishList) {
        this.wishList = wishList;
    }

    public void printUserUploadedItem(){
        System.out.println("User: "+this.userID);
        for(int i=0; i<this.ownsItems.size(); i++){
            System.out.println(this.ownsItems.get(i).toString());
        }
    }

    public void printUserWishList(){
        System.out.println("User: "+this.userID);
        for(int i=0; i<this.wishList.size(); i++){
            System.out.println(this.wishList.get(i));
        }
    }

    public String toString(){
        return "userID "+this.userID+" user name "+this.UserName +" preferedCategory "+this.preferCategories+" rating "+rating;
    }

}
