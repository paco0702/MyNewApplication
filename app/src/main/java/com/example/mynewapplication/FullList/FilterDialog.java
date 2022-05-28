package com.example.mynewapplication.FullList;

import static com.example.mynewapplication.FullList.ItemsPoolActivity.selectedCategory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.mynewapplication.DialogFragment;
import com.example.mynewapplication.R;

import java.util.ArrayList;
import java.util.List;

public class FilterDialog extends AppCompatDialogFragment {
    private List<String> selectedCategories ;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selectedCategories = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose your categories");

        builder.setMultiChoiceItems(R.array.categories, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String [] category = getActivity().getResources().getStringArray(R.array.categories);

                if(isChecked){
                    selectedCategories.add(category[which]);
                }else if(selectedCategories.contains(category[which])){
                    selectedCategories.remove(category[which]);
                }
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                selectedCategory = new ArrayList<>();
                for (String category: selectedCategories){
                    selectedCategory.add(category);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        return builder.create();
    }


}
