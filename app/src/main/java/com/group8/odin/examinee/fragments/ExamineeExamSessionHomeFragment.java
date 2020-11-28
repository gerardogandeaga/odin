package com.group8.odin.examinee.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.Utils;
import com.group8.odin.examinee.activities.ExamineeExamSessionActivity;

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
    @BindView(R.id.btnSubmitAuthPhoto)
    Button mBtnSubmitAuthPhoto;

    private FirebaseFirestore mFirestore;

    private Date authStart = OdinFirebase.ExamSessionContext.getAuthStartTime();
    private Date authEnd = OdinFirebase.ExamSessionContext.getAuthEndTime();
    private Date examEnd = OdinFirebase.ExamSessionContext.getExamEndTime();

    private Runnable runnable;
    private Runnable authTime;
    private static int secondsInDay = 24*60*60*1000; //milliseconds in a day
    private static int secondsInHour = 60*60*1000; //milliseconds in an hour
    private static int secondsInMinute = 60 * 1000; //milliseconds in a minute
    private static int secondsInSecond = 1000; //milliseconds in a second

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

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
        mBtnSubmitAuthPhoto.setVisibility(View.GONE);
        mTvExamName.setText(OdinFirebase.ExamSessionContext.getTitle());
        mTvExamID.setText(OdinFirebase.ExamSessionContext.getExamId());
        mTvExamStartTime.setText("Start Time: " + Utils.getDateTimeStringFromDate(OdinFirebase.ExamSessionContext.getExamStartTime()));
        mTvExamEndTime.setText("End Time: " + Utils.getDateTimeStringFromDate(OdinFirebase.ExamSessionContext.getExamEndTime()));
        countDownTimer();
        checkForAuthTime();

        mBtnSubmitAuthPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to submitting auth photos
                ExamineeExamSessionActivity.showAuthPhotoSubmission();
            }
        });
    }

    public void countDownTimer() {
        final Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    Date currentDate = Utils.getCurrentTime();
                    if(!Utils.isCurrentTimeAfterTime(examEnd)){
                        long diff = examEnd.getTime() - currentDate.getTime();
                        long days = diff /secondsInDay;
                        diff-= days * secondsInDay;
                        long hours = diff /secondsInHour;
                        diff -= hours * secondsInHour;
                        long minutes = diff / secondsInMinute;
                        diff -= minutes * secondsInMinute;
                        long seconds = diff /secondsInSecond;
                        mTvHours.setText(String.format("%02d", hours) + ":");
                        mTvMinutes.setText(String.format("%02d", minutes) + ":");
                        mTvSeconds.setText(String.format("%02d", seconds));
                    } else {
                        //TODO: Go to new screen showing exam is done (Gerardo)
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
       mTvHours.setVisibility(View.GONE);
       mTvMinutes.setVisibility(View.GONE);
       mTvSeconds.setVisibility(View.GONE);
    }

    public void checkForAuthTime() {
        final Handler handler = new Handler();
        authTime = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    Date currentDate = Utils.getCurrentTime();
                    if(!Utils.isCurrentTimeAfterTime(authEnd)){
                        //check auth time
                        if(Utils.isCurrentTimeAfterTime(authStart) || Utils.isCurrentTimeEqualToTime(authStart)) {
                            // Auth time is going on
                            //TODO: Message that authentication has started. Toast message wont work as it keeps popping every second when the condition is true
                            mBtnSubmitAuthPhoto.setVisibility(View.VISIBLE);
                        } else {
                            //Auth time has not started but exam has
                            //TODO: Put up a message that auth time ended. Toast message wont work as it keeps popping every second when the condition is true
                            //Toast.makeText(getContext(), R.string.auth_not_started, Toast.LENGTH_SHORT).show();
                            mBtnSubmitAuthPhoto.setVisibility(View.GONE);
                        }
                    } else {
                        //Auth time has ended.
                        //TODO: Put up a message that auth time ended. Find a way to kill this runnable
                        //Toast.makeText(getContext(), R.string.auth_finished, Toast.LENGTH_SHORT).show();
                        mBtnSubmitAuthPhoto.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        handler.postDelayed(authTime, 1 * secondsInSecond);
    }
}
