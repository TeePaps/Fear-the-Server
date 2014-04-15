package com.teepaps.fts.utils;

import java.text.SimpleDateFormat;

/**
 * Created by ted on 4/11/14.
 */
public class ConversionUtils {

    /**
     * Converts "time since epoch" to a string representation
     * @param epochTime
     * @return
     */
    public static String milliToString (long epochTime) throws IllegalArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
        return sdf.format(epochTime);
    }

    /**
     * Convert an integer to a boolean
     * @param value
     * @return
     */
    public static boolean intToBoolean(int value) {
        return (value > 0) ? true : false;
    }
}
