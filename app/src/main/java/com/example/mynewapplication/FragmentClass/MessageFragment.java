package com.example.mynewapplication.FragmentClass;

import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.mynewapplication.LLS;
import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.Participant;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.NotificationAdapter;
import com.example.mynewapplication.recyclerViewstorage.PendListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private DatabaseReference realTimeDatabaseForNotification;
    private DatabaseReference realTimeDatabaseForAllEvent;
    View view;

    //retrieve the User information
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    ArrayList<String> eventKey = new ArrayList<>();
    ArrayList<String> eventStatus = new ArrayList<>();

    private ValueEventListener getNotification= new ValueEventListener() { // get the event ID for each notification
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            eventKey = new ArrayList<>();
            eventStatus = new ArrayList<>();

            for(DataSnapshot ds: snapshot.getChildren()){
                eventKey.add(ds.getKey());
                System.out.println("event "+ds.getKey()+ " status "+ ds.child("status").getValue(String.class));
                eventStatus.add(ds.child("status").getValue(String.class));
            }

            if(eventKey.size()==0){
                System.out.println("No notification"); // if the list is empty then do process

            }else{
                realTimeDatabaseForAllEvent.addValueEventListener(getEvent);
            }

        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private ValueEventListener getEvent = new ValueEventListener() { // get back the transaction information from All_event
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ArrayList<LLS> allGroupInform = new ArrayList<>();
            ArrayList<Participant> loginParticipants = new ArrayList<>();
            for (int i=0; i<eventKey.size(); i++){
                for(DataSnapshot ds: snapshot.getChildren()){ // for each event
                    if(ds.getKey().compareTo(eventKey.get(i))==0){
                        DataSnapshot group = ds.child("group");
                        DataSnapshot participants = ds.child("participants").child(theCurrentLoginUser.getUserID());
                        LLS dummyGroup = new LLS();
                        for(DataSnapshot num: group.getChildren()){
                            System.out.println("event Num key is" +num.getKey());
                            Items eventItem = new Items();
                            User provider = new User();
                            User receiver = new User();
                            for(DataSnapshot eachItem: num.child("Item").getChildren()){
                                eventItem = eachItem.getValue(Items.class);
                            }
                            for(DataSnapshot eachProvider: num.child("Item provider").getChildren()){
                                provider = eachProvider.getValue(User.class);
                            }
                            for(DataSnapshot eachReceiver: num.child("Item receiver").getChildren()){
                                receiver = eachReceiver.getValue(User.class);
                            }
                            dummyGroup.insert(provider, receiver, eventItem);
                        } //group information

                        loginParticipants.add(participants.getValue(Participant.class)); // for each event, get the login user participant status
                        allGroupInform.add(dummyGroup);
                    }
                }

            }
            initNotificationList(view, mListView, allGroupInform, eventStatus, eventKey, loginParticipants);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_message, container, false);
        // Inflate the layout for this fragment
        //access database
        mAuth = FirebaseAuth.getInstance();
        getCurrentUser();

        mListView = (ListView) view.findViewById(R.id.notification_view);


        realTimeDatabaseForNotification = FirebaseDatabase.getInstance().getReference("Notification/"+currentUser.getUid());
        realTimeDatabaseForAllEvent = FirebaseDatabase.getInstance().getReference("All_event");
        realTimeDatabaseForNotification.addValueEventListener(getNotification);


        return view;
    }

    private void getCurrentUser(){
        currentUser = mAuth.getCurrentUser();
    }

    // list the pending event
    private void initNotificationList(View view, ListView mListView, ArrayList<LLS> list_of_event,  ArrayList<String> allGroupInformStatus ,  ArrayList<String> eventKeyList, ArrayList<Participant> loginParticipants){
        if(getActivity()!=null){
            System.out.println("stepping in notificaiton");
            NotificationAdapter adapter = new NotificationAdapter(getActivity(), R.layout.notification_showmessage,  list_of_event,  allGroupInformStatus,  eventKeyList, loginParticipants);
            mListView.setAdapter(adapter);
        }
    }

}