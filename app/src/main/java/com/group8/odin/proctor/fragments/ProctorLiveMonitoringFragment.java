package com.group8.odin.proctor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.GlideApp;
import com.group8.odin.R;
import com.group8.odin.common.models.ActivityLog;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.examinee.activities.ExamineeExamSessionActivity;
import com.group8.odin.examinee.list_items.RegisteredExamItem;
import com.group8.odin.proctor.activities.ProctorExamSessionActivity;
import com.group8.odin.proctor.activities.ProctorHomeActivity;
import com.group8.odin.proctor.list_items.ExamineeItem;
import com.group8.odin.proctor.list_items.HeaderAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.grpc.okhttp.internal.framed.Header;

/*
 * Created by: Shreya Jain
 * Updated by: Gerardo Gandeaga
 * Created on: 17/11/20
 * Description: Displays a list of examinees
 * Updated by: Shreya Jain
 * Updated on: 2020-11-22
 */
public class ProctorLiveMonitoringFragment extends Fragment {
    @BindView(R.id.fabAction) ExtendedFloatingActionButton mFab;
    @BindView(R.id.recycler_view) RecyclerView mRvExaminees;

    private ProctorExamSessionActivity mActivity;

    // recycler view adapaters and items
    private ItemAdapter<ExamineeItem> mItemAdapter;
    private ItemAdapter mHeaderAdapter;
    private FastAdapter<ExamineeItem> mFastAdapter;
    private FirebaseFirestore mFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (ProctorExamSessionActivity) getActivity();
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
        HeaderAdapter headerAdapter = new HeaderAdapter();
        mHeaderAdapter = new ItemAdapter();
        mItemAdapter = new ItemAdapter();

        mFastAdapter = FastAdapter.with(Arrays.asList(mHeaderAdapter, mItemAdapter));
        mFastAdapter.setHasStableIds(true);
        mFastAdapter.withSelectable(true);

        // setup recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRvExaminees.setLayoutManager(layoutManager);
        mRvExaminees.setItemAnimator(new DefaultItemAnimator());
        mRvExaminees.setAdapter(headerAdapter.wrap(mFastAdapter));

        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(headerAdapter);
        mRvExaminees.addItemDecoration(decoration);

        headerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });

        mFastAdapter.withOnClickListener(new OnClickListener<ExamineeItem>() {
            @Override
            public boolean onClick(@javax.annotation.Nullable View v, IAdapter<ExamineeItem> adapter, ExamineeItem item, int position) {
                if (item.getExaminee() != null) {
                    // load examinee profile
                    mActivity.showExamineeProfile(item.getExaminee());
                    return true;
                }
                return false;
            }
        });
    }

    public void updateRecyclerView() {
        // get examinees from activity
        ArrayList<Pair<UserProfile, ActivityLog>> examinees = ((ProctorExamSessionActivity) getActivity()).getExaminees();

        // create list items
        ArrayList<ExamineeItem> items = new ArrayList<>();
        int inactiveId = 2; // start the inactive ids
        int activeId = 1001; // start of active ids
        for (Pair<UserProfile, ActivityLog> examinee : examinees) {
            ExamineeItem item = new ExamineeItem().setExaminee(examinee);

            if (!item.getExaminee().second.getStatus()) {
                item.withIdentifier(inactiveId);
                inactiveId++;
            } else {
                item.withIdentifier(activeId);
                activeId++;
            }
            items.add(item);
        }

        // update adapter
        mItemAdapter.clear();
        mItemAdapter.add(items);
        mFastAdapter.notifyAdapterDataSetChanged();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            getActivity().setTitle(R.string.live_monitor);
            mActivity.getSupportActionBar().setHomeButtonEnabled(false);
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    getActivity().setTitle(R.string.photo_submission);
                    ((ProctorExamSessionActivity) getActivity()).showAuthPhotos();
                }
            });
        }
        super.onHiddenChanged(hidden);
    }
}