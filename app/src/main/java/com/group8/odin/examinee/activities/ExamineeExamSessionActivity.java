package com.group8.odin.examinee.activities;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.common.models.ExamSession;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.examinee.fragments.ExamineeAuthPhotoSubmissionFragment;
import com.group8.odin.examinee.fragments.ExamineeExamSessionHomeFragment;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-05
 * Updated by: Mathew Tong
 * Updated by: Raj Patel
 * Updated by: Marshall Tang
 * Description: This activity will handle the fragments of the application throughout the exam session.
 */
public class ExamineeExamSessionActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    public boolean InExam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_view);

        // Setup fragment manager
        mFragmentManager = getSupportFragmentManager();

        // check if photo has already been submitted for the exam session
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();
        reference.child(OdinFirebase.ExamSessionContext.getExamId()).child(OdinFirebase.UserProfileContext.getUserId() + ".jpg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // auth photo exists in storage
                        showExamSessionHome();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // auth photo does not exist in storage so we can take an auth photo
                        showAuthPhotoSubmission();
                    }
                });
    }

    private void showAuthPhotoSubmission() {
        InExam = false;
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ExamineeAuthPhotoSubmissionFragment fragment = new ExamineeAuthPhotoSubmissionFragment();
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
    }

    public void showExamSessionHome() {
        InExam = true;
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ExamineeExamSessionHomeFragment fragment = new ExamineeExamSessionHomeFragment();
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
        onResume();
    }

    @Override
    protected void onResume() {
        if (InExam) {
            if (mFirestore == null) mFirestore = FirebaseFirestore.getInstance();
            System.out.println("Activity has resumed");
            updateActivityLog();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (InExam) {
            if (mFirestore == null) mFirestore = FirebaseFirestore.getInstance();
            System.out.println("Activity has paused");
            updateActivityLog();
        }
        super.onPause();
    }

    public void updateActivityLog() {
        final Timestamp activityTimestamp = new Timestamp(new Date());

        //when activity log is triggered
        //create a document (activity log) for this examinee, under the collection exam_sessions -> activity_logs
        //this document will have the same id as the examinee's user profile document
        final DocumentReference activityLogRef = mFirestore
                .collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS)
                .document(OdinFirebase.ExamSessionContext.getExamId())
                .collection(OdinFirebase.FirestoreCollections.ACTIVITY_LOGS)
                .document(OdinFirebase.UserProfileContext.getUserId());

        activityLogRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        //store the activity description and timestamp
                        activityLogRef.update(OdinFirebase.FirestoreActivityLog.ACTIVITY, FieldValue.arrayUnion(activityTimestamp));
                    }
                    else {
                        Map<String, Object> data = new HashMap<>();
                        data.put(OdinFirebase.FirestoreActivityLog.ACTIVITY, Arrays.asList(activityTimestamp));
                        activityLogRef
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                }
            }
        });
        //TODO: store the message as well (optional)
    }
}
