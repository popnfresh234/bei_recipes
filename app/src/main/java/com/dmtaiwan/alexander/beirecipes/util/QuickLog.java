package com.dmtaiwan.alexander.beirecipes.util;

import android.util.Log;

/**
 * Created by Alexander on 10/26/2016.
 */

public class QuickLog {


    private static String tag ="BeiRecipe";

    public static QuickLog newInstance(String tag) {
        QuickLog quickLog = new QuickLog();
        quickLog.tag = tag;
        return quickLog;
    }

    public static void i(String log) {
        Log.i(tag, log);
    }

    public static void i(boolean bool) {
        Log.i(tag, String.valueOf(bool));
    }

    public static void i(int i) {
        Log.i(tag, String.valueOf(i));
    }

    public static void i(double d) {
        Log.i(tag, String.valueOf(d));
    }

    public static void i(long l) {
        Log.i(tag, String.valueOf(l));
    }

    public static void e(String log) {
        Log.e(tag, log);
    }

    public static void e(boolean bool) {
        Log.e(tag, String.valueOf(bool));
    }

    public static void e(int i) {
        Log.e(tag, String.valueOf(i));
    }

    public static void e(double d) {
        Log.e(tag, String.valueOf(d));
    }

    public static void e(long l) {
        Log.e(tag, String.valueOf(l));
    }

}
