package com.group8.odin.common.models;

import android.util.Pair;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.group8.odin.OdinFirebase;
import com.group8.odin.Utils;
import com.group8.odin.proctor.fragments.ProctorLiveMonitoringFragment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 17/11/20
 * Description:
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
        status = activity.size() % 2 == 1;
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
                log.append(MessageFormat.format("({0}) {1} logged in\n", time, "name will go here"));
                // odd entries are app sent to background
            else if (state % 2 == 1)
                log.append(MessageFormat.format("({0}) {1} sent app to background\n", time, "name will go here"));
                // even entries are app sent to foreground
            else if (state % 2 == 0)
                log.append(MessageFormat.format("({0}) {1} re-entered the app\n", time, "name will go here"));

            state++;
        }

        return log.toString();
    }

    // custom activity log comparator
    public static class Comparison implements Comparator<Pair<UserProfile, ActivityLog>> {

        @Override
        public int compare(Pair<UserProfile, ActivityLog> a, Pair<UserProfile, ActivityLog> b) {
            return a.first.getName().compareTo(b.first.getName());
        }
    }
}
