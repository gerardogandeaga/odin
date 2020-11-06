package com.group8.odin.examinee.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.group8.odin.R;
import com.group8.odin.examinee.fragments.ExamineeAuthPhotoSubmissionFragment;
import com.group8.odin.examinee.fragments.ExamineeDashboardFragment;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-05
 * Description: This activity will handle the fragments of the application throughout the exam session.
 */
public class ExamineeExamSessionActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        // Setup fragment manager
        mFragmentManager = getSupportFragmentManager();
        showAuthPhotoSubmission();
    }

    private void showAuthPhotoSubmission() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ExamineeAuthPhotoSubmissionFragment fragment = new ExamineeAuthPhotoSubmissionFragment();
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
    }
}
