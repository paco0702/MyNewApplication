package com.example.mynewapplication.entities;

import java.util.ArrayList;

public class Items {
    String name;
    String owner;
    String ownerEmail;
    String category;
    String itemsID;
    String ownerID;
    String status;
    String value;
    String description;

    private ArrayList<String> pathForImagesPictures = new ArrayList<>();

    public Items(String name, String owner, String ownerID, String ownerEmail, String category, String value, ArrayList<String> storeOfPath, String description){
        this.name = name;
        this.owner = owner;
        this.ownerID = ownerID;
        this.ownerEmail = ownerEmail;
        this.category = category;
        this.value = value;
        this.status = status;
        this.pathForImagesPictures= storeOfPath;
        this.description = description;
    }

    public Items(String name, String owner, String ownerID, String ownerEmail, String category, String value, String status, ArrayList<String> storeOfPath, String description){
        this.name = name;
        this.owner = owner;
        this.ownerID = ownerID;
        this.ownerEmail = ownerEmail;
        this.category = category;
        this.value = value;
        this.status = status;
        this.pathForImagesPictures= storeOfPath;
        this.description = description;
    }

    public Items(String name, String owner, String ownerID, String ownerEmail, String category, String value, String status, ArrayList<String> storeOfPath){
        this.name = name;
        this.owner = owner;
        this.ownerID = ownerID;
        this.ownerEmail = ownerEmail;
        this.category = category;
        this.value = value;
        this.status = status;
        this.pathForImagesPictures= storeOfPath;
    }


    public Items(String category, String name, String owner, String ownerID, String ownerEmail, String status, String value){
        this.name = name;
        this.owner = owner;
        this.ownerID = ownerID;
        this.ownerEmail = ownerEmail;
        this.category = category;
        this.value = value;
        this.status = status;
        this.pathForImagesPictures= new ArrayList<>();
    }

    public Items(String name, String owner, String ownerID, String ownerEmail, String category, String value, ArrayList<String> storeOfPath){
        this.name = name;
        this.owner = owner;
        this.ownerID = ownerID;
        this.ownerEmail = ownerEmail;
        this.category = category;
        this.value = value;
        this.status = "free";
        this.pathForImagesPictures= storeOfPath;
    }

    public Items(String name, String owner, String ownerID, String ownerEmail, String category, String value){
        this.name = name;
        this.owner = owner;
        this.ownerID = ownerID;
        this.ownerEmail = ownerEmail;
        this.category = category;
        this.value = value;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Items(){
        this("", "", "", "" ,"" ,"","", null, "");
    }
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItemsID() {
        return itemsID;
    }

    public void setItemsID(String itemsID) {
        this.itemsID = itemsID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<String> getPathForImagesPictures() {
        return pathForImagesPictures;
    }

    public void setPathForImagesPictures(ArrayList<String> pathForImagesPictures) {
        this.pathForImagesPictures = pathForImagesPictures;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        return "Item id: "+this.itemsID+"\n"+
                " name: "+this.name+ " owner: "+this.owner+"\n"+
                " owner ID: "+ownerID+" category: "+ this.category+" value: "+ this.value;
    }

}
