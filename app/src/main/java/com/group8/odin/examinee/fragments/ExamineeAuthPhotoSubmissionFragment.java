package com.group8.odin.examinee.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.R2;
import com.group8.odin.common.activities.LoginActivity;
import com.group8.odin.examinee.activities.ExamineeHomeActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-06
 * Description: Authentication photo submission screen.
 * Updated by: Shreya Jain
 * Updated on: 2020-11-08
 * Description: Added permissions check
 */
public class ExamineeAuthPhotoSubmissionFragment extends Fragment {
    @BindView(R2.id.imgResultPhoto) ImageView mImgAuthPhotoResult;
    @BindView(R2.id.btnSubmit)      Button mBtnSubmit;
    @BindView(R2.id.btnStartCamera) Button mBtnCamera;
    @BindView(R2.id.pbProgress)     ProgressBar mPbProgress;

    public static final int PIC_ID = 123;
    public static final int PERMISSIONS_REQUEST_CODE = 100;
    public int permissionsStatus = 0;

    private FirebaseStorage mStorage;
    private StorageReference mReference; // will point to exam session bucket
    private String mAuthPhotoUri; // Image kept in memory

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(checkPermission()) {
            permissionsStatus = 1;
            super.onCreate(savedInstanceState);
            mStorage = FirebaseStorage.getInstance();
            mReference = mStorage.getReference();
        } else {
            requestPermissions(
                    new String[]{Manifest.permission
                            .CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
        }
    }

    // function to check permissions
    private boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) + ContextCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (getActivity(), Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_CODE);
                    return true;
                }

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_CODE);
                    return true;
                }
            }
        } else {
            // Permissions are already granted
            return true;
        }
        return true; //General value to return in case of if-else failure so app doesn't crash.
    }


    // Function to initiate after permissions are given by user
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if(cameraPermission && writeExternalFile && readExternalFile)
                    {
                        Toast.makeText(getActivity(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                        permissionsStatus = 1;
                    }
                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(
                                new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_CODE);
                    }
                }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.examinee_auth_photo_submission, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        getActivity().setTitle("Authentication Photo Submission");

        // Camera activity
        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionsStatus == 1)
                    invokeCameraActivity();
                else {
                    Toast.makeText(getActivity(), "Permission Denied. Please grant permissions and try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Photo submission to storage bucket
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.fromFile(new File(mAuthPhotoUri));

                // store photo in exam session folder
                mReference.child(OdinFirebase.ExamSessionContext.getExamId() + "/" + uri.getLastPathSegment()).putFile(uri)// todo : when uploading files rename file to userid_time.jpg
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getActivity(), "Photo Uploaded!", Toast.LENGTH_SHORT).show();
                                mPbProgress.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mPbProgress.setVisibility(View.INVISIBLE);
                                mPbProgress.setProgress(0);
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                int progress = (int)(100 * (snapshot.getBytesTransferred() / snapshot.getTotalByteCount()));
                                mPbProgress.setProgress(progress);
                            }
                        });
            }
        });

        // Hide progress bar
        mPbProgress.setVisibility(View.INVISIBLE);
    }

    public void invokeCameraActivity() {
        // Create the camera intent from build in media store activities
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create temporary photo file
        File tmpPhotoFile = null;
        try {
            tmpPhotoFile = createImageFile();
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Camera could not start...", Toast.LENGTH_SHORT).show();
        }

        if (tmpPhotoFile != null) {
            // Start the camera activity where we will save image once image has been taken
            Uri photoUri = FileProvider.getUriForFile(getActivity(), "com.group8.odin.fileprovider", tmpPhotoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(cameraIntent, PIC_ID);
        }
    }

    // Method will be used to get the result of the camera activity from the ExamineeAuthPhotoSubmissionFragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Filter response code to pic result
        if (requestCode == ExamineeAuthPhotoSubmissionFragment.PIC_ID) {
            // Load but inter photo submission fragment with new data
            Glide.with(this).load(mAuthPhotoUri).into(mImgAuthPhotoResult);
        }

    }

    // This function is needed to get full resolution image
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image =  File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",   /* suffix */
                storageDir       /* directory */
        );
        System.out.println(image.toString());
        // Save a file: path for use with ACTION_VIEW intents
        mAuthPhotoUri = image.getAbsolutePath();
        return image;
    }
}
