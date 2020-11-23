package com.group8.odin.examinee.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.R2;
import com.group8.odin.Utils;
import com.group8.odin.common.activities.LoginActivity;
import com.group8.odin.common.models.ExamSession;
import com.group8.odin.examinee.activities.ExamineeExamSessionActivity;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;
import com.group8.odin.examinee.list_items.RegisteredExamItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-01
 * Description: Examinee dashboard fragment. Fragment will display the exams that the examinee is registered to.
 * Updated by: Shreya Jain
 */
public class ExamineeExamSessionHomeFragment extends Fragment {
    @BindView(R.id.tvExamInfo) TextView mTvExamInfo;
    @BindView(R.id.tvExamName) TextView mTvExamName;
    @BindView(R.id.textView3) TextView mTvExamID;
    @BindView(R.id.textView4) TextView mTvExamStartTime;
    @BindView(R.id.textView5) TextView mTvExamEndTime;

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
        mTvExamName.setText(OdinFirebase.ExamSessionContext.getTitle());
        mTvExamID.setText(OdinFirebase.ExamSessionContext.getExamId());
        mTvExamStartTime.setText(Utils.getDateTimeStringFromDate(OdinFirebase.ExamSessionContext.getExamStartTime()));
        mTvExamEndTime.setText(Utils.getDateTimeStringFromDate(OdinFirebase.ExamSessionContext.getExamEndTime()));
    }
}
