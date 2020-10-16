package com.frankui.frankmonitor.util;

import android.util.Log;

import java.util.List;

public class LogUtil {

    public static double getOutputTime(long time) {
        return ((double) time) / 1000000;
    }

    public static void log(List<String> text) {
        if (text == null) {
            return;
        }
        for (int i = 0; i < text.size(); i++) {
            log(text.get(i));
        }
    }

    static void log(String log) {
        Log.e("lxis", "lxis:" + log);
    }
}
