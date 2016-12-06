package com.weimc.history.utils;

import android.util.Log;

/**
 * Created by weiqun on 2016/12/6 0006.
 */

public class LogUtil {
    public static final int level = 6;

    public static void e(String tag, String msg) {
        if (level > 5) {
            Log.e(tag, msg);
        }
    }
}
