package com.financialcalculator.utility;

import android.support.annotation.NonNull;

import com.financialcalculator.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

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

    public static String getNumberFormatted(String number) {

        String s = "";
        int j = 1;
        for (int i = number.length() - 1; i >= 0; i--) {

            char c = number.charAt(i);
            s = c + s;
            if (j % 3 == 0 && i != 0) {
                s = "," + s;
            }
            j++;
        }
        return s;
    }

    public static int getRandomBackground() {
        Random rand = new Random();
        int n = rand.nextInt(2);
        switch (n) {
            case 0:
                return R.drawable.circle_green;
            case 1:
                return R.drawable.circle_blue;
            case 2:
                return R.drawable.circle_red;
            default:
                return R.drawable.circle_green;
        }
    }

    public static int getFixedBackground(int position) {
        Random rand = new Random();
        int n = rand.nextInt(2);
        switch (position % 3) {
            case 0:
                return R.drawable.circle_green;
            case 1:
                return R.drawable.circle_blue;
            case 2:
                return R.drawable.circle_red;
            default:
                return R.drawable.circle_green;
        }
    }

    public static String getFormattedDouble(double d) {
        return new DecimalFormat("#").format(d);
    }

    public static String getCommaSeparated(String number) {
        String result = "";
        try {
            if (number.contains(".")) {
                result = number.length() > 12 ? bigNumberWithDecimal(new BigDecimal(number))
                        : numberWithDecimal(Double.parseDouble(number));
            } else {
                result = number.length() > 12 ? bigNumWithoutDecimal(new BigDecimal(number))
                        : numWithoutDecimal(Double.parseDouble(number));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String numberWithDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(price);
    }

    public static String numWithoutDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return formatter.format(price);
    }

    public static String bigNumberWithDecimal(BigDecimal price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(price);
    }

    public static String bigNumWithoutDecimal(BigDecimal price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return formatter.format(price);
    }

    public static String removeComma(String number) {
        return number != null ? number.replace(",", "") : "";
    }
}
