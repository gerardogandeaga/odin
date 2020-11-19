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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;
import com.group8.odin.R;
import com.group8.odin.examinee.fragments.ExamineeAuthPhotoSubmissionFragment;
import com.group8.odin.examinee.fragments.ExamineeDashboardFragment;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

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


    //added by Raj, if there is a problem in the code, its probably here lol,
    //Matthew needs to append this to the firebase
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private Timestamp logActivity1() {
        long time = new Date().getTime();
        Timestamp temp = new Timestamp(time, 0);
        return temp;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public Timestamp logActivity2() {
        long time = new Date().getTime();
        Timestamp temp = new Timestamp(time, 0);
        return temp;
    }

}

