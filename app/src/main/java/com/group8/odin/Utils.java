package com.group8.odin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-11-06
 * Description:
 */
public class Utils {
    public static Date getDate(long time) {
        return new Date(time*1000L);
    }
}
