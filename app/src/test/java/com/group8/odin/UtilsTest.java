package com.group8.odin;

import android.content.Context;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.group8.odin.proctor.activities.ProctorExamSessionActivity;
import com.group8.odin.proctor.activities.ProctorHomeActivity;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

//Created by Matthew Tong

public class UtilsTest {

    @Test
    public void getDateTest() {
        //expected Date
        Date expectedDate= new Date();
        Timestamp timestamp = new Timestamp(expectedDate);
        //convert the expected date into seconds then pass it into getDate()
        Date actualDate = Utils.getDate(timestamp.getSeconds());
        //convert the dates into strings
        String expected = expectedDate.toString();
        String actual = actualDate.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void getTimeStringFromDateTest() {
        //expected date = Wed Dec 31 16:00:00 PST 1969
        Date expectedDate = new Date(0);
        String expectedTimeString = "16:00:00";
        //result from getTimeStringFromDate()
        String resultTimeString = Utils.getTimeStringFromDate(expectedDate);
        assertEquals(expectedTimeString, resultTimeString);
    }

    @Test
    public void getDateTimeStringFromDateTest() {
        //expected date = Wed Dec 31 16:00:00 PST 1969
        Date expectedDate = new Date(0);
        String expectedTimeString = "31 Dec 1969 16:00:00";
        //result from getTimeStringFromDate()
        String resultTimeString = Utils.getDateTimeStringFromDate(expectedDate);
        assertEquals(expectedTimeString, resultTimeString);
    }
}