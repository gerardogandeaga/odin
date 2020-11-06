package com.group8.odin.proctor.fragments;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;
import com.group8.odin.examinee.list_items.AuthPhotoItem;
import com.group8.odin.examinee.list_items.RegisteredExamItem;
import com.group8.odin.proctor.activities.ProctorHomeActivity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-06
 * Description:
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

        getActivity().setTitle("Auth Photo Submissions");

        // Hide action button
        mFabAction.setVisibility(View.GONE);


        // Setup recycler view with fastadapter
        mItemAdapter = new ItemAdapter();
        FastAdapter<RegisteredExamItem> fastAdapter = FastAdapter.with(mItemAdapter);

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 2);

        mRvAuthPhotos.setLayoutManager(gridLayout);
        mRvAuthPhotos.setAdapter(fastAdapter); // bind adapter

        loadPhotosFromStorage();
    }

    // Retrieves and displays photos from the storage bucket in firebase storage
    private void loadPhotosFromStorage() {
        mReference.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        loadAllInRecycler(listResult);
                        Toast.makeText(getActivity(), "Listed photos", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Could not load photos...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAllInRecycler(ListResult listResult) {
        // Recycler list items
        ArrayList<AuthPhotoItem> items = new ArrayList<>();
        for (StorageReference ref : listResult.getItems()) {
            items.add(new AuthPhotoItem().setPhotoReference(ref));
        }

        mItemAdapter.add(items);
    }
}
