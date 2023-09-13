package com.swaarm.sdk.common;

import android.util.Log;

public class Logger {

    private static boolean isEnabled = false;

    public static void debug(String tag, String message) {
        if (!isEnabled) {
            return;
        }
        Log.d(tag, message);
    }

    public static void error(String tag, String message, Throwable e) {
        if (!isEnabled) {
            return;
        }
        Log.e(tag, message, e);
    }

    public static void error(String tag, String message) {
        if (!isEnabled) {
            return;
        }
        Log.e(tag, message);
    }

    public static void setIsEnabled(boolean isEnabled) {
        Logger.isEnabled = isEnabled;
    }
}
