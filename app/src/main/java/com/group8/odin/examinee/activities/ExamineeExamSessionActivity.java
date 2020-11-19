package com.group8.odin.examinee.activities;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.group8.odin.R;
import com.group8.odin.examinee.fragments.ExamineeAuthPhotoSubmissionFragment;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-05
 * Description: This activity will handle the fragments of the application throughout the exam session.
 */
public class ExamineeExamSessionActivity<override> extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private static int counter = 0;

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

    @Override
    protected void onStart() {
        super.onStart();
        counter++;
        if (counter == 1){
            //come to foreground;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (counter > 0){
            counter--;
        }
        if (counter == 0){
            //go to background
        }
    }
}

