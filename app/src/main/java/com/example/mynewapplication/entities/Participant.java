package com.example.mynewapplication.entities;

public class Participant extends User {
    String status;

    public Participant(String email, String password, String userName, String phoneNum, String birthday, String userID, String profileImagePath, String status, String rating){
        super(email, password, userName, phoneNum,  birthday,  userID, rating, profileImagePath);
        this.status = status;
    }

    public Participant(String email, String password, String userName, String phoneNum, String birthday, String userID, String status, String rating){
        super(email, password, userName, phoneNum,  birthday,  userID, rating);
        this.status = status;
    }

    public Participant(){
        super("", "", "", "",  "",  "",  "");
        this.status = "";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
