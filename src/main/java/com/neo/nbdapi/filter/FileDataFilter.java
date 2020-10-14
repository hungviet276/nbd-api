package com.neo.nbdapi.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * @author thanglv on 10/14/2020
 * @project NBD
 */
public class FileDataFilter implements FilenameFilter {

    private int type = 0;
    private String pattern = "";
    private Pattern p = null;

    /**
     * constructor
     * @param pattern
     */
    public FileDataFilter(String pattern) {
        this.pattern = pattern.replace("*", "");
        boolean isStartWith = pattern.startsWith("*");
        boolean isEndWith = pattern.endsWith("*");

        // middle
        if (isStartWith && isEndWith) {
            type = 1;
            //right
        } else if (isStartWith) {
            type = 2;
            //left
        } else if (isEndWith) {
            type = 3;
        }else {
            p = Pattern.compile(pattern);
        }
    }

    @Override
    public boolean accept(File dir, String name) {
        switch (type) {
            case 1:
                return name.indexOf(pattern) > -1;
            case 2:
                return name.endsWith(pattern);
            case 3:
                return name.startsWith(pattern);
            default:
                return p.matcher(name).find();
        }
    }
}
