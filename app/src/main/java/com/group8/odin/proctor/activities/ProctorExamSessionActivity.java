package com.group8.odin.proctor.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.group8.odin.R;
import com.group8.odin.proctor.fragments.ProctorAuthPhotosFragment;
import com.group8.odin.proctor.fragments.ProctorDashboardFragment;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-06
 * Description:
 */
public class ProctorExamSessionActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        mFragmentManager = getSupportFragmentManager();
        showAuthPhotos();
    }

    public void showAuthPhotos() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        ProctorAuthPhotosFragment fragment = new ProctorAuthPhotosFragment();
        mFragmentTransaction.replace(R.id.container, fragment);
        mFragmentTransaction.commit();
    }
}
