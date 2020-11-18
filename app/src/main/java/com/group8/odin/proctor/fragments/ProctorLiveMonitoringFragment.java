package com.group8.odin.proctor.fragments;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.R;
import com.group8.odin.common.models.ActivityLog;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.examinee.list_items.RegisteredExamItem;
import com.group8.odin.proctor.activities.ProctorExamSessionActivity;
import com.group8.odin.proctor.list_items.ExamineeItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 17/11/20
 * Description: Displays a list of examinees
 */
public class ProctorLiveMonitoringFragment extends Fragment {
    @BindView(R.id.fabAction) ExtendedFloatingActionButton mFab;
    @BindView(R.id.recycler_view) RecyclerView mRvExaminees;

    private ItemAdapter<ExamineeItem> mItemAdapter;
    private FirebaseFirestore mFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_dashboard_layout, container, false); // re-use recycler view
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        // hid fab button
        mFab.setVisibility(View.GONE);


        // Setup recycler view with fastadapter
        mItemAdapter = new ItemAdapter();
        FastAdapter<ExamineeItem> fastAdapter = FastAdapter.with(mItemAdapter);

        // setup recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRvExaminees.setLayoutManager(layoutManager);
        mRvExaminees.setAdapter(fastAdapter);
    }

    public void updateRecyclerView() {
        // get examinees from activity
        ArrayList<Pair<UserProfile, ActivityLog>> examinees = ((ProctorExamSessionActivity) getActivity()).getExaminees();

        // create list items
        ArrayList<ExamineeItem> items = new ArrayList<>();
        for (Pair<UserProfile, ActivityLog> examinee : examinees) { items.add(new ExamineeItem().setExaminee(examinee)); }

        // update adapter
        mItemAdapter.clear();
        mItemAdapter.add(items);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) getActivity().setTitle("Live Monitoring");
        super.onHiddenChanged(hidden);
    }
}
