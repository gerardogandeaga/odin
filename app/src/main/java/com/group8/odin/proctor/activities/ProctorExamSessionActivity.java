package com.group8.odin.proctor.activities;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.common.models.ActivityLog;
import com.group8.odin.common.models.ExamSession;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.proctor.fragments.ProctorAuthPhotosFragment;
import com.group8.odin.proctor.fragments.ProctorExamineeProfileFragment;
import com.group8.odin.proctor.fragments.ProctorLiveMonitoringFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-06
 * Description: Activity that would display authentication photos to the proctor
 */
public class ProctorExamSessionActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private FragmentManager mFragmentManager;

    // todo: remove test
    private String EXAM_SESSION_ID = "4oY5scsnOGO99zmn8rNP";

    // fragments
    private ProctorExamineeProfileFragment mProctorExamineeProfileFragment;
    private ProctorLiveMonitoringFragment mProctorMonitoringFragment;
    private FragmentTransaction mFragmentTransaction;

    // reference to all examinees in the exam session
    private HashMap<String, Pair<UserProfile, ActivityLog>> mExaminees;
    private int mExpectedNumberOfExaminees, mCurrentExamineeCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_view);

        mExaminees = new HashMap<>();
        mFirestore = FirebaseFirestore.getInstance();

        mFragmentManager = getSupportFragmentManager();
        mProctorExamineeProfileFragment = new ProctorExamineeProfileFragment();
        mProctorMonitoringFragment = new ProctorLiveMonitoringFragment();

        // add fragments to memory
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.container, mProctorExamineeProfileFragment);
        mFragmentTransaction.add(R.id.container, mProctorMonitoringFragment);
        mFragmentTransaction.hide(mProctorExamineeProfileFragment);
        mFragmentTransaction.hide(mProctorMonitoringFragment);
        mFragmentTransaction.commit();

        showLiveMonitoring();

        // todo: just for testing
        DocumentReference examSession = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(EXAM_SESSION_ID);
        examSession.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                OdinFirebase.ExamSessionContext = new ExamSession(snapshot);
                startListeningToActivityLogsCollection();
                loadAllActivityLogs();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to load exam session", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // load activity logs
    private void loadAllActivityLogs() {
        CollectionReference activityLogs = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(OdinFirebase.ExamSessionContext.getExamId()).collection(OdinFirebase.FirestoreCollections.ACTIVITY_LOGS);
        activityLogs.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mExpectedNumberOfExaminees = queryDocumentSnapshots.size();

                // create activity log and user profile references
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    // create activity log and load user profile
                    loadUserProfile(snapshot.getId(), new ActivityLog(snapshot));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Could not get all documents for exam session -> " + OdinFirebase.ExamSessionContext.getExamId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // load all examinee profiles once the proctor enter the session
    private void loadUserProfile(String id, final ActivityLog activityLog) {
        DocumentReference userProfiles = mFirestore.collection(OdinFirebase.FirestoreCollections.USERS).document(id);
        userProfiles.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                mExaminees.put(snapshot.getId(), Pair.create(new UserProfile(snapshot), activityLog));
                mProctorMonitoringFragment.updateRecyclerView();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Was not able to get user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startListeningToActivityLogsCollection() {
        CollectionReference activityLogs = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(OdinFirebase.ExamSessionContext.getExamId()).collection(OdinFirebase.FirestoreCollections.ACTIVITY_LOGS);
        activityLogs.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), "There was an error, see run log...", Toast.LENGTH_SHORT).show();
                    Log.e("ProctorExamSessionActivity", error.toString());
                    return;
                }

                // react to document changes
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            // examinee has logged in
                            loadUserProfile(dc.getDocument().getId(), new ActivityLog(dc.getDocument()));
                            break;

                        case MODIFIED:
                            // examinee has written activity to log
                            updateList(mExaminees.get(dc.getDocument().getId()), dc.getDocument());
                            break;

                        case REMOVED:
                            // shouldn't be possible
                            try {
                                throw new Exception("Document was unexpectedly removed. id -> " + dc.getDocument().getId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                }
            }
        });
    }

    private void updateList(Pair<UserProfile, ActivityLog> examinee, DocumentSnapshot snapshot) {
        // update activity log
        examinee.second.update(snapshot);
        // update list
        mProctorMonitoringFragment.updateRecyclerView();
    }

    public ArrayList<Pair<UserProfile, ActivityLog>> getExaminees() {
        ArrayList<Pair<UserProfile, ActivityLog>> examinees = new ArrayList<>(mExaminees.values());
        examinees.sort(new ActivityLog.Comparison());
        System.out.println(examinees.get(0).first.getName());
        return examinees;
    }

    public void showAuthPhotos() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ProctorAuthPhotosFragment fragment = new ProctorAuthPhotosFragment();
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
    }

    public void showExamineeProfile() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.hide(mProctorMonitoringFragment);
        mFragmentTransaction.show(mProctorExamineeProfileFragment);
        mFragmentTransaction.commit();
    }

    public void showLiveMonitoring() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.show(mProctorMonitoringFragment);
        mFragmentTransaction.hide(mProctorExamineeProfileFragment);
        mFragmentTransaction.commit();
    }
}
