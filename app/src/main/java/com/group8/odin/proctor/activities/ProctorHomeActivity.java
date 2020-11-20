package com.group8.odin.proctor.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.group8.odin.R;
import com.group8.odin.proctor.fragments.ProctorDashboardFragment;
import com.group8.odin.proctor.fragments.ProctorExamCreationFragment;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-01
 * Description: Activity to show the proctor's dashboard
 */
public class ProctorHomeActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_view);
        setTitle("Proctor Dashboard");

        mFragmentManager = getSupportFragmentManager();
        showProctorDashboard();
    }

    public void showProctorDashboard() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ProctorDashboardFragment fragment = new ProctorDashboardFragment();
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
    }

    public void showProctorExamCreation() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ProctorExamCreationFragment fragment = new ProctorExamCreationFragment();
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
    }
}
