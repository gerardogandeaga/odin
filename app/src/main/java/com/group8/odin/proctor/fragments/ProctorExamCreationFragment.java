package com.group8.odin.proctor.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.R2;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.proctor.activities.ProctorHomeActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-05
 * Description: Fragment to add an exam session in firestore and reload list of exam sessions
 * Updated by: Shreya Jain
 * Updated on: 2020-11-07
 * Description: error handling, real time syncing of user profile and its associated exam sessions
 * Bug: The date set is one month behind. Eg: November 30, 2020 would be equivalent to 2020-10-30. -> Fixed On: 2020-11-07 by Shreya Jain
 */
public class ProctorExamCreationFragment extends Fragment {
    // Bind views
    // Edit Text
    @BindView(R2.id.etExamTitle) EditText mEtExamTitle;
    @BindView(R2.id.etExamDate)  EditText mEtExamDate;
    @BindView(R2.id.etExamTime)  EditText mEtExamTime;
    @BindView(R2.id.etAuthTime)  EditText mEtAuthTime;
    // Buttons
    @BindView(R2.id.btnExamDate)   Button mBtnExamDate;
    @BindView(R2.id.btnExamTime)   Button mBtnExamTime;
    @BindView(R2.id.btnAuthTime)   Button mBtnAuthTime;
    @BindView(R2.id.btnCreateExam) Button mBtnCreateExam;

    // Exam date
    int mYear, mMonth, mDay;
    boolean mExamDateSet;

    // Exam time
    int mStartExamHour, mStartExamMinute, mEndExamHour, mEndExamMinute;
    boolean mExamTimeSet;

    // Exam auth time
    int mStartAuthHour, mStartAuthMinute, mEndAuthHour, mEndAuthMinute;
    boolean mAuthTimeSet;

    // Firebase
    private FirebaseFirestore mFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.proctor_exam_creation_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        getActivity().setTitle("Exam Session Creation");

        // Handle on back button pressed in the fragment
        getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // return to the exminee dashboard
                ((ProctorHomeActivity)getActivity()).showProctorDashboard();
            }
        });

        // Lock date and time fields
        mEtExamDate.setEnabled(false);
        mEtExamTime.setEnabled(false);
        mEtAuthTime.setEnabled(false);

        // Initialize date picker
        mBtnExamDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // build date dialog
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mYear = year;
                        mDay = day;
                        mMonth = month;
                        mEtExamDate.setText(day + "-" + (month+1) + "-" + year); //Since date picker has months [0..11] ~ [Jan .. Dec]
                        mExamDateSet = true;
                    }
                }, mYear, mMonth, mDay);

                // show date dialog
                dialog.show();
            }
        });

        // Set exam time
        mBtnExamTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Start Time dialog
                TimePickerDialog startTime = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mStartExamHour = hour;
                        mStartExamMinute = minute;

                        // End time dialog
                        TimePickerDialog endTime = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                mEndExamHour = hour;
                                mEndExamMinute = minute;
                                // Finally display result in field
                                mEtExamTime.setText("From " + mStartExamHour + ":" + mStartExamMinute + " to " + mEndExamHour + ":" + mEndExamMinute);
                                mExamTimeSet = true;
                            }
                        }, mEndExamHour, mEndExamMinute, true);
                        endTime.show();

                    }

                }, mStartExamHour, mStartExamMinute, true);
                // Show start time
                startTime.show();
            }
        });

        // Set auth time
        mBtnAuthTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Time dialog
                TimePickerDialog startTime = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mStartAuthHour = hour;
                        mStartAuthMinute = minute;

                        // End time dialog
                        TimePickerDialog endTime = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                mEndAuthHour = hour;
                                mEndAuthMinute = minute;
                                // Finally display result in field
                                mEtAuthTime.setText("From " + mStartAuthHour + ":" + mStartAuthMinute + " to " + mEndAuthHour + ":" + mEndAuthMinute);
                                mAuthTimeSet = true;
                            }
                        }, mEndAuthHour, mEndAuthMinute, true);
                        endTime.show();
                    }

                }, mStartExamHour, mStartExamMinute, true);
                // Show start time
                startTime.show();
            }
        });


        // Adds an exam to firestore
        mBtnCreateExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldsAreValid()) {
                    // Convert times to dates
                    //The year is taken as (Year+1900) by the date picker.
                    Date examStart, examEnd, authStart, authEnd;
                    examStart = new Date(mYear - 1900, mMonth, mDay, mStartExamHour, mStartExamMinute);
                    examEnd = new Date(mYear - 1900, mMonth, mDay, mEndExamHour, mEndExamMinute);
                    authStart = new Date(mYear - 1900, mMonth, mDay, mStartAuthHour, mStartAuthMinute);
                    authEnd = new Date(mYear - 1900, mMonth, mDay, mEndAuthHour, mEndAuthMinute);

                    // Add a new document with a generated id.
                    Map<String, Object> data = new HashMap<>();
                    data.put(OdinFirebase.FirestoreExamSession.TITLE, mEtExamTitle.getText().toString().trim());
                    data.put(OdinFirebase.FirestoreExamSession.EXAM_START_TIME, new Timestamp(examStart));
                    data.put(OdinFirebase.FirestoreExamSession.EXAM_END_TIME, new Timestamp(examEnd));
                    data.put(OdinFirebase.FirestoreExamSession.AUTH_START_TIME, new Timestamp(authStart));
                    data.put(OdinFirebase.FirestoreExamSession.AUTH_END_TIME, new Timestamp(authEnd));

                    final Map<String, Object> ghostData = new HashMap<>();

                    // add exam session
                    mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS)
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    linkExamToProctorProfile(documentReference);
                                    documentReference.collection(OdinFirebase.FirestoreCollections.ACTIVITY_LOGS).add(ghostData);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Exam creation failed! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
    }

    // Function will check if we have a valid start and end time respective to each other
    private boolean validStartEndTimes(int startHour, int startMin, int endHour, int endMin) {
        if (startHour == endHour)
            return startMin <= endMin;
        return startHour < endHour;
    }

    // Check if auth and exam times are valid with respect to each other
    private boolean validExamAndAuthTimes() {
        return (validStartEndTimes(mStartExamHour, mStartExamMinute, mStartAuthHour, mStartAuthMinute) && validStartEndTimes(mEndAuthHour, mEndAuthMinute, mEndExamHour, mEndExamMinute));
    }

    // Checks if the exam creation fields are valid
    private boolean checkFieldsAreValid() {
        if (mEtExamTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Need an Exam Title!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!mExamDateSet) {
            Toast.makeText(getActivity(), "Exam date not set!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mExamTimeSet) {
            Toast.makeText(getActivity(), "Exam time not set!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mAuthTimeSet) {
            Toast.makeText(getActivity(), "Auth time not set!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validStartEndTimes(mStartExamHour, mStartExamMinute, mEndExamHour, mEndExamMinute)) {
            Toast.makeText(getActivity(), "Invalid exam times!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validStartEndTimes(mStartAuthHour, mStartAuthMinute, mEndAuthHour, mEndAuthMinute)) {
            Toast.makeText(getActivity(), "Invalid auth times!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validExamAndAuthTimes()) {
            Toast.makeText(getActivity(), "Invalid exam and auth times!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Add exam to user profile
    private void linkExamToProctorProfile(DocumentReference documentReference) {
        OdinFirebase.UserProfileContext.getUserProfileReference()
                // Update user profile in the cloud
                .update(OdinFirebase.FirestoreUserProfile.EXAM_IDS, FieldValue.arrayUnion(documentReference.getId())) // Add exam session id to the exam_ids array
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Exam created!", Toast.LENGTH_SHORT).show();

                        //Syncing user profile again to reflect updated changes
                        OdinFirebase.UserProfileContext.getUserProfileReference()
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                OdinFirebase.UserProfileContext = new UserProfile(snapshot);
                                ((ProctorHomeActivity)getActivity()).showProctorDashboard();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Something went wrong...Please refresh.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
