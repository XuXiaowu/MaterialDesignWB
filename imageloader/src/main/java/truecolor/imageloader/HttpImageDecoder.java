package truecolor.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xiaowu on 15/7/15.
 */
public class HttpImageDecoder extends LocalImageDecoder {
    private static final String TAG = HttpImageDecoder.class.getName();
    private static final boolean DEBUG = ImageConfigure.DEBUG;

    private static HttpImageDecoder sDecoder;

    public static HttpImageDecoder getInstance() {
        if(sDecoder == null) {
            sDecoder = new HttpImageDecoder();
        }
        return sDecoder;
    }

    protected HttpImageDecoder() {
    }

    @Override
    public Bitmap decodeImage(String uri) {
        Bitmap image = super.decodeImage(uri); //加载本地缓存
        if(image != null) return image;

        boolean hasLocalDir = mkLocalDir();
        try {
//            URL aryURI = new URL(UrlRegularExpressionUtils.parseUrl(uri));
            URL aryURI = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection)aryURI.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();

            String path = null;
//            OutputStream os = null;
            RandomAccessFile raf = null;
            if(hasLocalDir) {
                path = getLocalPath(uri);
                File file = new File(path);
                if(!file.exists() || file.isFile() || file.delete()) {
//                    os = new FileOutputStream(path);
                    raf = new RandomAccessFile(file, "rwd");
                }
            }

            if(raf == null) { // no local space, decode http image directly
                image = BitmapFactory.decodeStream(is);
                is.close();
                conn.disconnect();
                return image;
            } else {
                int read;
                byte[] buffer = new byte[8192]; //65536
                do {
                    read = is.read(buffer);
                    if (read > 0) {
//                        os.write(buffer, 0, read);
                        raf.write(buffer, 0, read); //写入流
                    }
                } while (read != -1);

//                os.close();
                raf.close();

                is.close();
                conn.disconnect();

                return BitmapFactory.decodeFile(path);
            }
        } catch(IOException e) {
            if(DEBUG) Log.e(TAG, "ImageLoader error: ", e);
        } catch(Exception e) {
            if(DEBUG) Log.e(TAG, "ImageLoader error: ", e);
        }
        return null;
    }
}