package com.example.mynewapplication;

import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;

public class Node {
    String wishUser;
    String giveUser;
    String wishItemCategory;


    User provider;
    User receiver;
    Items exchange_item;
    String eventID;
    //node for exchange group
    public Node(User provider, User receiver, Items exchange_item){
        this.provider = provider;
        this.receiver = receiver;
        this.exchange_item = exchange_item;

    }

    public Node(User provider, User receiver, Items exchange_item, String eventID){
        this.provider = provider;
        this.receiver = receiver;
        this.exchange_item = exchange_item;
        this.eventID = eventID;

    }

    // node for suggestion event // provider      // receiver   // item category
    public Node(String wishUser, String giveUser, String wishItemCategory){
        this.wishUser = wishUser;
        this.giveUser = giveUser;
        this.wishItemCategory = wishItemCategory;

    }

    public Node(){
        this.wishUser = "";
        this.giveUser = "";

    }

    public String getWishUser() {
        return wishUser;
    }

    public void setWishUser(String wishUser) {
        this.wishUser = wishUser;
    }

    public String getGiveUser() {
        return giveUser;
    }

    public void setGiveUser(String giveUser) {
        this.giveUser = giveUser;
    }

    public User getProvider() {
        return provider;
    }

    public void setProvider(User provider) {
        this.provider = provider;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Items getExchange_item() {
        return exchange_item;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setExchange_item(Items exchange_item) {
        this.exchange_item = exchange_item;
    }


}

