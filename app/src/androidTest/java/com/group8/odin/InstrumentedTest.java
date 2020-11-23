package com.group8.odin;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bumptech.glide.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    private int result = 0;
    private FirebaseAuth testAuth;
    private FirebaseFirestore testStore;

    //Created by: Matthew
    //Created on: 2020-11-22
    //Description: Check if the user profile data is being written to firebase correctly
    @Test
    public void user_profile_data_correctness() {
        //expected profile data
        final String expectedFullName = "Matthew Tong";
        final String expectedEmailAddress = "testexaminee@sfu.ca";
        boolean expectedRole = false;
        //create a hashmap from the expected data
        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put(OdinFirebase.FirestoreUserProfile.NAME, expectedFullName);
        expectedData.put(OdinFirebase.FirestoreUserProfile.EMAIL, expectedEmailAddress);
        expectedData.put(OdinFirebase.FirestoreUserProfile.EXAM_IDS, new ArrayList<String>());
        expectedData.put(OdinFirebase.FirestoreUserProfile.ROLE, false);
        //firebase variables
        testStore = FirebaseFirestore.getInstance();
        testAuth = FirebaseAuth.getInstance();
        //create a user with the expected profile data
        testAuth.createUserWithEmailAndPassword(expectedEmailAddress, "Cookie@123")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = testAuth.getCurrentUser();

                        // Write data to user profile
                        DocumentReference profileRef = testStore.collection(OdinFirebase.FirestoreCollections.USERS).document(user.getUid());
                        profileRef.set(expectedData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                    }
                });

        //get the user profile data from firebase and put it into a hashmap
        DocumentReference profileRef = testStore.collection(OdinFirebase.FirestoreCollections.USERS).document(testAuth.getCurrentUser().getUid());
        profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot profileDoc = task.getResult();
                Map<String, Object> resultData = profileDoc.getData();

                //check if the user profile data on firebase matches with the expected data
                assertEquals(expectedData, resultData);
            }
        });
    }

    //Written By: Shreya Jain
    //Test to check whether the exam created writes correct data to the firebase.
    @Test
    public void exam_information_correctness(){
        final String examID = "testexam05"; //exam id for this test. This id can be used to run the test since it is unused. This value needs to be changed every time the test is run.
        final String expectedExamTitle = "CMPT 276 MT1";
        final String expectedExamStartTime = Utils.getDateTimeStringFromDate(new Date(2020, 12, 10, 12, 00));
        final String expectedExamEndTime = Utils.getDateTimeStringFromDate(new Date(2020, 12, 10, 15, 00));
        final String expectedAuthStartTime = Utils.getDateTimeStringFromDate(new Date(2020, 12, 10, 12, 00));
        final String expectedAuthEndTime = Utils.getDateTimeStringFromDate(new Date(2020, 12, 10, 12, 15));

        //create a hashmap from the expected data
        final Map<String, Object> expectedData = new HashMap<>();
        expectedData.put(OdinFirebase.FirestoreExamSession.TITLE, expectedExamTitle);
        expectedData.put(OdinFirebase.FirestoreExamSession.EXAM_START_TIME, expectedExamStartTime);
        expectedData.put(OdinFirebase.FirestoreExamSession.EXAM_END_TIME, expectedExamEndTime);
        expectedData.put(OdinFirebase.FirestoreExamSession.AUTH_START_TIME, expectedAuthStartTime);
        expectedData.put(OdinFirebase.FirestoreExamSession.AUTH_END_TIME, expectedAuthEndTime);
        //firebase variables
        testStore = FirebaseFirestore.getInstance();
        testAuth = FirebaseAuth.getInstance();

        //create a exam with the expected data
        CollectionReference examSessions = testStore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS);
        examSessions.document(examID).set(expectedData);

    //get the exam data from firebase and put it into a hashmap
        DocumentReference examRef = testStore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(examID);
        examRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot examDoc = task.getResult();
                Map<String, Object> resultData = examDoc.getData();

                //check if the user profile data on firebase matches with the expected data
                assertEquals(expectedData, resultData);
            }
        });

    }

    //Written by Shreya Jain
    @Test
    public void getExamineeStatusColourTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        boolean status = true; //examinee is active.
        int expectedResult = -12345273; //Value of color the green tint is -12345273 in int data format.
        int actualResult = Utils.getExamineeStatusColour(appContext, status);
        assertEquals(expectedResult, actualResult);
    }
}
