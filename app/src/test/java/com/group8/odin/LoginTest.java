package com.group8.odin;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.group8.odin.common.activities.LoginActivity;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;
import com.group8.odin.proctor.activities.ProctorHomeActivity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by: Shreya Jain
 * Created on: 2020-11-20
 */
public class LoginTest {
    private static String test_password = "Cookie@123";
    private static String test_email = "testexaminee@gmail.com";
    private int result = 0;

    private FirebaseAuth testAuth;

    @Test
    public void login_successful() {
        testAuth = FirebaseAuth.getInstance();
        testAuth.signInWithEmailAndPassword(test_email, test_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = testAuth.getCurrentUser();
                            result = 1;

                        } else {
                            result = 2;
                        }
                    }
                });
        assertEquals(1, result);
    }
}