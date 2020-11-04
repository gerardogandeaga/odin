package com.group8.odin.common.models;

import android.content.Intent;
import android.os.Bundle;
//import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.group8.odin.R;

/*  Created by: Matthew Tong
    Created on: 2020-11-04
    Description: Activity for Sign Up Screen
*/

public class SignUp extends AppCompatActivity {

    private static String usrFirstName;
    private static String usrLastName;
    private static String usrEmailAddress;
    private static String usrPassword;

    public static String getUsrFirstName() {
        return usrFirstName;
    }

    public static String getUsrLastName() {
        return usrLastName;
    }

    public static String getUsrEmailAddress() {
        return usrEmailAddress;
    }

    public static String getUsrPassword(){
        return usrPassword;
    }

    public void setUsrFirstName(String usrFirstName) {
        this.usrFirstName = usrFirstName;
    }

    public void setUsrLastName(String usrLastName) {
        this.usrLastName = usrLastName;
    }

    public void setUsrEmailAddress(String usrEmailAddress) { this.usrEmailAddress = usrEmailAddress; }

    public void setUsrPassword(String usrPassword){
        this.usrPassword = usrPassword;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void onBtnSubmitAccountCreationClick (View view){
        Button btnSubmitAccountCreation = findViewById(R.id.btnSubmitAccountCreation);
        btnSubmitAccountCreation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Intent backToMain = new Intent(AccountCreation.this, MainActivity.class);
                //startActivity(backToMain);
                EditText edtFirstName = findViewById(R.id.editTextFirstName);
                EditText edtLastName = findViewById(R.id.editTextLastName);
                EditText edtEmailAddress = findViewById(R.id.editTextEmail);
                EditText edtPassword = findViewById(R.id.editPassword);
                String myFirstName = edtFirstName.getText().toString();
                String myLastName = edtLastName.getText().toString();
                String myEmailAddress = edtEmailAddress.getText().toString();
                String myPassword = edtPassword.getText().toString();
                setUsrFirstName(myFirstName);
                setUsrLastName(myLastName);
                setUsrEmailAddress(myEmailAddress);
                setUsrPassword(myPassword);
                Intent accountCreationConfirmation = new Intent(SignUp.this, AccountCreationConfirmation.class);
                startActivity(accountCreationConfirmation);

            }
        });
    }
}

/*TODO: SignUp
*   create back button -> welcome screen
*   add input checks on all input field
*   */