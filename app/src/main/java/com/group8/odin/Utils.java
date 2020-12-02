package com.group8.odin;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-06
 * Description: Utility functions
 * Updated by: Shreya Jain
 * Updated on: 2020-11-26
 */
public class Utils {
    public static Date getDate(long time) {
        return new Date(time*1000L);
    }
    public static String getTimeStringFromDate(Date date) { return new SimpleDateFormat("HH:mm:ss").format(date); }
    public static String getDateTimeStringFromDate(Date date) { return new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(date); }
    public static String getDateStringFromDate(Date date) { return new SimpleDateFormat("dd MMM yyyy").format(date); }

    public static int getExamineeStatusColour(Context context, boolean online) {
        // set icon colour tints
        if (online) {
            return ContextCompat.getColor(context, R.color.online);
        } else {
            return ContextCompat.getColor(context, R.color.offline);
        }
    }

    //Get current timestamp
    public static Date getCurrentTime(){
        long ts = Timestamp.now().getSeconds();
        return getDate(ts);
    }

    //Check if given timestamp is equal to current timestamp
    public static boolean isCurrentTimeEqualToTime(Date time) {
        return getCurrentTime().equals(time);
    }

    //Check if current timestamp is before given timestamp
    public static boolean isCurrentTimeBeforeTime(Date time){
        return getCurrentTime().before(time);
    }

    //Check if current timestamp is after given timestamp
    public static boolean isCurrentTimeAfterTime(Date time){

        return getCurrentTime().after(time);
    }

    public static boolean isCurrentTimeBetweenTimes(Date time1, Date time2) {
        return getCurrentTime().after(time1) && getCurrentTime().before(time2);
    }
}
