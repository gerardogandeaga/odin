package com.group8.odin.proctor.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.Utils;
import com.group8.odin.common.models.ExamSession;
import com.group8.odin.common.models.UserProfile;
import com.group8.odin.proctor.activities.ProctorHomeActivity;

import java.time.LocalDate;
import java.time.ZoneId;
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
 * Updated by: Shreya Jain
 * Updated on: 2020-11-22
 * Updated by: Matthew Tong
 * Updated on 2020-12-01
 * Description: fixed time format display and changed icons appearances
 * Updated by: Shreya Jain - added validation programming
 * TODO: Gerardo please check the code for validExamDate. Currently, date before current date is accepted which shouldn't be. Everything else working fine. Please fix it.
 */
public class ProctorExamCreationFragment extends Fragment {
    // Bind views
    // cards
    @BindView(R.id.cardExamDate)     CardView mCardExamDate;
    @BindView(R.id.cardAuthDuration) CardView mCardAuthDuration;
    @BindView(R.id.cardExamStart)    CardView mCardExamStart;
    @BindView(R.id.cardExamEnd)      CardView mCardExamEnd;
    // Edit Text
    @BindView(R.id.etExamTitle) EditText mEtExamTitle;
    @BindView(R.id.etEST_H) EditText mEtEST_H;
    @BindView(R.id.etEST_M) EditText mEtEST_M;
    @BindView(R.id.etEET_H) EditText mEtEET_H;
    @BindView(R.id.etEET_M) EditText mEtEET_M;
    @BindView(R.id.etAD_H) EditText mEtAD_H;
    @BindView(R.id.etAD_M) EditText mEtAD_M;
    @BindView(R.id.tvExamDate) TextView mTvExamDate;
    // Buttons
    @BindView(R.id.imgCalendar) ImageView mImgCalendar;
    @BindView(R.id.btnCreateExam) Button mBtnCreateExam;

    private boolean mIsNew = true;
    private ExamSession mExamSessionEdit;

    // Exam date
    int mYear, mMonth, mDay;
    boolean mExamDateSet;

    // Exam time
    private int mStartExamHour, mStartExamMinute, mEndExamHour, mEndExamMinute, mAuthHour, mAuthMin;
    // Auth duration
    private int mAuthDuration;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (mIsNew) {
            getActivity().setTitle(R.string.exam_creation);
            mBtnCreateExam.setText(R.string.create_exam);
        }
        else {
            getActivity().setTitle("Exam Session Details");
            mEtEST_H.setText(Integer.toString(mExamSessionEdit.getExamStartTime().getHours()));
            mEtEST_M.setText(Integer.toString(mExamSessionEdit.getExamStartTime().getMinutes()));
            mEtEET_H.setText(Integer.toString(mExamSessionEdit.getExamEndTime().getHours()));
            mEtEET_M.setText(Integer.toString(mExamSessionEdit.getExamEndTime().getMinutes()));
            mEtAD_H.setText(Integer.toString(new Date(mExamSessionEdit.getAuthDuration()).getHours()));
            mEtAD_M.setText(Integer.toString(new Date(mExamSessionEdit.getAuthDuration()).getMinutes()));
            mTvExamDate.setText(Utils.getDateStringFromDate(mExamSessionEdit.getExamStartTime()));
            Date examDate = mExamSessionEdit.getExamStartTime();
            Calendar cal = Calendar.getInstance();
            cal.setTime(examDate);
            mYear = cal.get(Calendar.YEAR);
            mMonth = cal.get(Calendar.MONTH);
            mDay = cal.get(Calendar.DAY_OF_MONTH);
            mExamDateSet = true;
            mEtExamTitle.setText(mExamSessionEdit.getTitle());
            mBtnCreateExam.setText(R.string.update_exam);
        }

        // Handle on back button pressed in the fragment
        getActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // return to the examinee dashboard
                ((ProctorHomeActivity)getActivity()).showProctorDashboard();
            }
        });

        // Initialize date picker
        mImgCalendar.setOnClickListener(new View.OnClickListener() {
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
                        mTvExamDate.setText(Utils.getDateStringFromDate(new Date(mYear - 1900, mMonth, mDay)));
                        mExamDateSet = true;
                    }
                }, mYear, mMonth, mDay);

                // show date dialog
                dialog.show();
            }
        });

        // Adds an exam to firestore
        mBtnCreateExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // first we reset the card views
                resetCards();

                if (!validInput()) {
                    return;
                }

                mStartExamHour = Integer.parseInt(mEtEST_H.getText().toString().trim());
                mStartExamMinute = Integer.parseInt(mEtEST_M.getText().toString().trim());
                mEndExamHour = Integer.parseInt(mEtEET_H.getText().toString().trim());
                mEndExamMinute = Integer.parseInt(mEtEET_M.getText().toString().trim());
                mAuthHour = Integer.parseInt(mEtAD_H.getText().toString().trim());
                mAuthMin = Integer.parseInt(mEtAD_M.getText().toString().trim());

                // Convert times to dates
                //The year is taken as (Year+1900) by the date picker.
                mYear = mYear - 1900;
                Date examStart, examEnd;
                examStart = new Date(mYear, mMonth, mDay, mStartExamHour, mStartExamMinute);
                examEnd = new Date(mYear, mMonth, mDay, mEndExamHour, mEndExamMinute);
                long authDuration = new Date(mYear , mMonth, mDay, Integer.parseInt(mEtAD_H.getText().toString().trim()), Integer.parseInt(mEtAD_M.getText().toString().trim()), 0).getTime();

                // Add a new document with a generated id.
                Map<String, Object> data = new HashMap<>();
                data.put(OdinFirebase.FirestoreExamSession.TITLE, mEtExamTitle.getText().toString().trim());
                data.put(OdinFirebase.FirestoreExamSession.EXAM_START_TIME, new Timestamp(examStart));
                data.put(OdinFirebase.FirestoreExamSession.EXAM_END_TIME, new Timestamp(examEnd));
                data.put(OdinFirebase.FirestoreExamSession.AUTH_DURATION, authDuration);

                // create a new exam session
                if (mIsNew) {
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
                                        Toast.makeText(getActivity(), R.string.exam_create_error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    // update the exam session details
                    else {
                        mExamSessionEdit.getReference().update(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), R.string.exam_edit_success, Toast.LENGTH_SHORT).show();
                                        ((ProctorHomeActivity)getActivity()).showProctorDashboard();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), R.string.exam_edit_error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
            }
        });
    }

    public void editExamSession(ExamSession examSession) {
        mIsNew = false;
        mExamSessionEdit = examSession;
    }

    private boolean validInput() {
        boolean pass = true;
        String est_h = mEtEST_H.getText().toString().trim();
        String est_m = mEtEST_M.getText().toString().trim();
        String eet_h = mEtEET_H.getText().toString().trim();
        String eet_m = mEtEET_M.getText().toString().trim();
        String ad_h = mEtAD_H.getText().toString().trim();
        String ad_m = mEtAD_M.getText().toString().trim();
        Date examDate = new Date(mYear, mMonth, mDay, Integer.parseInt(est_h), Integer.parseInt(est_m));

        if (est_h.isEmpty() | est_m.isEmpty()) {
            errorInExamStart();
            Toast.makeText(getActivity(), R.string.exam_start_time_error, Toast.LENGTH_SHORT).show();
            pass = false;
        }

        if (eet_h.isEmpty() | eet_m.isEmpty()) {
            errorInExamEnd();
            Toast.makeText(getActivity(), R.string.exam_end_time_error, Toast.LENGTH_SHORT).show();
            pass = false;
        }

        if (ad_h.isEmpty() | ad_m.isEmpty()) {
            errorInAuthDuration();
            Toast.makeText(getActivity(), R.string.ad_time_error, Toast.LENGTH_SHORT).show();
            pass = false;
        }

        // check the values
        if (pass) {
            // value
            int est_h_val = Integer.parseInt(est_h);
            int est_m_val = Integer.parseInt(est_m);
            int eet_h_val = Integer.parseInt(eet_h);
            int eet_m_val = Integer.parseInt(eet_m);
            int ad_h_val = Integer.parseInt(ad_h);
            int ad_m_val = Integer.parseInt(ad_m);

            if (validTime(est_h_val, est_m_val)) {
                errorInExamStart();
                Toast.makeText(getActivity(), R.string.est_error, Toast.LENGTH_SHORT).show();
                pass = false;
            }

            if (validTime(eet_h_val, eet_m_val)) {
                errorInExamEnd();
                Toast.makeText(getActivity(), R.string.eet_error, Toast.LENGTH_SHORT).show();
                pass = false;
            }

            if (validTime(ad_h_val, ad_m_val)) {
                errorInAuthDuration();
                Toast.makeText(getActivity(), R.string.ad_error, Toast.LENGTH_SHORT).show();
                pass = false;
            }

            if (invalidStartEndTimes(est_h_val, est_m_val, eet_h_val, eet_m_val)) {
                errorInExamEnd();
                Toast.makeText(getActivity(), R.string.time_error, Toast.LENGTH_SHORT).show();
                pass = false;
            }

            if(!validAuthTime(est_h_val, est_m_val, eet_h_val, eet_m_val, ad_h_val, ad_m_val)) {
                errorInAuthDuration();
                Toast.makeText(getActivity(), R.string.auth_longer_than_exam, Toast.LENGTH_SHORT).show();
                pass = false;
            }
        }

        if(!validExamDate(examDate)) {
            errorInExamDate();
            Toast.makeText(getActivity(), "Invalid date", Toast.LENGTH_SHORT).show();
            pass = false;
        }

        if (mEtExamTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.exam_title_error, Toast.LENGTH_SHORT).show();
            pass = false;
        }

        if (!mExamDateSet) {
            errorInExamDate();
            Toast.makeText(getActivity(), R.string.exam_date_error, Toast.LENGTH_SHORT).show();
            pass = false;
        }
        return pass;
    }

    private boolean validExamDate(Date exam_date) {
        return exam_date.after(Utils.getCurrentTime());
    }

    private boolean validTime(int hour, int minute) {
        return !validHour(hour) || !validMinute(minute);
    }

    private boolean validHour(int hour) {
        return hour >= 0 && hour <= 24;
    }

    private boolean validMinute(int minute) {
        return minute >= 0 && minute <= 60;
    }

    // Function will check if we have a valid start and end time respective to each other
    private boolean invalidStartEndTimes(int startHour, int startMin, int endHour, int endMin) {
        if (startHour == endHour) {
            return startMin >= endMin;
        } else {
            return startHour > endHour;
        }
    }

    private boolean validAuthTime(int startHour, int startMin, int endHour, int endMin, int authHour, int authMin){
        int examDuration = ((endHour - startHour) * 60 ) + (endMin - startMin);
        int authDuration = (authHour * 60) + authMin;
        return authDuration < examDuration;
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
                        Toast.makeText(getActivity(), R.string.general_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void errorInExamDate() {
        mCardExamDate.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_error));
    }

    private void errorInAuthDuration() {
        mCardAuthDuration.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_error));
    }

    private void errorInExamStart() {
        mCardExamStart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_error));
    }

    private void errorInExamEnd() {
        mCardExamEnd.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_error));
    }

    // reset the card colors
    private void resetCards() {
        mCardExamDate.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        mCardAuthDuration.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        mCardExamEnd.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        mCardExamEnd.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
    }
}
