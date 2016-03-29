package truecolor.imageloader;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

/**
 * Created by xiaowu on 15/7/15.
 */
public class Configure {
    /*
    * http headers
    */
    private static final String CLIENT_BRAND        = "Client-Brand";
    private static final String CLIENT_DEVICE       = "Client-Device";
    //    private static final String CLIENT_CPU           = "Client-Cpu";
    private static final String CLIENT_OS           = "Client-Os";
    private static final String CLIENT_RESOLUTION   = "Client-Resolution";
    private static final String CLIENT_UID          = "Client-Uid";
    //    private static final String CLIENT_SID          = "Client-Sid";
//    private static final String CLIENT_EID          = "Client-Eid";
    private static final String CLIENT_ANDROID_ID   = "Client-AndroidId";
    private static final String CLIENT_VERSION      = "Client-Version";
    private static final String CLIENT_SOURCE       = "Client-Source";
    //    private static final String CLIENT_COUNTRY      = "Client-Country";
//    private static final String CLIENT_LANGUAGE     = "Client-Language";
    private static final String CLIENT_SIM          = "Client-Sim";
    private static final String CLIENT_PACKAGE      = "Client-Package";

    /*
     *
     */
    public static String uid;
    public static String site;
    //    public static String sid;
//    public static String eid;
    public static String aid;

    public static String android_id;

    public static String cpu_arch;
    public static String os;
    public static String resolution;
    public static String sim;
//    public static String country;
//    public static String language;

    public static String package_name;
    public static String version;

    public static boolean isNetConnected;
    public static boolean isWifiConnected;
    //    public static boolean is3GConnected;
    public static String network_name;

    public static boolean hasExternalStorage;
    public static String sExternalStoragePath;
    public static String sInternalStoragePath;
    public static String sExternalSdcardPath;

    /*
     *
     */
    public static void init(Context context) {
        init(context, null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init(final Context context, String cacheName) {
        /*
         *
         */
//        sCachePath = Utils.getCachePath();
//        String state = Environment.getExternalStorageState();
//        if(Environment.MEDIA_MOUNTED.equals(state)) {
//            hasExternalStorage = true;
//            sExternalStoragePath = Utils.getKankanPath();
//        } else {
//            hasExternalStorage = false;
//        }

//        uid = Utils.getUid(context);
//        site = Utils.getSite(context);
////        sid = SecurityUtils.encodeString(Utils.getImsi(context));
////        eid = SecurityUtils.encodeString(Utils.getImei(context));
//        new Thread(new Runnable() {
//            public void run() {
//                aid = Utils.getAid(context);
//                if(aid != null) {
//                    HttpConnectUtils.addDefaultQuery("_aid", aid);
//                }
//            }
//        }).start();
//
//        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//
//        cpu_arch = System.getProperty("os.arch");
//        os = Utils.getOs();
//        resolution = Utils.getResolution(context);
//        sim = Utils.getSimOperator(context);
////        country = Locale.getDefault().getCountry();
////        language = Locale.getDefault().getLanguage();
//
//        package_name = Utils.getPackageName(context);
//        version = Utils.getVersion(context);
//
//        isNetConnected = Utils.checkIfNetConnected(context);
//        isWifiConnected = Utils.checkIfWifiConnected(context);
////        is3GConnected = Utils.checkIf3GConnected(context);
//        network_name = Utils.getNetwork(context);

        if(TextUtils.isEmpty(cacheName)) cacheName = "qianxun";
        sExternalStoragePath = Environment.getExternalStorageDirectory().getPath() + "/" + cacheName + "/";
        sInternalStoragePath = context.getCacheDir().getAbsolutePath() + "/";
//        String extPath = Utils.getExtSdPath();
//        if(extPath != null) {
//            sExternalSdcardPath = extPath + "/Android/data/" + package_name + "/";
//            File file = new File(sExternalSdcardPath);
//            if(!file.exists() || !file.isDirectory()) {
//                context.getExternalCacheDir();
//            }
//        }
//        String state = Environment.getExternalStorageState();
//        if(Environment.MEDIA_MOUNTED.equals(state)) {
//            hasExternalStorage = true;
//            File dir = new File(sExternalStoragePath);
//            if (!dir.exists()) dir.mkdirs();
//        } else {
//            hasExternalStorage = false;
//        }
//
////        _aid: 安卓设备的广告id (仅安卓适用)
//        // set http headers
//        HttpConnectUtils.addDefaultHeader(CLIENT_BRAND, android.os.Build.MANUFACTURER);
//        HttpConnectUtils.addDefaultQuery("_brand", android.os.Build.MANUFACTURER);
//        HttpConnectUtils.addDefaultHeader(CLIENT_DEVICE, android.os.Build.MODEL);
//        HttpConnectUtils.addDefaultQuery("_model", android.os.Build.MODEL);
//        HttpConnectUtils.addDefaultHeader(CLIENT_OS, os);
//        HttpConnectUtils.addDefaultQuery("_ov", os);
////        HttpConnectUtils.addDefaultHeader(CLIENT_CPU, cpu_arch);
//        HttpConnectUtils.addDefaultQuery("_cpu", cpu_arch);
//        HttpConnectUtils.addDefaultHeader(CLIENT_RESOLUTION, resolution);
//        HttpConnectUtils.addDefaultQuery("_resolution", resolution);
//        HttpConnectUtils.addDefaultHeader(CLIENT_PACKAGE, package_name);
//        HttpConnectUtils.addDefaultQuery("_package", package_name);
//        HttpConnectUtils.addDefaultHeader(CLIENT_VERSION, version);
//        HttpConnectUtils.addDefaultQuery("_v", version);
//        HttpConnectUtils.addDefaultHeader(CLIENT_SOURCE, site);
//        HttpConnectUtils.addDefaultQuery("_channel", site);
////        HttpConnectUtils.addDefaultHeader(CLIENT_IMEI, imei);
////        HttpConnectUtils.addDefaultHeader(CLIENT_MAC, mac);
//        if(!TextUtils.isEmpty(sim)) {
//            HttpConnectUtils.addDefaultHeader(CLIENT_SIM, sim);
//            HttpConnectUtils.addDefaultQuery("_carrier", sim);
//        }
////        HttpConnectUtils.addDefaultHeader(CLIENT_COUNTRY, country);
////        HttpConnectUtils.addDefaultQuery("_country", country);
////        HttpConnectUtils.addDefaultHeader(CLIENT_LANGUAGE, language);
////        HttpConnectUtils.addDefaultQuery("_locale", language);
//        HttpConnectUtils.addDefaultHeader(CLIENT_UID, uid);
//        HttpConnectUtils.addDefaultQuery("_udid", uid);
////        if(!TextUtils.isEmpty(access_token)) {
////            HttpConnectUtils.addDefaultHeader(ACCESS_TOKEN, access_token);
////        }
////        if(!TextUtils.isEmpty(sid)) {
////            HttpConnectUtils.addDefaultHeader(CLIENT_SID, sid);
////        }
////        if(!TextUtils.isEmpty(eid)) {
////            HttpConnectUtils.addDefaultHeader(CLIENT_EID, eid);
////        }
//        HttpConnectUtils.addDefaultHeader(CLIENT_ANDROID_ID, android_id);
//        HttpConnectUtils.addDefaultQuery("_android_id", android_id);
//
//        HttpConnectUtils.addDefaultQuery("_network", network_name);
    }

    public static void setVersionPrefix(String prefix) {
        if(prefix != null) {
//            version = prefix + version;
//            HttpConnectUtils.addDefaultHeader(CLIENT_VERSION, version);
            HttpConnectUtils.addDefaultHeader(CLIENT_VERSION, prefix + version);
        }
    }

//    private static String getUid(Context context) {
//        String id = PropertyUtils.getProperty(PROPERTY_ID);
//        if(id == null || "NULL".equals(id)) {
//            id = PreferenceUtils.getStringPref(context, PROPERTY_ID, null);
//            if(id == null) {
//                UUID uuid = UUID.randomUUID();
//                if(uuid == null) {
//                    return "NULL";
//                }
//                id = uuid.toString();
//                PreferenceUtils.setStringPref(context, PROPERTY_ID, id);
//            }
//            PropertyUtils.saveProperties(PROPERTY_ID, uid);
//        }
//        return id;
//
//    }
//
//    private static String getSite(Context context) {
//        try {
//            String site = context.getPackageManager().getApplicationInfo(
//                    context.getPackageName(), PackageManager.GET_META_DATA)
//                    .metaData.getString("com.qianxun.site");
//            if(site != null) {
//                return site;
//            }
//        } catch(NameNotFoundException ignore) {
//        }
//        return "1kxun";
//    }
//
//    private static String getImei(Context context) {
//        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//        if(tm != null) {
//            final String imei = tm.getDeviceId();
//            if(imei != null) {
//                return imei;
//            }
//        }
//        return "";
//    }
//
//    private static String getImsi(Context context) {
//        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//        if(tm != null) {
//            String imsi = tm.getSubscriberId();
//            if(imsi != null) {
//                return imsi;
//            }
//        }
//        return "";
//    }
//
//    private static String getSimOperator(Context context) {
//        TelephonyManager tm = (TelephonyManager)context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        if(tm != null) {
//            String simOperator = tm.getSimOperator();
//            if(simOperator != null) {
//                return simOperator;
//            }
//        }
//        return null;
//    }
//
//    private static String getOs() {
//        return "Android" + android.os.Build.VERSION.RELEASE;
//    }
//
//    private static String getResolution(Context context) {
//        DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        return dm.widthPixels + "," + dm.heightPixels;
//    }
//
//    private static String getPackageName(Context context) {
//        return context.getPackageName();
//    }
//
//    private static String getVersion(Context context) {
//        // Get a version string for the app.
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
////            return Constant.PACKAGE_NAME + ":" + String.valueOf(pi.versionCode);
//            return pi.versionName;
//        } catch(NameNotFoundException ignore) {
//        }
//        return "";
//    }
}
