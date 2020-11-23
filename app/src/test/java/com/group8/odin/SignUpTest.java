package com.group8.odin;

import android.content.Context;
import android.content.pm.InstrumentationInfo;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.common.activities.SignUpActivity;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/*
 * Created By: Shreya Jain
 * Created On: 2020-11-20
 */

public class SignUpTest {
    private int result = 0;
    private FirebaseAuth testAuth;
    private FirebaseFirestore testStore;

    @Test
    public void signUp_examinee() {
        testStore = FirebaseFirestore.getInstance();
        testAuth.createUserWithEmailAndPassword("testexaminee@gmail.com", "Cookie@123")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = testAuth.getCurrentUser();

                            // Write data to user profile
                            Map<String, Object> data = new HashMap<>();
                            data.put(OdinFirebase.FirestoreUserProfile.NAME, "Shreya Jain");
                            data.put(OdinFirebase.FirestoreUserProfile.EMAIL, "testexaminee@gmail.com");
                            data.put(OdinFirebase.FirestoreUserProfile.EXAM_IDS, new ArrayList<String>());
                            data.put(OdinFirebase.FirestoreUserProfile.ROLE, false);
                            DocumentReference profileRef = testStore.collection(OdinFirebase.FirestoreCollections.USERS).document(user.getUid());
                            profileRef.set(data)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            result = 1;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            result = 0;
                                        }
                                    });
                        } else {
                            //If sign up fails, display a message to the user.
                            result = 0;
                        }
                    }
                });

        assertEquals(1, result);
    }

    @Test
    public void signUp_proctor() {
        testStore = FirebaseFirestore.getInstance();
        testAuth.createUserWithEmailAndPassword("testproctor@gmail.com", "Cookie@123")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = testAuth.getCurrentUser();

                            // Write data to user profile
                            Map<String, Object> data = new HashMap<>();
                            data.put(OdinFirebase.FirestoreUserProfile.NAME, "Prof Joe");
                            data.put(OdinFirebase.FirestoreUserProfile.EMAIL, "testproctor@gmail.com");
                            data.put(OdinFirebase.FirestoreUserProfile.EXAM_IDS, new ArrayList<String>());
                            data.put(OdinFirebase.FirestoreUserProfile.ROLE, true);
                            DocumentReference profileRef = testStore.collection(OdinFirebase.FirestoreCollections.USERS).document(user.getUid());
                            profileRef.set(data)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            result = 1;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            result = 0;
                                        }
                                    });
                        } else {
                            //If sign up fails, display a message to the user.
                            result = 0;
                        }
                    }
                });

        assertEquals(1, result);
    }
}