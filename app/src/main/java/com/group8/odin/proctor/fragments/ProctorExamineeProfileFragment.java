package com.group8.odin.proctor.fragments;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.group8.odin.GlideApp;
import com.group8.odin.MyAppGlideModule;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.Utils;
import com.group8.odin.common.models.ActivityLog;
import com.group8.odin.common.models.UserProfile;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
* Created by: Shreya Jain
* Updated by: Gerardo Gandeaga
* Created On: November 13, 2020
* Description: Examinee summary view for proctor
 */

public class ProctorExamineeProfileFragment extends Fragment {
    @BindView(R.id.imgAuthPhoto)    ImageView mImgAuthPhoto;
    @BindView(R.id.tvAuthTimeStamp) TextView mTvAuthPhotoTimestamp;
    @BindView(R.id.tvEmail)         TextView mTvEmail;
    @BindView(R.id.tvId)            TextView mTvId;
    @BindView(R.id.tvActivityLog)   TextView mTvActivityLog;

    // todo: remove test
    private String EXAM_SESSION_ID = "4oY5scsnOGO99zmn8rNP";
    private String USER_ID = "3HzKSTS0IHUJFs84P5GG2Ldu9fU2";

    private UserProfile mExamineeContext;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.proctor_examinee_profile_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    private void displayProfileAndActivityData() {
        // set fields
        mTvEmail.setText("Email: " + mExamineeContext.getEmail());
        mTvId.setText("ID: " + mExamineeContext.getUserId());

        // get the auth photo
        StorageReference authPhoto = mStorage.getReference().child(EXAM_SESSION_ID + "/" + USER_ID + ".jpg");
        authPhoto.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Date date = new Date(storageMetadata.getUpdatedTimeMillis());
                mTvAuthPhotoTimestamp.setText(Utils.getDateTimeStringFromDate(date));
            }
        });

        GlideApp.with(getActivity()).load(authPhoto).into(mImgAuthPhoto);

        // get activity data
        DocumentReference examineeActivityLog = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(EXAM_SESSION_ID).collection("activity_logs").document(mExamineeContext.getUserId());
        System.out.println(examineeActivityLog.getPath());
        examineeActivityLog.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                ActivityLog log = new ActivityLog(snapshot);
                // set string
                mTvActivityLog.setText(log.toString());
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            // todo: change activity title
            getActivity().setTitle(mExamineeContext.getName());
            // get user profile
            DocumentReference userProfile = mFirestore.collection(OdinFirebase.FirestoreCollections.USERS).document(USER_ID);
            userProfile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    mExamineeContext = new UserProfile(snapshot); // get the examinee profile
                    displayProfileAndActivityData();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Was not able to get examinee profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
        super.onHiddenChanged(hidden);
    }
}
