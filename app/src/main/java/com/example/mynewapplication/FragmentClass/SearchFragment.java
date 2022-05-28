package com.example.mynewapplication.FragmentClass;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.UserListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;
    ListView mListView;

    //retrieve the User information
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference realTimeDatabaseForRetrievingUsers;
    //for retrieving item
    private StorageReference mStorageRef; // to store the upload picture
    ArrayList<User> UserList;
    //for retrieveing item 's first picture

    private ValueEventListener getUserList = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println("Function of get User list with the login user id: "+currentUser.getUid());
            for (DataSnapshot ds : snapshot.getChildren()) {
                User eachUser = ds.getValue(User.class);
                System.out.println("Ds.getKey() " +ds.getKey()+" current user id "+ currentUser.getUid());
                    if(ds.getKey().compareTo(currentUser.getUid())!=0){    // dont return the logined user
                        User insertToList;
                        System.out.println("retrieved user id "+ ds.getKey());
                        insertToList = new User(eachUser.getEmail(), eachUser.getPassword(),
                                eachUser.getUserName(), eachUser.getPhoneNum(), eachUser.getBirthday(), ds.getKey(), eachUser.getRating(), eachUser.getPreferCategories());
                        if(eachUser.getProfileImagePath()!=null && !eachUser.getProfileImagePath().equals("")){
                            insertToList.setProfileImagePath(eachUser.getProfileImagePath());
                        }
                        UserList.add(insertToList);
                    }
            }
            initListView(view, mListView, UserList);
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


        System.out.println("search user fragment page: the current user: "+ currentUser.getUid());
        System.out.println("Capture list of user... ");


        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_search, container, false);
        mListView = (ListView) view.findViewById(R.id.list_user_view);

        UserList = new ArrayList<>();

        realTimeDatabaseForRetrievingUsers = FirebaseDatabase.getInstance().getReference("user");
        realTimeDatabaseForRetrievingUsers.addValueEventListener(getUserList);

        mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference();

        //initListView(view, mListView, UserList);
        return view;

    }

    private void getCurrentUser(){
        currentUser = mAuth.getCurrentUser();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_bar, menu);
        // the search icon
        MenuItem searchItem = menu.findItem(R.id.search_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText == null||newText.trim().isEmpty()){
            resetSearch();  // if there is no text, then reset the original friend list to the view
            return false;
        }

        ArrayList<User> returnList = UserList;
        for (int i=0; i<UserList.size(); i++){   // if the friend list doesnt contain the input text
            // then dont show the name in the return list
            if(!returnList.get(i).getUserName().toLowerCase().contains(newText.toLowerCase())) {
                returnList.remove(returnList.get(i));
            }
        }// but dont change the original loaded friend list

        initListView(view,  mListView,  returnList); // change the adapter
        return false;
    }

    public void resetSearch() {
        initListView(view, mListView,  UserList);
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    // list the friends
    private void initListView(View view, ListView mListView, ArrayList<User> UserList){
        // get Friend List
        if(getActivity()!=null) { //handle null pointer
            UserListAdapter adapter = new UserListAdapter(getActivity(), R.layout.friend_of_user_layout, UserList);
            mListView.setAdapter(adapter);
        }
    }


}