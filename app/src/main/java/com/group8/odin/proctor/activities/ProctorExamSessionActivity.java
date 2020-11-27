package com.group8.odin.proctor.activities;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.proctor.fragments.ProctorAuthPhotosFragment;
import com.group8.odin.proctor.fragments.ProctorExamineeProfileFragment;
import com.group8.odin.proctor.fragments.ProctorLiveMonitoringFragment;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-06
 * Description: Activity that would display authentication photos to the proctor
 * Updated by: Shreya Jain
 * Updated on: 2020-11-22
 */
public class ProctorExamSessionActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private FragmentManager mFragmentManager;

    // fragments
    private ProctorExamineeProfileFragment mProctorExamineeProfileFragment;
    private ProctorLiveMonitoringFragment mProctorMonitoringFragment;
    private ProctorAuthPhotosFragment mProctorAuthPhotoFragment;
    private FragmentTransaction mFragmentTransaction;

    // reference to all examinees in the exam session
    private HashMap<String, Pair<UserProfile, ActivityLog>> mExaminees;

    // examinee profile context
    private Pair<UserProfile, ActivityLog> mExamineeProfileContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_view);

        mExaminees = new HashMap<>();
        mFirestore = FirebaseFirestore.getInstance();

        mFragmentManager = getSupportFragmentManager();
        mProctorExamineeProfileFragment = new ProctorExamineeProfileFragment();
        mProctorMonitoringFragment = new ProctorLiveMonitoringFragment();
        mProctorAuthPhotoFragment = new ProctorAuthPhotosFragment();

        // add fragments to memory
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.container, mProctorExamineeProfileFragment);
        mFragmentTransaction.add(R.id.container, mProctorMonitoringFragment);
        mFragmentTransaction.add(R.id.container, mProctorAuthPhotoFragment);
        mFragmentTransaction.hide(mProctorExamineeProfileFragment);
        mFragmentTransaction.hide(mProctorMonitoringFragment);
        mFragmentTransaction.hide(mProctorAuthPhotoFragment);
        mFragmentTransaction.commit();

        startListeningToActivityLogsCollection();
        showAuthPhotos();
    }

    // load activity logs
    // todo: remove unused function
    private void loadAllActivityLogs() {
        CollectionReference activityLogs = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(OdinFirebase.ExamSessionContext.getExamId()).collection(OdinFirebase.FirestoreCollections.ACTIVITY_LOGS);
        activityLogs.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // create activity log and user profile references
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    // create activity log and load user profile
                    loadUserProfile(snapshot.getId(), new ActivityLog(snapshot));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), R.string.doc_fetch_error + OdinFirebase.ExamSessionContext.getExamId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // load a user profile, this will implicitly be an examinee profile
    private void loadUserProfile(String id, final ActivityLog activityLog) {
        DocumentReference userProfiles = mFirestore.collection(OdinFirebase.FirestoreCollections.USERS).document(id);
        userProfiles.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                UserProfile examinee = new UserProfile(snapshot);
                mExaminees.put(snapshot.getId(), Pair.create(examinee, activityLog));
                mProctorMonitoringFragment.updateRecyclerView();

                // show auth photo in auth photo list
                mProctorAuthPhotoFragment.addPhoto(examinee);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), R.string.auth_profile_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startListeningToActivityLogsCollection() {
        CollectionReference activityLogs = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(OdinFirebase.ExamSessionContext.getExamId()).collection(OdinFirebase.FirestoreCollections.ACTIVITY_LOGS);
        activityLogs.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), R.string.listening_error, Toast.LENGTH_SHORT).show();
                    Log.e("ProctorExamSessionActivity", error.toString());
                    return;
                }

                // react to document changes
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    String documentId = dc.getDocument().getId();
                    switch (dc.getType()) {
                        case ADDED:
                            ActivityLog log = new ActivityLog(dc.getDocument());
                            if (log.isValid()) {
                                // examinee has logged in
                                loadUserProfile(documentId, log);
                            }
                            break;

                        case MODIFIED:
                            // examinee has written activity to log
                            updateList(mExaminees.get(documentId), dc.getDocument());

                            // live update the profile fragment
                            if (mExamineeProfileContext != null && documentId.equals(mExamineeProfileContext.first.getUserId())) {
                                mProctorExamineeProfileFragment.update(dc.getDocument());
                            }
                            break;

                        case REMOVED:
                            // shouldn't be possible
                            try {
                                throw new Exception(R.string.doc_remove_error + dc.getDocument().getId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                }
            }
        });
    }

    // get the examinee profile and activity proctor is currently viewing
    public Pair<UserProfile, ActivityLog> getExamineeProfileContext() { return mExamineeProfileContext;}

    private void updateList(Pair<UserProfile, ActivityLog> examinee, DocumentSnapshot snapshot) {
        // update activity log
        examinee.second.update(snapshot);
        // update list
        mProctorMonitoringFragment.updateRecyclerView();
    }

    // convert hash-map to list
    public ArrayList<Pair<UserProfile, ActivityLog>> getExaminees() {
        ArrayList<Pair<UserProfile, ActivityLog>> examinees = new ArrayList<>(mExaminees.values());
        examinees.sort(new ActivityLog.Comparison());
        return examinees;
    }

    // display a list of all auth photos
    public void showAuthPhotos() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.hide(mProctorMonitoringFragment);
        mFragmentTransaction.show(mProctorAuthPhotoFragment);
        mFragmentTransaction.hide(mProctorExamineeProfileFragment);
        mFragmentTransaction.commit();
    }

    // display the profile of the selected examinee
    public void showExamineeProfile(Pair<UserProfile, ActivityLog> examinee) {
        // MUST SET EXAMINEE CONTEXT FIRST
        mExamineeProfileContext = examinee;
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.hide(mProctorMonitoringFragment);
        mFragmentTransaction.hide(mProctorAuthPhotoFragment);
        mFragmentTransaction.show(mProctorExamineeProfileFragment);
        mFragmentTransaction.commit();

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLiveMonitoring();
            }
        });
    }

    // display a list of all examinee activities
    public void showLiveMonitoring() {
        mExamineeProfileContext = null;
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.show(mProctorMonitoringFragment);
        mFragmentTransaction.hide(mProctorAuthPhotoFragment);
        mFragmentTransaction.hide(mProctorExamineeProfileFragment);
        mFragmentTransaction.commit();
    }
}
