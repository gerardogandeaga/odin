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

/*  Created by: Matthew Tong
    Created on: 2020-11-04
    Description: Activity for Sign Up Screen
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
    @BindView(R.id.cbProctor)
    CheckBox mCbRole;
    @BindView(R.id.btnSubmitAccountCreation)
    Button mBtnSubmit;

    // user credentials
    private static String UserFirstName;
    private static String UserLastName;
    private static String UserEmailAddress;
    private static String UserPassword;
    private static boolean UserRole;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        setTitle("Sign Up");

        mFirestore = FirebaseFirestore.getInstance();

        // when Submit Confirmation is clicked
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                    //updateUI(user);
                                } else {
                                    //If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Account Creation failed. Please Retry.",
                                            Toast.LENGTH_SHORT).show(); // TODO: improve the toast message\
                                    // updateUI(null);
                                }
                            }
                        });
                // End of firebase authentication
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
        //updateUI(currentUser);
    }
}

/*TODO: SignUp
 *   create back button -> welcome screen
 *   perform checks on all user inputs: name, email, password
 *   show hint in RED if the user's input is invalid
 *   */