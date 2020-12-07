package com.group8.odin.common.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.group8.odin.OdinFirebase;
import com.group8.odin.Utils;

import java.util.Date;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description: Object representation of exam session
 * Updated by: Matthew Tong
 * Updated on: 2020-12-01
 * Description: removed objects and functions related to exam_end_time and auth_end_time
 * and added exam duration and auth duration
 */
public class ExamSession {
    private String examId; // Exam id in
    private String title;
    private Date examStartTime;
    private Date examEndTime;
    private long authDuration; // time in seconds
    private DocumentReference mReference;

    public ExamSession() {}

    // Constructor for Firestore exam session documents
    public ExamSession(DocumentSnapshot examSessionDocument) {
        title = examSessionDocument.get(OdinFirebase.FirestoreExamSession.TITLE).toString();
        examId = examSessionDocument.getId();
        examStartTime = Utils.getDate(((Timestamp)examSessionDocument.get(OdinFirebase.FirestoreExamSession.EXAM_START_TIME)).getSeconds());
        examEndTime = Utils.getDate(((Timestamp)examSessionDocument.get(OdinFirebase.FirestoreExamSession.EXAM_END_TIME)).getSeconds());
        authDuration = (long)examSessionDocument.get(OdinFirebase.FirestoreExamSession.AUTH_DURATION);
        mReference = examSessionDocument.getReference();
    }

    // setters

    public ExamSession setExamId(String examId) {
        this.examId = examId;
        return this;
    }
    public ExamSession setTitle(String title) {
        this.title = title;
        return this;
    }
    public ExamSession setExamStartTime(Date examStartTime) {
        this.examStartTime = examStartTime;
        return this;
    }
    public ExamSession setExamEndTime(Date examStartTime) {
        this.examStartTime = examStartTime;
        return this;
    }
    public ExamSession setAuthDuration(long authDuration) {
        this.authDuration = authDuration;
        return this;
    }

    // getters

    public String getExamId() {
        return examId;
    }
    public String getTitle() {
        return title;
    }
    public Date getExamStartTime() {
        return examStartTime;
    }
    public Date getExamEndTime() {
        return examEndTime;
    }
    public long getAuthDuration(){
        return authDuration;
    }
    public Date getAuthEndTime() {
        Date duration = new Date(authDuration);
        Date endDate = new Date(examStartTime.getTime());
        endDate.setHours(endDate.getHours() + duration.getHours());
        endDate.setMinutes(endDate.getMinutes() + duration.getMinutes());
        return endDate;
    }
    public DocumentReference getReference() {
        return mReference;
    }
}
