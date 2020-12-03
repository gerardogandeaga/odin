package com.group8.odin.proctor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.common.models.ActivityLog;
import com.group8.odin.common.models.UserProfile;
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

/*
 * Created by: Shreya Jain
 * Updated by: Gerardo Gandeaga
 * Created on: 17/11/20
 * Description: Displays a list of examinees
 * Updated by: Shreya Jain
 * Updated on: 2020-11-22 and 2020-11-28
 */
public class ProctorPostExamReportFragment extends Fragment {
    private static final String TAG = "ProctorPostExamReportFr";
    @BindView(R.id.fabAction) ExtendedFloatingActionButton mFab;
    @BindView(R.id.recycler_view) RecyclerView mRvExaminees;

    private ProctorExamSessionActivity mActivity;

    // recycler view adapaters and items
    private ItemAdapter<ExamineeItem> mItemAdapter;
    private ItemAdapter mHeaderAdapter;
    private FastAdapter<ExamineeItem> mFastAdapter;

    private int examineeCount =0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (ProctorExamSessionActivity) getActivity();
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

        //set up the button
        mFab.setText(R.string.back_to_dashboard);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), ProctorHomeActivity.class));
            }
        });


        // Setup recycler view with fastadapter
        HeaderAdapter headerAdapter = new HeaderAdapter(false);
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
            ExamineeItem item = new ExamineeItem().setExaminee(examinee, false);

            if (!item.getExaminee().second.getOverallStatus()) {
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

    //TODO: Gerardo, please Update examinee count. Current code not working?
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            examineeCount = mItemAdapter.getAdapterItems().size();
            String title = getString(R.string.post_exam_report) + ": " + Integer.toString(examineeCount);
            getActivity().setTitle(title);
            mActivity.getSupportActionBar().setHomeButtonEnabled(false);
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // exit the exam session and go to report
                    getActivity().startActivity(new Intent(getActivity(), ProctorHomeActivity.class));
                }
            });
        }
        super.onHiddenChanged(hidden);
    }
}
