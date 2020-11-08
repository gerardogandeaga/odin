package com.group8.odin.common.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/*  Created by: Matthew Tong
    Created on: 2020-11-04
    Modified on: 2020-11-06
    Description: Activity for Login Screen
    Updated by: Shreya Jain
    Updated On: 2020-11-07
    Description: Fixed design bugs, removed redundant part, added permissions check
*/
public class LoginActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 100;

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
        if (checkPermission()) {
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
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Invalid credentials. Please try again!",
                                                Toast.LENGTH_SHORT).show();
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
        } else {
            requestPermissions(
                    new String[]{Manifest.permission
                            .CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
        }
    }

    // function to check permissions
    private boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.CAMERA) + ContextCompat
                .checkSelfPermission(LoginActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (LoginActivity.this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_CODE);
                    return true;
                }

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_CODE);
                    return true;
                }
            }
        } else {
            // Permissions are already granted
            return true;
        }
        return true; //General value to return in case of if-else failure so app doesn't crash.
    }


    // Function to initiate after permissions are given by user
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if(cameraPermission && writeExternalFile && readExternalFile)
                    {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(
                                new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_CODE);
                    }
                }
        }
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

/*TODO: LoginScreen
 *       create fragment for login
 *       create fragment for forgot password
 */