package com.group8.odin;

import com.google.firebase.Timestamp;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void getDate() {
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
    public void getTimeStringFromDate() {
        //expected date = Wed Dec 31 16:00:00 PST 1969
        Date expectedDate = new Date(0);
        String expectedTimeString = "16:00:00";
        //result from getTimeStringFromDate()
        String resultTimeString = Utils.getTimeStringFromDate(expectedDate);
        assertEquals(expectedTimeString, resultTimeString);
    }

    @Test
    public void getDateTimeStringFromDate() {
        //expected date = Wed Dec 31 16:00:00 PST 1969
        Date expectedDate = new Date(0);
        String expectedTimeString = "31 Dec 1969 16:00:00";
        //result from getTimeStringFromDate()
        String resultTimeString = Utils.getDateTimeStringFromDate(expectedDate);
        assertEquals(expectedTimeString, resultTimeString);
    }

    @Test
    public void getExamineeStatusColour() {

    } //todo: complete this
}