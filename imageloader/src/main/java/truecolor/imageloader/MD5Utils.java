package truecolor.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xiaowu on 15/7/15.
 */
public class MD5Utils {

    private static final char HEX_DIGITS[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static String toHexString(byte[] b) {
        int len = b.length;
        StringBuilder sb = new StringBuilder(len * 2);
        for(int i = 0; i < len; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    private static void md5sum(MessageDigest md5, File file) throws IOException {
        if(file.isDirectory()) {
            File files[] = file.listFiles();
            int len = files.length;
            for(int i = 0; i < len; i++) {
                md5sum(md5, files[i]);
            }
        } else {
            byte[] buffer = new byte[1024];
            int numRead;
            InputStream fis = new FileInputStream(file);
            while((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
        }
    }

    public static String md5sum(String filename) {
        File file = new File(filename);
        if(!file.exists()) return null;

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5sum(md5, file);

            return toHexString(md5.digest());
        } catch(NoSuchAlgorithmException ignore) {
        } catch(IOException ignore) {
        }
        return null;
    }

    public static String md5sum(File file) {
        if(file == null || !file.exists()) return null;

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5sum(md5, file);

            return toHexString(md5.digest());
        } catch(NoSuchAlgorithmException ignore) {
        } catch(IOException ignore) {
        }
        return null;
    }

    public static String md5(String str) {
        if(str == null) return null;

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = str.getBytes();
            md5.update(buffer, 0, buffer.length);

            return toHexString(md5.digest());
        } catch(NoSuchAlgorithmException ignore) {
        }
        return null;
    }
}
