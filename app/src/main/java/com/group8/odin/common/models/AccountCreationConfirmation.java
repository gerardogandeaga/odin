package com.group8.odin.common.models;

import android.content.Intent;
import android.os.Bundle;
//import android.support.wearable.activity.WearableActivity;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.group8.odin.R;

/*  Created by: Matthew Tong
    Created on: 2020-11-04
    Description: Activity for Account Creation Confirmation Screen
*/

public class AccountCreationConfirmation extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation_confirmation);

        FirebaseAuth mAuth;
        // ...
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        String myConfirmedFirstName = SignUp.getUsrFirstName();
        String myConfirmedLastName = SignUp.getUsrLastName();
        String myConfirmedEmailAddress = SignUp.getUsrEmailAddress();
        String myConfirmedPassword = SignUp.getUsrPassword();

        TextView txtViewDisplayUsrFirstName = findViewById(R.id.textReviewFirstName);
        txtViewDisplayUsrFirstName.setText("First name: " + myConfirmedFirstName);

        TextView txtViewDisplayUsrLastName = findViewById(R.id.textReviewLastName);
        txtViewDisplayUsrLastName.setText("Last name: " + myConfirmedLastName);

        TextView txtViewDisplayUsrEmailAddress = findViewById(R.id.textReviewEmail);
        txtViewDisplayUsrEmailAddress.setText("E-mail Address: " + myConfirmedEmailAddress);

        TextView txtViewDisplayUsrPassword = findViewById(R.id.textReviewPassword);
        txtViewDisplayUsrPassword.setText("Password: " + myConfirmedPassword);

        //final Intent backToWelcomeScreen = new Intent(AccountCreationConfirmation.this, WelcomeScreen.class);

        // Begin firebase authentication
        // Un-comment the following lines to enable firebase authentication

        /*mAuth.createUserWithEmailAndPassword(myConfirmedEmailAddress, myConfirmedPassword)
                .addOnCompleteListener(AccountCreationConfirmation.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(backToWelcomeScreen); // TODO: add confirmation page
                            //comment out the following lines
                                    //Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "createUserWithEmail:success");
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);
                        } else {
                            startActivity(backToWelcomeScreen); // TODO: <-- change this, account creation is unsuccessful, let user try again

                                    //comment out the following lines
                                    //If sign in fails, display a message to the user.
                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                           // Toast.LENGTH_SHORT).show();
                                   // updateUI(null);


                        }
                    }
                });
                */

        // End of firebase authentication

    }
}

/*TODO: AccountCreationConfirmation
*   create fragment for Edit
*   add confirmation page
*   change function for unsuccessful account creation */