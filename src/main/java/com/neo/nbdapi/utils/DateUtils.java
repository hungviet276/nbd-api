package com.neo.nbdapi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author thanglv on 10/12/2020
 * @project NBD
 */
public class DateUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");

    public static final String DEFAULT_DATE_FORMAT = "dd/mm/yyyy";

    public static String convertDateToString(Date date) {
        if (date == null)
            return "";

        try {
            return dateFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static  String getDateAndTimeFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        return dateFormat.format(new Date());
    }

    public static boolean isValid(String value, String dateFormatString) {
        if (null == value) {
            return true;
        }
        if (value.length() != dateFormatString.length()) {
            return false;
        }
        try {

            int year = Integer.parseInt(value.substring(0, 4));
            int month = Integer.parseInt(value.substring(5, 7));
            int day = Integer.parseInt(value.substring(9, 11));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatString);
            Date date = simpleDateFormat.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
