package com.zriton.pigeon.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by aditya on 24/10/16.
 */

public class DateUtils {

    public static String changeFormat(String date,String oldFormat,String newFormat) {
        DateFormat originalFormat = new SimpleDateFormat(oldFormat, Locale.US);
        DateFormat targetFormat = new SimpleDateFormat(newFormat, Locale.US);
        Date newdate = null;
        try {
            newdate = originalFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return targetFormat.format(newdate);
    }

    public static String changeFormat(Long milliseconds,String newFormat) {
        Calendar lCalendar = Calendar.getInstance();
        lCalendar.setTimeInMillis(milliseconds);
        DateFormat targetFormat = new SimpleDateFormat(newFormat, Locale.US);
        return targetFormat.format(lCalendar.getTime());
    }

    public static String getCurrentDate()
    {
        Calendar lCalendar = Calendar.getInstance();
        DateFormat lDateFormat = new SimpleDateFormat("dd MMM", Locale.US);
        return lDateFormat.format(lCalendar.getTime());
    }

    public static long getCurrentTimeStamp()
    {
        Calendar lCalendar = Calendar.getInstance();
        return lCalendar.getTimeInMillis();
    }
}
