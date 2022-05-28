package com.example.mynewapplication;

import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;

public class LLS{
    LLSNode head;
    int count;
    LLSNode current;
    String targetCategory;
    public class LLSNode{
        String wishUser;
        String giveUser;
        String wishItemCategory;
        // better version


        LLSNode prev;
        LLSNode toHead;
        LLSNode next;
        User provider; //wishuser
        User receiver; //providerUser
        Items exchange_item; //wishItem

        //node for exchange group
                      //User providerUser, User wishuser, Items wishItem
        public LLSNode(User provider, User receiver, Items exchange_item){
            this.provider = provider;
            this.receiver = receiver;
            this.exchange_item = exchange_item;
            this.next = null;
            this.prev = null;
            this.toHead = null;
        }
        // node for suggestion event // provider      // receiver   // item category
        public LLSNode(String wishUser, String giveUser, String wishItemCategory){
            this.wishUser = wishUser;
            this.giveUser = giveUser;
            this.wishItemCategory = wishItemCategory;
            this.next = null;
            this.prev = null;
            this.toHead = null;
        }


        public LLSNode(){
            this.wishUser = "";
            this.giveUser = "";
            this.next = null;
            this.prev = null;
            this.toHead = null;
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

        public void setExchange_item(Items exchange_item) {
            this.exchange_item = exchange_item;
        }

        public LLSNode getNext(){
            return this.next;
        }

    }

    public LLS(){
        this.head = new LLSNode();
        this.current = new LLSNode();
        this.count = 0;
        this.targetCategory = "";
    }

    public LLSNode getHead(){
        return this.head;
    }

    public String getTargetCategory() {
        return targetCategory;
    }

    public void setTargetCategory(String targetCategory) {
        this.targetCategory = targetCategory;
    }

    public void insert(String wishUser, String giveUser, String wishItemCategory){
        if(isEmpty()==true){
            this.head = new LLSNode(wishUser, giveUser, wishItemCategory);
            this.head.next = null;
            this.head.prev = null;
            this.current.next = this.head;
            this.count++;
            //System.out.println("Inserted "+ this.head.wishUser+" "+this.head.giveUser);
        }else{
            LLSNode newNode = new LLSNode(wishUser, giveUser, wishItemCategory);
            LLSNode current = this.current.next;
            //System.out.println("current "+current.wishUser+", "+current.giveUser);
            current.next = newNode;
            newNode.prev = current;
            newNode.toHead = this.head;
            //System.out.println("to head "+newNode.toHead.wishUser+", "+newNode.toHead.giveUser);
            this.current.next = newNode;
            this.count++;
            //System.out.println("Inserted "+ this.current.next.wishUser+" "+this.current.next.giveUser);
        }
    }

    public void insert(User provider, User receiver, Items exchange_item){
        if(isEmpty()==true){
            this.head = new LLSNode(provider, receiver, exchange_item);
            this.head.next = null;
            this.head.prev = null;
            this.current.next = this.head;
            this.count++;
            //System.out.println("Inserted "+ this.head.provider.getUserID()+" "+this.head.receiver.getUserID());
        }else{
            LLSNode newNode = new LLSNode(provider, receiver, exchange_item);
            LLSNode current = this.current.next;
            //System.out.println("current "+current.provider.getUserID()+", "+current.receiver.getUserID());
            current.next = newNode;
            newNode.prev = current;
            newNode.toHead = this.head;
            //System.out.println("to head "+newNode.toHead.provider.getUserID()+", "+newNode.toHead.receiver.getUserID());
            this.current.next = newNode;
            this.count++;
            //System.out.println("Inserted "+ this.current.next.provider.getUserID()+" "+this.current.next.receiver.getUserID());
        }
    }

    //for start exchange event
    public boolean isExchangeCyclic(){
        if (isEmpty()) {
            //System.out.println("the list is empty");
            return false;
        }else if(this.count==1){
            //System.out.println("Only have one node");
            return false;
        }else if(this.current.next.receiver.getUserID().compareTo(this.current.next.toHead.provider.getUserID())==0){
            //System.out.println(" is cyclic "+this.current.next.receiver.getUserID() +" and "+this.current.next.toHead.provider.getUserID());
            return true;
        }else {
            return false;
        }
    }

    public boolean isCyclic(){
        if (isEmpty()) {
            //System.out.println("the list is empty");
            return false;
        }else if(this.count==1){
            //System.out.println("Only have one node");
            return false;
        }else if(this.current.next.giveUser.compareTo(this.current.next.toHead.wishUser)==0){
            //System.out.println(" is cyclic "+this.current.next.giveUser +" and "+this.current.next.toHead.wishUser);
            return true;
        }else {
            return false;
        }
    }

    public boolean isEmpty(){
        if(count ==0){
            return true;
        }else {return false;}
    }

    public int getSize(){
        return this.count;
    }

    public void printNode(){
        LLSNode node = this.head;
        //System.out.println("Target wish item is "+this.targetCategory);
        while(node!=null){
            System.out.println("Wish item userID "+node.wishUser+" item offerID "+ node.giveUser+" for category "+node.wishItemCategory);
            node = node.next;
        }
        System.out.println();
    }

    public void printExchangeNode(){
        LLSNode node = this.head;
        System.out.println("Target wish item is "+this.targetCategory);
        while(node!=null){
            System.out.println("Provider userID "+node.provider.getUserID()+" item receiver "+ node.receiver.getUserID()+" for time "+node.exchange_item.getItemsID()+", "+node.exchange_item.getName());
            node = node.next;
        }
        System.out.println();
    }

}
