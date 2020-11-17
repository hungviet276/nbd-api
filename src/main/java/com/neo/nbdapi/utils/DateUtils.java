package com.neo.nbdapi.utils;

import com.neo.nbdapi.exception.BusinessException;

import java.nio.Buffer;
import java.sql.Timestamp;
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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatString);
            simpleDateFormat.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static Date getDateFromStringFormat(String date, String format) throws BusinessException {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            throw new BusinessException("Ngày tháng không hợp lệ");
        }
    }

    public static String getStringFromDateFormat(Date date, String format) {
        if (date == null)
            return null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            return null;
        }
    }
}
