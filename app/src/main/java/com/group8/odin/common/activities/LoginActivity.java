package com.group8.odin.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;
import com.group8.odin.proctor.activities.ProctorHomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/*  Created by: Matthew Tong
    Created on: 2020-11-04
    Modified on: 2020-11-06
    Description: Activity for Login Screen
*/
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.btnLogin)
    Button mBtnLogIn;
    @BindView(R.id.btnSignUp)
    Button mBtnSignUp;
    @BindView(R.id.editTextEmailAddress)
    EditText etMyLogInEmail;
    @BindView(R.id.editTextPassword)
    EditText etMyLogInPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);

        setTitle("Login");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Action for the Log In button
        mBtnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myLogInEmail = etMyLogInEmail.getText().toString();
                String myLogInPassword = etMyLogInPassword.getText().toString();

                //Begin firebase authentication (log in)
                mAuth.signInWithEmailAndPassword(myLogInEmail, myLogInPassword)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            private final String TAG = LoginActivity.class.getName();
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Authentication Success. Welcome.",
                                            Toast.LENGTH_SHORT).show(); //TODO: improve this toast message

                                    // get user profile
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference userProfile = db.collection(OdinFirebase.FirestoreCollections.USERS).document(user.getUid());
                                    userProfile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot snapshot) {
                                            OdinFirebase.UserProfileContext = new UserProfile(snapshot);

                                            // Move to next screen
                                            // Either move to the proctor or examinee screen based on user role
                                            Intent next;
                                            if (OdinFirebase.UserProfileContext.getRole() == UserProfile.Role.PROCTOR) {
                                                next = new Intent(LoginActivity.this, ProctorHomeActivity.class);
                                            } else {
                                                next = new Intent(LoginActivity.this, ExamineeHomeActivity.class);
                                            }
                                            startActivity(next);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this, "Profile fetch failed...", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed. Please try again",
                                            Toast.LENGTH_SHORT).show(); //TODO: improve this toast message
                                    //updateUI(null);
                                }
                            }
                        });
                //End firebase authentication (log in)
            }
        });

        //  Action for the Sign Up button
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to the Sign Up screen
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }


}

/*TODO: LoginScreen
 *       create fragment for login
 *       create fragment for forgot password
 *       perform input checks on all user inputs*/
