package com.group8.odin.common.activities;

import android.content.Intent;
import android.os.Bundle;
//import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

//For email validation
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*  Created by: Matthew Tong
    Created on: 2020-11-04
    Description: Activity for Sign Up Screen
    Updated by: Shreya Jain
    Updated on: 2020-11-07
    Description: Added Validation Code, Design Modifications and back button
*/

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.editTextFirstName)
    EditText mEtFirstName;
    @BindView(R.id.editTextLastName)
    EditText mEtLastName;
    @BindView(R.id.editTextEmail)
    EditText mEtEmail;
    @BindView(R.id.editPassword)
    EditText mEtPassword;
    //Declaring a regular expression for email as per RFC standards
    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    @BindView(R.id.cbProctor)
    CheckBox mCbRole;
    @BindView(R.id.btnSubmitAccountCreation)
    Button mBtnSubmit;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // user credentials
    private static String UserFirstName;
    private static String UserLastName;
    private static String UserEmailAddress;
    private static String UserPassword;
    //Declaring a regular expression for passwords as per the rules:
    /*
    * Minimum 1 lowercase letter
    * Minimum 1 uppercase letter
    * Minimum 1 numeric character
    * Minimum 1 special character from %$#@
    * Length should be 8 to 20
     */
    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
    private static boolean UserRole;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private static String ConfirmPassword;
    @BindView(R.id.editConfirmPassword)
    EditText mEtConfirmPassword;
    @BindView(R.id.btnBackToLogin)
    Button mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        setTitle("Sign Up");

        mFirestore = FirebaseFirestore.getInstance();

        // when create button is clicked
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int errorFlag = 0;

                //Checks for valid data
                UserFirstName = mEtFirstName.getText().toString();
                if(UserFirstName.isEmpty()){
                    errorFlag = 1;
                    Toast.makeText(SignUpActivity.this, "First name cannot be empty. Please enter your first name.", Toast.LENGTH_SHORT).show();
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }

                UserLastName = mEtLastName.getText().toString();
                if(UserLastName.isEmpty()){
                    errorFlag = 1;
                    Toast.makeText(SignUpActivity.this, "Last name cannot be empty. Please enter your last name.", Toast.LENGTH_SHORT).show();
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }

                UserEmailAddress = mEtEmail.getText().toString();
                if(UserEmailAddress.isEmpty()){
                    errorFlag = 1;
                    Toast.makeText(SignUpActivity.this, "Email address cannot be empty. Please enter your email address.", Toast.LENGTH_SHORT).show();
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }

                Matcher matcher = EMAIL_PATTERN.matcher(UserEmailAddress);
                if(!(matcher.matches())){
                    errorFlag = 1;
                    Toast.makeText(SignUpActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }

                UserPassword = mEtPassword.getText().toString();
                if(UserPassword.isEmpty()){
                    errorFlag = 1;
                    Toast.makeText(SignUpActivity.this, "Password cannot be empty. Please enter your Password.", Toast.LENGTH_SHORT).show();
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }

                ConfirmPassword = mEtConfirmPassword.getText().toString();
                if(ConfirmPassword.isEmpty()){
                    errorFlag = 1;
                    Toast.makeText(SignUpActivity.this, "Kindly confirm your password.", Toast.LENGTH_SHORT).show();
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }

                if(!(UserPassword.equals(ConfirmPassword))){
                    errorFlag = 1;
                    Toast.makeText(SignUpActivity.this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }

                Pattern pattern = Pattern.compile(PASSWORD_REGEX);
                Matcher matcher1 = pattern.matcher(UserPassword);
                if(!(matcher1.matches())){
                    errorFlag = 1;
                    Toast.makeText(SignUpActivity.this, "Invalid Password. Please try again.", Toast.LENGTH_SHORT).show();
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }

                if(errorFlag == 0) {
                    // Begin firebase authentication
                    mAuth.createUserWithEmailAndPassword(mEtEmail.getText().toString().trim(), mEtPassword.getText().toString().trim())
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //Sign in success, update UI with the signed-in user's information
                                        //Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        // Write data to user profile

                                        Map<String, Object> data = new HashMap<>();
                                        data.put(OdinFirebase.FirestoreUserProfile.NAME, mEtFirstName.getText().toString().trim() + " " + mEtLastName.getText().toString().trim());
                                        data.put(OdinFirebase.FirestoreUserProfile.EMAIL, mEtEmail.getText().toString().trim());
                                        data.put(OdinFirebase.FirestoreUserProfile.EXAM_IDS, new ArrayList<String>());
                                        data.put(OdinFirebase.FirestoreUserProfile.ROLE, mCbRole.isChecked());
                                        DocumentReference profileRef = mFirestore.collection(OdinFirebase.FirestoreCollections.USERS).document(user.getUid());
                                        profileRef.set(data)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(SignUpActivity.this, "User profile created!", Toast.LENGTH_SHORT).show();

                                                        Intent backToLogInScreen = new Intent(SignUpActivity.this, LoginActivity.class);
                                                        startActivity(backToLogInScreen);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SignUpActivity.this, "Could not create a user profile", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        //If sign up fails, display a message to the user.
                                        Toast.makeText(SignUpActivity.this, "Account Creation failed. Please Retry.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    // End of firebase authentication
                }
            }
        });

        //When back button is clicked
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToLogin = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(backToLogin);
            }
        });
    }


    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // ...
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}

/*TODO: SignUp
 *   show hint in RED if the user's input is invalid
 *   */