package com.group8.odin.proctor.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.R2;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.proctor.activities.ProctorHomeActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Matthew Tong
 * Created on: 2020-11-30
 * Description: Fragment to edit exam sessions
 */
public class ProctorEditExamSessionFragment extends Fragment {
    //Bind Views
    @BindView(R.id.etEditExamTitle) EditText mEtEditExamTitle;
    @BindView(R.id.etEditExamDate) EditText mEtEditExamDate;
    @BindView(R.id.etEditExamTime) EditText mEtEditExamTime;
    @BindView(R.id.etExamDuration) EditText mEtEditExamDuration;
    @BindView(R.id.etEditAuthTime) EditText mEtEditAuthTime;
    @BindView(R.id.etAuthDuration) EditText mEtEditAuthDuration;
    // Buttons
    @BindView(R.id.btnEditExamDate) Button mBtnEditExamDate;
    @BindView(R.id.btnEditExamTime)   Button mBtnEditExamTime;
    @BindView(R.id.btnEditExamDuration) Button mBtnEditExamDuration;
    @BindView(R.id.btnEditAuthTime)   Button mBtnEditAuthTime;
    @BindView(R.id.btnEditAuthDuration) Button mBtnEditAuthDuration;
    @BindView(R.id.btnSubmitExamEdit) Button mBtnSubmitExamEdit;

    // Exam date
    int mYear, mMonth, mDay;
    boolean mExamDateSet = false;

    // Exam time
    int mStartExamHour, mStartExamMinute, mEndExamHour, mEndExamMinute;
    String myExamDuration;
    boolean mExamTimeSet = false;
    boolean mExamDurationSet = false;

    // Exam auth time
    int mStartAuthHour, mStartAuthMinute, mEndAuthHour, mEndAuthMinute;
    String myAuthDuration;
    boolean mAuthTimeSet = false;
    boolean mAuthDurationSet = false;

    // Firebase
    private FirebaseStorage mStorage;
    private FirebaseFirestore mFirestore;
    private StorageReference mReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorage = FirebaseStorage.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mReference = mStorage.getReference().child(OdinFirebase.ExamSessionContext.getExamId());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstnaceState) {
        return inflater.inflate(R.layout.proctor_exam_edit_info_layout, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        getActivity().setTitle(R.string.exam_edit);

        // Handle on back button pressed in the fragment
        getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // return to the proctor dashboard
                ((ProctorHomeActivity)getActivity()).showProctorDashboard();
            }
        });

        // Lock date and time fields
        mEtEditExamDate.setEnabled(false);
        mEtEditExamTime.setEnabled(false);
        mEtEditAuthTime.setEnabled(false);
        mEtEditExamDuration.setEnabled(false);
        mEtEditAuthDuration.setEnabled(false);

        // Display the current info of the exam sesssion
        // Exam Title
        String examTitle = OdinFirebase.ExamSessionContext.getTitle();
        mEtEditExamTitle.setText(examTitle);

        // Date of the exam
        final Date originalExamDate = OdinFirebase.ExamSessionContext.getExamStartTime();
        //final Date examEndTime = OdinFirebase.ExamSessionContext.getExamEndTime(); // Exam End Time
        //parse and convert the original date string for display
        String originalExamDateString = originalExamDate.toString();
        try {
            Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(originalExamDateString);
            String newStr = new SimpleDateFormat("dd MMM yyyy").format(date);
            mEtEditExamDate.setText(newStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Exam Start Time
        //parse and convert the original date string for display
        try {
            Date date = new SimpleDateFormat("EEE MMM DD HH:mm:ss z yyyy").parse(originalExamDateString);
            String newStr = new SimpleDateFormat("HH:mmaa").format(date);
            mEtEditExamTime.setText(newStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Exam Duration
        String originalExamDuration = OdinFirebase.ExamSessionContext.getExamDuration();
        mEtEditExamDuration.setText(originalExamDuration);

        // Auth Start Time
        final Date originalAuthStartTime = OdinFirebase.ExamSessionContext.getAuthStartTime();
        //final Date authEndTime = OdinFirebase.ExamSessionContext.getAuthEndTime(); // Auth End Time
        String strAuthStartTime = originalAuthStartTime.toString();
        try {
            Date date = new SimpleDateFormat("EEE MMM DD HH:mm:ss z yyyy").parse(strAuthStartTime);
            String newStr = new SimpleDateFormat("HH:mmaa").format(date);
            mEtEditAuthTime.setText(newStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Auth Duration
        String originalAuthDuration = OdinFirebase.ExamSessionContext.getAuthDuration();
        mEtEditAuthDuration.setText(originalAuthDuration);

        // Initialize date picker
        mBtnEditExamDate.setOnClickListener(new View.OnClickListener() {
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
                        try {
                            String newStr = day + "-" + (month+1) + "-" + year;
                            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                            Date newDate = formatter.parse(newStr);
                            formatter = new SimpleDateFormat("dd MMM yyyy");
                            newStr = formatter.format(newDate);
                            mEtEditExamDate.setText(newStr);
                            mExamDateSet = true;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);

                // show date dialog
                dialog.show();
            }
        });

        // Edit Exam Start Time
        mBtnEditExamTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Start Time dialog
                TimePickerDialog startTime = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mStartExamHour = hour;
                        mStartExamMinute = minute;

                        // End time dialog
                        /*TimePickerDialog endTime = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                mEndExamHour = hour;
                                mEndExamMinute = minute;
                                */
                                Date examStart = new Date(mYear - 1900, mMonth, mDay, mStartExamHour, mStartExamMinute);
                                String newStr = new SimpleDateFormat("HH:mmaa").format(examStart);
                                // Display result in field
                                mEtEditExamTime.setText(newStr);
                                mExamTimeSet = true;
                            /*}
                        }, mEndExamHour, mEndExamMinute, true);
                        endTime.show();*/
                    }
            }, mStartExamHour, mStartExamMinute, true);
            // Show start time
            startTime.show();
        }
    });

        // Edit Exam Duration
        mBtnEditExamDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalender = Calendar.getInstance();
                int hour = 0;
                int minute = 0;

                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            myCalender.set(Calendar.MINUTE, minute);
                            myExamDuration = Integer.toString(hourOfDay * 60 + minute);
                            mEtEditExamDuration.setText(myExamDuration);
                            mExamDurationSet = true;
                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose duration:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });

        // Edit Auth Start Time
        mBtnEditAuthTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Time dialog
                TimePickerDialog startTime = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mStartAuthHour = hour;
                        mStartAuthMinute = minute;
                        Date examStart = new Date(mYear - 1900, mMonth, mDay, mStartAuthHour, mStartAuthMinute);
                        String newStr = new SimpleDateFormat("HH:mmaa").format(examStart);
                        // End time dialog
                        /*TimePickerDialog endTime = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                mEndAuthHour = hour;
                                mEndAuthMinute = minute;
                                */
                                // Display result in field
                                mEtEditAuthTime.setText(newStr);
                                mAuthTimeSet = true;
                            /*}
                        }, mEndAuthHour, mEndAuthMinute, true);
                        endTime.show();*/
                    }
                }, mStartExamHour, mStartExamMinute, true);
                // Show start time
                startTime.show();
            }
        });

        // Edit Auth Duration
        mBtnEditAuthDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalender = Calendar.getInstance();
                int hour = 0;
                int minute = 0;

                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            myCalender.set(Calendar.MINUTE, minute);
                            myAuthDuration = Integer.toString(hourOfDay * 60 + minute);
                            mEtEditAuthDuration.setText(myAuthDuration);
                            mAuthDurationSet = true;
                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose duration:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });

        // Update Firestore
        mBtnSubmitExamEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldsAreValid()) {
                    // Convert times to dates
                    //The year is taken as (Year+1900) by the date picker.
                    Date examStart, examEnd, authStart, authEnd;
                    if(!mExamTimeSet){
                        Date tempDate = originalExamDate;
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(originalExamDate);
                        mStartExamHour = cal.get(Calendar.HOUR_OF_DAY);
                        mEndExamMinute = cal.get(Calendar.MINUTE);
                    }
                    if(!mAuthTimeSet) {
                        Date tempDate = originalAuthStartTime;
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(originalExamDate);
                        mStartAuthHour = cal.get(Calendar.HOUR_OF_DAY);
                        mEndAuthMinute = cal.get(Calendar.MINUTE);
                    }
                    examStart = new Date(mYear - 1900, mMonth, mDay, mStartExamHour, mStartExamMinute);
                    //examEnd = new Date(mYear - 1900, mMonth, mDay, mEndExamHour, mEndExamMinute);
                    authStart = new Date(mYear - 1900, mMonth, mDay, mStartAuthHour, mStartAuthMinute);
                    //authEnd = new Date(mYear - 1900, mMonth, mDay, mEndAuthHour, mEndAuthMinute);
                    if(!mExamDateSet) {
                        //String strOriginalExamDate= originalExamDate.toString();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(originalExamDate);
                        mYear = cal.get(Calendar.YEAR);
                        mMonth = cal.get(Calendar.MONTH);
                        mDay = cal.get(Calendar.DAY_OF_MONTH);
                        examStart = new Date(mYear - 1900, mMonth, mDay, mStartExamHour, mStartExamMinute);
                        //examEnd = new Date(mYear - 1900, mMonth, mDay, mEndExamHour, mEndExamMinute);
                        authStart = new Date(mYear - 1900, mMonth, mDay, mStartAuthHour, mStartAuthMinute);
                        //authEnd = new Date(mYear - 1900, mMonth, mDay, mEndAuthHour, mEndAuthMinute);

                        if(!mExamTimeSet) examStart = originalExamDate;
                        if(!mAuthTimeSet) authStart = originalAuthStartTime;
                    }
                    if(!mExamDurationSet) myExamDuration = mEtEditExamDuration.getText().toString();
                    if(!mAuthDurationSet) myAuthDuration = mEtEditAuthDuration.getText().toString();

                    // final check for the properness of the data before updating to the firestore
                    // compute Exam End Time and Auth End Time
                    Calendar examEndCal = Calendar.getInstance();
                    Calendar authStartCal = Calendar.getInstance();
                    Calendar authEndCal = Calendar.getInstance();
                    examEndCal.setTime(examStart);
                    int examDurationInMinutes = Integer.parseInt(myExamDuration);
                    examEndCal.add(Calendar.MINUTE, examDurationInMinutes);
                    authStartCal.setTime(authStart);
                    authEndCal.setTime(authStart);
                    int authDurationInMinutes = Integer.parseInt(myAuthDuration);
                    authEndCal.add(Calendar.MINUTE, authDurationInMinutes);

                    //

                    Date temp1 = examEndCal.getTime();
                    Date temp2 = authStartCal.getTime();
                    Date temp3 = authEndCal.getTime();
                    System.out.println(temp1);
                    System.out.println(temp2);
                    System.out.println(temp3);
                    // Auth Start Time should not be later than the Exam End Time
                    // Auth End Time should not be later than the Exam End Time
                    if(!(examEndCal.before(authStartCal)) && !(examEndCal.before(authEndCal))){
                        // make a new hashmap for latest info of the exam session
                        Map<String, Object> data = new HashMap<>();
                        data.put(OdinFirebase.FirestoreExamSession.TITLE, mEtEditExamTitle.getText().toString().trim());
                        data.put(OdinFirebase.FirestoreExamSession.EXAM_START_TIME, new Timestamp(examStart));
                        //data.put(OdinFirebase.FirestoreExamSession.EXAM_END_TIME, new Timestamp(examEnd));
                        data.put(OdinFirebase.FirestoreExamSession.EXAM_DURATION, myExamDuration);
                        data.put(OdinFirebase.FirestoreExamSession.AUTH_START_TIME, new Timestamp(authStart));
                        //data.put(OdinFirebase.FirestoreExamSession.AUTH_END_TIME, new Timestamp(authEnd));
                        data.put(OdinFirebase.FirestoreExamSession.AUTH_DURATION, myAuthDuration);

                        Map<String, Object> ghostData = new HashMap<>();

                        // push updates to firestore
                        mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(OdinFirebase.ExamSessionContext.getExamId())
                                .update(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), R.string.exam_edit_success, Toast.LENGTH_SHORT).show();
                                        //DocumentReference doc = mFirestore.collection(OdinFirebase.FirestoreCollections.EXAM_SESSIONS).document(OdinFirebase.ExamSessionContext.getExamId());
                                        //linkExamToProctorProfile(doc);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), R.string.exam_edit_error, Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                    // display the warnings
                    // Auth Start Time should not be later than the Exam End Time
                    //TODO: fix issue with only icons but no error message
                    if(examEndCal.before(authStartCal)){
                        mEtEditAuthTime.setError("Authentication needs to start before the exam ends");
                        mEtEditAuthTime.requestFocus();
                        Toast.makeText(getActivity(), R.string.auth_start_after_exam_ends_error, Toast.LENGTH_LONG).show();
                    }
                    else if(examEndCal.before(authEndCal)){
                        mEtEditAuthTime.setError("Authentication needs to end before the exam ends");
                        mEtEditAuthTime.requestFocus();
                        Toast.makeText(getActivity(),R.string.auth_longer_than_exam, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    // Checks if the exam creation fields are valid
    //TODO: still necessary?
    private boolean checkFieldsAreValid() {
        return true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().startActivity(new Intent(getActivity(), ProctorHomeActivity.class));
            }
        });
        super.onHiddenChanged(hidden);
    }
}