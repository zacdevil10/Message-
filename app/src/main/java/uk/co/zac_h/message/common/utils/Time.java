package uk.co.zac_h.message.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Time {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String convertMessageDate(Long dateMilli) {
        if (dateMilli < 1000000000000L) {
            //Timestamp is in seconds
            dateMilli *= 1000;
        }

        long currentTime = System.currentTimeMillis();
        if (dateMilli > currentTime || dateMilli <= 0) {
            return null;
        }

        Date date = new Date(dateMilli);
        Date currentDate = new Date(currentTime);

        SimpleDateFormat lessThanOneDays = new SimpleDateFormat("", Locale.UK);
        SimpleDateFormat lessThanSevenDays = new SimpleDateFormat("EEE", Locale.UK);
        SimpleDateFormat moreThanSevenDays = new SimpleDateFormat("MMM dd", Locale.UK);

        //*
        final long diff = currentTime - dateMilli;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff/MINUTE_MILLIS + " mins ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff/HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 7);

            String formattedDate;
            if (calendar.getTime().compareTo(currentDate) < 0) {
                formattedDate = moreThanSevenDays.format(date);
            } else {
                formattedDate = lessThanSevenDays.format(date);
            }

            return formattedDate;
        }
        //*/

        /*

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 7);

        String formattedDate;
        if (calendar.getTime().compareTo(currentDate) < 0) {
            formattedDate = moreThanSevenDays.format(date);
        } else {
            formattedDate = lessThanSevenDays.format(date);
        }

        return formattedDate;
        //*/
    }

}
