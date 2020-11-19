package com.neo.nbdapi.utils;

import java.util.List;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */
public class CsvUtils {

    public static String writeToCsvText(List<Object[]> input, String header) {
        StringBuffer dataRes = new StringBuffer();
        dataRes.append(header);
        if (input.size() > 0)
            dataRes.append("\n");
        input.parallelStream().forEach(rowItem -> {
            StringBuilder resRow = new StringBuilder();
            for (int i = 0; i < rowItem.length; i++) {
                if (i == 0) {
                    if (rowItem[i] == null) {
                        resRow.append("");
                    } else {
                        if (String.valueOf(rowItem[i]).indexOf(",") > -1) {
                            resRow.append("\"").append(rowItem[i]).append("\"");
                        } else {
                            resRow.append(rowItem[i]);
                        }
                    }
                } else if (i != rowItem.length - 1) {
                    if (rowItem[i] == null) {
                        resRow.append(",").append("");
                    } else {
                        if (String.valueOf(rowItem[i]).indexOf(",") > -1) {
                            resRow.append(",").append("\"").append(rowItem[i]).append("\"");
                        } else {
                            resRow.append(",").append(rowItem[i]);
                        }
                    }
                } else if (i == rowItem.length - 1) {
                    if (rowItem[i] == null) {
                        resRow.append(",").append("").append("\n");
                    } else {
                        if (String.valueOf(rowItem[i]).indexOf(",") > -1) {
                            resRow.append(",").append("\"").append(rowItem[i]).append("\"").append("\n");
                        } else {
                            resRow.append(",").append(rowItem[i]).append("\n");
                        }
                    }
                }
            }
            dataRes.append(resRow.toString());
        });
        return dataRes.toString();
    }
}
