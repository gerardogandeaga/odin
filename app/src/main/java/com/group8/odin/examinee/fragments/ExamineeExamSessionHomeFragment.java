package com.group8.odin.examinee.fragments;

import android.content.Intent;
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

import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.Utils;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-01
 * Description: Examinee dashboard fragment. Fragment will display the exams that the examinee is registered to.
 * Updated by: Shreya Jain
 * Updated on 2020-12-01
 * Updated by: Matthew Tong
 * Description: removed the displaying of exam end time (mTvExamEndTime)
 * Updated by: Shreya Jain
 */
public class ExamineeExamSessionHomeFragment extends Fragment {
    @BindView(R.id.tvExamInfo) TextView mTvExamInfo;
    @BindView(R.id.tvExamName) TextView mTvExamName;
    @BindView(R.id.tvExamStartTime) TextView mTvExamStartTime;
    @BindView(R.id.tvTimer) TextView mTvTimer;
    @BindView(R.id.timer_layout) LinearLayout mTimer_layout;
    @BindView(R.id.tvHours) TextView mTvHours;
    @BindView(R.id.tvMinutes) TextView mTvMinutes;
    @BindView(R.id.tvSeconds) TextView mTvSeconds;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        mTvExamName.setText(OdinFirebase.ExamSessionContext.getTitle());
        mTvExamStartTime.setText("Start Time: " + Utils.getDateTimeStringFromDate(OdinFirebase.ExamSessionContext.getExamStartTime()));
    }

    private static int secondsInDay = 24*60*60*1000; //milliseconds in a day
    private static int secondsInHour = 60*60*1000; //milliseconds in an hour
    private static int secondsInMinute = 60 * 1000; //milliseconds in a minute
    private static int secondsInSecond = 1000; //milliseconds in a second

    public void updateTime(long time) {
        long days = time /secondsInDay;
        time-= days * secondsInDay;
        long hours = time /secondsInHour;
        time -= hours * secondsInHour;
        long minutes = time / secondsInMinute;
        time -= minutes * secondsInMinute;
        long seconds = time /secondsInSecond;
        mTvHours.setText(String.format("%02d", hours) + ":");
        mTvMinutes.setText(String.format("%02d", minutes) + ":");
        mTvSeconds.setText(String.format("%02d", seconds));
    }
}
