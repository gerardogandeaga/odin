package com.group8.odin.examinee.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.group8.odin.R;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
    * Created by: Shreya Jain
    * Created on: 2020-12-02
 */
public class ExamineeExamEndFragment extends Fragment {
    @BindView(R.id.tvExamEnd)
    TextView mTvExamEnd;
    @BindView(R.id.btnBackToDashBoard)
    Button mBtnBackToDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.examinee_exam_end_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getActivity().setTitle(R.string.exam_finished_title);

        mBtnBackToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ExamineeHomeActivity)getActivity()).showExamineeDashboard();
            }
        });
    }
}
