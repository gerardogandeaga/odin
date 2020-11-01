package com.group8.odin.examinee.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.R;
import com.group8.odin.examinee.fragments.ExamineeDashboardFragment;
import com.group8.odin.examinee.fragments.ExamineeExamRegistrationFragment;
import com.group8.odin.common.models.UserProfile;


/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description:
 */
public class ExamineeHomeActivity extends  AppCompatActivity {
    private Context mContext;
    private UserProfile mUserProfile;
    private FirebaseFirestore mFirestore;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        mContext = this;

        // Setup fragment manager
        mFragmentManager = getSupportFragmentManager();
        showExamineeDashboard();
    }


    public void showExamineeDashboard() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ExamineeDashboardFragment fragment = new ExamineeDashboardFragment();
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
    }

    public void showExamineeExamRegistration() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ExamineeExamRegistrationFragment fragment = new ExamineeExamRegistrationFragment();
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
    }
}
