package truecolor.webdataloader.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SecurityUtils {
//    private static final String TAG = SecurityUtils.class.getName();
//    private static final boolean DEBUG = false;
    
    private static final int RSA_MAX_LENGTH        = 117;
    private static final int RSA_BUFFER_LENGTH    = 128;
    
    private static boolean mIsLoadLibrary = false;
    private static Cipher mCipher = null;

    static {
        try {
            System.loadLibrary("security");
            mIsLoadLibrary = true;
        } catch (UnsatisfiedLinkError ignore) {
            mIsLoadLibrary = false;
        }
    }
    
//    private static void initLibrary(){
//        if(mIsLoadLibrary) return;
//
//        try {
//            System.loadLibrary("security");
//            mIsLoadLibrary = true;
//            return;
//        } catch (UnsatisfiedLinkError ignore) {
////            Log.e(TAG, "UnsatisfiedLinkError: " + e, e);
//        }
//
////        try {
////            System.load("/data/data/com.qianxun.kankan/lib/libsecurity.so");
////            mIsLoadLibrary = true;
////            return;
////        } catch (UnsatisfiedLinkError e) {
////            Log.e(TAG, "UnsatisfiedLinkError: " + e, e);
////        }
//
//        mIsLoadLibrary = false;
//    }
    
    private static PublicKey getPublicKey(String key) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        byte[] keyBytes;
        keyBytes = Base64.decode(key, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private static void initCipher() {
        if(mCipher != null) return;

        try {
            mCipher = Cipher.getInstance("RSA/None/PKCS1Padding");
            mCipher.init(Cipher.ENCRYPT_MODE, getPublicKey(nativeGetPublicKey()));
        } catch (NoSuchAlgorithmException ignore) {
        } catch (NoSuchPaddingException ignore) {
        } catch (InvalidKeySpecException ignore) {
        } catch (InvalidKeyException ignore) {
        }
    }
    
    public static String getRsaString(String str, long timestamp) {
        if(!mIsLoadLibrary) return null;

        if(mCipher == null) {
            initCipher();
            if(mCipher == null) return null;
        }

        try {
            byte[] plainText = str.getBytes();
            byte[] enBytes = new byte[RSA_BUFFER_LENGTH * ((plainText.length + RSA_MAX_LENGTH - 1) / RSA_MAX_LENGTH)];
            for(int i = 0, offset = 0; i < plainText.length; i += RSA_MAX_LENGTH) {
                int len = plainText.length - i > RSA_MAX_LENGTH ? RSA_MAX_LENGTH : plainText.length - i;
                byte[] d = mCipher.doFinal(plainText, i, len);
                System.arraycopy(d, 0, enBytes, offset, d.length);
                offset += d.length;
            }
            
            enBytes = Base64.encode(enBytes, Base64.DEFAULT);

            return nativeGetSecurityString(enBytes, timestamp);
            
        } catch (BadPaddingException ignore) {
//            if(DEBUG) Log.d(TAG, "BadPaddingException: " + e, e);
        } catch (IllegalBlockSizeException ignore) {
//            if(DEBUG) Log.d(TAG, "IllegalBlockSizeException: " + e, e);
        }
        return null;
    }

    public static String encodeString(String str) {
        if(!mIsLoadLibrary) return null;

        if(str == null || str.length() == 0) return str;

        return nativeEncodeString(new String(
                android.util.Base64.encode(str.getBytes(), android.util.Base64.DEFAULT)).trim());
    }

    public static String getSign(String url, String query) {
        if(!mIsLoadLibrary) return null;
        return nativeGetSign(url, query);
    }
    
    private static native String nativeGetPublicKey();
    private static native String nativeGetSecurityString(byte[] buf, long timestamp);
    private static native String nativeEncodeString(String str);
    private static native String nativeGetSign(String url, String query);
}
