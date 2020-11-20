package com.group8.odin;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-06
 * Description: Utility function to convert actual time to the formatted time expected.
 */
public class Utils {
    public static Date getDate(long time) {
        return new Date(time*1000L);
    }

    public static String getTimeStringFromDate(Date date) { return new SimpleDateFormat("HH:mm:ss").format(date); }
    public static String getDateTimeStringFromDate(Date date) { return new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(date); }

    public static int getExamineeStatusColour(Context context, boolean online) {
        // set icon colour tints
        if (online) {
            return ContextCompat.getColor(context, R.color.online);
        } else {
            return ContextCompat.getColor(context, R.color.offline);
        }
    }

}
