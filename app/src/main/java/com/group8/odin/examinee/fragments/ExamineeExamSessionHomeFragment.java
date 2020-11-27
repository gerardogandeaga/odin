package com.group8.odin.examinee.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.Utils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-01
 * Description: Examinee dashboard fragment. Fragment will display the exams that the examinee is registered to.
 * Updated by: Shreya Jain
 * Update: By Shreya on 2020-11-27: Added timer
 */
public class ExamineeExamSessionHomeFragment extends Fragment {
    @BindView(R.id.tvExamInfo) TextView mTvExamInfo;
    @BindView(R.id.tvExamName) TextView mTvExamName;
    @BindView(R.id.textView3) TextView mTvExamID;
    @BindView(R.id.textView4) TextView mTvExamStartTime;
    @BindView(R.id.textView5) TextView mTvExamEndTime;
    @BindView(R.id.timer) TextView mTvTimer;
    @BindView(R.id.timer_layout) LinearLayout mTimer_layout;
    @BindView(R.id.tvHours) TextView mTvHours;
    @BindView(R.id.tvMinutes) TextView mTvMinutes;
    @BindView(R.id.tvSeconds) TextView mTvSeconds;
    @BindView(R.id.message_layout)
    LinearLayout mMessage_layout;
    @BindView(R.id.exam_finished) TextView mTvExam_finished;

    private FirebaseFirestore mFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.examinee_exam_session_home_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getActivity().setTitle(R.string.exam_progress);
        mMessage_layout.setVisibility(View.GONE);
        mTvExamName.setText(OdinFirebase.ExamSessionContext.getTitle());
        mTvExamID.setText(OdinFirebase.ExamSessionContext.getExamId());
        mTvExamStartTime.setText("Start Time: " + Utils.getDateTimeStringFromDate(OdinFirebase.ExamSessionContext.getExamStartTime()));
        mTvExamEndTime.setText("End Time: " + Utils.getDateTimeStringFromDate(OdinFirebase.ExamSessionContext.getExamEndTime()));
        countDownTimer();
    }

    private Runnable runnable;
    public void countDownTimer() {
        final Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    Date examEnd = OdinFirebase.ExamSessionContext.getExamEndTime();
                    Date currentDate = new Date();
                    if(!currentDate.after(examEnd)){
                        long diff = examEnd.getTime() - currentDate.getTime();
                        long days = diff /(24*60*60*1000);
                        diff-= days * (24 * 60 * 60 * 1000);
                        long hours = diff /(60*60*1000);
                        diff -= hours * (60*60*1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff /1000;
                        mTvHours.setText(String.format("%02d", hours) + ":");
                        mTvMinutes.setText(String.format("%02d", minutes) + ":");
                        mTvSeconds.setText(String.format("%02d", seconds));
                    } else {
                        //TODO: Invesitgate why message is not shown yet timer is gone.
                        mMessage_layout.setVisibility(View.VISIBLE);
                        mTvExam_finished.setVisibility(View.VISIBLE);
                        timerViewGone();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }

    public void timerViewGone() {
        mTimer_layout.setVisibility(View.GONE);
    }
}
