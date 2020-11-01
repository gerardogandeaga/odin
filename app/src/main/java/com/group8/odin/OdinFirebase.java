package com.group8.odin;

import com.google.firebase.firestore.FirebaseFirestore;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description:
 */
public class OdinFirebase {
    // collection names
    public static class FirestoreCollections {
        public static final String USERS ="users";
        public static final String EXAM_SESSIONS ="exam_sessions";
        public static final String EXAMINEE_ACTIVITY_LOGS ="examinee_activity_logs";
    }

    public static class FirestoreUserProfile {
        public static final String NAME = "full_name";
        public static final String EMAIL = "email";
        public static final String ROLE = "role";
        public static final String EXAM_IDS = "exam_ids";
    }

    public static class FirestoreExamSession {
        public static final String TITLE = "title";
        public static final String EXAM_START_TIME = "exam_start_time";
        public static final String EXAM_END_TIME = "exam_end_time";
        public static final String AUTH_START_TIME = "auth_start_time";
        public static final String AUTH_END_TIME = "auth_end_time";
    }
}
