package com.group8.odin.examinee.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.group8.odin.R;
import com.group8.odin.examinee.fragments.ExamineeDashboardFragment;
import com.group8.odin.examinee.fragments.ExamineeExamRegistrationFragment;


/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description: Examinee home (dashboard) activity
 */
public class ExamineeHomeActivity extends  AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_view);

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
