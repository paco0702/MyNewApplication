package com.example.mynewapplication.register;


import com.example.mynewapplication.LoginInActivity;
import com.example.mynewapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.mynewapplication.LoginInActivity.theUser;
import static com.example.mynewapplication.register.Information_category_registor.Profile_Image;
//import static com.example.mynewapplication.register.Information_register.theUser;

public class Register extends AppCompatActivity {

    private Button tryRegister;
    private Button backToRegisterInformationPage;
    private EditText editTextEmailAddress;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;


    private String getEditTextEmailAddress ="";
    private String getEditTextPassword ="";
    private String getEditTextConfirmPassword = "";

    private CheckBox allowPhoneNumShow;

    private TextView messageFromTheButton;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private static final String USER = "user";
    private static final String TAG = "Register";

    private boolean setShowPhoneNUmber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance();
        System.out.println(database.getReference().toString()); //see the default database
        mDatabase = database.getReference(USER);
        System.out.println(mDatabase.toString());
        mAuth = FirebaseAuth.getInstance();

        System.out.println("The entered user name is "+theUser.getUserName());
        //System.out.println("The entered surname is "+theUser.getSurname());
        //System.out.println("The entered firstname is"+ theUser.getFirstName());
        System.out.println("THe enter Phone Number is "+ theUser.getPhoneNum());
        //theUser = new User();

        // activity register page
        backToRegisterInformationPage = (Button) findViewById(R.id.register_page_back_information_button);
        editTextEmailAddress = (EditText) findViewById(R.id.registerPageTextEmailAddress);
        editTextPassword = (EditText) findViewById(R.id.registerEditTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.registerEditTextConfirmPassword);

        messageFromTheButton = (TextView) findViewById(R.id.register_page_show_button_message);
        tryRegister = (Button) findViewById(R.id.register_enter_button);

        allowPhoneNumShow = (CheckBox) findViewById(R.id.checkBox_phoneNumberShow);
        setShowPhoneNUmber = false;
        allowPhoneNumShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allowPhoneNumShow.isChecked()){
                    setShowPhoneNUmber = false;
                }
                if(!allowPhoneNumShow.isChecked()){
                    setShowPhoneNUmber = true;
                }
            }
        });
        backToRegisterInformationPage.setOnClickListener(handleBackToRegisterInformation);
        tryRegister.setOnClickListener(handleRegisterEnter);

    }


    View.OnClickListener handleRegisterEnter = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v==tryRegister){

                // try to register
                // get the value from the input box
                // check the values from the Edit text
                messageFromTheButton.setText("");
                getEditTextEmailAddress = getEditTextPassword =  getEditTextConfirmPassword = "";
                getEditTextEmailAddress =  getEditTextEmailAddress + editTextEmailAddress.getText().toString();
                getEditTextPassword = getEditTextPassword + editTextPassword.getText().toString();
                getEditTextConfirmPassword = getEditTextConfirmPassword + editTextConfirmPassword.getText().toString();

                //messageFromTheButton.setText("Either email address or password is not filled");

                if(checkEmptyColumn(getEditTextEmailAddress, getEditTextPassword, getEditTextConfirmPassword)==true){
                    messageFromTheButton.setText("Either email address or password is not filled");
                }else{

                    if(isEmail(getEditTextEmailAddress)==true){
                        if(checkPasswordsAreTheSame(getEditTextPassword, getEditTextConfirmPassword)==true){
                            //check if the email is registered in the databased
                            //theUser = new User(getEditTextEmailAddress, getEditTextPassword);
                            theUser.setEmail(getEditTextEmailAddress);
                            theUser.setPassword(getEditTextPassword);
                            //System.out.println("the email is "+ getEditTextEmailAddress+" the password: "+getEditTextPassword);
                            System.out.println("The user's email is " +theUser.getEmail()+ " the user's password is " + theUser.getPassword());
                            createAccount(theUser.getEmail(), theUser.getPassword());
                        }else{
                            messageFromTheButton.setText("The passwords you entered are not matched");
                        }
                    }else{
                        messageFromTheButton.setText("The email entered is not valid");
                    }
                }

                // messageFromTheButton.setText(editTextEmailAddress.getText().toString())*/
            }
        }
    };

    View.OnClickListener handleBackToRegisterInformation = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v==backToRegisterInformationPage){
                // back loginPage
                setBackToRegisterInformationPageActivity();
            }
        }
    };

    private boolean checkPasswordsAreTheSame(String password, String confirmPassword){
        if(password.compareTo(confirmPassword)==0){
            return true;
        }else{
            return false;
        }
    }

    private boolean isEmpty(String theColumn){
        if(theColumn.compareTo("")==0){
            return true;
        }else
            return false;
    }

    private boolean checkEmptyColumn(String getEditTextEmailAddress, String getEditTextPassword, String getEditTextConfirmPassword){
        if(getEditTextEmailAddress.compareTo("")==0||
                getEditTextPassword.compareTo("")==0||
                getEditTextConfirmPassword.compareTo("")==0){
            return true;
        }else{
            return false;
        }
    }

    private boolean isEmail(String getEditTextEmailAddress){
        if(Patterns.EMAIL_ADDRESS.matcher(getEditTextEmailAddress).matches()){
            return true;
        }else{
            return false;
        }
    }

    private void createAccount(String  email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "creatUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            System.out.println(user.getUid());
                            theUser.setUserID(user.getUid().toString());
                            if(Profile_Image!=null){
                                setProfileImage(Profile_Image, user.getUid().toString());
                            }
                            theUser.setRating("5");
                            updateUI(user);
                        }else{
                            //log in fails 
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setProfileImage(Uri image, String userID){
        String storeThePath = "";
        StorageReference mStorageRef = FirebaseStorage.getInstance("gs://exchange-items-01.appspot.com/").getReference();
        StorageReference storageReference = mStorageRef.child("profile_images/" + userID + "/" + theUser.getUserName()+ ".jpg");
        storeThePath = "profile_images/" + userID + "/" + theUser.getUserName()+ ".jpg";
        theUser.setProfileImagePath(storeThePath);
        storageReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                return;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Upload picture has problem", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI(FirebaseUser user){
        //mDatabase.child(user.getUid().toString()).setValue(theUser);
        //mDatabase.child(user.getUid().toString()).setValue(theUser);
        FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).setValue(theUser);
        if(setShowPhoneNUmber==false){
            FirebaseDatabase.getInstance().getReference("Allow_Show_PhoneNum").child(user.getUid()).child("Allow_Show_Num").setValue("False");
        }else{
            FirebaseDatabase.getInstance().getReference("Allow_Show_PhoneNum").child(user.getUid()).child("Allow_Show_Num").setValue("True");
        }
        System.out.println(mDatabase.child(user.getUid()).toString());
        //mDatabase.child("exchanged_items_records").setValue(theUser);
        finish();
        Intent loginIntent = new Intent (this, LoginInActivity.class );
        startActivity(loginIntent);
    }

    private void setBackToRegisterInformationPageActivity(){
        //Intent intent = new Intent(this, Information_register.class);
        //startActivity(intent);\
        finish();
    }

}