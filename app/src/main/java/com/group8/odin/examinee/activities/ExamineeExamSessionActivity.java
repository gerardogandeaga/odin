package com.group8.odin.examinee.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.math.MathUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.Utils;
import com.group8.odin.examinee.fragments.ExamineeAuthPhotoSubmissionFragment;
import com.group8.odin.examinee.fragments.ExamineeExamEndFragment;
import com.group8.odin.examinee.fragments.ExamineeExamSessionHomeFragment;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-05
 * Updated by: Mathew Tong
 * Updated by: Raj Patel
 * Updated by: Marshall Tang
 * Description: This activity will handle the fragments of the application throughout the exam session.
 * Updated by: Shreya Jain
 */
public class ExamineeExamSessionActivity extends AppCompatActivity {
    @BindView(R.id.pbTime) ProgressBar mPbTime;

    private FirebaseFirestore mFirestore;
    private static FragmentManager mFragmentManager;
    private static FragmentTransaction mFragmentTransaction;
    public boolean mInExam;
    public boolean mUploadedAuthPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examinee_exam_session_container_view);
        ButterKnife.bind(this);

        // Setup fragment manager
        mFragmentManager = getSupportFragmentManager();

        // create a new handler for the clock
        mClockHandler = new Handler();
        mListenForActivity = true;

        mPbTime.setMax(100);
        mPbTime.setMin(0);

        //check if auth time has ended
        if (Utils.isCurrentTimeBeforeTime(OdinFirebase.ExamSessionContext.getAuthEndTime())) {
            //auth time not yet ended
            // check if photo has already been submitted for the exam session
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference reference = storage.getReference();
            reference.child(OdinFirebase.ExamSessionContext.getExamId()).child(OdinFirebase.UserProfileContext.getUserId() + ".jpg")
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // auth photo exists in storage
                    showExamSessionHome(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // auth photo does not exist in storage so we can take an auth photo
                    showAuthPhotoSubmission();
                }
            });
        }
        else {
            showExamSessionHome(false);
        }

        // from anywhere in the exam session, back pressed will take you home
    }

    private ExamineeAuthPhotoSubmissionFragment mAuthFrag;
    private ExamineeExamSessionHomeFragment mSessionFrag;
    private ExamineeExamEndFragment mEndFrag;

    public void showAuthPhotoSubmission() {
        mInExam = false;
        mUploadedAuthPhoto = false;
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mAuthFrag = new ExamineeAuthPhotoSubmissionFragment();
        mFragmentTransaction.replace(R.id.container, mAuthFrag);
        mFragmentTransaction.commit();
    }

    public void showExamSessionHome(boolean report) {
        mInExam = true;
        mUploadedAuthPhoto = true;
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mSessionFrag = new ExamineeExamSessionHomeFragment();
        mFragmentTransaction.replace(R.id.container, mSessionFrag);
        mFragmentTransaction.commit();
        if (report) onResume();
    }

    public void showExamEndScreen() {
        mInExam = false;
        mUploadedAuthPhoto = true;
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mEndFrag = new ExamineeExamEndFragment();
        mFragmentTransaction.replace(R.id.container, mEndFrag);
        mFragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        startClock();
        if (mInExam) {
            if (mFirestore == null) mFirestore = FirebaseFirestore.getInstance();
            System.out.println(R.string.resume);
            updateActivityLog();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        killClock(mClockRunnable);
        if (mInExam && mListenForActivity) {
            if (mFirestore == null) mFirestore = FirebaseFirestore.getInstance();
            System.out.println(R.string.pause);
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
    }

    // Exam session timer ==========================================================================

    private Runnable mClockRunnable;
    private Handler mClockHandler;
    private boolean mAuthEnded;
    private boolean mExamEnded;
    private boolean mListenForActivity;

    public void killClock(Runnable runnable) {
        System.out.println("Killing exam clock...");
        mClockHandler.removeCallbacks(runnable);
    }

    public void startClock() {
        final Date examStart = OdinFirebase.ExamSessionContext.getExamStartTime();
        final Date examEnd = OdinFirebase.ExamSessionContext.getExamEndTime();
        final Date authEnd = OdinFirebase.ExamSessionContext.getAuthEndTime();

        System.out.println("Starting exam clock...");
        mClockRunnable = new Runnable() {
            @Override
            public void run() {
                mClockHandler.postDelayed(mClockRunnable, 1000);
                try {
                    long timeLength = (examEnd.getTime() - examStart.getTime()) / 1000;
                    long timeLeft =  (examEnd.getTime() - Utils.getCurrentTime().getTime()) / 1000; // time left in seconds
                    float rat = 1.f - ((float)timeLeft / timeLength);
                    System.out.println("rat -> " + rat);

                    if (timeLeft < 2) {
                        mListenForActivity = false; // cancel listening for examinee activity
                    }

                    if (mSessionFrag != null) {
                        mSessionFrag.updateTime(examEnd.getTime() - Utils.getCurrentTime().getTime());
                    }

                    // end auth time
                    if (!mUploadedAuthPhoto && !mAuthEnded && !mExamEnded && Utils.isCurrentTimeAfterTime(authEnd)) {
                        Toast.makeText(ExamineeExamSessionActivity.this, R.string.auth_ended, Toast.LENGTH_SHORT).show();
                        showExamSessionHome(true);
                    }

                    // end exam session
                    if (mAuthEnded && !mExamEnded && Utils.isCurrentTimeAfterTime(examEnd)) {
                        //go to exam session end screen
                        showExamEndScreen();
                        // stop the clock
                        killClock(this);
                    }

                    if (Utils.isCurrentTimeAfterTime(examEnd)) {
                        killClock(this);
                    }

                    mAuthEnded = Utils.isCurrentTimeAfterTime(authEnd);
                    mExamEnded = Utils.isCurrentTimeAfterTime(examEnd);

                    mPbTime.setProgress((int)(rat * 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mClockHandler.postDelayed(mClockRunnable, 1000);
    }

    @Override
    public void onBackPressed() {
        killClock(mClockRunnable);
        startActivity(new Intent(getApplicationContext(), ExamineeHomeActivity.class));
    }
}
