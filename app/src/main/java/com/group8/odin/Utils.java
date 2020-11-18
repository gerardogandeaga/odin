package com.group8.odin;

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
}
