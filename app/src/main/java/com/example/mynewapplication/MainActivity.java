package com.example.mynewapplication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


//import com.chaquo.python.PyObject;
//import com.chaquo.python.Python;
//import com.chaquo.python.android.AndroidPlatform;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.opencsv.CSVReader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity  {
    RelativeLayout layout;
    
    // for server and client
    private Button enterLoginPage, backToHomePage;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    public static ArrayList<NodeHash> csvDictionary = new ArrayList<>();

    View.OnClickListener handlerLogin = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v==enterLoginPage){
                setEnterLoginPageActivity();
            }
        }
    };
    private TextView textViewPython;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page); // means related to the activity main layer

        mAuth = FirebaseAuth.getInstance();

        //enter the next page
        enterLoginPage = (Button) findViewById(R.id.home_page_start_button);
        enterLoginPage.setOnClickListener(handlerLogin);

        /*
        backToHomePage = (Button) findViewById(R.id.back_home_page_button);
        backToHomePage.setOnClickListener(handlerBackToHomePage);
        */

        textViewPython = (TextView) findViewById(R.id.pythonTry);


        InputStream inputStream = getResources().openRawResource(R.raw.output_dict);
        //CSVFile csvFile = new CSVFile(inputStream);
        //csvDictionary = csvFile.read();
        //System.out.println(csvDictionary.size());

        //if(!Python.isStarted()){
           // Python.start(new AndroidPlatform(this));
        //}

        // start python
        //Python py = Python.getInstance();
        //PyObject pyobj = py.getModule("myScript"); // give python script name

        //now call this function
       // PyObject obj = pyobj.callAttr("main", "enter_words");

        // now set returned text to

        //test python
        //textViewPython.setText(obj.toString());


    }

    public void getS(){

    }
    public void createsSignInIntent(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN);
    }

    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser user){

    }

    public void onBtnClick(View view){
        TextView txtHello = findViewById(R.id.textView);
        txtHello.setText("you click the button");
    }

    public void onSecBtnClick(View view){
        EditText [] edtTxtInput = new EditText[2];
        edtTxtInput[0] = findViewById(R.id.editTextInputEmailAddress);
        edtTxtInput[1] = findViewById(R.id.editTextInputPassword);
                //= findViewById(R.id.editTextTextEmailAddress);
        String emailInput = edtTxtInput[0].getText().toString();
        String passwordInput = edtTxtInput[1].getText().toString();

        TextView txtEmail = findViewById(R.id.emailTextView);
        TextView txtPassword = findViewById(R.id.passwordTextView);

        txtEmail.setText(emailInput);
        txtPassword.setText(passwordInput);
    }

    public void setEnterLoginPageActivity(){
        Intent intent = new Intent(this, LoginInActivity.class);
        startActivity(intent);
    }


    public class CSVFile {
        InputStream inputStream;

        public CSVFile(InputStream inputStream){
            this.inputStream = inputStream;
        }

        public ArrayList read(){
            ArrayList<NodeHash> csvDictionaryInside = new ArrayList();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String csvLine;
                while ((csvLine = reader.readLine()) != null) {
                    String[] row = csvLine.split(",");
                    NodeHash dummy= new NodeHash(row[0],  Integer. parseInt(row[1]));
                    //System.out.println(dummy.getValue()+" "+dummy.getIndex());
                    csvDictionaryInside.add(dummy);
                }
            }
            catch (IOException ex) {
                throw new RuntimeException("Error in reading CSV file: "+ex);
            }
            finally {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    throw new RuntimeException("Error while closing input stream: "+e);
                }
            }
            return csvDictionaryInside;
        }
    }



}