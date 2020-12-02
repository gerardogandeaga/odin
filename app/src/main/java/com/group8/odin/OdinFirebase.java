package com.group8.odin;

import com.group8.odin.common.models.ExamSession;
import com.group8.odin.common.models.UserProfile;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description: Driver class for Firebase connections
 * Updated by: Matthew Tong
 * Updated on 2020-12-01
 * Description: removed exam_end_time and auth_end_time
 * added exam duration and auth duration
 */
public class OdinFirebase {
    // Consistent references throughout the application
    public static ExamSession ExamSessionContext; // Exam session user is currently engaged in
    public static UserProfile UserProfileContext; // User profile singleton that exists through the app life cycle

    // collection names
    public static class FirestoreCollections {
        public static final String USERS ="users";
        public static final String EXAM_SESSIONS ="exam_sessions";
        public static final String ACTIVITY_LOGS ="activity_logs";
    }

    // Field names
    public static class FirestoreUserProfile {
        public static final String NAME = "full_name";
        public static final String EMAIL = "email";
        public static final String ROLE = "role";
        public static final String EXAM_IDS = "exam_ids";
        public static final String EDU_ID = "edu_id";
    }

    public static class FirestoreExamSession {
        public static final String TITLE = "title";
        public static final String EXAM_START_TIME = "exam_start_time";
        public static final String EXAM_END_TIME = "exam_end_time";
        public static final String AUTH_DURATION = "auth_duration";
    }

    public static class FirestoreActivityLog {
        public static final String ACTIVITY = "activity";
    }
}
