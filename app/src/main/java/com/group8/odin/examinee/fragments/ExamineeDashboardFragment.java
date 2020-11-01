package com.group8.odin.examinee.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.group8.odin.examinee.list_items.RegisteredExamItem;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;
import com.group8.odin.common.models.ExamSession;
import com.group8.odin.common.models.UserProfile;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-01
 * Description: Examinee dashboard fragment. Fragment will display the exams that the
 * examinee is registered to.
 */
public class ExamineeDashboardFragment extends Fragment {
    private FirebaseFirestore mFirestore;
    private UserProfile mUserProfile;

    // View references
    @BindView(R2.id.recycler_view)
    RecyclerView mRvRegisteredExams;
    @BindView(R2.id.fabRegister)
    ExtendedFloatingActionButton mFabRegister;


    ItemAdapter mItemAdapter = new ItemAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.examinee_dashboard_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        // Setup recycler view with fastadapter
        mItemAdapter = new ItemAdapter();
        FastAdapter fastAdapter = FastAdapter.with(mItemAdapter);

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 2);


        mRvRegisteredExams.setLayoutManager(gridLayout);
        mRvRegisteredExams.setAdapter(fastAdapter); // bind adapter

        // Get reference to Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get user profile data
        final DocumentReference userProfile = mFirestore.collection(OdinFirebase.FirestoreCollections.USERS).document("PEXPr1CcCysorRApWSK7");
        userProfile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userProfileDoc = task.getResult();
                    mUserProfile = new UserProfile(userProfileDoc);
                    LoadExamSessions();
                }
                else {
                    Toast.makeText(getActivity(), "User profile could not be retrieved", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Setup floating action button
        mFabRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load and go to examinee registration fragment
                ((ExamineeHomeActivity)getActivity()).showExamineeExamRegistration();
            }
        });
    }

    // Load user exam sessions
    public boolean LoadExamSessions() {
        for (String id : mUserProfile.getExamSessionIds()) {
            DocumentReference exam = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(id);
            exam.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot examSessionDoc = task.getResult();
                        ExamSession session = new ExamSession(examSessionDoc);
                        mUserProfile.getExamSessions().add(session);

                        // Display exam sessions in recycler view
                        mItemAdapter.add(new RegisteredExamItem().setExamSession(session));
                    }
                    else {
                        Log.e("UserProfile -> LoadExamSessions: ", "Error loading exam sessions");
                    }
                }
            });
        }
        return true;
    }
}
