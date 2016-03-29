package truecolor.imageloader;

import android.text.TextUtils;

/**
 * Created by xiaowu on 15/7/15.
 */
public class ImageConfigure {

    public static final boolean DEBUG = false;

    public static final String TASK_IMAGE_TAG = "image_task";
    public static final int    TASK_MAX_NUM   = 2;
    public static final String TASK_WEB_IMAGE_TAG = "web_image_task";
    public static final int    TASK_WEB_MAX_NUM   = 5;


    private static final String IMAGE_CACHE_DIR = "cache/";

    private static String sImageCacheDir;
    private static String sImageExtCacheDir = Configure.sExternalStoragePath + IMAGE_CACHE_DIR;
    private static String sImageIntCacheDir = Configure.sInternalStoragePath + IMAGE_CACHE_DIR;

    public static void init(String externalPath) {
        if(TextUtils.isEmpty(externalPath)) {
//            throw new IllegalArgumentException("external path should not be null");
            return;
        }
        sImageCacheDir = externalPath + IMAGE_CACHE_DIR;
    }

    public static String getImageCacheDir() {
        if(sImageCacheDir != null) return sImageCacheDir;
        if(Configure.hasExternalStorage) return sImageExtCacheDir;
        return sImageIntCacheDir;
    }

    public static String getInternalImageCacheDir() {
        return sImageIntCacheDir;
    }

    public static String getExternalImageCacheDir() {
        return sImageExtCacheDir;
    }
}
