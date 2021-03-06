package com.group8.odin.proctor.fragments;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.group8.odin.GlideApp;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.Utils;
import com.group8.odin.common.models.ActivityLog;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.proctor.activities.ProctorExamSessionActivity;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
* Created by: Shreya Jain
* Updated by: Gerardo Gandeaga
* Created On: November 13, 2020
* Description: Examinee summary view for proctor
* Updated by: Shreya Jain
* Updated on: 2020-11-22
 */

public class ProctorExamineeProfileFragment extends Fragment {
    @BindView(R.id.imgAuthPhoto)    ImageView mImgAuthPhoto;
    @BindView(R.id.imgFailed)       ImageView mImgFailed;
    @BindView(R.id.tvAuthTimeStamp) TextView mTvAuthPhotoTimestamp;
    @BindView(R.id.tvEmail)         TextView mTvEmail;
    @BindView(R.id.tvId)            TextView mTvId;
    @BindView(R.id.tvActivityLog)   TextView mTvActivityLog;

    private ProctorExamSessionActivity mActivity;
    private UserProfile mExaminee;
    private ActivityLog mExamineeActivityLog;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mActivity = (ProctorExamSessionActivity) getActivity();
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

    // loads data from firestore into views
    private void displayProfileAndActivityData() {
        // set fields
        mTvEmail.setText("Email: " + mExaminee.getEmail());
        mTvId.setText("ID: " + mExaminee.getEduId());

        // get the auth photo
        final StorageReference authPhoto = mStorage.getReference().child(OdinFirebase.ExamSessionContext.getExamId() + "/" + mExaminee.getUserId() + ".jpg");
        authPhoto.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Date date = new Date(storageMetadata.getUpdatedTimeMillis());
                mTvAuthPhotoTimestamp.setText("Submitted at: " + Utils.getDateTimeStringFromDate(date));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mTvAuthPhotoTimestamp.setText("Did not submit ID");
            }
        });

        // load auth photo into imageview
        GlideApp.with(mActivity)
                .load(authPhoto)
//                .placeholder(R.drawable.ic_profile)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mImgAuthPhoto.setVisibility(View.GONE);
                        mImgFailed.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mImgAuthPhoto.setVisibility(View.VISIBLE);
                        mImgFailed.setVisibility(View.GONE);
                        mImgAuthPhoto.setColorFilter(Color.TRANSPARENT);
                        return false;
                    }
                })
                .into(mImgAuthPhoto);
        mActivity.getSupportActionBar().setHomeButtonEnabled(true);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        update();
    }

    // updates examinee activity
    public void update(DocumentSnapshot activityLog) {
        mExamineeActivityLog.update(activityLog);
        update();
    }

    private void update() {
        // set string
        mTvActivityLog.setText(mExamineeActivityLog.toString());

        // set icon
        Drawable icon = ContextCompat.getDrawable(mActivity, R.drawable.ic_small_dot);
        icon.setTint(Utils.getExamineeStatusColour(mActivity, ((ProctorExamSessionActivity) getActivity()).IsLive ? mExamineeActivityLog.getStatus() : mExamineeActivityLog.getOverallStatus()));
        mActivity.getSupportActionBar().setHomeAsUpIndicator(icon);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mExaminee = mActivity.getExamineeProfileContext().first;
            mExamineeActivityLog = mActivity.getExamineeProfileContext().second;
            // load data into views
            displayProfileAndActivityData();

            getActivity().setTitle(mExaminee.getName());
            // get user profile
            DocumentReference userProfile = mFirestore.collection(OdinFirebase.FirestoreCollections.USERS).document(mExaminee.getUserId());
            userProfile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    mExaminee = new UserProfile(snapshot); // get the examinee profile
                    displayProfileAndActivityData();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), R.string.auth_profile_fail, Toast.LENGTH_SHORT).show();
                }
            });
        }
        super.onHiddenChanged(hidden);
    }
}
