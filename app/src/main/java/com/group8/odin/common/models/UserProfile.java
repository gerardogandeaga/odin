package com.group8.odin.common.models;


import com.google.firebase.firestore.DocumentSnapshot;
import com.group8.odin.OdinFirebase;

import java.util.ArrayList;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description:
 */
public class UserProfile {
    public enum Role { PROCTOR, EXAMINEE }
    private String name;
    private String email;
    private Role role;
    // list of associated exam sessions
    private ArrayList<ExamSession> examSessions;
    // list of exam sessions ids
    private ArrayList<String> examSessionIds;

    public UserProfile() {}

    // Create user from firebase reference and load the related data from firebase
    // DONT USE UNTIL WE START CONNECTING TO FIRESTORE
    public UserProfile(DocumentSnapshot userProfileDocument) {
        name = userProfileDocument.get(OdinFirebase.FirestoreUserProfile.NAME).toString();
        email  = userProfileDocument.get(OdinFirebase.FirestoreUserProfile.EMAIL).toString();
        role = (boolean)userProfileDocument.get(OdinFirebase.FirestoreUserProfile.ROLE) ? Role.PROCTOR : Role.EXAMINEE;
        examSessionIds = (ArrayList<String>)userProfileDocument.get(OdinFirebase.FirestoreUserProfile.EXAM_IDS);
        examSessions = new ArrayList<>();
    }

    // Setters and getters

    public UserProfile setName(String name) {
        this.name = name;
        return this;
    }
    public UserProfile setEmail(String email) {
        this.email = email;
        return this;
    }
    public UserProfile setRole(Role role) {
        this.role = role;
        return this;
    }
    public UserProfile setExamSessions(ArrayList<ExamSession> examSessions) {
        this.examSessions = examSessions;
        return this;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public Role getRole() {
        return role;
    }
    public ArrayList<ExamSession> getExamSessions() {
        return examSessions;
    }
    public ArrayList<String> getExamSessionIds() {
        return examSessionIds;
    }
}
