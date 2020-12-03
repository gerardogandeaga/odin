package com.group8.odin.examinee.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.group8.odin.examinee.activities.ExamineeExamSessionActivity;
import com.group8.odin.examinee.list_items.RegisteredExamItem;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;
import com.group8.odin.common.models.ExamSession;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-01
 * Updated by: Shreya Jain
 * Description: Examinee dashboard fragment. Fragment will display the exams that the examinee is registered to.
 * Updated by: Shreya Jain
 * Updated on: 2020-11-26
 * Description: Added time checks
 */
public class ExamineeDashboardFragment extends Fragment {
    private FirebaseFirestore mFirestore;

    // View references
    @BindView(R2.id.recycler_view)
    RecyclerView mRvRegisteredExams;
    @BindView(R2.id.fabAction)
    ExtendedFloatingActionButton mFabRegister;

    private ItemAdapter mItemAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_dashboard_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        getActivity().setTitle(R.string.dashboard);

        // Setup recycler view with fastadapter
        mItemAdapter = new ItemAdapter();
        FastAdapter<RegisteredExamItem> fastAdapter = FastAdapter.with(mItemAdapter);

        fastAdapter.withOnClickListener(new OnClickListener<RegisteredExamItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RegisteredExamItem> adapter, RegisteredExamItem item, int position) {
                // set exam context
                if (Utils.isCurrentTimeBeforeTime(item.getExamSession().getExamStartTime())) {
                    Toast.makeText(getActivity(), "Exam session has not started", Toast.LENGTH_SHORT).show();
                }
                else
                if (Utils.isCurrentTimeBetweenTimes(item.getExamSession().getExamStartTime(), item.getExamSession().getExamEndTime())) {
                    startActivity(new Intent(getActivity(), ExamineeExamSessionActivity.class));
                    OdinFirebase.ExamSessionContext = item.getExamSession();
                }
                else
                if (Utils.isCurrentTimeAfterTime(item.getExamSession().getExamEndTime())) {
                    Toast.makeText(getActivity(), "Exam is over", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 1);

        mRvRegisteredExams.setLayoutManager(gridLayout);
        mRvRegisteredExams.setAdapter(fastAdapter); // bind adapter

        // adapter button clicks

        // Get reference to Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Setup floating action button
        mFabRegister.setText(R.string.register_exam);
        mFabRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load and go to examinee registration fragment
                ((ExamineeHomeActivity)getActivity()).showExamineeExamRegistration();
            }
        });

        loadExamSessions();

        // Logout of odin
        getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { startActivity(new Intent(getActivity(), LoginActivity.class)); }
        });
    }

    // Load user exam sessions
    public void loadExamSessions() {
        OdinFirebase.UserProfileContext.setExamSessions(new ArrayList<ExamSession>()); // clear exam sessions list for re-population
        System.out.println("loading exam sessions");
        for (String id : OdinFirebase.UserProfileContext.getExamSessionIds())
        {
            final DocumentReference exam = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(id);
            exam.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot examSessionDoc = task.getResult();
                        ExamSession session = new ExamSession(examSessionDoc);
                        OdinFirebase.UserProfileContext.getExamSessions().add(session);

                        // Display exam sessions in recycler view

                        if (OdinFirebase.UserProfileContext.getExamSessions().size() == OdinFirebase.UserProfileContext.getExamSessionIds().size()) {
                            ArrayList<RegisteredExamItem> adapterItems = new ArrayList<>(); // list item (gui)

                            // creates registered exam sessions and puts them in list
                            for (ExamSession examSession: OdinFirebase.UserProfileContext.getExamSessions()) adapterItems.add(new RegisteredExamItem().setExamSession(examSession));

                            // sort
                            adapterItems.sort(new RegisteredExamItem.Comparison());
                            // add to adapter
                            mItemAdapter.add(adapterItems);
                        }
                    } else
                        Log.e("UserProfile -> LoadExamSessions: ", "Error loading exam sessions");
                }
            });
        }
    }
}
