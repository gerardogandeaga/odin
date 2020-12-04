package com.group8.odin.proctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.util.Util;
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
import com.group8.odin.Utils;
import com.group8.odin.common.models.ActivityLog;
import com.group8.odin.common.models.ExamSession;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.examinee.activities.ExamineeExamSessionActivity;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;
import com.group8.odin.proctor.fragments.ProctorAuthPhotosFragment;
import com.group8.odin.proctor.fragments.ProctorExamineeProfileFragment;
import com.group8.odin.proctor.fragments.ProctorLiveMonitoringFragment;
import com.group8.odin.proctor.fragments.ProctorPostExamReportFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-06
 * Description: Activity that would display authentication photos to the proctor
 * Updated by: Shreya Jain
 * Updated on: 2020-11-22
 * Updated by: Matthew Tong
 * Updated on: 2020-12-01
 * Description: added a new frament for editing exam session (ProctorEditExamSessionFragment)
 */
public class ProctorExamSessionActivity extends AppCompatActivity {
    private static final String TAG = "ProctorExamSessionActiv";
    private FirebaseFirestore mFirestore;
    private FragmentManager mFragmentManager;

    // fragments
//    private ProctorEditExamSessionFragment mProctorEditExamSessionFragment;
    private ProctorExamineeProfileFragment mProctorExamineeProfileFragment;
    private ProctorLiveMonitoringFragment mProctorMonitoringFragment;
    private ProctorAuthPhotosFragment mProctorAuthPhotoFragment;
    private ProctorPostExamReportFragment mProctorPostExamReportFragment;
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
//        mProctorEditExamSessionFragment = new ProctorEditExamSessionFragment();
        mProctorExamineeProfileFragment = new ProctorExamineeProfileFragment();
        mProctorMonitoringFragment = new ProctorLiveMonitoringFragment();
        mProctorAuthPhotoFragment = new ProctorAuthPhotosFragment();
        mProctorPostExamReportFragment = new ProctorPostExamReportFragment();

        // add fragments to memory
        mFragmentTransaction = mFragmentManager.beginTransaction();
        // add all fragments
        mFragmentTransaction.add(R.id.container, mProctorExamineeProfileFragment);
        mFragmentTransaction.add(R.id.container, mProctorMonitoringFragment);
        mFragmentTransaction.add(R.id.container, mProctorAuthPhotoFragment);
        mFragmentTransaction.add(R.id.container, mProctorPostExamReportFragment);
        // hid them
        mFragmentTransaction.hide(mProctorExamineeProfileFragment);
        mFragmentTransaction.hide(mProctorMonitoringFragment);
        mFragmentTransaction.hide(mProctorAuthPhotoFragment);
        mFragmentTransaction.hide(mProctorPostExamReportFragment);
        mFragmentTransaction.commit();

        // create a new handler for the clock
        mClockHandler = new Handler();

        // if exam time is over show the post exam report
        if (Utils.isCurrentTimeAfterTime(OdinFirebase.ExamSessionContext.getExamEndTime()) ||
                Utils.isCurrentTimeEqualToTime(OdinFirebase.ExamSessionContext.getExamEndTime())) {
            showPostExamReport();
            generateReport();
        }
        else {
            // if the exam is underway then go into live proctoring
            startListeningToActivityLogsCollection();
            showLiveMonitoring();
        }
    }


    // load a user profile, this will implicitly be an examinee profile
    private void loadUserProfileToLive(String id, final ActivityLog activityLog) {
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

    // load a user profile, this will implicitly be an examinee profile
    private void loadUserProfileToReport(String id, final ActivityLog activityLog) {
        DocumentReference userProfiles = mFirestore.collection(OdinFirebase.FirestoreCollections.USERS).document(id);
        userProfiles.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                UserProfile examinee = new UserProfile(snapshot);
                mExaminees.put(snapshot.getId(), Pair.create(examinee, activityLog));
                mProctorPostExamReportFragment.updateRecyclerView();
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
                                loadUserProfileToLive(documentId, log);
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

    private void generateReport() {
        OdinFirebase.ExamSessionContext.getReference().collection(OdinFirebase.FirestoreCollections.ACTIVITY_LOGS).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), "could not get activity logs", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, error.toString());
                    return;
                }

                // get activity logs
                for (DocumentSnapshot snapshot : snapshots.getDocuments()) {
                    ActivityLog log = new ActivityLog(snapshot);
                    if (log.isValid()) {
                        // examinee has logged in
                        loadUserProfileToReport(snapshot.getId(), log);
                    }
                }

                // update the post exam report fragment
                mProctorPostExamReportFragment.updateRecyclerView();
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
        mFragmentTransaction.hide(mProctorPostExamReportFragment);
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
        mFragmentTransaction.hide(mProctorPostExamReportFragment);
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
        mFragmentTransaction.hide(mProctorPostExamReportFragment);
        mFragmentTransaction.commit();
    }

    public void showPostExamReport() {
        mExamineeProfileContext = null;
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.hide(mProctorMonitoringFragment);
        mFragmentTransaction.hide(mProctorAuthPhotoFragment);
        mFragmentTransaction.hide(mProctorExamineeProfileFragment);
        mFragmentTransaction.show(mProctorPostExamReportFragment);
        mFragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (!mProctorPostExamReportFragment.isHidden() || !mProctorAuthPhotoFragment.isHidden() || !mProctorMonitoringFragment.isHidden()) {
            killClock(mClockRunnable);
            startActivity(new Intent(this, ProctorHomeActivity.class));
        }
        else
        if (!mProctorExamineeProfileFragment.isHidden()) {
            showLiveMonitoring();
        }
    }

    // Exam session timer ==========================================================================

    private Runnable mClockRunnable;
    private Handler mClockHandler;
    private boolean mExamEnded;

    public void killClock(Runnable runnable) {
        System.out.println("Killing exam clock...");
        mClockHandler.removeCallbacks(runnable);
    }

    public void startClock() {
        final Date examEnd = OdinFirebase.ExamSessionContext.getExamEndTime();
        mExamEnded = false;

        System.out.println("Starting exam clock...");
        mClockRunnable = new Runnable() {
            @Override
            public void run() {
                mClockHandler.postDelayed(mClockRunnable, 1000);
                try {
                    System.out.println("Proctor tick!");
                    // end exam session
                    if (!mExamEnded && Utils.isCurrentTimeAfterTime(examEnd)) {
                        System.out.println("showing post exam report");
                        //go to exam session end screen
                        showPostExamReport();
                        // stop the clock
                        killClock(this);
                    }

                    if (Utils.isCurrentTimeAfterTime(examEnd)) {
                        killClock(this);
                    }

                    mExamEnded = Utils.isCurrentTimeAfterTime(examEnd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mClockHandler.postDelayed(mClockRunnable, 1000);
    }

    @Override
    protected void onResume() {
        startClock();
        super.onResume();
    }

    @Override
    protected void onPause() {
        killClock(mClockRunnable);
        super.onPause();
    }
}
