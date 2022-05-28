package com.example.mynewapplication.FullList;

import static com.example.mynewapplication.IndexActivity.FLAT_FOR_ItemsPOOL;
import static com.example.mynewapplication.IndexActivity.fullList;
import static com.example.mynewapplication.IndexActivity.preferCategoryList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.Items;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class ItemsPoolActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener{
    ListView itemsListView;
    ArrayList<Items> itemsList;
    FloatingActionButton filter;
    public static ArrayList<String> selectedCategory = new ArrayList<>();
    ArrayList<Items> filteredItems; // name search
    ArrayList<Items> categoryFilter; // filter category
    Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_pool);
        filter = (FloatingActionButton) findViewById(R.id.filter_floatingActionButton);
        back = (Button) findViewById(R.id.back_button_itemPool);

        itemsList = new ArrayList<>();
        filteredItems = new ArrayList<>();
        categoryFilter = new ArrayList<>();
        if(FLAT_FOR_ItemsPOOL.compareTo("popularList")==0){
            itemsList = fullList;
        }else if(FLAT_FOR_ItemsPOOL.compareTo("preferList")==0){
            itemsList = preferCategoryList;
        }

        setUpList(itemsList);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategoryDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //initSearchwidgets();
    }

    private void openCategoryDialog(){
        //FilterDialog dialog = new FilterDialog();
        //dialog.show(getSupportFragmentManager(), "category dialog");
        //System.out.println("selected category");
        //for(String category: selectedCategory){
        //    System.out.println(category);
        //}
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItemsPoolActivity.this);
        alertDialog.setTitle("Choose a category");
        selectedCategory = new ArrayList<>();
        alertDialog.setMultiChoiceItems(R.array.categories, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String [] category = getResources().getStringArray(R.array.categories);
                if(isChecked){
                    selectedCategory.add(category[which]);
                }else if(selectedCategory.contains(category[which])){
                    selectedCategory.remove(category[which]);
                }
            }
        });

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               categoryFilter = new ArrayList<>();
               if(!selectedCategory.isEmpty()) {
                   if (filteredItems.isEmpty()) { // if the filter
                       for (String category : selectedCategory) {
                           System.out.println(category);
                           for (Items item : itemsList) {
                               String [] Categoryline = item.getCategory().toString().split(",");
                               if (Categoryline[0].compareTo(category) == 0) {
                                 categoryFilter.add(item);
                               }
                               //if (item.getCategory().compareTo(category) == 0) {
                                 //  categoryFilter.add(item);
                               //}
                           }
                       }
                       setUpList(categoryFilter);
                   } else {
                       for (String category : selectedCategory) {
                           System.out.println(category);
                           for (Items item : filteredItems) {
                               String [] Categoryline = item.getCategory().toString().split(",");
                               if (Categoryline[0].compareTo(category) == 0) {
                                   categoryFilter.add(item);
                               }
                               /*
                               if (item.getCategory().compareTo(category) == 0) {
                                   categoryFilter.add(item);
                               }
                               */
                           }
                       }
                       setUpList(categoryFilter);
                   }
               }else{
                   if(filteredItems.isEmpty()){
                       setUpList(itemsList);
                   }else{
                       setUpList(filteredItems);
                   }

               }
            }
        });

        alertDialog.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }


    public void resetSearch() {
        if(categoryFilter.isEmpty()){
            setUpList(itemsList);
        }else{
            setUpList(categoryFilter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.search_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText == null||newText.trim().isEmpty()){
            resetSearch();  // if there is no text, then reset the original friend list to the view
            return false;
        }
        filteredItems = new ArrayList<>();
        if(categoryFilter.isEmpty()){
            for(Items item: itemsList){
                if(item.getName().toLowerCase().contains(newText.toLowerCase())){
                    System.out.println(item.getName());
                    filteredItems.add(item);
                }
            }
            setUpList(filteredItems);
        }else{
            for(Items item: categoryFilter){
                if(item.getName().toLowerCase().contains(newText.toLowerCase())){
                    System.out.println(item.getName());
                    filteredItems.add(item);
                }
            }
            setUpList(filteredItems);
        }

        return false;
    }

    private void setUpList(ArrayList<Items> ListOfItems){
        itemsListView =(ListView) findViewById(R.id.itemsPoolListView);

        ItemsAdapter adapter = new ItemsAdapter(this, R.layout.item_of_user_info_layout, ListOfItems);
        itemsListView.setAdapter(adapter);
    }
}