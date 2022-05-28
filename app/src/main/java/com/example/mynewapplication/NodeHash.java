package com.example.mynewapplication;

public class NodeHash {
    String value;
    int index;

    NodeHash(String value, int index){
            this.value = value;
            this.index = index; //e.g. 0.841564.. *10 = 8.5645 = int 8
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    NodeHash(){
        this.index = 0;
        this.value = "";
    }

}

