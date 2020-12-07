package com.group8.odin.common.models;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.auth.User;
import com.group8.odin.OdinFirebase;

import java.util.ArrayList;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description: Object representation of user profile
 */
public class UserProfile {
    public enum Role { PROCTOR, EXAMINEE }
    private String userId;
    private String eduId;
    private String name;
    private String email;
    private Role role;
    // list of associated exam sessions
    private ArrayList<ExamSession> examSessions;
    // list of exam sessions ids
    private ArrayList<String> examSessionIds;

    // Hold reference to document
    private DocumentReference userProfileReference;

    public UserProfile() {}

    // Create user from firebase reference and load the related data from firebase
    public UserProfile(DocumentSnapshot userProfileDocument) {
        userId = userProfileDocument.getId();
        name = userProfileDocument.get(OdinFirebase.FirestoreUserProfile.NAME).toString();
        email  = userProfileDocument.get(OdinFirebase.FirestoreUserProfile.EMAIL).toString();
        eduId = userProfileDocument.get(OdinFirebase.FirestoreUserProfile.EDU_ID).toString();
        role = (boolean)userProfileDocument.get(OdinFirebase.FirestoreUserProfile.ROLE) ? Role.PROCTOR : Role.EXAMINEE;
        examSessionIds = (ArrayList<String>)userProfileDocument.get(OdinFirebase.FirestoreUserProfile.EXAM_IDS);
        examSessions = new ArrayList<>();
        userProfileReference = userProfileDocument.getReference();
    }

    // Setters and getters
    public UserProfile setUserId(String userId) {
        this.userId = userId;
        return this;
    }
    public UserProfile setName(String name) {
        this.name = name;
        return this;
    }
    public UserProfile setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserProfile setEduId(String eduId) {
        this.eduId = eduId;
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
    public UserProfile setUserProfileReference(DocumentReference userProfileReference) {
        this.userProfileReference = userProfileReference;
        return this;
    }

    public String getUserId() { return userId; }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getEduId() {
        return eduId;
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
    public DocumentReference getUserProfileReference() {
        return userProfileReference;
    }
}
