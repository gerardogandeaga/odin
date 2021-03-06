package com.group8.odin.examinee.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.R2;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-01
 * Description: Fragment to handle registration of exam from the examinee dashboard
 * Updated by: Shreya Jain
 * Updated on: 2020-11-07
 * Description: Real time syncing of examinee dashboard and user profile
 * Updated by: Shreya Jain
 * Updated on: 2020-11-22
 */
public class ExamineeExamRegistrationFragment extends Fragment {
    private Context mContext;
    private FirebaseFirestore mFirestore;

    @BindView(R2.id.btnRegister)
    Button mBtnRegister;
    @BindView(R2.id.etExamID)
    EditText mEtExamId;

    // Fragment "constructor"
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // Attach custom view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.examinee_exam_registration_layout, container, false);
    }

    // Work with attached view here
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        getActivity().setTitle(R.string.exam_register);

        mFirestore = FirebaseFirestore.getInstance();

        // Handle on back button pressed in the fragment
        getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // return to the examinee's dashboard
                ((ExamineeHomeActivity)getActivity()).showExamineeDashboard();
            }
        });

        // Register for exam button
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load and go to examinee registration fragment
                getExamSession();
            }
        });
    }


    private void getExamSession() {
        DocumentReference examDocRef = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(mEtExamId.getText().toString().trim());
        // Get the exam document
        examDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        // Register exam session to user profile
                        registerExamToUserProfile(task.getResult());
                    }
                    else {
                        Toast.makeText(getContext(), R.string.exam_not_found, Toast.LENGTH_SHORT).show();
                        ((ExamineeHomeActivity)getActivity()).showExamineeDashboard();
                    }
                }
                else {
                    Toast.makeText(getContext(), R.string.get_exam_error, Toast.LENGTH_LONG).show();
                    ((ExamineeHomeActivity)getActivity()).showExamineeDashboard();
                }
            }
        });
    }

    private void registerExamToUserProfile(DocumentSnapshot snapshot) {
        OdinFirebase.UserProfileContext.getUserProfileReference()
                // Update user profile in the cloud
                .update(OdinFirebase.FirestoreUserProfile.EXAM_IDS, FieldValue.arrayUnion(snapshot.getId())) // Add exam session id to the exam_ids array
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), R.string.register_success, Toast.LENGTH_SHORT).show();

                        //Syncing user profile again to reflect updated changes
                        OdinFirebase.UserProfileContext.getUserProfileReference()
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                OdinFirebase.UserProfileContext = new UserProfile(snapshot);
                                ((ExamineeHomeActivity)getActivity()).showExamineeDashboard();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), R.string.general_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
