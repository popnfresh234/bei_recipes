package com.dmtaiwan.alexander.beirecipes.util;

import android.util.Log;

/**
 * Created by Alexander on 10/26/2016.
 */

public class QuickLog {


    private String tag;

    public static QuickLog newInstance(String tag) {
        QuickLog quickLog = new QuickLog();
        quickLog.tag = tag;
        return quickLog;
    }

    public void i(String log) {
        Log.i(tag, log);
    }

    public void i(boolean bool) {
        Log.i(tag, String.valueOf(bool));
    }

    public void i(int i) {
        Log.i(tag, String.valueOf(i));
    }

    public void i(double d) {
        Log.i(tag, String.valueOf(d));
    }

    public void i(long l) {
        Log.i(tag, String.valueOf(l));
    }

    public void e(String log) {
        Log.e(tag, log);
    }

    public void e(boolean bool) {
        Log.e(tag, String.valueOf(bool));
    }

    public void e(int i) {
        Log.e(tag, String.valueOf(i));
    }

    public void e(double d) {
        Log.e(tag, String.valueOf(d));
    }

    public void e(long l) {
        Log.e(tag, String.valueOf(l));
    }

}
