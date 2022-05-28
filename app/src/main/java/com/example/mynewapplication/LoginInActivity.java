package com.example.mynewapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;

import com.example.mynewapplication.entities.User;
import com.example.mynewapplication.register.Information_register;
import com.example.mynewapplication.register.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//import static com.example.mynewapplication.register.Information_register.theUser;

public class LoginInActivity extends AppCompatActivity {

    private Button backToHomePage;
    private Button enterRegisterPage;
    private Button enterIndexPage;
    private EditText userEnteredEmail;
    private EditText userEnterPassword;

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private DatabaseReference usersRef;
    //catch the userInformation
    //private DatabaseReference usersInformation;

    private List<String> usersRefID = new ArrayList<>();
    public static List<User> users = new ArrayList<>();

    public static User theUser;

    // log in activity
    private FirebaseAuth mAuth;
    public static FirebaseUser saveCurrentUser;

    //return all the list of users into arrays
    ValueEventListener userRefEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            for(DataSnapshot ds: snapshot.getChildren()){
                User theUser = ds.getValue(User.class);
                System.out.println("The user name is :"+ theUser.getUserName());
                System.out.println("The user birthday is :"+ theUser.getBirthday());
                System.out.println("The user phone Num is :"+ theUser.getPhoneNum());
                System.out.println("The user email is :"+ theUser.getEmail());
                //System.out.println("The user data is: "+ );
                users.add(theUser);
            }
            System.out.println("the user size after the database "+ usersRefID.size());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    View.OnClickListener hanlderBackToHomePage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v==backToHomePage){
                setBackToHomeActivity();
            }
        }
    };

    View.OnClickListener handlerEnterToRegisterPage = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v==enterRegisterPage){
                setEnterRegisterInformationPageActivity();
            }
        }
    };

    View.OnClickListener handlerEnterToIndexPage = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //printReferenceList();
            //loopUserRefList();
            if(v==enterIndexPage){

               if(userEnteredEmail.getText().toString().isEmpty() || userEnterPassword.getText().toString().isEmpty() ){
                   Toast.makeText(LoginInActivity.this, "You need to enter email and password", Toast.LENGTH_SHORT).show();
                }else if(userEnteredEmail.getText().toString().isEmpty()){
                   Toast.makeText(LoginInActivity.this, "You need to enter email address", Toast.LENGTH_SHORT).show();
                }else if (userEnterPassword.getText().toString().isEmpty()){
                   Toast.makeText(LoginInActivity.this, "You need to enter password", Toast.LENGTH_SHORT).show();
                }else{
                    //set login user to theUser variable
                    //matchUser("hh@gmail.com", "123456");
                    //matchUser(userEnteredEmail.getText().toString(), userEnterPassword.getText().toString());
                    //login to the UI
                    //Login("Mary.Lynn.Baxter51650@yahoo.com", "p5JXaEUt9seH");
                    //Login("pp@gmail.com", "123456");
                    //Login("Barbara.Seranella9060@mail.com", "Ifj2c1ahoZH7U");
                    //Login("Gregg.Sacon192815@yahoo.com", "9W7ra6Mt");
                    Login(userEnteredEmail.getText().toString(), userEnterPassword.getText().toString());

                }
               // Login("pp@gmail.com", "123456");
            }


        }
    };

    private void Login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }else {
                            // if sign in fails, display a message to the user
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // means related to the activity main layer

        mAuth = FirebaseAuth.getInstance();

        theUser = new User();

        backToHomePage = (Button) findViewById(R.id.main_act_back_home_page_button);
        backToHomePage.setOnClickListener(hanlderBackToHomePage);

        enterRegisterPage = (Button) findViewById(R.id.main_act_register_main_button);
        enterRegisterPage.setOnClickListener(handlerEnterToRegisterPage);

        enterIndexPage = (Button) findViewById(R.id.main_act_enter_index_main_button);
        enterIndexPage.setOnClickListener(handlerEnterToIndexPage);

        userEnteredEmail = (EditText) findViewById(R.id.editTextInputEmailAddress);
        userEnterPassword = (EditText) findViewById(R.id.editTextInputPassword);



        mDatabase = FirebaseDatabase.getInstance("https://exchange-items-01-default-rtdb.firebaseio.com/").getReference();
        System.out.println("mDatabase is: "+mDatabase);
        usersRef = mDatabase.child("user");
        System.out.println("usersRef is: "+usersRef);
        //usersRef.addValueEventListener(userRefEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is signed in and Update UI
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            //reload();
        }
    }


    private void setBackToHomeActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setEnterRegisterInformationPageActivity(){
        Intent intent = new Intent(this, Information_register.class);
        startActivity(intent);
    }

    public void updateUI(FirebaseUser user){
        if(user!= null){
            setEnterIndexPageActivity();
        }else{
            System.out.println("Email or password is not matched");
        }
    }

    private void setEnterIndexPageActivity (){
        Intent intent = new Intent ( this, IndexActivity.class);
        startActivity(intent);
    }


    ValueEventListener userDataEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String infors = "";
            for(DataSnapshot ds: snapshot.getChildren()){
                infors = ds.getKey();
                System.out.println(" "+ infors);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    // old function, might not need anymore
    private void matchUser(String email, String password){
        for(int i=0; i<users.size(); i++){
            if(email.compareTo(users.get(i).getEmail())==0 && password.compareTo(users.get(i).getPassword())==0){
                System.out.println("User match: "+email+", "+password+" with "+users.get(i).getEmail()+", "+users.get(i).getPassword());
                theUser = users.get(i);
            }
        }

    }


}
