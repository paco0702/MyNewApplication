package com.example.mynewapplication.entities;

import com.example.mynewapplication.LLS;
import java.util.ArrayList;

public class Events {
    LLS groups;
    String status;
    ArrayList<Participant> participants;

    public Events(LLS groups, String status, ArrayList<Participant>participants){
        this.groups = groups;
        this.status = status;
        this.participants = participants;
    }

    public Events(){

    }

    public LLS getGroups() {
        return groups;
    }

    public void setGroups(LLS groups) {
        this.groups = groups;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }
}
