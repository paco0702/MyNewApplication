package com.example.mynewapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mynewapplication.entities.Items;
import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.ml.Model;
import com.example.mynewapplication.register.Information_register;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.gpu.CompatibilityList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.mynewapplication.LoginInActivity.theUser;
import static org.tensorflow.lite.support.model.Model.*;

public class UploadActivity extends AppCompatActivity {

    private Button choosePictureButton;
    private Button backToIndexPageButton;
    private Button uploadButton;
    private Button resetPictureButton;

    private ImageView showChoseImage;
    private ArrayList<ImageView> showChoseImageList = new ArrayList<>();
    private EditText imageName;
    private EditText itemsValue;
    private RadioGroup categories;

    private ProgressDialog mProgressDialog;

    private final static int mWidth = 512;
    private final static int mLength = 512;

    private ArrayList<String> pathArray;
    private ArrayList<Uri> uriImageArray;
    private int array_position;

    private String Tag = "uploadActivity";

    //
    private static final int RESULT_LOAD_IMAGE = 1;

    private String getCategories;
    private String getValue;

    private FirebaseDatabase database;
    private DatabaseReference itemDatabase;
    private DatabaseReference itemUserDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private StorageReference mStorageRef; // to store the upload picture
    private DatabaseReference realTimeDatabaseForUsers; // user information
    // for upload action
    private ProgressBar mProgressBar;
    private ArrayList<String> storeThePathForInsert;

    private EditText description;

    private String nameOfUser;
    private String emailOfUser;
    private String phoneNumberOfUser;
    private String idOfUser;
    private String birthdayOfUser;
    //public static User theCurrentLoginUser;

    // private Bitmap bitmap;


    private List<String> label;
    //private HorizontalBarChart mBarChat;
    Button btnClassify;
    private List<String> labels;

    // output result
    private float result_of_instrumental_categories;
    private String[] instruments_categories = {"acordian",
            "alphorn",
            "banjo",
            "bongo drum",
            "casaba",
            "castanets",
            "clarinet",
            "flute",
            "guitar",
            "piano",
            "recorder"};

    private int[] predict_index_array;
    ArrayList<Integer> predict_index_list;
    ArrayList<Float> predict_value_forEach_index_list;


    private ValueEventListener getUserInfoEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            //System.out.println("Index page");
            for (DataSnapshot ds : snapshot.getChildren()) {
                User theUser = ds.getValue(User.class);
                //System.out.println("User id: "+theUser.getUserID()+"login in id: "+currentUser.getUid().toString());
                if (theUser.getUserID().compareTo(currentUser.getUid().toString()) == 0) {
                    System.out.println("User id: "+theUser.getUserID()+"login in id: "+currentUser.getUid().toString());
                    System.out.println("The user first name is :"+ theUser.getUserName());
                    System.out.println("The user birthday is :"+ theUser.getBirthday());
                    System.out.println("The user phone Num is :"+ theUser.getPhoneNum());
                    System.out.println("The user email is :"+ theUser.getEmail());

                    nameOfUser = theUser.getUserName();
                    emailOfUser = theUser.getEmail();
                    phoneNumberOfUser = String.valueOf(theUser.getPhoneNum());
                    phoneNumberOfUser = theUser.getPhoneNum();
                    idOfUser = theUser.getUserID();
                    birthdayOfUser = theUser.getBirthday();

                    break;
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    // for get image function
    private Bitmap[] img = new Bitmap[5];
    private ImageView todoTest_imageView;

    // for classification
    private RadioGroup classifiedCategories;
    private RadioButton radioButton_classifed;
    private RadioButton radioButton_classifed2;
    private RadioButton radioButton_classifed3;
    private RadioButton radioButton_classifed4 ;
    private RadioButton radioButton_classifed5;
    private ArrayList<RadioButton> radioButtons;
    private RadioButton userinputRadioButton;
    String classifiedResult = "";
    String selected_categories = ""; // for dialog box user chose

    //fro the server
    private OkHttpClient okhttpclient;
    private String postBodyString;
    private MediaType mediaType;
    private RequestBody requestBody;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_pic_page);

        choosePictureButton = (Button) findViewById(R.id.ChoosePicture_button);
        backToIndexPageButton = (Button) findViewById(R.id.back_indexPage_button);
        uploadButton = (Button) findViewById(R.id.uploadImage_button);

        resetPictureButton = (Button) findViewById(R.id.reset_picture);
        showChoseImage = (ImageView) findViewById(R.id.uploadImage_View);

        imageName = (EditText) findViewById(R.id.enter_imageName);  // name of the image
        itemsValue = (EditText) findViewById(R.id.enter_imageValue); // value of the items
        description = (EditText) findViewById(R.id.item_description);

        //for uploading action
        pathArray = new ArrayList<>(); // to store the path of the items
        uriImageArray = new ArrayList<>();
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressDialog = new ProgressDialog(UploadActivity.this);
        storeThePathForInsert = new ArrayList<>();

        // set up the firebase database reference
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // store location reference
        mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference();
        //user information reference
        realTimeDatabaseForUsers = FirebaseDatabase.getInstance().getReference("user");
        realTimeDatabaseForUsers.addValueEventListener(getUserInfoEventListener);

        //add the item to the database
        database = FirebaseDatabase.getInstance();
        itemDatabase = database.getReference("Items");
        itemUserDatabase = database.getReference("Items_Users");


        setUpShowChoseImageListLocation();
        checkFilePermission();

        System.out.println("upload page: The current user is: " + currentUser.getUid());

        array_position = -1;

        // set  result text view
        btnClassify = (Button) findViewById(R.id.classifyBtn);
        todoTest_imageView = (ImageView) findViewById(R.id.imageView_todoTest);

        predict_index_array = new int[]{-1, -1, -1, -1, -1};
        predict_index_list = new ArrayList<Integer>();
        predict_value_forEach_index_list = new ArrayList<Float>();

        // after classified, change the radio button to the output
        classifiedCategories = (RadioGroup) findViewById(R.id.radioGroup_categories_classified);
        radioButtons = new ArrayList<>();
        radioButton_classifed = (RadioButton) findViewById(R.id.radioButton_classifed);
        radioButton_classifed2 = (RadioButton) findViewById(R.id.radioButton_classifed2);;
        radioButton_classifed3 = (RadioButton) findViewById(R.id.radioButton_classifed3);
        radioButton_classifed4 = (RadioButton) findViewById(R.id.radioButton_classifed4);
        radioButton_classifed5 = (RadioButton) findViewById(R.id.radioButton_classifed5);
        userinputRadioButton = (RadioButton) findViewById(R.id.radioButton_classifed_userInput);
        classifiedCategories.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radioButton_classifed:
                        break;
                    case R.id.radioButton_classifed2:
                        break;
                    case R.id.radioButton_classifed3:
                        break;
                    case R.id.radioButton_classifed4:
                        break;
                    case R.id.radioButton_classifed5:
                        break;
                    case R.id.radioButton_classifed_userInput:
                        showChoseCategoryDialog();
                        break;
                }
            }
        });

        radioButtons.add(radioButton_classifed);
        radioButtons.add(radioButton_classifed2);
        radioButtons.add(radioButton_classifed3);
        radioButtons.add(radioButton_classifed4);
        radioButtons.add(radioButton_classifed5);



        userinputRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        choosePictureButton.setOnClickListener(new View.OnClickListener() {
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

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == uploadButton) {
                    Log.d(Tag, "OnClick: uploading Image.");

                    mProgressDialog.setMessage("Uploading Image...");
                    mProgressDialog.show();

                    //get the signe in user
                    FirebaseUser user = mAuth.getCurrentUser();

                    String name = imageName.getText().toString();
                    String value = itemsValue.getText().toString();
                    String categories = getFromRadioGroup();
                    String userID = user.getUid();
                    String itemDescription = description.getText().toString();
                    System.out.println("itemDescription "+itemDescription);

                    //TODO move here for testing
                    if (!name.equals("")) {
                        if (!value.equals("")) {
                            if (!categories.equals("")) {
                                if(!itemDescription.equals("")) {
                                    //TODO move back here
                                    ArrayList<String> storagePath = new ArrayList<>();
                                    storeThePathForInsert = new ArrayList<>();
                                    if (array_position > -1) {
                                        // loop through all the pictures Path insert into storage
                                        String itemId = generateItemID();
                                        for (int i = 0; i <= array_position; i++) {
                                            String storeThePath = "";
                                            System.out.println("array path is " + pathArray.get(i));

                                            //Uri uri = Uri.fromFile(new File(pathArray.get(i)));

                                            StorageReference storageReference = mStorageRef.child("images/users/" + userID + "/" + itemId + "/" + name + i + ".jpg");
                                            storeThePath = "images/users/" + userID + "/" + itemId + "/" + name + i + ".jpg";
                                            System.out.println("Store the path, the path name will be " + storeThePath);
                                            storeThePathForInsert.add(storeThePath);

                                            storageReference.putFile(uriImageArray.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mProgressBar.setProgress(0);
                                                        }
                                                    }, 5000);

                                                    //get a URL to the upload content
                                                    //Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                                                    toastMessage("Upload Success");
                                                    mProgressDialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    toastMessage("Upload Failed");
                                                    mProgressDialog.dismiss();
                                                }
                                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                                @Override
                                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                    mProgressBar.setProgress((int) progress);
                                                }
                                            });
                                        }

                                        //Items newItems = new Items(name, nameOfUser, idOfUser, emailOfUser, categories, value, storeThePathForInsert, itemDescription);
                                        Items newItems = new Items(name, nameOfUser, idOfUser, emailOfUser, categories, value, "free", storeThePathForInsert, itemDescription);

                                        System.out.println("Upload page: THe ownerId is " + idOfUser);

                                        // insert to items (dataset for all the items )
                                        itemDatabase.child(idOfUser + "_" + itemId).setValue(newItems);

                                        //insert to items_user dataset for retrieve
                                        itemUserDatabase.child(user.getUid().toString()).child(itemId).setValue(newItems);
                                        setBackToIndexPageActivity();
                                    } else {
                                        toastMessage("Upload Failed, there is no picture is chosen");
                                        mProgressDialog.dismiss();
                                    }
                                    // add this items to the database
                                }else{
                                    System.out.println("Haven't entered the description of the item");
                                    createDialogBoxAlertForDescriptions();
                                    toastMessage("Upload Failed");
                                    mProgressDialog.dismiss();
                                }
                            } else {
                                System.out.println("Haven't entered the categories of the item");
                                createDialogBoxAlertForCategories();
                                toastMessage("Upload Failed");
                                mProgressDialog.dismiss();
                            }
                        } else {
                            System.out.println("Haven't enter the value of the item");
                            createDialogBoxAlertForValue();
                            toastMessage("Upload Failed");
                            mProgressDialog.dismiss();
                        }
                    } else {
                        //error message show dialog alert
                        System.out.println("Haven't enter the name of the item");
                        createDialogBoxAlert();
                        toastMessage("Upload Failed");
                        mProgressDialog.dismiss();
                    }
                }
            }
        });


        btnClassify.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 //Request request = new Request.Builder().url("http://192.168.0.157:5000/").build();
                    //postUserName(currentUser.getUid(), "http://192.168.0.157:5000/");
                 if (array_position >= 0 && array_position <= 5) {
                     // upload the image to the server
                     //img is the bitmap
                     mProgressDialog.setMessage("Processing");
                     mProgressDialog.show();
                     postRequest(currentUser.getUid(), "http://192.168.0.157:5000/predict");
                 }else{
                     toastMessage("Please input picture");
                 }


                 //new MyTask().execute();
              }
        });

        resetPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                array_position = -1;
                pathArray = new ArrayList<>();
                uriImageArray = new ArrayList<>();
                img = new Bitmap[5];

                showChoseImageList.get(0).setImageURI(null);
                showChoseImageList.get(1).setImageURI(null);
                showChoseImageList.get(2).setImageURI(null);
                showChoseImageList.get(3).setImageURI(null);
                showChoseImageList.get(4).setImageURI(null);

                radioButtons.get(0).setText("");
                radioButtons.get(1).setText("");
                radioButtons.get(2).setText("");
                radioButtons.get(3).setText("");
                radioButtons.get(4).setText("");
                userinputRadioButton.setText("");
            }
        });
                // change this part to connect the server to run CNN
        //connect to the server

        backToIndexPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        okhttpclient = new OkHttpClient();
        okhttpclient.setConnectTimeout(15, TimeUnit.SECONDS);
        okhttpclient.setReadTimeout(6, TimeUnit.MINUTES);
    }


    private void showChoseCategoryDialog(){
        AlertDialog.Builder b = new AlertDialog.Builder(UploadActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner_category_userchoice, null);
        b.setTitle("Category");

        ArrayList <String> categoryselection = new ArrayList<>();
        categoryselection.add("Please select a category");
        categoryselection.add("Accessory");
        categoryselection.add("Sport item");
        categoryselection.add("Instrument");
        categoryselection.add("Fashion");
        categoryselection.add("Electronic product");
        categoryselection.add("Stationery");
        categoryselection.add("Furniture");
        categoryselection.add("Daily product");
        EditText subCategory = mView.findViewById(R.id.subCategory_subCategory);

        Spinner categorySpinner = (Spinner) mView.findViewById(R.id.spinner_major_category_selection);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                android.R.layout.simple_spinner_item, categoryselection);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Toast.makeText(getApplication(),
                            "Please select a category", Toast.LENGTH_SHORT).show();
                }else{
                    // Get select value
                    //int index = position-1;
                    selected_categories = categoryselection.get(position);
                    System.out.println("selected category "+selected_categories);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Set up the buttons
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!categorySpinner.getSelectedItem().toString().equalsIgnoreCase("") && (!subCategory.getText().toString().equalsIgnoreCase(""))){
                    System.out.println("select categories "+ categorySpinner.getSelectedItem().toString() +subCategory.getText());
                    userinputRadioButton.setText(categorySpinner.getSelectedItem().toString()+", "+subCategory.getText());
                    System.out.println("set button "+userinputRadioButton.getText().toString());
                }
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        b.setView(mView);
        Dialog inputCategory = b.create();
        inputCategory.show();
    }
    /*
    private class MyTask extends AsyncTask<Void, Void, Void> {
        String result;
        @Override
        protected Void doInBackground(Void... voids) {
            String id = generateItemID();
            //Request request = new Request.Builder().url("http://192.168.0.157:5000/").build();
            //postUserName(currentUser.getUid()+" "+id, "http://192.168.0.157:5000/");

            if (array_position >= 0 && array_position <= 5) {
                // upload the image to the server
                //img is the bitmap
                postRequest(currentUser.getUid()+" "+id, "http://192.168.0.157:5000/predict");
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    */


    //server
    private void postUserName(String userID, String URL) {
        mediaType = MediaType.parse("text/plain");
        requestBody = RequestBody.create(mediaType, userID);

        Request user_request = new Request
                .Builder()
                .post(requestBody).addHeader("userID", userID)
                .url(URL)
                .build();

        okhttpclient.newCall(user_request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            @Override
            public void onResponse(Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(UploadActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        });
    }


    private void postRequest(String userID, String URL) {
        MultipartBuilder multipartBuilder = new MultipartBuilder();
        multipartBuilder.type(MultipartBuilder.FORM);


        int numberOfTotalPics = this.array_position + 1;
        for (int i = 0; i < numberOfTotalPics; i++) {
            Bitmap imgBit = img[i];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imgBit.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream);
            byte [] bytearray = byteArrayOutputStream.toByteArray();
            multipartBuilder.addFormDataPart("image"+i, i+".jpg", RequestBody.create(MediaType.parse("image/*jpg"), bytearray));
        }

        RequestBody postBodyImage = multipartBuilder.build();


        Request request = new Request
                .Builder()
                .post(postBodyImage).addHeader("userID", userID)
                .url(URL)
                .build();

        okhttpclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        Toast.makeText(UploadActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mProgressDialog.dismiss();
                            //Toast.makeText(UploadActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
                            classifiedResult = response.body().string();
                            //System.out.println( "result "+response.body().string());
                            //got the result, set the radio buttons
                            System.out.println("Result is "+classifiedResult);
                            String [] line = classifiedResult.split("\\|");
                            int j =0;
                            for(int i=0; i<(line.length); i++){
                                if(!line[i].equals(" ") && !line[i].equals("")){
                                    System.out.println(i+" "+ line[i]);
                                    radioButtons.get(j).setText(line[i]);
                                    System.out.println(line[i]);
                                    j++;
                                }
                            }

                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        });
    }


    //generate the items key for realtime database
    private String generateItemID(){
        int lengthOfTheID = 15;
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

    @Override  // when access the photo gallery
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // make sure the gallery has called
        // change img to img bitmap
        //if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!=null) {
            // get the address of the image which is selected
            Uri selectedImage = data.getData();

            String realPathFromUri = selectedImage.getPath().toString();

            if (array_position < 4) {
                //combine code
                try {   // loop through all the picture index, from 0 to 4 to store the path array for furture use and put the photo back to image view
                    array_position++;

                    System.out.println("THe array position is " + array_position + " THe path of the chose image is " + realPathFromUri);

                    pathArray.add(realPathFromUri); // store the path aaray
                    uriImageArray.add(data.getData());
                    //System.out.println(selectedImage.getPath().toString());


                    img[array_position] = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                    showChoseImageList.get(array_position).setImageURI(data.getData()); // set the image view
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // for upload pictures
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private String getRealPathFromURI(Uri contentUri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // after uploaded the picture, allowed to classify
    private void doClassifyForAllPicture(Bitmap[] img) {
        System.out.println("total number Of picture " + array_position);
        int numberOfTotalPics = array_position + 1;
        try {
            Model model = Model.newInstance(getApplicationContext());

            for (int i = 0; i < numberOfTotalPics; i++) {
                System.out.println("The " + (i + 1) + " picture");
                img[i] = Bitmap.createScaledBitmap(img[i], 224, 224, true);

                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
                TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                tensorImage.load(img[i]);

                ByteBuffer byteBuffer = tensorImage.getBuffer();

                inputFeature0.loadBuffer(byteBuffer);

                // Runs model inference and gets result.
                com.example.mynewapplication.ml.Model.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                /*
                System.out.println("around number" + i + " " + outputFeature0.getFloatArray()[0]
                        + "\n" + outputFeature0.getFloatArray()[1]
                        + "\n" + outputFeature0.getFloatArray()[2]
                        + "\n" + outputFeature0.getFloatArray()[3]
                        + "\n" + outputFeature0.getFloatArray()[4]
                        + "\n" + outputFeature0.getFloatArray()[5]
                        + "\n" + outputFeature0.getFloatArray()[6]
                        + "\n" + outputFeature0.getFloatArray()[7]
                        + "\n" + outputFeature0.getFloatArray()[8]
                        + "\n" + outputFeature0.getFloatArray()[9]
                        + "\n" + outputFeature0.getFloatArray()[10]

                );

                System.out.println("getFloatArray flat size " + outputFeagetMaxInstrumentFloatArrayture0.getFlatSize());
*/
                int largest_output_index = getMaxInstrumentFloatArray(outputFeature0.getFloatArray());

                //String output_result = instruments_categories[largest_output_index];
                predict_index_list.add(largest_output_index);
                predict_value_forEach_index_list.add(outputFeature0.getFloatArray()[largest_output_index]);
/*
                System.out.println("The " + (i + 1) + " picture predicted index is "+ largest_output_index);

*/
                // Releases model resources if no longer used.
            }
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    // they are actually the same
    private int getMaxInstrumentFloatArray(float[] instrumentFloatArray) {
        float max = 0;
        int index = 0;
        for (int i = 0; i < instrumentFloatArray.length; i++) {
            if (instrumentFloatArray[i] > max) {
                max = instrumentFloatArray[i];
                System.out.println("index is " + i + " max is " + max);
                index = i;
            }
        }
        System.out.println("final index is " + index);
        return index;
    }

    private String getFromRadioGroup() {
        //int selectedId = categories.getCheckedRadioButtonId();
        int selectedId = classifiedCategories.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return "";
        } else {
            RadioButton theButton = (RadioButton) findViewById(selectedId);
            System.out.println("final category "+theButton.getText().toString());
            return theButton.getText().toString();
        }
    }

    private void createDialogBoxAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setPositiveButton(R.string.dialog_miss_nameOfItem_message_for_uploadPicturesPage_positiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        })
                .setTitle(R.string.dialog_miss_nameOfItem_message_for_uploadPicturesPage_title)
                .setMessage(R.string.dialog_miss_nameOfItem_message_for_uploadPicturesPage)
                .create();
        builder.show();

    }

    private void createDialogBoxAlertForValue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setPositiveButton(R.string.dialog_miss_nameOfItem_message_for_uploadPicturesPage_positiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        })
                .setTitle(R.string.dialog_miss_nameOfItem_message_for_uploadPicturesPage_title)
                .setMessage(R.string.dialog_miss_valueOfItem_message_for_uploadPicturesPage)
                .create();
        builder.show();

    }

    private void createDialogBoxAlertForDescriptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setPositiveButton(R.string.dialog_miss_nameOfItem_message_for_uploadPicturesPage_positiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        })
                .setTitle("Miss information")
                .setMessage("Please enter description of the item")
                .create();
        builder.show();

    }

    private void createDialogBoxAlertForCategories() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setPositiveButton(R.string.dialog_miss_nameOfItem_message_for_uploadPicturesPage_positiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        })
                .setTitle(R.string.dialog_miss_nameOfItem_message_for_uploadPicturesPage_title)
                .setMessage(R.string.dialog_miss_categoriesOfItem_message_for_uploadPicturesPage)
                .create();
        builder.show();

    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkFilePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = UploadActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += UploadActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        } else {
            Log.d(Tag, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    private void setUpShowChoseImageListLocation() {
        showChoseImageList.add((ImageView) findViewById(R.id.uploadImage_View));
        showChoseImageList.add((ImageView) findViewById(R.id.uploadImage_View_2));
        showChoseImageList.add((ImageView) findViewById(R.id.uploadImage_View_3));
        showChoseImageList.add((ImageView) findViewById(R.id.uploadImage_View_4));
        showChoseImageList.add((ImageView) findViewById(R.id.uploadImage_View_5));
    }

    //sorting class
    private static class SortingForClassification{
        ArrayList<Integer> inputList;
        ArrayList<Float> inputValueList;
        ArrayList<Integer> outputList;

        SortingForClassification(ArrayList<Integer> inputList,  ArrayList<Float> predict_value_forEach_index_list){
            this.inputList = inputList;
            this.inputValueList = predict_value_forEach_index_list;
        }

        private ArrayList sortList(){
            //int[] arr = new int[inputList.size()];
            Node [] arr = new Node[inputValueList.size()];
           int[] outputArr = new int[inputValueList.size()];

            ArrayList<Integer> output = new ArrayList<Integer>();
            for (int i = 0; i < inputList.size(); i++) {
                //arr[i] = (inputList.get(i));
                arr[i] = new Node(inputList.get(i), inputValueList.get(i));
            }

            outputArr = countSort(arr);

            for (int i = 0; i <  outputArr.length; i++) {
                output.add( outputArr[i]);
            }
            this.outputList = output;
            return output;
        }

        private int[] countSort(Node[] arr) {

            int[] count = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            //Node [] count = { new Node(), new Node(),new Node(),new Node(),new Node(),new Node(),new Node(),new Node(),new Node(),new Node(), new Node(),};

            System.out.println("array length "+ arr.length);
            int[] output = new int[arr.length];
            Node[] input = arr;

            for (int i = 0; i < arr.length; i++) {
                int value = (int)(input[i].getValue());
                System.out.println("value "+value);
                count[value]++;
            }
            int culmulate = 0;
            System.out.println("culmulate");
            // because culmulate so the index is skipped
            for (int i = 1; i < count.length; i++) {
                culmulate = count[i - 1];
                count[i] = count[i] + culmulate;
                System.out.print(count[i]+" ");
            }
            System.out.println();

            for (int i = 0; i < input.length; i++) {
                System.out.println("index "+count[input[i].getValue()]+" value "+input[i]);
                int index = count[input[i].getValue()]-1 ; // e.g. originally count[input[i]] is 2 , the human read index, array read is 1
                output[index] = input[i].getIndex();
                count[input[i].getValue()]--;
            }
            // print output
            System.out.println("sorted array ");
            for (int i = 0; i < output.length; i++) {
                System.out.print("output index " + i + " " + output[i]);
                System.out.println();
            }
            return output;
        }

        // get frequent index from classified items
        private int [] getFrequent(){
            ArrayList<Integer> inputList = this.outputList; //sorted output
            int[] arr = new int[inputList.size()];

            int [] result = new int[inputList.size()];

            for (int i = 0; i < inputList.size(); i++) {
                System.out.println("arr[i] "+ arr[i]);
                arr[i] = inputList.get(i);
            }
            result = getMaxArr(arr);
            return result;
        }

        private int [] getMaxArr(int [] arr){
            int[] count = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            System.out.println("array length "+ arr.length);

            for (int i = 0; i < arr.length; i++) {
                System.out.println("i "+ i+" input "+ arr[i]);
                count[arr[i]] += 1;
            }
            int max = 0;
            int sec_max = 0;
            int third_max = 0;
            int [] result = new int [5]; // because maximum input image input in the UI is five

            for (int i = 0; i < arr.length; i++) {
                System.out.println("count[arr[i]]" + count[arr[i]] +" arr[i] "+ arr[i]);
                if(count[arr[i]]>max){
                    max = count[arr[i]];
                    result[i] = arr[i];
                }else if(count[arr[i]]>sec_max) {
                    sec_max = count[arr[i]];
                    result[i] = arr[i];
                }else if(count[arr[i]]>third_max) {
                    third_max = count[arr[i]];
                    result[i] = arr[i];
                }
                System.out.println("max is "+max+" index is "+result[0]);
                // get the most first three frequent number and if there are only one to two picture, handle the error
                if(arr.length>2) {
                    System.out.println("second max is " + sec_max + " index is " + result[1]);
                    System.out.println("third max is "+third_max +" index is "+result[2]);
                }else
                if(arr.length>1) {
                    System.out.println("sec max is " + sec_max + " index is " + result[1]);
                    result[2] = max;
                }else{
                    result[1] = max;
                    result[2] = max;
                }
            }
            return result;
        }

        private class Node{
            int index;
            int value;
            Node(int index, float value){
                this.index = index;
                this.value = (int)(value*10); //e.g. 0.841564.. *10 = 8.5645 = int 8
            }

            Node (){
                this.index = 0;
                this.value = 0;
            }
            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public int getValue() {
                return value;
            }

            public void setValue(float value) {
                this.value = (int)(value*10);
            }
        }

    }



    // go back the pervious page
    private void setBackToIndexPageActivity() {
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);
    }
}
