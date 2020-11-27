package com.group8.odin.common.models;

import android.util.Pair;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.group8.odin.OdinFirebase;
import com.group8.odin.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 17/11/20
 * Description: Object representation of activity logs
 */
public class ActivityLog {
    private ArrayList<Timestamp> activity;
    private boolean status;

    public ActivityLog(DocumentSnapshot activityLog) {
        activity = new ArrayList<>();
        update(activityLog);
    }

    public void update(DocumentSnapshot activityLog) {
        activity = (ArrayList<Timestamp>) activityLog.get(OdinFirebase.FirestoreActivityLog.ACTIVITY);
        if (activity != null) {
            status = activity.size() % 2 == 1;
        }
    }

    public boolean isValid() {
        return activity != null;
    }

    public boolean getStatus() { return status; }

    @Override
    public String toString() {
        // set timestamps
        int state = 0;
        StringBuilder log = new StringBuilder();
        for (Timestamp timestamp : activity) {
            String time = Utils.getTimeStringFromDate(Utils.getDate(timestamp.getSeconds()));
            // first entry is implicitly a login
            if (state == 0)
                log.append(MessageFormat.format("({0}) Examinee logged in\n", time));
                // odd entries are app sent to background
            else if (state % 2 == 1)
                log.append(MessageFormat.format("({0}) Examinee sent app to background\n", time));
                // even entries are app sent to foreground
            else if (state % 2 == 0)
                log.append(MessageFormat.format("({0}) Examinee re-entered the app\n", time));

            state++;
        }

        return log.toString().trim();
    }

    // custom activity log comparator
    public static class Comparison implements Comparator<Pair<UserProfile, ActivityLog>> {

        @Override
        public int compare(Pair<UserProfile, ActivityLog> a, Pair<UserProfile, ActivityLog> b) {
            int comp = Boolean.compare(a.second.getStatus(), b.second.getStatus());
            // if they have the same status value then compare by name
            if (comp == 0) {
                return a.first.getName().compareTo(b.first.getName());
            }
            // if they are different status values then compare by status
            else {
                return comp;
            }
        }
    }
}
