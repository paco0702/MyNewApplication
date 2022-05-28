package com.example.mynewapplication.Sorting;

import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;

import java.util.ArrayList;

public class BucketSort {
    Bucket [] list = new Bucket[6];
    public class Bucket{
        public class Node{
            Items items;
            Node prev;
            Node next;

            Node(Items items){
                this.items = items;
            }
            public Items getItems() {
                return items;
            }

            public void setItems(Items items) {
                this.items = items;
            }

            public Node getPrev() {
                return prev;
            }

            public void setPrev(Node prev) {
                this.prev = prev;
            }

            public Node getNext() {
                return next;
            }

            public void setNext(Node next) {
                this.next = next;
            }
        }
        Node head;
        Node current;

        int count;
        Bucket(){
            this.head = null;
            this.count = 0;
        }

        public void insert(Items items){
            if(isEmpty()){
                Node node = new Node(items);
                this.head = node;
                this.current = node;
                this.head.next = null;
                count++;
            }else{
                Node node = new Node(items);
                Node currentNode = this.current;
                currentNode.next = node;
                node.prev = currentNode;
                node.next = null;
                this.current = node;
                count++;
            }
        }



        private boolean isEmpty(){
            if (this.count==0){
                return true;
            }else{
                return false;
            }
        }
        public int getCount(){
            return this.count;
        }

        public Node getHead() {
            return head;
        }

        public void setHead(Node head) {
            this.head = head;
        }
    }

    public BucketSort(){
        list[0] = new Bucket();
        list[1] = new Bucket();
        list[2] = new Bucket();
        list[3] = new Bucket();
        list[4] = new Bucket();
        list[5] = new Bucket();
    }

    public void insert(String userRating, Items items){
        int rating = Integer.parseInt(userRating);
       // System.out.println("OwnerID "+items.getOwnerID()+" rating "+rating);
        switch (rating){
            case 1:
                list[0].insert(items);
                break;
            case 2:
                list[1].insert(items);
                break;
            case 3:
                list[2].insert(items);
                break;
            case 4:
                list[3].insert(items);
                break;
            case 5:
                list[4].insert(items);
                break;
            case 6:
                list[5].insert(items);
                break;
        }
    }

    public ArrayList<Items> getOutput(){
        ArrayList<Items> outputList = new ArrayList<>();
        for(int i =5; i>=0; i--){
           // System.out.println("i: "+i+" bucket size "+ " rating "+ (i+1)+ " size "+ list[i].getCount());
            Bucket.Node node =list[i].getHead();
            while(node!=null){
               // System.out.print(node.getItems().getName()+", ");
                outputList.add(node.getItems());
                node = node.next;
            }
            //System.out.println();
        }
        return outputList;
    }
}
