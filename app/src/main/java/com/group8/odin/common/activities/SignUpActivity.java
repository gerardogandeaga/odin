package com.group8.odin.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
    Updated by: Shreya Jain
    Updated on: 2020-11-20 and 2020-11-21
*/

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.etFirstName)        EditText mEtFirstName;
    @BindView(R.id.etLastName)         EditText mEtLastName;
    @BindView(R.id.etEduId)            EditText mEtEduId;
    @BindView(R.id.etEmail)            EditText mEtEmail;
    @BindView(R.id.etPassword)         EditText mEtPassword;
    @BindView(R.id.etConfirmPassword)  EditText mEtConfirmPassword;
    @BindView(R.id.tilFirstName)       TextInputLayout mTilFirstName;
    @BindView(R.id.tilLastName)        TextInputLayout mTilLastName;
    @BindView(R.id.tilEduId)           TextInputLayout mTilEduId;
    @BindView(R.id.tilEmail)           TextInputLayout mTilEmail;
    @BindView(R.id.tilPassword)        TextInputLayout mTilPassword;
    @BindView(R.id.tilConfirmPassword) TextInputLayout mTilConfirmPassword;
    //Declaring a regular expression for email as per RFC standards
    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    @BindView(R.id.cbProctor)                CheckBox mCbRole;
    @BindView(R.id.btnSubmitAccountCreation) Button mBtnSubmit;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    //Declaring a regular expression for passwords as per the rules:
    /*
    * Minimum 1 lowercase letter
    * Minimum 1 uppercase letter
    * Minimum 1 numeric character
    * Minimum 1 special character from %$#@
    * Length should be 8 to 20
     */
    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_signup_layout);
        ButterKnife.bind(this);

        setTitle(R.string.signup);

        mFirestore = FirebaseFirestore.getInstance();

        mTilPassword.setPasswordVisibilityToggleEnabled(true);
        mTilConfirmPassword.setPasswordVisibilityToggleEnabled(true);

        // when create button is clicked
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetInputs();
                if (!validInput()) return;

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
                                        data.put(OdinFirebase.FirestoreUserProfile.EDU_ID, mEtEduId.getText().toString().trim());
                                        data.put(OdinFirebase.FirestoreUserProfile.EXAM_IDS, new ArrayList<String>());
                                        data.put(OdinFirebase.FirestoreUserProfile.ROLE, mCbRole.isChecked());
                                        DocumentReference profileRef = mFirestore.collection(OdinFirebase.FirestoreCollections.USERS).document(user.getUid());
                                        profileRef.set(data)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(SignUpActivity.this, R.string.create_success, Toast.LENGTH_SHORT).show();

                                                        Intent backToLogInScreen = new Intent(SignUpActivity.this, LoginActivity.class);
                                                        startActivity(backToLogInScreen);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SignUpActivity.this, R.string.create_fail, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        //If sign up fails, display a message to the user.
                                        Toast.makeText(SignUpActivity.this, R.string.acc_create_fail,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    // End of firebase authentication
            }
        });
    }

    // validate input fields
    private boolean validInput() {
        boolean pass = true;
        String first, last, email, pass1, pass2, id;

        //Checks for valid data
        first = mEtFirstName.getText().toString().trim();
        if(first.isEmpty()){
            mTilFirstName.setError(getString(R.string.first_name_error));
            pass = false;
        }

        last = mEtLastName.getText().toString().trim();
        if(last.isEmpty()){
            mTilLastName.setError(getString(R.string.last_name_error));
            pass = false;
        }

        id = mEtEduId.getText().toString().trim();
        if(id.isEmpty()) {
            mTilEduId.setError(getString(R.string.eduId_error));
            pass = false;
        }

        email = mEtEmail.getText().toString().trim();
        if(email.isEmpty()){
            mTilEmail.setError(getString(R.string.email_error));
            pass = false;
        }

        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if(!(matcher.matches())){
            mTilEmail.setError(getString(R.string.email_error_valid));
            pass = false;
        }

        pass1 = mEtPassword.getText().toString().trim();
        if(pass1.isEmpty()){
            mTilPassword.setError(getString( R.string.password_error));
            pass = false;
        }

        pass2 = mEtConfirmPassword.getText().toString().trim();
        if(pass2.isEmpty()){
            mTilConfirmPassword.setError(getString(R.string.confirm_pass_error));
            pass = false;
        }

        if(!(pass2.equals(pass1))){
            mTilConfirmPassword.setError(getString(R.string.confirm_pass_error_valid));
            pass = false;
        }

        if(pass1.length() < 6){
            mTilPassword.setError(getString(R.string.password_length_error));
            pass = false;
        }

        return pass;
    }

    private void resetInputs() {
        mTilFirstName.setError(null);
        mTilLastName.setError(null);
        mTilEmail.setError(null);
        mTilEduId.setError(null);
        mTilPassword.setError(null);
        mTilConfirmPassword.setError(null);
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
