package com.example.mynewapplication.register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mynewapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

import static com.example.mynewapplication.LoginInActivity.theUser;

public class Information_category_registor extends AppCompatActivity {
    //profile image
    private Bitmap profileImage;
    private ImageView profile_image;
    private Button chooseProfilePicButton;
    private Uri selectedImage;

    private static final int RESULT_LOAD_IMAGE = 1;
    public static Uri Profile_Image;


    // for the options (which category interested
    //private RadioGroup categoriesGroup;
    //private RadioButton radioButton_classifed;
    //private RadioButton radioButton_classifed2;
    //private RadioButton radioButton_classifed3;
    private CheckBox fashionBox;
    private CheckBox dailyProductBox;
    private CheckBox stationeryBox;
    private CheckBox furnitureBox;
    private CheckBox electricalProductsBox;
    private CheckBox instrumentsBox;
    private CheckBox sportItemsBox;
    private CheckBox accessaryBox;
    private CheckBox transportBox;
    private CheckBox othersBox;
    private Uri profileImagePage;

    private Button submitBtn;
    private Button previousBtn;

    HashMap<String, Integer> selectedCategories ;

    ListView choicesList;
    ArrayAdapter<String> adapter;
    String[] categories_choices = {
      "Fashion", "Daily product", "Stationery",
            "Furniture", "Electrical products", "Instruments",
            "Sport items", "Accessary", "Transport", "others"
    };

    int countCheck = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Profile_Image = null;
        setContentView(R.layout.activity_information_category_registor);
        //for uploading picture
        profile_image =findViewById(R.id.uploadProfileImage_View);
        chooseProfilePicButton = (Button) findViewById(R.id.upload_profile_picture_button);

        //hoicesList = findViewById(R.id.listView_categories);
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,categories_choices);
        //choicesList.setAdapter(adapter);

        //for choosing category
        chooseProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                //startActivityForResult(intent, 100);

                startActivityForResult(intent, RESULT_LOAD_IMAGE);
                //Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }
        });

        selectedCategories = new HashMap<>();
        fashionBox = (CheckBox) findViewById(R.id.checkBox_fashion);
        fashionBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedCategories.get("Fashion")==null){
                    countCheck++;
                    selectedCategories.put("Fashion",1);
                    if(countCheck>=3){
                        unableCheckBox();
                    }
                }else{
                    selectedCategories.remove("Fashion");
                    countCheck--;
                    if(countCheck<3){
                        ableCheckBox();
                    }
                }
            }
        });
        dailyProductBox = (CheckBox) findViewById(R.id.checkBox_daily_product);
        dailyProductBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategories.get("Daily product") == null) {
                    countCheck++;
                    selectedCategories.put("Daily product", 1);
                    if(countCheck>=3){
                        unableCheckBox();
                    }
                } else {
                    selectedCategories.remove("Daily product");
                    countCheck--;
                    if(countCheck<3){
                        ableCheckBox();
                    }
                }
            }
        });

        stationeryBox = (CheckBox) findViewById(R.id.checkBox_stationery);
        stationeryBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategories.get("Stationery") == null) {
                    countCheck++;
                    selectedCategories.put("Stationery", 1);
                    if(countCheck>=3){
                        unableCheckBox();
                    }
                } else {
                    selectedCategories.remove("Stationery");
                    countCheck--;
                    if(countCheck<3){
                        ableCheckBox();
                    }
                }
            }
        });

        furnitureBox = (CheckBox) findViewById(R.id.checkBox_furniture);
        furnitureBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategories.get("Furniture") == null) {
                    countCheck++;
                    selectedCategories.put("Furniture", 1);
                    if(countCheck>=3){
                        unableCheckBox();
                    }
                } else {
                    selectedCategories.remove("Furniture");
                    countCheck--;
                    if(countCheck<3){
                        ableCheckBox();
                    }
                }
            }
        });
        electricalProductsBox = (CheckBox) findViewById(R.id.checkBox_electrical_products);
        electricalProductsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategories.get("Electronic product") == null) {
                    countCheck++;
                    selectedCategories.put("Electronic product", 1);
                    if(countCheck>=3){
                        unableCheckBox();
                    }
                } else {
                    selectedCategories.remove("Electronic product");
                    countCheck--;
                    if(countCheck<3){
                        ableCheckBox();
                    }
                }
            }
        });
        instrumentsBox = (CheckBox) findViewById(R.id.checkBox_instruments);
        instrumentsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategories.get("Instrument") == null) {
                    countCheck++;
                    selectedCategories.put("Instrument", 1);
                    if(countCheck>=3){
                        unableCheckBox();
                    }
                } else {
                    selectedCategories.remove("Instrument");
                    countCheck--;
                    if(countCheck<3){
                        ableCheckBox();
                    }
                }
            }
        });
        sportItemsBox = (CheckBox) findViewById(R.id.checkBox_sport_Items);
        sportItemsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategories.get("Sport item") == null) {
                    countCheck++;
                    selectedCategories.put("Sport item", 1);
                    if(countCheck>=3){
                        unableCheckBox();
                    }
                } else {
                    selectedCategories.remove("Sport item");
                    countCheck--;
                    if(countCheck<3){
                        ableCheckBox();
                    }
                }
            }
        });
        accessaryBox = (CheckBox) findViewById(R.id.checkBox_accessary);
        accessaryBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategories.get("Accessory") == null) {
                    countCheck++;
                    selectedCategories.put("Accessory", 1);
                    if(countCheck>=3){
                        unableCheckBox();
                    }
                } else {
                    selectedCategories.remove("Accessory");
                    countCheck--;
                    if(countCheck<3){
                        ableCheckBox();
                    }
                }
            }
        });

        submitBtn = findViewById(R.id.category_register_submit);
        previousBtn = findViewById(R.id.category_register_back);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "";
                //assign category
                if(selectedCategories.get("Fashion")!=null){
                    category = category+ "Fashion,";
                }
                if(selectedCategories.get("Daily product")!=null){
                    category = category+"Daily product,";
                }
                if(selectedCategories.get("Stationery")!=null){
                    category = category+"Stationery,";
                }
                if(selectedCategories.get("Furniture")!=null){
                    category = category+"Furniture,";
                }
                if(selectedCategories.get("Electronic product")!=null){
                    category = category+"Electronic product,";
                }
                if(selectedCategories.get("Accessory")!=null){
                    category = category+"Accessory,";
                }
                if(selectedCategories.get("Sport item")!=null){
                    category = category+ "Sport item";
                }
                System.out.println("input category: "+ category);
                theUser.setPreferCategories(category);

                if(selectedImage!=null){
                    Profile_Image = selectedImage;
                }

                setEnterToRegisterPage();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackToInformationPageActivity();
            }
        });
    }

    private void unableCheckBox(){
        if (!fashionBox.isChecked()){
            fashionBox.setEnabled(false);
        }
        if (!dailyProductBox.isChecked()){
            dailyProductBox.setEnabled(false);
        }

        if (!stationeryBox.isChecked()){
            stationeryBox.setEnabled(false);
        }

        if(!furnitureBox.isChecked()){
            furnitureBox.setEnabled(false);
        }

        if (!electricalProductsBox.isChecked()){
            electricalProductsBox.setEnabled(false);
        }

        if (!instrumentsBox.isChecked()){
            instrumentsBox.setEnabled(false);
        }

        if (!sportItemsBox.isChecked()){
            sportItemsBox.setEnabled(false);
        }

        if (!accessaryBox.isChecked()){
            accessaryBox.setEnabled(false);
        }

    }

    private void ableCheckBox(){
        if (fashionBox.isEnabled()==false){
            fashionBox.setEnabled(true);
        }
        if (dailyProductBox.isEnabled()==false){
            dailyProductBox.setEnabled(true);
        }
        if (stationeryBox.isEnabled()==false){
            stationeryBox.setEnabled(true);
        }
        if (electricalProductsBox.isEnabled()==false){
            electricalProductsBox.setEnabled(true);
        }
        if (sportItemsBox.isEnabled()==false){
            sportItemsBox.setEnabled(true);

        }if (accessaryBox.isEnabled()==false){
            accessaryBox.setEnabled(true);

        }if (instrumentsBox.isEnabled()==false){
            instrumentsBox.setEnabled(true);
        }
        if (furnitureBox.isEnabled()==false){
            furnitureBox.setEnabled(true);
        }




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!=null) {
            // get the address of the image which is selected
            selectedImage = data.getData();
            String realPathFromUri = selectedImage.getPath().toString();

            //combine code
                try {   // loop through all the picture index, from 0 to 4 to store the path array for furture use and put the photo back to image view
                    profileImagePage = data.getData();
                    profileImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    profile_image.setImageURI(data.getData()); // set the image view

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void setBackToInformationPageActivity(){
        finish();
    }

    private void setEnterToRegisterPage(){
        Intent intent = new Intent (this, Register.class);
        startActivity(intent);
    }
}