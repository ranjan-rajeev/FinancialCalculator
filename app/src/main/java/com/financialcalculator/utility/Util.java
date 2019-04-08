package com.financialcalculator.utility;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Handler;

/**
 * Created by Rajeev Ranjan -  ABPB on 05-04-2019.
 */
public class Util {


    public static long getLongDate(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    public static String getDatefromLong(long timeStamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        final Date date = calendar.getTime();
        return simpleDateFormat.format(date);
    }

    public static int getYearMonthDate(long timeStamp, int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return calendar.get(type);
    }

    public static int getYearMonthDate(String dateString, int type) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = simpleDateFormat.parse(dateString);
            calendar.setTime(date);
            return getYearMonthDate(calendar.getTimeInMillis(), type);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getYearMonthDate(calendar.getTimeInMillis(), type);
    }

}
