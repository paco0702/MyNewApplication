package com.example.mynewapplication.register;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mynewapplication.LoginInActivity;
import com.example.mynewapplication.R;
import com.example.mynewapplication.entities.User;


import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import static com.example.mynewapplication.LoginInActivity.theUser;


public class Information_register extends AppCompatActivity {
    //from information page

    private EditText editTextGivenName;
    private EditText editTextSurname;
    private EditText editTextPhoneNum;

    String givenName;
    String surname;
    String phoneNum;
    //Long phoneNum;
    String birthday;

    private Button enterToRegisterPage;
    private Button backToLoginPage;
    private Button birthdayPicking;

    private TextView messageFromTheButton;
    Calendar c;
    TextView  pickedDate;
    DatePickerDialog dpd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_register);
        //this.theUser = new User();

        givenName = "";
        surname = "";
        phoneNum = "";
        birthday = "";

        //information page
        editTextGivenName = (EditText) findViewById(R.id.register_information_page_editTextTextPersonName);
        editTextSurname = (EditText) findViewById(R.id.register_information_page_editTextTextSurname);
        editTextPhoneNum = (EditText) findViewById(R.id.register_information_editTextPhone);

        backToLoginPage = (Button) findViewById(R.id.register_information_page_back_login_button);
        enterToRegisterPage = (Button) findViewById(R.id.register_information_page_next_button);

        pickedDate = (TextView) findViewById(R.id.picked_Birthday_textView);
        birthdayPicking = (Button) findViewById(R.id.register_information_pickedBDates_button);

        messageFromTheButton = (TextView) findViewById(R.id.register_information_page_message_from_button_textView);


        birthdayPicking.setOnClickListener(handleBirthdayPicking);
        enterToRegisterPage.setOnClickListener(handleEnterRegisterPage);
        backToLoginPage.setOnClickListener(handleBackToLoginPage);
    }

    View.OnClickListener handleBirthdayPicking = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            c = Calendar.getInstance();
            c.add(Calendar.YEAR, -18);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int mouth = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);

            dpd = new DatePickerDialog(Information_register.this, new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int mDayOfMonth) {
                    pickedDate.setText(mDayOfMonth+"/"+ (mMonth+1)+"/"+mYear);
                }
            }, day, mouth, year);
            dpd.show();
        };
    };


    View.OnClickListener handleBackToLoginPage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v==backToLoginPage){
                // back loginPage
                setBackToLoginInPageActivity();
            }
        }
    };

    View.OnClickListener handleEnterRegisterPage = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v==enterToRegisterPage){
                givenName = givenName+editTextGivenName.getText().toString();
                surname = surname+editTextSurname.getText().toString();
                phoneNum = phoneNum + editTextPhoneNum.getText().toString();
                birthday = birthday + pickedDate.getText().toString();
                // the picked birthday


                if(isEmpty(givenName)==false && isEmpty(surname)==false && isEmpty(birthday)==false ){
                    theUser.setUserName(givenName+" "+surname);
                    theUser.setBirthday(birthday);
                    if(phoneNum.compareTo("")==0){
                        theUser.setPhoneNum("");
                    }else{
                        // validate the phone
                        if(isValidPhoneNum(phoneNum)==true){
                            theUser.setPhoneNum(phoneNum);
                            //theUser.setPhoneNum(Long.parseLong(phoneNum));
                            messageFromTheButton.setText("");
                            //setEnterToRegisterPage();
                            //go to another page to enter more infomration
                            setEnterToRegisterCategoryPage();
                        }else{
                            messageFromTheButton.setText("The phone you entered is not vaild");
                        }
                    }
                }else if(isEmpty(givenName)==true && isEmpty(surname)==true && isEmpty(birthday)==true){
                    messageFromTheButton.setText("You did not enter your birthday and your name");
                } else if (isEmpty(givenName)==true && isEmpty(surname)==false && isEmpty(birthday)==true) {
                    messageFromTheButton.setText("You did not enter your birthday and your given name");
                } else if (isEmpty(givenName)==false && isEmpty(surname)==false && isEmpty(birthday)==true){
                    messageFromTheButton.setText("You did not enter your birthday ");
                }
                // back loginPage
            }
        }
    };

    private boolean isValidPhoneNum(String phoneNum){
        return Patterns.PHONE.matcher(phoneNum).matches();
    }

    private boolean isEmpty(String theColumn){
        if(theColumn.compareTo("")==0){
            return true;
        }else
            return false;
    }



    private void setBackToLoginInPageActivity(){
        finish();
    }



    private void setEnterToRegisterCategoryPage(){
        Intent intent = new Intent (this, Information_category_registor.class);
        startActivity(intent);
    }
}
