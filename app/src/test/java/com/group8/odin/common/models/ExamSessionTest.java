package com.group8.odin.common.models;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

//Created by Matthew Tong
//Created on 2020-11-22
//Description: Unit tests for the ExamSession Class
public class ExamSessionTest {
    //expected data
    private static final String expectedExamId = "expected exam id";
    private static final String expectedExamTitle = "expected exam title";
    private static final Date expectedAuthStartTime = null;
    private static final Date expectedAuthEndTime = null;
    private static final Date expectedExamStartTime = new Date(2);
    private static final Date expectedExamEndTime = new Date (3);

    @Test
    public void setExamId() {
        ExamSession myExamSession = new ExamSession();
        myExamSession.setExamId(expectedExamId);
        String result = myExamSession.getExamId();
        assertEquals(expectedExamId,result);
    }

    @Test
    public void setTitle() {
        ExamSession myExamSession = new ExamSession();
        myExamSession.setTitle(expectedExamTitle);
        String result = myExamSession.getTitle();
        assertEquals(expectedExamTitle,result);
    }

    @Test
    public void setExamStartTime() {
        ExamSession myExamSession = new ExamSession();
        myExamSession.setExamStartTime(expectedExamStartTime);
        Date result = myExamSession.getExamStartTime();
        assertEquals(expectedExamStartTime,result);
    }

    @Test
    public void setExamEndTime() {
        ExamSession myExamSession = new ExamSession();
        myExamSession.setExamEndTime(expectedExamEndTime);
        Date result = myExamSession.getExamEndTime();
        assertEquals(expectedExamEndTime,result);
    }

    /*
    The following getters are already tested in their setters.
    @Test
    public void getExamId() {
    }

    @Test
    public void getTitle() {
    }

    @Test
    public void getExamStartTime() {
    }

    @Test
    public void getExamEndTime() {
    }*/

    @Test
    public void getAuthStartTime() {
        ExamSession myExamSession = new ExamSession();
        Date result = myExamSession.getAuthStartTime();
        assertEquals(expectedAuthStartTime, result);
    }

    @Test
    public void getAuthEndTime() {
        ExamSession myExamSession = new ExamSession();
        Date result = myExamSession.getAuthEndTime();
        assertEquals(expectedAuthEndTime, result);
    }
}