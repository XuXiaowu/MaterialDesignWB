package truecolor.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;

/**
 * Created by xiaowu on 15/7/15.
 */
public class LocalImageDecoder implements ImageLoader.ImageDecoder {
    private static final String  TAG   = LocalImageDecoder.class.getName();
    private static final boolean DEBUG = ImageConfigure.DEBUG;

    private static LocalImageDecoder sDecoder;

    public static LocalImageDecoder getInstance() {
        if(sDecoder == null) {
            sDecoder = new LocalImageDecoder();
        }
        return sDecoder;
    }

    protected LocalImageDecoder() {
    }

    @Override
    public Bitmap decodeImage(String uri) {
        String path = getLocalPath(uri);
        File file = new File(path);
        if(file.exists()) {
            Bitmap image = BitmapFactory.decodeFile(path);
            if(image != null) {
                return image;
            }

            if(!file.delete()) {
                if(DEBUG) Log.d(TAG, "delete image file failed: " + path);
            }
        }
        return null;
    }

    protected boolean mkLocalDir() {
        File file = new File(ImageConfigure.getImageCacheDir());
        return (file.exists() && file.isDirectory()) || file.mkdirs();
    }

    public static String getLocalPath(String url) {
        StringBuilder sb = new StringBuilder(ImageConfigure.getImageCacheDir());
        String filename;
        int index = url.indexOf('/', 8);
        if(index < 0) filename = url;
        else filename = url.substring(index + 1);

        String str = MD5Utils.md5(filename);
//        String str = filename = "ruanraunbing2.jpg";
        sb.append(str);
        return sb.toString();
    }
}
