package com.group8.odin.proctor.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
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
import com.group8.odin.common.models.ExamSession;
import com.group8.odin.examinee.list_items.RegisteredExamItem;
import com.group8.odin.proctor.activities.ProctorExamSessionActivity;
import com.group8.odin.proctor.activities.ProctorHomeActivity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-05
 * Description: Fragment to handle the proctor's dashboard that displays all the exams created by them
 * Updated by: Shreya Jain
 * Updated on: 2020-11-22
 * Updated by: Shreya on 2020-11-26: Added time checks
 */
public class ProctorDashboardFragment extends Fragment {
    @BindView(R2.id.recycler_view) RecyclerView mRvCreatedExams;
    @BindView(R2.id.fabAction)     ExtendedFloatingActionButton mFabCreateExam;

    private ItemAdapter mItemAdapter;
    private FirebaseFirestore mFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
    }

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

        // Setup recycler view with fast adapter
        mItemAdapter = new ItemAdapter();
        FastAdapter<RegisteredExamItem> fastAdapter = FastAdapter.with(mItemAdapter);

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 1);

        mRvCreatedExams.setLayoutManager(gridLayout);
        mRvCreatedExams.setAdapter(fastAdapter); // bind adapter

        // Open the auth photo bucket
        fastAdapter.withOnClickListener(new OnClickListener<RegisteredExamItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RegisteredExamItem> adapter, RegisteredExamItem item, int position) {
                OdinFirebase.ExamSessionContext = item.getExamSession();

                if (Utils.isCurrentTimeBeforeTime(OdinFirebase.ExamSessionContext.getExamStartTime())) {
                    ((ProctorHomeActivity) getActivity()).showProctorExamCreateOrEdit(item.getExamSession());
                }
                // after exam has started
                else if (Utils.isCurrentTimeAfterTime(OdinFirebase.ExamSessionContext.getExamStartTime())) {
                    startActivity(new Intent(getActivity(), ProctorExamSessionActivity.class));
                }
                return true;
            }
        });

        // Copy id into clipboard so user can share it
        fastAdapter.withOnLongClickListener(new OnLongClickListener<RegisteredExamItem>() {
            @Override
            public boolean onLongClick(View v, IAdapter<RegisteredExamItem> adapter, RegisteredExamItem item, int position) {
                ClipboardManager clipboard = getActivity().getSystemService(ClipboardManager.class);
                clipboard.setPrimaryClip(ClipData.newPlainText("examid", item.getExamSession().getExamId()));
                Toast.makeText(getActivity(), R.string.copy_message, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Setup floating action button
        mFabCreateExam.setText(R.string.create_exam);
        mFabCreateExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load and go to examinee registration fragment
                ((ProctorHomeActivity)getActivity()).showProctorExamCreateOrEdit(null);
            }
        });

        loadExamSessions();

        // Logout of odin
        getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.logout)
                        .setMessage(R.string.logout_confirm)
                        .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
    }

    // Load user exam sessions
//    public void loadExamSessions() {
//        for (String id : OdinFirebase.UserProfileContext.getExamSessionIds()) {
//            DocumentReference exam = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(id);
//            exam.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot examSessionDoc = task.getResult();
//                        ExamSession session = new ExamSession(examSessionDoc);
//                        OdinFirebase.UserProfileContext.getExamSessions().add(session);
//
//                        // Display exam sessions in recycler view
//                        mItemAdapter.add(new RegisteredExamItem().setExamSession(session));
//                    }
//                    else {
//                        Log.e("UserProfile -> LoadExamSessions: ", "Error loading exam sessions");
//                    }
//                }
//            });
//        }
//    }

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
