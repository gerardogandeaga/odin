package com.group8.odin.common.models;

import com.google.firebase.firestore.DocumentReference;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/*
Created by: Matthew Tong
Created On: 2020-11-22
Updated by Raj Patel
Updated on: 2020-11-28
Description: Added Education ID tests
 */

public class UserProfileTest {
    public enum Role { PROCTOR, EXAMINEE }
    private static final String expectedUserId = "expected user id";
    private static final String expectedName = "expected name";
    private static final String expectedEmail = "expected email";
    private static final String expectedEducID = "expected education id";
    private static final UserProfile.Role expectedRole1 = UserProfile.Role.PROCTOR;
    private static final UserProfile.Role expectedRole2 = UserProfile.Role.EXAMINEE;
    // list of associated exam sessions
    private static final ArrayList<ExamSession> expectedExamSessions = null;
    // list of exam sessions ids
    private static final ArrayList<String> expectedExamSessionIds = null;

    // Hold reference to document
    private static final DocumentReference expectedUserProfileReference = null;

    @Test
    public void setUserId() {
        UserProfile myUserProfile = new UserProfile();
        myUserProfile.setUserId(expectedUserId);
        String result = myUserProfile.getUserId();
        assertEquals(expectedUserId, result);
    }

    @Test
    public void setName() {
        UserProfile myUserProfile = new UserProfile();
        myUserProfile.setName(expectedName);
        String result = myUserProfile.getName();
        assertEquals(expectedName, result);
    }

    @Test
    public void setEmail() {
        UserProfile myUserProfile = new UserProfile();
        myUserProfile.setEmail(expectedEmail);
        String result = myUserProfile.getEmail();
        assertEquals(expectedEmail, result);
    }

    @Test
    public void setEducationID() {
        UserProfile myUserProfile = new UserProfile();
        myUserProfile.setEducationID(expectedEducID);
        String result = myUserProfile.getEducationID();
        assertEquals(expectedEducID, result);
    }

    @Test
    public void setRole() {
        UserProfile myUserProfile1 = new UserProfile();
        myUserProfile1.setRole(expectedRole1);
        UserProfile.Role result = myUserProfile1.getRole();
        assertEquals(expectedRole1, result);

        UserProfile myUserProfile2 = new UserProfile();
        myUserProfile2.setRole(expectedRole2);
        UserProfile.Role result2 = myUserProfile1.getRole();
        assertEquals(expectedRole1, result2);
    }

    @Test
    public void setExamSessions() {
        UserProfile myUserProfile = new UserProfile();
        myUserProfile.setExamSessions(expectedExamSessions);
        ArrayList<ExamSession> result = myUserProfile.getExamSessions();
        assertEquals(expectedExamSessions, result);
    }

    @Test
    public void setUserProfileReference() {
        UserProfile myUserProfile = new UserProfile();
        myUserProfile.setUserProfileReference(expectedUserProfileReference);
        DocumentReference result = myUserProfile.getUserProfileReference();
        assertEquals(expectedUserProfileReference, result);
    }

    /*
    The following getters are already tested in their setters.
    @Test
    public void getUserId() {
    }
    @Test
    public void getName() {
    }
    @Test
    public void getEmail() {
    }
    @Test
    public void getRole() {
    }
    @Test
    public void getExamSessions() {
    }
    @Test
    public void getUserProfileReference() {
    }
     */

    @Test
    public void getExamSessionIds() {
        UserProfile myUserProfile = new UserProfile();
        ArrayList<String> result = myUserProfile.getExamSessionIds();
        assertEquals(expectedExamSessionIds, result);
    }
}