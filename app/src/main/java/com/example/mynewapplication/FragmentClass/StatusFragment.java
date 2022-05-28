package com.example.mynewapplication.FragmentClass;

import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.mynewapplication.LLS;
import com.example.mynewapplication.Node;
import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.HistoryListAdapter;
import com.example.mynewapplication.recyclerViewstorage.PendListAdapter;
import com.example.mynewapplication.recyclerViewstorage.ProcessingListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatusFragment() {
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
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
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

    View view;
    Button history_list;
    Button pending_list;
    Button progressing_list;


    ListView mListView;
    private DatabaseReference realTimeDatabaseForUploadingExchangePending;
    private DatabaseReference realTimeDatabaseForUploadingExchangeHistory;
    private DatabaseReference realTimeDatabaseForUploadingExchangeProgress;

    //pending event
    private ArrayList <Node> pendingList = new ArrayList<>();

    ValueEventListener retrieve_pendingList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ArrayList<LLS> allGroupInform = new ArrayList<>();
            ArrayList<String> eventKey = new ArrayList<>();
            for (DataSnapshot ds : snapshot.getChildren()) {
                //TODO continue pending event
                if(ds.getKey().compareTo(theCurrentLoginUser.getUserID())==0){
                    for(DataSnapshot event: ds.getChildren()){
                        System.out.println(ds.getKey()+" has event "+event.getKey());
                        eventKey.add(event.getKey());
                        LLS dummyGroup = new LLS();
                        for (DataSnapshot eventNum: event.getChildren()){
                            System.out.println("event Num key is" +eventNum.getKey());
                            Items eventItem = new Items();
                            User provider = new User();
                            User receiver = new User();
                            for(DataSnapshot eachItem: eventNum.child("Item").getChildren()){
                                eventItem = eachItem.getValue(Items.class);
                            }
                            for(DataSnapshot eachProvider: eventNum.child("Item provider").getChildren()){
                                provider = eachProvider.getValue(User.class);
                            }
                            for(DataSnapshot eachReceiver: eventNum.child("Item receiver").getChildren()){
                                receiver = eachReceiver.getValue(User.class);
                            }

                            dummyGroup.insert(provider, receiver, eventItem);
                        }
                        allGroupInform.add(dummyGroup);
                    }
                }
            }
            //after all the loop
            // send dummpyGroup to the Listview adapter to list the event some when user click can view the details
            initPendingViewList(view, mListView, allGroupInform,  eventKey);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener retrieve_ProgressingList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ArrayList<LLS> allGroupInform = new ArrayList<>();
            ArrayList<String> eventKey = new ArrayList<>();
            for (DataSnapshot ds : snapshot.getChildren()) {
                //TODO continue pending event
                if(ds.getKey().compareTo(theCurrentLoginUser.getUserID())==0){
                    for(DataSnapshot event: ds.getChildren()){
                        System.out.println(ds.getKey()+" has event "+event.getKey());
                        eventKey.add(event.getKey());
                        LLS dummyGroup = new LLS();
                        for (DataSnapshot eventNum: event.getChildren()){
                            System.out.println("event Num key is" +eventNum.getKey());
                            Items eventItem = new Items();
                            User provider = new User();
                            User receiver = new User();
                            for(DataSnapshot eachItem: eventNum.child("Item").getChildren()){
                                eventItem = eachItem.getValue(Items.class);
                            }
                            for(DataSnapshot eachProvider: eventNum.child("Item provider").getChildren()){
                                provider = eachProvider.getValue(User.class);
                            }
                            for(DataSnapshot eachReceiver: eventNum.child("Item receiver").getChildren()){
                                receiver = eachReceiver.getValue(User.class);
                            }

                            dummyGroup.insert(provider, receiver, eventItem);
                        }
                        allGroupInform.add(dummyGroup);
                    }
                }
            }
            //after all the loop
            // send dummpyGroup to the Listview adapter to list the event some when user click can view the details
            initProcessingViewList(view, mListView, allGroupInform,  eventKey);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener retrieve_historyList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ArrayList<LLS> allGroupInform = new ArrayList<>();
            ArrayList<String> eventKey = new ArrayList<>();
            for (DataSnapshot ds : snapshot.getChildren()) {
                //TODO continue pending event
                if(ds.getKey().compareTo(theCurrentLoginUser.getUserID())==0){
                    for(DataSnapshot event: ds.getChildren()){
                        System.out.println(ds.getKey()+" has event "+event.getKey());
                        eventKey.add(event.getKey());
                        LLS dummyGroup = new LLS();
                        for (DataSnapshot eventNum: event.getChildren()){
                            System.out.println("event Num key is" +eventNum.getKey());
                            Items eventItem = new Items();
                            User provider = new User();
                            User receiver = new User();
                            for(DataSnapshot eachItem: eventNum.child("Item").getChildren()){
                                eventItem = eachItem.getValue(Items.class);
                            }
                            for(DataSnapshot eachProvider: eventNum.child("Item provider").getChildren()){
                                provider = eachProvider.getValue(User.class);
                            }
                            for(DataSnapshot eachReceiver: eventNum.child("Item receiver").getChildren()){
                                receiver = eachReceiver.getValue(User.class);
                            }

                            dummyGroup.insert(provider, receiver, eventItem);
                        }
                        allGroupInform.add(dummyGroup);
                    }
                }
            }
            //after all the loop
            // send dummpyGroup to the Listview adapter to list the event some when user click can view the details
            initHistoryViewList(view, mListView, allGroupInform, eventKey);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_status, container, false);
        history_list = (Button) view.findViewById(R.id.exchange_history_button);
        pending_list = (Button) view.findViewById(R.id.exchange_pending_button);
        progressing_list = (Button) view.findViewById(R.id.exchange_processing_button);

        mListView = (ListView) view.findViewById(R.id.exchange_event_view);
        realTimeDatabaseForUploadingExchangePending = FirebaseDatabase.getInstance().getReference("Exchange_event/pending");
        realTimeDatabaseForUploadingExchangeHistory = FirebaseDatabase.getInstance().getReference("Exchange_event/history");
        realTimeDatabaseForUploadingExchangeProgress = FirebaseDatabase.getInstance().getReference("Exchange_event/processing");

        pending_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realTimeDatabaseForUploadingExchangePending.addValueEventListener(retrieve_pendingList);
            }
        });

        progressing_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realTimeDatabaseForUploadingExchangeProgress.addValueEventListener(retrieve_ProgressingList);
            }
        });

        history_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realTimeDatabaseForUploadingExchangeHistory.addValueEventListener(retrieve_historyList);
            }
        });
        return view;
    }

    // list the pending event
    private void initPendingViewList(View view, ListView mListView, ArrayList<LLS> list_of_pending, ArrayList<String> eventKeyList){
        // get Friend List
        if(getActivity()!=null) {
            PendListAdapter adapter = new PendListAdapter(getActivity(), R.layout.pending_list_showmessages, list_of_pending, eventKeyList);
            mListView.setAdapter(adapter);
        }
    }

    // list the pending event
    private void initProcessingViewList(View view, ListView mListView, ArrayList<LLS> list_of_processing, ArrayList<String> eventKeyList){
        // get Friend List
        if(getActivity()!=null) {
            ProcessingListAdapter adapter = new ProcessingListAdapter(getActivity(), R.layout.processing_list_showmessage, list_of_processing, eventKeyList);
            mListView.setAdapter(adapter);
        }
    }

    private void initHistoryViewList(View view, ListView mListView, ArrayList<LLS> list_of_processing, ArrayList<String> eventKeyList){
        if(getActivity()!=null) {
            HistoryListAdapter adapter = new HistoryListAdapter(getActivity(), R.layout.processing_list_showmessage, list_of_processing, eventKeyList);
            mListView.setAdapter(adapter);
        }
    }
}