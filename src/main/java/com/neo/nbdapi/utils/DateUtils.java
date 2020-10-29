package com.neo.nbdapi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author thanglv on 10/12/2020
 * @project NBD
 */
public class DateUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");

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
}
