package com.group8.odin.common.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.group8.odin.OdinFirebase;

import java.util.ArrayList;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description:
 */
public class ExamSession {
    private String examId;
    private String title;
    private Timestamp startTime;
    private Timestamp endTime;

    public ExamSession() {}

    // Constructor for Firestore exam session documents
    public ExamSession(DocumentSnapshot examSessionDocument) {
        title = examSessionDocument.get(OdinFirebase.FirestoreExamSession.TITLE).toString();
        startTime = (Timestamp)examSessionDocument.get(OdinFirebase.FirestoreExamSession.EXAM_START_TIME);
        endTime = (Timestamp)examSessionDocument.get(OdinFirebase.FirestoreExamSession.EXAM_END_TIME);
    }

    public ExamSession setExamId(String examId) {
        this.examId = examId;
        return this;
    }
    public ExamSession setTitle(String title) {
        this.title = title;
        return this;
    }
    public ExamSession setStartTime(Timestamp startTime) {
        this.startTime = startTime;
        return this;
    }
    public ExamSession setEndTime(Timestamp endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getExamId() {
        return examId;
    }
    public String getTitle() {
        return title;
    }
    public Timestamp getStartTime() {
        return startTime;
    }
    public Timestamp getEndTime() {
        return endTime;
    }
}
