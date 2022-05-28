package com.example.mynewapplication.FragmentClass;

import static com.example.mynewapplication.IndexActivity.theCurrentLoginUser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mynewapplication.AddWishListActivity;
import com.example.mynewapplication.R;
import com.example.mynewapplication.ShowItems.ShowItemActivity;
import com.example.mynewapplication.ViewExchangeRecordActivity;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.NameDisplayAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static User selectedUser;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    private Button goToItemListPage;
    private Button goAddWishListPage;
    private Button viewExchangeRecord;
    private TextView displayUserName;
    private TextView displayRating;
    private TextView displayEmail;
    private TextView displayPhoneNum;
    private CircleImageView userProfilePic;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private User matchedCurrentLoginUser;

    private DatabaseReference realTimeDatabaseForRetrievingUsers;
    //for retrieving item
    private StorageReference mStorageRef; // to store the upload picture

    private DatabaseReference realTimeDatabaseForRetrievingWishList;
    public ArrayList<String> wishList; // change static in the future and use it in add wish list page
    public ArrayList<String> uploadedItemList;
    private ListView wishListView;
    private String saveSelectedUserID = "";
    private User saveSelectedUser = new User();

    // login user's wish list
    private ValueEventListener getUserWishList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println("Function of get User Wishlist with the login user id: "+currentUser.getUid());
            wishList = new ArrayList<>();
            for (DataSnapshot ds : snapshot.getChildren()) {
                System.out.println("Ds.getKey() " +ds.getKey()+" current user id "+ currentUser.getUid());
                if(ds.getKey().compareTo(currentUser.getUid())==0){    // dont return the logined user
                    for (DataSnapshot wishitemds :ds.getChildren()){
                        //System.out.println(wishitemds.getValue().toString());
                        wishList.add(wishitemds.getValue().toString());
                    }
                    break;
                }
            }
            initListView(wishListView, wishList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private ValueEventListener getTargetUserWishList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println("Function of get User Wishlist with the login user id: "+saveSelectedUserID);
            wishList = new ArrayList<>();
            for (DataSnapshot ds : snapshot.getChildren()) {
                System.out.println("Ds.getKey() " +ds.getKey()+" current user id "+ saveSelectedUserID);
                if(ds.getKey().compareTo(saveSelectedUserID)==0){    // dont return the logined user
                    for (DataSnapshot wishitemds :ds.getChildren()){
                        //System.out.println(wishitemds.getValue().toString());
                        wishList.add(wishitemds.getValue().toString());
                    }
                    break;
                }
            }
            initListView(wishListView, wishList);
            System.out.println("wish list done");
            saveSelectedUserID = "";
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    private ValueEventListener getTargetUserUploadedItems = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println("Function of getting target User Uploaded  items user id: "+saveSelectedUserID);
            uploadedItemList = new ArrayList<>();
            ListView ListUploadedItemsView = view.findViewById(R.id.list_name_uploaded_Items);
            for (DataSnapshot ds : snapshot.getChildren()) {
                System.out.println("Ds.getKey() " +ds.getKey()+" target User id "+ saveSelectedUserID);
                if(ds.getKey().compareTo(saveSelectedUserID)==0){    // dont return the logined user
                    for (DataSnapshot items :ds.getChildren()){
                        Items dummy = items.getValue(Items.class);
                        uploadedItemList.add(dummy.getCategory());
                    }
                    break;
                }
            }
            initListView(ListUploadedItemsView, uploadedItemList);
            System.out.println("uploaded item done");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //access database
        mAuth = FirebaseAuth.getInstance();
        getCurrentUser();

        // Inflate the layout for this fragment
        // some view only avaliable for the login user
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        displayUserName = view.findViewById(R.id.nameOfProfile);
        displayRating = view.findViewById(R.id.RatingOfProfile);
        displayEmail = view.findViewById(R.id.emailOfProfile);
        displayPhoneNum = view.findViewById(R.id.phoneNumOfProfile);
        wishListView = view.findViewById(R.id.list_name_wish_list);
        userProfilePic = view.findViewById(R.id.profilePicture);
        // return user profile picture
        //mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference().child("profile_image/"); // always get the first picture

        //displayUserName.setText
        if(selectedUser!=null){
            System.out.println("selected user id "+ selectedUser.getUserID()+" login user "+ currentUser.getUid());
            // if selected user is not login user, only have limited asscess
            displayUserName.setText("User name: "+selectedUser.getUserName());
            displayRating.setText("Rating: "+selectedUser.getRating());
            displayEmail.setText("Email: "+selectedUser.getEmail());
            setupProfilePicture(selectedUser);

            if(selectedUser.getUserID().compareTo(currentUser.getUid().toString())!=0){
                view.findViewById(R.id.gridLayout_action).setEnabled(true);
                view.findViewById(R.id.gridLayout_action).setVisibility(View.VISIBLE);
                view.findViewById(R.id.edit_profile).setEnabled(false);
                view.findViewById(R.id.gridLayout_action).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.exchange_record_profile).setEnabled(false);
                view.findViewById(R.id.exchange_record_profile).setVisibility(View.INVISIBLE);
                saveSelectedUserID = selectedUser.getUserID();
                saveSelectedUser = selectedUser;
                FirebaseDatabase.getInstance().getReference("Allow_Show_PhoneNum").child(selectedUser.getUserID()).addValueEventListener(setShowPhoneNum);
            }

            realTimeDatabaseForRetrievingWishList = FirebaseDatabase.getInstance().getReference("Wish_list");
            realTimeDatabaseForRetrievingWishList.addValueEventListener(getTargetUserWishList);
            realTimeDatabaseForRetrievingWishList = FirebaseDatabase.getInstance().getReference("Items_Users");
            realTimeDatabaseForRetrievingWishList.addValueEventListener(getTargetUserUploadedItems);
        }else{
            //if null, without searching and current user
            realTimeDatabaseForRetrievingUsers = FirebaseDatabase.getInstance().getReference("user");
            realTimeDatabaseForRetrievingUsers.addValueEventListener(matchUser);
            realTimeDatabaseForRetrievingWishList = FirebaseDatabase.getInstance().getReference("Wish_list");
            realTimeDatabaseForRetrievingWishList.addValueEventListener(getUserWishList);
        }

        //for the search view
        selectedUser = null;
        return view;
    }

    private ValueEventListener setShowPhoneNum = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String showNum ="";
            for (DataSnapshot ds : snapshot.getChildren()) {
                showNum = ds.getValue(String.class);
            }
            if(showNum.compareTo("True")==0){
                displayPhoneNum.setText("Phone number: "+saveSelectedUser.getPhoneNum());
            }else if(showNum.compareTo("False")==0){
                displayPhoneNum.setVisibility(View.INVISIBLE);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void setupProfilePicture(User user){
        if(user.getProfileImagePath()!=null && !user.getProfileImagePath().equals("")){
            System.out.println("Image path is "+user.getProfileImagePath());
            mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference().child(user.getProfileImagePath()); // always get the first picture
            try {
                File localfile = File.createTempFile("tempfile", ".jpg");
                mStorageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        userProfilePic.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            userProfilePic.setImageResource(R.drawable.profile_default);
        }
    }

    private ValueEventListener matchUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println("Function of match User with the login user id: "+currentUser.getUid());
            for (DataSnapshot ds : snapshot.getChildren()) {
                User eachUser = ds.getValue(User.class);
                if(ds.getKey().compareTo(currentUser.getUid().toString())==0){
                    System.out.println("Matched user is "+ eachUser.getUserName()+ " user id is "+ ds.getKey()+" login user id is "+ currentUser.getUid());
                    matchedCurrentLoginUser = new User(eachUser.getEmail(),  eachUser.getUserName(), eachUser.getPhoneNum(), eachUser.getBirthday(), ds.getKey().toString());
                    //System.out.println("profile Picture Image Path " + ds.getRef().child("profileImagePath").toString());
                    matchedCurrentLoginUser.setRating(eachUser.getRating());
                    matchedCurrentLoginUser.setPreferCategories(eachUser.getPreferCategories());
                    matchedCurrentLoginUser.setProfileImagePath(eachUser.getProfileImagePath());
                    System.out.println("profile Picture Image Path " + eachUser.getProfileImagePath());
                    break;
                }
            }
            view.findViewById(R.id.gridLayout_action).setEnabled(true);
            view.findViewById(R.id.gridLayout_action).setVisibility(View.VISIBLE);
            view.findViewById(R.id.edit_profile).setEnabled(true);
            view.findViewById(R.id.gridLayout_action).setVisibility(View.VISIBLE);

            displayUserName.setText("User name: "+matchedCurrentLoginUser.getUserName());
            displayRating.setText("Rating: "+matchedCurrentLoginUser.getRating());
            displayEmail.setText("Email: "+matchedCurrentLoginUser.getEmail());

            goToItemListPage = view.findViewById(R.id.item_list_button_fragment_home);
            goToItemListPage.setOnClickListener(enterListOfItemPage);
            goAddWishListPage = view.findViewById(R.id.addWishList_fragment_home);
            goAddWishListPage.setOnClickListener(enterAddWishListPage);
            viewExchangeRecord = view.findViewById(R.id.exchange_record_profile);
            viewExchangeRecord.setOnClickListener(enterExchnageRecord);
            getUploadItemList();
            setupProfilePicture(matchedCurrentLoginUser);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };



    private void getUploadItemList(){
        ArrayList<String> uploadedItemsnames = new ArrayList<>();
        int length = theCurrentLoginUser.getOwnsItems().size(); // from index page
        for(int i=0; i<length ; i++){
            uploadedItemsnames.add(theCurrentLoginUser.getOwnsItems().get(i).getCategory());
        }
        ListView ListUploadedItemsView = view.findViewById(R.id.list_name_uploaded_Items);
        initListView(ListUploadedItemsView, uploadedItemsnames);
    }

    private void getCurrentUser(){
        currentUser = mAuth.getCurrentUser();
    }


    View.OnClickListener enterListOfItemPage = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v==goToItemListPage){
                setEnterListOfItemPage();
            }
        }
    };

    View.OnClickListener enterAddWishListPage = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v==goAddWishListPage){
                setEnterAddWishListPage();
            }
        }
    };

    View.OnClickListener enterExchnageRecord = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v==viewExchangeRecord){
                setEnterExchangeRecordPage();
            }
        }
    };
    // go to list items page
    private void setEnterListOfItemPage(){
        Intent intent = new Intent(getActivity(), ShowItemActivity.class);
        startActivity(intent);
    }

    // go to list items page
    private void setEnterAddWishListPage(){
        Intent intent = new Intent(getActivity(), AddWishListActivity.class);
        startActivity(intent);
    }

    //go to exchange record
    private void setEnterExchangeRecordPage(){
        Intent intent = new Intent(getActivity(), ViewExchangeRecordActivity.class);
        startActivity(intent);
    }
    // list the friends
    private void initListView(ListView ListView, ArrayList<String> wishList){
        // get Friend List
        NameDisplayAdapter adapter  = new NameDisplayAdapter(getActivity(), R.layout.wish_list_name_layout, wishList);
        ListView.setAdapter(adapter);
    }
}