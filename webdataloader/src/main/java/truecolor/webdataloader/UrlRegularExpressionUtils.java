package truecolor.webdataloader;//package truecolor.webdataloader;
//
//import android.content.Context;
//
//import com.truecolor.task.TaskUtils;
//import com.truecolor.util.PreferenceUtils;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//
///**
// *
// */
//public class UrlRegularExpressionUtils {
//
//    private static final String RULE_URL        = "http://kankan.1kxun.com/datacenter.jp.conf";
//    private static final String RULE_FILE_NAME  = "url_re.conf";
//
//    private static final String USER_LINE = "user_line";
//
//    private static int sUserLine = 1;
//    private static String[] sRules;
//
//    public static void setUserLine(Context context, int userLine) {
//        sUserLine = userLine;
//        PreferenceUtils.setIntPref(context, USER_LINE, userLine);
//    }
//
//    public static int getUserLine() {
//        return sUserLine;
//    }
//
//    public static void parseHttpRequest(HttpRequest request) {
//        if(request == null) return;
//        request.setUrl(parseUrl(request.getUrl()));
//    }
//
//    public static String parseUrl(String uri) {
//        if(sUserLine != 1 || sRules == null) return uri;
//
//        String ret = uri;
//        int len = sRules.length / 2;
//        for(int i = 0; i < len; i++) {
//            ret = ret.replaceAll(sRules[i * 2], sRules[i * 2 + 1]);
//        }
//        return ret;
//    }
//
//    public static void initUrlRules(Context context) {
//        sUserLine = PreferenceUtils.getIntPref(context, USER_LINE, 1);
//
//        String path = String.format("%s/%s", context.getCacheDir().getAbsolutePath(), RULE_FILE_NAME);
//        File file = new File(path);
//        if(!file.exists()) return;
//
//        try {
//            // parse rules
//            FileInputStream fis = new FileInputStream(path);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//
//            ArrayList<String> list = new ArrayList<String>();
//            for(String line = br.readLine(); line != null; line = br.readLine()) {
//                String[] strs = line.split(" ");
//                if(strs != null && strs.length >= 2) {
//                    list.add(strs[0]);
//                    list.add(strs[1]);
//                }
//            }
//            if(!list.isEmpty()) {
//                sRules = new String[list.size()];
//                list.toArray(sRules);
//            }
//
//            br.close();
//            isr.close();
//            fis.close();
//
//        } catch(IOException e) {
////            if(DEBUG) Log.e(TAG, "GetRulesTask error: ", e);
//        } catch(Exception e) {
////            if(DEBUG) Log.e(TAG, "GetRulesTask error: ", e);
//        }
//    }
//
//    public static void getUrlRules(Context context) {
//        TaskUtils.executeTask("other", new GetRulesTask(context));
//    }
//
//    public static class GetRulesTask extends AbstractTask {
//
//        public GetRulesTask(Context context) {
//            super(context);
//        }
//
//        @Override
//        protected void work() {
//            try {
//                String path = String.format("%s/%s", mContext.getCacheDir().getAbsolutePath(), RULE_FILE_NAME);
//                String tmpPath = path + "_tmp";
//                File tmpFile = new File(tmpPath);
//                if(tmpFile.exists() && tmpFile.isFile() && !tmpFile.delete()) {
//                    return;
//                }
//
//                URL aryURI = new URL(RULE_URL);
//                HttpURLConnection conn = (HttpURLConnection)aryURI.openConnection();
////                WebUtils.setHeaders(conn);
//                conn.setDoInput(true);
//                conn.connect();
//                InputStream is = conn.getInputStream();
//                OutputStream os = new FileOutputStream(tmpFile);
//
//                int read;
//                byte[] buffer = new byte[8192]; //65536
//                do {
//                    read = is.read(buffer);
//                    if (read > 0) {
//                        os.write(buffer, 0, read);
//                    }
//                } while (read != -1);
//
//                os.close();
//
//                is.close();
//                conn.disconnect();
//
//                tmpFile.renameTo(new File(path));
//            } catch(IOException e) {
////                if(DEBUG) Log.e(TAG, "GetRulesTask error: ", e);
//            } catch(Exception e) {
////                if(DEBUG) Log.e(TAG, "GetRulesTask error: ", e);
//            }
//
//            initUrlRules(mContext);
//        }
//    }
//}
