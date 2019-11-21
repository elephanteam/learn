package com.limit.learn.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * Record application error log when used
     */
    public static final String getSimpleDate() {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        currentDate = Calendar.getInstance().getTime();
        return formatter.format(currentDate);
    }
}
