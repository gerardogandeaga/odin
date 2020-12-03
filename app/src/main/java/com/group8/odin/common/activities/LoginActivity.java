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
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
    Updated by: Shreya Jain
    Updated On: 2020-11-07
    Description: Fixed design bugs, removed redundant part
*/
public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.btnLogin)   Button mBtnLogIn;
    @BindView(R.id.btnSignUp)  Button mBtnSignUp;
    @BindView(R.id.etEmail)    EditText mEtEmail;
    @BindView(R.id.etPassword) EditText mEtPassword;
    @BindView(R.id.tilEmail)    TextInputLayout mTilEmail;
    @BindView(R.id.tilPassword) TextInputLayout mTilPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.common_login_layout);
            ButterKnife.bind(this);

            setTitle(R.string.login);

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            // Action for the Log In button
            mBtnLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!validInput()) return;

                    //Begin firebase authentication (log in)
                    mAuth.signInWithEmailAndPassword(mEtEmail.getText().toString().trim(), mEtPassword.getText().toString().trim())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                private final String TAG = LoginActivity.class.getName();

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(LoginActivity.this, R.string.auth_success,
                                                Toast.LENGTH_SHORT).show();

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
                                                Toast.makeText(LoginActivity.this, R.string.auth_profile_fail, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        mTilEmail.setError(getString(R.string.signIn_fail));
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

    // valid the input fields
    private boolean validInput() {
        boolean pass = true;
        String myLogInEmail = mEtEmail.getText().toString().trim();
        String myLogInPassword = mEtPassword.getText().toString().trim();

        if(myLogInEmail.isEmpty()){
            mTilEmail.setError(getString(R.string.email_error));
            pass = false;
        }

        if(myLogInPassword.isEmpty()){
            mTilPassword.setError(getString(R.string.password_error));
            pass = false;
        }

        return pass;
    }

    // send user out of application
    @Override
    public void onBackPressed(){
        Intent exit = new Intent(Intent.ACTION_MAIN);
        exit.addCategory(Intent.CATEGORY_HOME);
        exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(exit);
    }
}

