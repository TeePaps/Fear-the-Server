package com.teepaps.fts.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ted on 4/12/14.
 */
public class PrefsUtils {

    private static final String DEFAULT_FILE = "fts";

    public static final String KEY_MAC = "MAC";

    /**
     * Gets a string stored in the default shared preferences file
     * @param context
     * @param key
     * @param defaultVal
     * @return
     */
    public static String getString(Context context, String key, String defaultVal) {
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_FILE, Context.MODE_PRIVATE);
        return prefs.getString(key, defaultVal);
    }

    /**
     * Stores a string in the default shared preferences file
     * @param context
     * @param key
     * @return
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context
                .getSharedPreferences(DEFAULT_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }
}
