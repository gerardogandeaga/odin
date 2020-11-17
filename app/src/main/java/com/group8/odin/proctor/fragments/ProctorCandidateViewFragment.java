package com.group8.odin.proctor.fragments;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.group8.odin.R;
import com.group8.odin.R2;

import butterknife.BindView;

/*
* Created by: Shreya Jain
* Created On: November 13, 2020
* Description: Examinee summary view for proctor
 */

public class ProctorCandidateViewFragment extends Fragment {
    @BindView(R.id.textView2)
    TextView mExamineeName;
    @BindView(R.id.imageView)
    ImageView mExamineeImage;
    @BindView(R.id.textView3)
    TextView mExamineeTimestamp;
    @BindView(R.id.textView5)
    TextView mSummary;
    @BindView(R.id.textView6)
    TextView mActivityLog;
    @BindView(R.id.recycler_view)
    RecyclerView mActivityLogItems;
}
