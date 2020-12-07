package com.group8.odin;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bumptech.glide.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Utils Tester.
 *
 * @author <Shreya Jain>
 * @since <pre>Dec 4, 2020</pre>
 * @version 1.0
 */
@RunWith(AndroidJUnit4.class)
public class UtilsInstrumentedTest {
    private FirebaseAuth testAuth;
    private FirebaseFirestore testStore;

    /*
     * Method: getDate(long time)
     */
    @Test
    public void testGetDate() throws Exception {
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

    /**
     * Method: getTimeStringFromDate(Date date)
     */
    @Test
    public void testGetTimeStringFromDate() throws Exception {
        //expected date = Wed Dec 31 16:00:00 PST 1969
        Date expectedDate = new Date(0);
        String expectedTimeString = "16:00:00";
        //result from getTimeStringFromDate()
        String resultTimeString = Utils.getTimeStringFromDate(expectedDate);
        assertEquals(expectedTimeString, resultTimeString);
    }

    /**
     * Method: getDateTimeStringFromDate(Date date)
     */
    @Test
    public void testGetDateTimeStringFromDate() throws Exception {
        //expected date = Wed Dec 31 16:00:00 PST 1969
        Date expectedDate = new Date(0);
        String expectedTimeString = "31 Dec 1969 16:00:00";
        //result from getTimeStringFromDate()
        String resultTimeString = Utils.getDateTimeStringFromDate(expectedDate);
        assertEquals(expectedTimeString, resultTimeString);
    }

    /**
     * Method: getDateStringFromDate(Date date)
     */
    @Test
    public void testGetDateStringFromDate() throws Exception {
    //expected date = Wed Dec 31 16:00:00 PST 1969
        Date expectedDate = new Date(0);
        String expectedTimeString = "31 Dec 1969";
        //result from getTimeStringFromDate()
        String resultTimeString = Utils.getDateStringFromDate(expectedDate);
        assertEquals(expectedTimeString, resultTimeString);
    }

    /**
     * Method: getTimeNoSecondsStringFromDate(Date date)
     */
    @Test
    public void testGetTimeNoSecondsStringFromDate() throws Exception {
        //expected date = Wed Dec 31 16:00:00 PST 1969
        Date expectedDate = new Date(0);
        String expectedTimeString = "16:00";
        //result from getTimeStringFromDate()
        String resultTimeString = Utils.getTimeNoSecondsStringFromDate(expectedDate);
        assertEquals(expectedTimeString, resultTimeString);
    }
    /**
     * Method: getCurrentTime()
     */
    @Test
    public void testGetCurrentTime() throws Exception {
        int status = 0;
        Date date = Utils.getCurrentTime();
        String dateString = date.toString();
        if(dateString.isEmpty()){
            status = 0;
        } else {
            status = 1;
        }
        assertEquals(1, status);
    }

    /**
     * Method: isCurrentTimeEqualToTime(Date time)
     */
    @Test
    public void testIsCurrentTimeEqualToTime() throws Exception {
        //date = Wed Dec 31 16:00:00 PST 1969
        Date date = new Date(0);
        boolean result = Utils.isCurrentTimeEqualToTime(date);
        assertFalse(result);
    }

    /**
     * Method: isCurrentTimeBeforeTime(Date time)
     */
    @Test
    public void testIsCurrentTimeBeforeTime() throws Exception {
        //date = Wed Dec 31 16:00:00 PST 1969
        Date date = new Date(0);
        boolean result = Utils.isCurrentTimeBeforeTime(date);
        assertFalse(result);
    }

    /**
     * Method: isCurrentTimeAfterTime(Date time)
     */
    @Test
    public void testIsCurrentTimeAfterTime() throws Exception {
        //date = Wed Dec 31 16:00:00 PST 1969
        Date date = new Date(0);
        boolean result = Utils.isCurrentTimeAfterTime(date);
        assertTrue(result);
    }

    /**
     * Method: isCurrentTimeBetweenTimes(Date time1, Date time2)
     */
    @Test
    public void testIsCurrentTimeBetweenTimes() throws Exception {
        //date1 = Wed Dec 31 16:00:00 PST 1969
        Date date1 = new Date(0);
        Date date2 = new Date(100);
        boolean result = Utils.isCurrentTimeBetweenTimes(date1, date2);
        assertFalse(result);
    }

    /**
     * Method: getExamineeStatusColour(Context context, boolean online)
     */
    @Test
    public void testGetExamineeStatusColour() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        boolean status = true; //examinee is active.
        int expectedResult = -12345273; //Value of color the green tint is -12345273 in int data format.
        int actualResult = Utils.getExamineeStatusColour(appContext, status);
        assertEquals(expectedResult, actualResult);
    }
}
