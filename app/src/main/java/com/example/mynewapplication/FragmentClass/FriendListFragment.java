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
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.recyclerViewstorage.UserListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendListFragment newInstance(String param1, String param2) {
        FriendListFragment fragment = new FriendListFragment();
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

    @Override
    public void onDetach() {
        super.onDetach();
    }


    View view;
    ListView mListView;
    ArrayList<User> friendList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       // System.out.println("Frag tag is "+this.getParentFragmentManager().findFragmentByTag("fragExchange"));
        view =  inflater.inflate(R.layout.fragment_friend_list, container, false);
        mListView = (ListView) view.findViewById(R.id.list_friend_view);

        friendList = new ArrayList<>();
        friendList.add(new User("Mary"));
        friendList.add(new User("Peter"));
        friendList.add(new User("Sam"));
        friendList.add(new User("Tom"));
        friendList.add(new User("Amy"));
        friendList.add(new User("Mandy"));
        friendList.add(new User("ALice"));
        friendList.add(new User("Lee"));


        initListView(view, mListView, friendList);
        return view;
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

        ArrayList<User> returnList = friendList;
        for (int i=0; i<friendList.size(); i++){   // if the friend list doesnt contain the input text
                // then dont show the name in the return list
            if(!returnList.get(i).getUserName().toLowerCase().contains(newText.toLowerCase())) {
                returnList.remove(returnList.get(i));
            }
        }// but dont change the original loaded friend list

        initListView(view,  mListView,  returnList); // change the adapter
        return false;
    }

    public void resetSearch() {
        initListView(view, mListView,  friendList);
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
    private void initListView(View view, ListView mListView, ArrayList<User> friendList){
        // get Friend List
        UserListAdapter adapter = new UserListAdapter(getActivity(), R.layout.friend_of_user_layout, friendList);
        mListView.setAdapter(adapter);
    }


}