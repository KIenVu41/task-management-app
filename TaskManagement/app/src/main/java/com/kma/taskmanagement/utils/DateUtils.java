package com.kma.taskmanagement.utils;

import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static String findPreviousDate(int type) {
        Calendar cal = Calendar.getInstance();
        if(type == 0) {
            cal.add(Calendar.DATE, 0);
        } else if(type == 1) {
            cal.add(Calendar.DATE, -3);
        } else {
            cal.add(Calendar.DATE, -7);
        }

        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        return s.format(new Date(cal.getTimeInMillis()));
    }

    public static String convert(String dd, String mm, String yyyy) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter output = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String value = output.format(LocalDate.of(Integer.valueOf(yyyy),Integer.valueOf(mm), Integer.valueOf(dd)));
            return value;
        }
        return  "";
    }

    public static String convertTime(String hour, String min) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalTime.of(Integer.valueOf(hour), Integer.valueOf(min)).format(
                    // using a desired pattern
                    DateTimeFormatter.ofPattern("HH:mm")
            );
        }
        return "";
    }

    public static String getLocalDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(calendar.getTime());
    }

    public static String findDateByType(int type) {
        Calendar cal = Calendar.getInstance();
        if(type == 0) {
            cal.add(Calendar.DATE, 0);
        } else if(type == 1) {
            cal.add(Calendar.DATE, -3);
        } else {
            cal.add(Calendar.DATE, -7);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(cal.getTimeInMillis());
    }
}
