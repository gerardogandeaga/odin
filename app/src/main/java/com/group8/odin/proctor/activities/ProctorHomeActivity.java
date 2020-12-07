package com.group8.odin.proctor.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.group8.odin.R;
import com.group8.odin.common.models.ExamSession;
import com.group8.odin.proctor.fragments.ProctorDashboardFragment;
import com.group8.odin.proctor.fragments.ProctorExamCreationFragment;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-01
 * Description: Activity to show the proctor's dashboard
 * Updated on: Shreya Jain
 * Updated by: 2020-11-21
 */
public class ProctorHomeActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_view);
        setTitle(R.string.dashboard);

        mFragmentManager = getSupportFragmentManager();
        showProctorDashboard();
    }

    public void showProctorDashboard() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ProctorDashboardFragment fragment = new ProctorDashboardFragment();
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
    }

    public void showProctorExamCreateOrEdit(ExamSession examSessionEdit) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ProctorExamCreationFragment fragment = new ProctorExamCreationFragment();
        if (examSessionEdit != null) fragment.editExamSession(examSessionEdit); // set exam session edit mode
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
    }
}
