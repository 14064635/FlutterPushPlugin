package com.vivo.push.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static final String TAG = "PushDemoActivity";

    public static String logStringCache = "";

    public static void updateContent(String content) {
        Log.d(TAG, "updateContent");
        String logText = "";

        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss", Locale.CHINA);
        logText += sDateFormat.format(new Date()) + ": ";
        logText += content;

        Log.i(TAG, "updateContent : " + logText);

        if (!TextUtils.isEmpty(Utils.logStringCache)) {
            Utils.logStringCache = Utils.logStringCache + "\n";
        }

        Utils.logStringCache = logText + "\n" + Utils.logStringCache;
        String[] logSp = Utils.logStringCache.split("\n");

        if (logSp.length > 50) {
            Utils.logStringCache = Utils.logStringCache.substring(0,
                    Utils.logStringCache.indexOf(logSp[logSp.length - 1]));
        }

        if (MainActivity.sMainActivity != null) {
            MainActivity.sMainActivity.updateDisplay();
        }
    }

    public static void setLogText(Context context, String text) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("log_text", text);
        editor.commit();
    }


}
