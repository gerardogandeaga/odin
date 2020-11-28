package com.group8.odin.proctor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.examinee.list_items.AuthPhotoItem;
import com.group8.odin.examinee.list_items.RegisteredExamItem;
import com.group8.odin.proctor.activities.ProctorExamSessionActivity;
import com.group8.odin.proctor.activities.ProctorHomeActivity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-06
 * Description: Fragment that would pull all the photos and display them to the proctor on call for a particular exam session
 */
public class ProctorAuthPhotosFragment extends Fragment {
    @BindView(R.id.recycler_view)
    RecyclerView mRvAuthPhotos;
    @BindView(R.id.fabAction)
    ExtendedFloatingActionButton mFabAction;

    private FirebaseStorage mStorage;
    private StorageReference mReference;
    private ItemAdapter mItemAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorage = FirebaseStorage.getInstance();
        mReference = mStorage.getReference().child(OdinFirebase.ExamSessionContext.getExamId());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Handle on back button pressed in the fragment
        getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // return to the exminee dashboard
                Intent back = new Intent(getActivity(), ProctorHomeActivity.class);
                startActivity(back);
            }
        });

        return inflater.inflate(R.layout.common_dashboard_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        getActivity().setTitle(R.string.photo_submission);

        // Hide action button
        mFabAction.setText(R.string.live_proctor);
        mFabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProctorExamSessionActivity) getActivity()).showLiveMonitoring();
            }
        });


        // Setup recycler view with fastadapter
        mItemAdapter = new ItemAdapter();
        FastAdapter<RegisteredExamItem> fastAdapter = FastAdapter.with(mItemAdapter);
        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 2);
        mRvAuthPhotos.setLayoutManager(gridLayout);
        mRvAuthPhotos.setAdapter(fastAdapter); // bind adapter

//        loadPhotosFromStorage();
    }

    public void addPhoto(UserProfile examinee) {
        System.out.println(mReference.child(examinee.getUserId()).toString());
        mItemAdapter.add(new AuthPhotoItem().setPhotoReference(mReference.child(examinee.getUserId() + ".jpg")).setName(examinee.getName()));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().setTitle(R.string.live_monitor);
                ((ProctorExamSessionActivity)getActivity()).showLiveMonitoring();
            }
        });
        super.onHiddenChanged(hidden);
    }
}
