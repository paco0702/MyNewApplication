package com.example.mynewapplication.FragmentClass;

import static com.example.mynewapplication.IndexActivity.exchangeGroup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynewapplication.IndexActivity;
import com.example.mynewapplication.InformationBaxActivity;
import com.example.mynewapplication.LLS;
import com.example.mynewapplication.R;
import com.example.mynewapplication.UploadActivity;
import com.example.mynewapplication.entities.Events;
import com.example.mynewapplication.entities.Participant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExchangeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExchangeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExchangeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExchangeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExchangeFragment newInstance(String param1, String param2) {
        ExchangeFragment fragment = new ExchangeFragment();
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
    ImageButton button1;
    ImageButton button2;
    ImageButton button3;
    ImageButton button4;
    ImageButton button5;
    ImageButton button6;

    TextView showInformOne;
    TextView showInformTwo;
    TextView showInformThree;
    TextView showInformFour;
    TextView showInformFive;
    TextView showInformSix;

    Button exchange_group_submit;
    Button reset;

    //upload the group the database
    private DatabaseReference realTimeDatabaseForUploadingExchangeEvent;
    private DatabaseReference realTimeDatabaseForUploadingExchangeEventProcessing;
    private DatabaseReference databaseUploadLocation;
    private DatabaseReference realTimeDatabaseForAllEventRecord;
    //show message to user that the progress
    private ProgressDialog mProgressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("Running the exchange fragment");
        mProgressDialog = new ProgressDialog(getActivity());

        view = inflater.inflate(R.layout.fragment_exchange, container, false);
        //System.out.println("Frag tag is "+this.getParentFragmentManager().findFragmentByTag("fragExchange"));
        button1 = (ImageButton) view.findViewById(R.id.imageButton1);
        button2 = (ImageButton) view.findViewById(R.id.imageButton2);
        button3 = (ImageButton) view.findViewById(R.id.imageButton3);
        button4 = (ImageButton) view.findViewById(R.id.imageButton4);
        button5 = (ImageButton) view.findViewById(R.id.imageButton5);
        button6 = (ImageButton) view.findViewById(R.id.imageButton6);

        showInformOne = (TextView) view.findViewById(R.id.textView1_showInform);
        showInformTwo = (TextView) view.findViewById(R.id.textView2_showInform);
        showInformThree = (TextView) view.findViewById(R.id.textView3_showInform);
        showInformFour = (TextView) view.findViewById(R.id.textView4_showInform);
        showInformFive = (TextView) view.findViewById(R.id.textView5_showInform);
        showInformSix = (TextView) view.findViewById(R.id.textView6_showInform);

        exchange_group_submit = (Button) view.findViewById(R.id.exchange_group_submit);
        reset = (Button) view.findViewById(R.id.exchange_fragment_group_reset);

        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        button5.setEnabled(false);
        button6.setEnabled(false);

        realTimeDatabaseForUploadingExchangeEvent = FirebaseDatabase.getInstance().getReference("Exchange_event/pending");
        realTimeDatabaseForUploadingExchangeEventProcessing = FirebaseDatabase.getInstance().getReference("Exchange_event/processing");
        realTimeDatabaseForAllEventRecord = FirebaseDatabase.getInstance().getReference("All_event");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InformationBaxActivity.class);
                startActivityForResult(intent, 10000);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InformationBaxActivity.class);
                startActivityForResult(intent, 10000);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InformationBaxActivity.class);
                startActivityForResult(intent, 10000);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InformationBaxActivity.class);
                startActivityForResult(intent, 10000);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InformationBaxActivity.class);
                startActivityForResult(intent, 10000);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InformationBaxActivity.class);
                startActivityForResult(intent, 10000);
            }
        });

        exchange_group_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage("Exchange event uploading.");
                mProgressDialog.show();
                if(exchangeGroup.isExchangeCyclic()){
                    System.out.println("Going to submit to database and send notification to other user");
                    UploadExchangeEvent();
                }else{
                    //TODO
                    //show the dialog box that there no group found
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(),"Group can't be found, upload fail",Toast.LENGTH_SHORT).show();
                }
            }
        });



        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reset_event();
            }
        });

        if(!exchangeGroup.isEmpty()){
            showInformOne.setText(getNodeInform(exchangeGroup.getHead()));
            button1.setEnabled(false);
            button1.setVisibility(View.INVISIBLE);
            button2.setEnabled(true);

            if(!exchangeGroup.isEmpty() && exchangeGroup.getHead().getNext()!=null){
                showInformTwo.setText(getNodeInform(exchangeGroup.getHead().getNext()));
                button2.setEnabled(false);
                button2.setVisibility(View.INVISIBLE);
                button3.setEnabled(true);

                if(!exchangeGroup.isEmpty() && exchangeGroup.getHead().getNext().getNext()!=null){
                    showInformTwo.setText(getNodeInform(exchangeGroup.getHead().getNext().getNext()));
                    button3.setEnabled(false);
                    button3.setVisibility(View.INVISIBLE);
                    button4.setEnabled(true);

                    if(!exchangeGroup.isEmpty() && exchangeGroup.getHead().getNext().getNext().getNext()!=null){
                        showInformTwo.setText(getNodeInform(exchangeGroup.getHead().getNext().getNext().getNext()));
                        button4.setEnabled(false);
                        button4.setVisibility(View.INVISIBLE);
                        button5.setEnabled(true);

                        if(!exchangeGroup.isEmpty() && exchangeGroup.getHead().getNext().getNext().getNext().getNext()!=null){
                            showInformTwo.setText(getNodeInform(exchangeGroup.getHead().getNext().getNext().getNext().getNext()));
                            button5.setEnabled(false);
                            button5.setVisibility(View.INVISIBLE);
                            button6.setEnabled(true);

                            if(!exchangeGroup.isEmpty() && exchangeGroup.getHead().getNext().getNext().getNext().getNext().getNext()!=null){
                                showInformTwo.setText(getNodeInform(exchangeGroup.getHead().getNext().getNext().getNext().getNext().getNext()));
                                button6.setEnabled(false);
                                button6.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            }
        }


        return view;
    }

    private void Reset_event(){
        exchangeGroup = new LLS();; // stand for LinkedList
        showInformOne.setText("");
        showInformTwo.setText("");
        showInformThree.setText("");
        showInformFour.setText("");
        showInformFive.setText("");
        showInformSix.setText("");
        button1.setEnabled(true);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        button5.setEnabled(false);
        button6.setEnabled(false);
        button1.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        button3.setVisibility(View.VISIBLE);
        button4.setVisibility(View.VISIBLE);
        button5.setVisibility(View.VISIBLE);
        button6.setVisibility(View.VISIBLE);
    }

    private String getNodeInform(LLS.LLSNode node){
        return "Item Provider: "+node.getProvider().getUserName()+"\n"+
                "Items: "+ node.getExchange_item().getName()+"\n"+
                "Receiver: "+node.getReceiver().getUserName();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (((requestCode == 10000)) && (resultCode == Activity.RESULT_OK)){
            System.out.println("Recreate the fragment now");
            getActivity().getSupportFragmentManager().beginTransaction().detach(ExchangeFragment.this).attach(ExchangeFragment.this).commit();
            //ft.detach(frag).attach(frag).commit();
        }

    }

    private void UploadExchangeEvent(){
        String event_id = getEventID();

        LLS.LLSNode node = exchangeGroup.getHead();
        int count = 0;

        //seperate pending and processing (event starter is in processin)
        ArrayList<String> participate_userID = new ArrayList<>(); // for pending list user ID
        String initiate = node.getProvider().getUserID();
        while(node!=null){  // processing for who start the evnet , already accpet becasue they start the event
            realTimeDatabaseForUploadingExchangeEventProcessing.child(initiate).child(event_id).child(""+(count+1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
            realTimeDatabaseForUploadingExchangeEventProcessing.child(initiate).child(event_id).child(""+(count+1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
            realTimeDatabaseForUploadingExchangeEventProcessing.child(initiate).child(event_id).child(""+(count+1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());

            if(count>0){
                participate_userID.add(node.getProvider().getUserID());
                // storing to all event dataset
                realTimeDatabaseForAllEventRecord.child(event_id).child("participants").child(node.getProvider().getUserID()).setValue(new Participant(node.getProvider().getEmail(),node.getProvider().getPassword(), node.getProvider().getUserName(),
                        node.getProvider().getPhoneNum(), node.getProvider().getBirthday(), node.getProvider().getUserID() ,node.getProvider().getProfileImagePath(), "Waiting"));

                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child(""+(count+1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child(""+(count+1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child(""+(count+1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());
            }else{

                realTimeDatabaseForAllEventRecord.child(event_id).child("participants").child(node.getProvider().getUserID()).setValue(new Participant(node.getProvider().getEmail(),node.getProvider().getPassword(), node.getProvider().getUserName(),
                        node.getProvider().getPhoneNum(), node.getProvider().getBirthday(), node.getProvider().getUserID() ,node.getProvider().getProfileImagePath(), "Accepted"));
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child(""+(count+1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child(""+(count+1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                realTimeDatabaseForAllEventRecord.child(event_id).child("group").child(""+(count+1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());
            }
            count++;
            node = node.getNext();
        }

        //add to all event dataset
        realTimeDatabaseForAllEventRecord.child(event_id).child("status").setValue("Waiting");

        //pending is rest of the user
        for (int i=0; i<participate_userID.size(); i++){
            count = 0;
            node = exchangeGroup.getHead();
            while(node!=null) { // uploading as pending (for other participate ) each participate need group details
                realTimeDatabaseForUploadingExchangeEvent.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item provider").child(node.getProvider().getUserID()).setValue(node.getProvider());
                realTimeDatabaseForUploadingExchangeEvent.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item").child(node.getExchange_item().getItemsID()).setValue(node.getExchange_item());
                realTimeDatabaseForUploadingExchangeEvent.child(participate_userID.get(i)).child(event_id).child("" + (count + 1)).child("Item receiver").child(node.getReceiver().getUserID()).setValue(node.getReceiver());
                count++;
                node = node.getNext();
            }
        }


        // finish upload , reset the page
        Reset_event();

        // show message that upload finish
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(),"Exchange event uploaded.",Toast.LENGTH_SHORT).show();

    }

    private String getEventID(){
        int lengthOfTheID = 10;
        String id = "";
        for (int i =0; i<lengthOfTheID; i++){
            id = id + randomChooseAChar();
        }
        return id;
    }

    private char randomChooseAChar(){
        char [] valueRange= "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        int upperBound = valueRange.length;
        int n = (int)(Math.random() * upperBound );
        return valueRange[n];
    }
}