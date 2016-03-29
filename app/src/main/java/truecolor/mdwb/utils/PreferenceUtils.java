/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package truecolor.mdwb.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtils {
//    private static final String TAG = PreferenceUtils.class.getName();
//    private static final boolean DEBUG = false;

//    private static final String PREFERENCES_NAME = "com.qianxun.kankan_preferences";

    private static Context sApplication;

    private static String sName;
    private static SharedPreferences getPreferences(Context context) {
        if(context == null) {
            throw new RuntimeException("Application Context not set! Call PreferenceUtils.init when application onCreate");
        }
        if(sName == null) {
            sName = context.getPackageName() + "_preferences";
        }
        return context.getSharedPreferences(sName, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getPreferences(Context context, String name) {
        if(context == null) {
            throw new RuntimeException("Application Context not set! Call PreferenceUtils.init when application onCreate");
        }
        if(name == null) {
            throw new IllegalArgumentException("PreferenceUtils need not empty shared preference name!");
        }
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void setPreferencesName(String name) {
        if(sName != null && sName.equals(name)) return;
        sName = name;
    }

    public static void setApplication(Application application) {
        sApplication = application;
    }

    /*
     *
     */
    public static boolean getBooleanPref(Context context, String name, boolean def) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getBoolean(name, def);
    }

    public static void setBooleanPref(Context context, String name, boolean value) {
        SharedPreferences prefs = getPreferences(context);
        Editor ed = prefs.edit();
        ed.putBoolean(name, value);
        ed.commit();
    }

    public static boolean getBooleanPref(String name, boolean def) {
        SharedPreferences prefs = getPreferences(sApplication);
        return prefs.getBoolean(name, def);
    }

    public static void setBooleanPref(String name, boolean value) {
        SharedPreferences prefs = getPreferences(sApplication);
        Editor ed = prefs.edit();
        ed.putBoolean(name, value);
        ed.commit();
    }

    public static boolean getBooleanPref(String pref, String name, boolean def) {
        SharedPreferences prefs = getPreferences(sApplication, pref);
        return prefs.getBoolean(name, def);
    }

    public static void setBooleanPref(String pref, String name, boolean value) {
        SharedPreferences prefs = getPreferences(sApplication, pref);
        Editor ed = prefs.edit();
        ed.putBoolean(name, value);
        ed.commit();
    }

    /*
     *
     */
    public static int getIntPref(Context context, String name, int def) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getInt(name, def);
    }

    public static void setIntPref(Context context, String name, int value) {
        SharedPreferences prefs = getPreferences(context);
        Editor ed = prefs.edit();
        ed.putInt(name, value);
        ed.commit();
    }

    public static int getIntPref(String name, int def) {
        SharedPreferences prefs = getPreferences(sApplication);
        return prefs.getInt(name, def);
    }

    public static void setIntPref(String name, int value) {
        SharedPreferences prefs = getPreferences(sApplication);
        Editor ed = prefs.edit();
        ed.putInt(name, value);
        ed.commit();
    }

    public static int getIntPref(String pref, String name, int def) {
        SharedPreferences prefs = getPreferences(sApplication, pref);
        return prefs.getInt(name, def);
    }

    public static void setIntPref(String pref, String name, int value) {
        SharedPreferences prefs = getPreferences(sApplication, pref);
        Editor ed = prefs.edit();
        ed.putInt(name, value);
        ed.commit();
    }

    /*
     *
     */
    public static long getLongPref(Context context, String name, long def) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getLong(name, def);
    }

    public static void setLongPref(Context context, String name, long value) {
        SharedPreferences prefs = getPreferences(context);
        Editor ed = prefs.edit();
        ed.putLong(name, value);
        ed.commit();
    }

    public static long getLongPref(String name, long def) {
        SharedPreferences prefs = getPreferences(sApplication);
        return prefs.getLong(name, def);
    }

    public static void setLongPref(String name, long value) {
        SharedPreferences prefs = getPreferences(sApplication);
        Editor ed = prefs.edit();
        ed.putLong(name, value);
        ed.commit();
    }

    public static long getLongPref(String pref, String name, long def) {
        SharedPreferences prefs = getPreferences(sApplication, pref);
        return prefs.getLong(name, def);
    }

    public static void setLongPref(String pref, String name, long value) {
        SharedPreferences prefs = getPreferences(sApplication, pref);
        Editor ed = prefs.edit();
        ed.putLong(name, value);
        ed.commit();
    }

    /*
     *
     */
    public static float getFloatPref(Context context, String name, float def) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getFloat(name, def);
    }

    public static void setFloatPref(Context context, String name, float value) {
        SharedPreferences prefs = getPreferences(context);
        Editor ed = prefs.edit();
        ed.putFloat(name, value);
        ed.commit();
    }

    public static float getFloatPref(String name, float def) {
        SharedPreferences prefs = getPreferences(sApplication);
        return prefs.getFloat(name, def);
    }

    public static void setFloatPref(String name, float value) {
        SharedPreferences prefs = getPreferences(sApplication);
        Editor ed = prefs.edit();
        ed.putFloat(name, value);
        ed.commit();
    }

    public static float getFloatPref(String pref, String name, float def) {
        SharedPreferences prefs = getPreferences(sApplication, pref);
        return prefs.getFloat(name, def);
    }

    public static void setFloatPref(String pref, String name, float value) {
        SharedPreferences prefs = getPreferences(sApplication, pref);
        Editor ed = prefs.edit();
        ed.putFloat(name, value);
        ed.commit();
    }

    /*
     *
     */
    public static String getStringPref(Context context, String name, String def) {
        SharedPreferences prefs = getPreferences(context);
        String result = prefs.getString(name, def);
        if("null".equals(result)) result = null;
        return result;
    }

    public static void setStringPref(Context context, String name, String value) {
        SharedPreferences prefs = getPreferences(context);
        Editor ed = prefs.edit();
        if(value == null) value = "null";
        ed.putString(name, value);
        ed.commit();
    }

    public static String getStringPref(String name, String def) {
        SharedPreferences prefs = getPreferences(sApplication);
        String result = prefs.getString(name, def);
        if("null".equals(result)) result = null;
        return result;
    }

    public static void setStringPref(String name, String value) {
        SharedPreferences prefs = getPreferences(sApplication);
        Editor ed = prefs.edit();
        if(value == null) value = "null";
        ed.putString(name, value);
        ed.commit();
    }

    /*
     *
     */
    public static String getStringPref(String pref, String name, String def) {
        SharedPreferences prefs = getPreferences(sApplication, pref);
        String result = prefs.getString(name, def);
        if("null".equals(result)) result = null;
        return result;
    }

    public static void setStringPref(String pref, String name, String value) {
        SharedPreferences prefs = getPreferences(sApplication, pref);
        Editor ed = prefs.edit();
        if(value == null) value = "null";
        ed.putString(name, value);
        ed.commit();
    }
}
