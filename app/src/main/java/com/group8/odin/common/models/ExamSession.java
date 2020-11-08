package com.group8.odin.common.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.group8.odin.OdinFirebase;
import com.group8.odin.Utils;

import java.util.Date;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description: Object representation of exam session
 */
public class ExamSession {
    private String examId; // Exam id in
    private String title;
    private Date examStartTime, examEndTime;
    private Date authStartTime, authEndTime;

    public ExamSession() {}

    // Constructor for Firestore exam session documents
    public ExamSession(DocumentSnapshot examSessionDocument) {
        title = examSessionDocument.get(OdinFirebase.FirestoreExamSession.TITLE).toString();
        examId = examSessionDocument.getId();
        examStartTime = Utils.getDate(((Timestamp)examSessionDocument.get(OdinFirebase.FirestoreExamSession.EXAM_START_TIME)).getSeconds());
        examEndTime = Utils.getDate(((Timestamp)examSessionDocument.get(OdinFirebase.FirestoreExamSession.EXAM_END_TIME)).getSeconds());
        authStartTime = Utils.getDate(((Timestamp)examSessionDocument.get(OdinFirebase.FirestoreExamSession.AUTH_START_TIME)).getSeconds());
        authEndTime = Utils.getDate(((Timestamp)examSessionDocument.get(OdinFirebase.FirestoreExamSession.AUTH_END_TIME)).getSeconds());
    }

    //Setters and getters
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
    public ExamSession setExamEndTime(Date examEndTime) {
        this.examEndTime = examEndTime;
        return this;
    }

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
    public Date getAuthStartTime() {
        return authStartTime;
    }
    public Date getAuthEndTime() {
        return authEndTime;
    }
}
