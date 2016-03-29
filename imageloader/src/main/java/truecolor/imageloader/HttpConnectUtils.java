package truecolor.imageloader;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

/**
 * Created by xiaowu on 15/7/15.
 */
public class HttpConnectUtils {

    private static final String TAG = HttpConnectUtils.class.getCanonicalName();
    private static final boolean DEBUG = false;

    private static final int BUFFER_SIZE = 8192; // 65536

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int HTTP_TIMEOUT = 20000;

    private static final String TIMESTAMP_PARAM = "_";
    private static final String COUNTRY_PARAM = "_country";
    private static final String LOCALE_PARAM = "_locale";

    private static final String CLIENT_COUNTRY      = "Client-Country";
    private static final String CLIENT_LANGUAGE     = "Client-Language";

//    private static final HashMap<String, String> sDefaultHeader = new HashMap<String, String>();

    private static HttpHeader sDefaultHeader;
    private static final HashMap<String, String> sDefaultParams = new HashMap<String, String>();
    private static String sDefaultParamsStr;

    @CalledByNative
    public static void addDefaultHeader(String key, String value) {
        if(key == null || value == null) return;
//        sDefaultHeader.put(key, value);
        sDefaultHeader = HttpHeader.addHeader(sDefaultHeader, key, value);
    }

    @CalledByNative
    public static void removeDefaultHeader(String key) {
        if(key == null) return;
//        sDefaultHeader.remove(key);
        sDefaultHeader = HttpHeader.removeDefaultHeader(sDefaultHeader, key);
    }

//    @CalledByNative
//    public static void removeDefaultHttpHeader(String key) {
//        if(key == null) return;
//        sDefaultHeader.remove(key);
//    }

    @CalledByNative
    public static void setDefaultHeaders(HttpRequestBase request) {
        if(request == null) return;

//        Set<Map.Entry<String, String>> values = sDefaultHeader.entrySet();
//        for(Map.Entry<String, String> value : values) {
//            request.addHeader(value.getKey(), value.getValue());
//        }
        HttpHeader header = sDefaultHeader;
        while(header != null) {
            request.addHeader(header.key, header.value);
            header = header.next;
        }
        request.addHeader(CLIENT_COUNTRY, Locale.getDefault().getCountry());
        request.addHeader(CLIENT_LANGUAGE, Locale.getDefault().getLanguage());
    }

    @CalledByNative
    public static void setDefaultHeaders(HttpURLConnection connection) {
        if(connection == null) return;

//        Set<Map.Entry<String, String>> values = sDefaultHeader.entrySet();
//        for(Map.Entry<String, String> value : values) {
//            connection.setRequestProperty(value.getKey(), value.getValue());
//        }
        HttpHeader header = sDefaultHeader;
        while(header != null) {
            connection.setRequestProperty(header.key, header.value);
            header = header.next;
        }
        connection.setRequestProperty(CLIENT_COUNTRY, Locale.getDefault().getCountry());
        connection.setRequestProperty(CLIENT_LANGUAGE, Locale.getDefault().getLanguage());
    }

    @CalledByNative
    public static void setDefaultHeaders(HttpRequest request) {
        if(request == null) return;
        request.setHeaders(sDefaultHeader);
    }

    @CalledByNative
    public static void addDefaultQuery(String key, String value) {
        if(key == null || value == null) return;
        sDefaultParamsStr = null;
        String v;
        try {
            v = URLEncoder.encode(value, HTTP.UTF_8);
        } catch (UnsupportedEncodingException problem) {
            v = value;
        }
        sDefaultParams.put(key, v);
    }

    @CalledByNative
    public static void removeDefaultQuery(String key) {
        if(key == null) return;
        sDefaultParamsStr = null;
        sDefaultParams.remove(key);
    }

    @CalledByNative
    public static String getDefaultParams(boolean timestampOnly) {
        if(timestampOnly || sDefaultParams.isEmpty()) {
            return String.format("%s=%d", TIMESTAMP_PARAM,
                    (int)(System.currentTimeMillis() / 1000));
        }
        if(sDefaultParamsStr == null) {
            StringBuilder sb = new StringBuilder();
            Set<HashMap.Entry<String, String>> headers = sDefaultParams.entrySet();
            for(HashMap.Entry<String, String> entry : headers) {
                sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
            }
            sDefaultParamsStr = sb.toString();
        }
        return String.format("%s&%s=%s&%s=%s&%s=%d", sDefaultParamsStr,
                COUNTRY_PARAM, Locale.getDefault().getCountry(),
                LOCALE_PARAM, Locale.getDefault().getLanguage(),
                TIMESTAMP_PARAM, (int)(System.currentTimeMillis() / 1000));
    }

    @CalledByNative
    public static String getDefaultParamsKey() {
        if(sDefaultParams.isEmpty()) {
            return null;
        }
        if(sDefaultParamsStr == null) {
            StringBuilder sb = new StringBuilder();
            Set<HashMap.Entry<String, String>> headers = sDefaultParams.entrySet();
            for(HashMap.Entry<String, String> entry : headers) {
                sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
            }
            sDefaultParamsStr = sb.toString();
        }
        return String.format("%s&%s=%s&%s=%s", sDefaultParamsStr,
                COUNTRY_PARAM, Locale.getDefault().getCountry(),
                LOCALE_PARAM, Locale.getDefault().getLanguage());
    }

    @CalledByNative
    public static String connect(HttpRequest request) {
        if(request == null) return null;

//        if(request.headers == null || request.headers.isEmpty()) {
//            // set default header
//            request.addHeader("Connection", "keep-alive");
//            request.addHeader("User-Agent", "stagefright/1.2 (Linux;Android 4.2.2)");
//        }

        HttpURLConnection connection = null;
        int statusCode;
        try {
            URL aryURI = new URL(request.getUriStr());
            connection = (HttpURLConnection)aryURI.openConnection();
            if(HttpRequest.METHOD_POST.equalsIgnoreCase(request.method)) {
                connection.setRequestMethod(HttpRequest.METHOD_POST);
            }
            if(request.headers == null) {
                connection.setRequestProperty("Connection", "keep-alive");
                connection.setRequestProperty("User-Agent", "stagefright/1.2 (Linux;Android 4.4.2)");
            } else {
//                Set<HashMap.Entry<String, String>> headers = request.headers.entrySet();
//                for(HashMap.Entry<String, String> entry : headers) {
//                    connection.setRequestProperty(entry.getKey(), entry.getValue());
//                }
                HttpHeader header = request.headers;
                while(header != null) {
                    connection.setRequestProperty(header.key, header.value);
                    header = header.next;
                }
            }

            if(request.multiPartList != null) {
                String charset = "UTF-8";
                String boundary = Long.toHexString(System.currentTimeMillis());
                String CRLF = "\r\n";
                connection.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + boundary);

                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                for(HttpRequest.MultiPart part : request.multiPartList) {
                    if(part == null) continue;
                    if(part.file == null) {
                        writer.append("--").append(boundary).append(CRLF);
                        writer.append("Content-Disposition: form-data; name=\"").append(part.name).append(
                                "\"").append(CRLF);
                        writer.append("Content-Type: text/plain; charset=");
                        if(part.charset != null) {
                            writer.append(part.charset).append(CRLF);
                            writer.append(CRLF).flush();
//                            writer.append(part.value);
                            output.write(part.value.getBytes());
                            output.flush();
                            writer.append(CRLF).flush();
                        } else {
                            writer.append(charset).append(CRLF);
                            writer.append(CRLF).append(part.value).append(CRLF).flush();
                        }
                    } else {
                        // Send binary file.
                        writer.append("--").append(boundary).append(CRLF);
                        writer.append("Content-Disposition: form-data; name=\"").append(part.name).append(
                                "\"; filename=\"").append(part.file.getName()).append("\"").append(
                                CRLF);
                        writer.append("Content-Type: ").append(part.type).append(CRLF);
                        writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                        writer.append(CRLF).flush();
                        copyFile(part.file, output);
                        output.flush();
                        writer.append(CRLF).flush();
                    }
                }
                writer.append("--").append(boundary).append("--").append(CRLF).flush();
            } else if(request.bodyData != null) {
                if(request.bodyDataLength > 0) {
                    connection.getOutputStream().write(request.bodyData, request.bodyDataOffset,
                            request.bodyDataLength);
                } else {
                    connection.getOutputStream().write(request.bodyData);
                }
            } else if(!TextUtils.isEmpty(request.body)) {
                connection.getOutputStream().write(request.body.getBytes());
            } else if(HttpRequest.METHOD_POST.equalsIgnoreCase(request.method)) {
                String query = request.getQuery();
                if(!TextUtils.isEmpty(query)) {
                    connection.getOutputStream().write(query.getBytes());
                }
            }
            if(request.timeout > 0) {
                connection.setConnectTimeout(request.timeout * 1000);
                connection.setReadTimeout(request.timeout * 1000);
            } else {
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(HTTP_TIMEOUT);
            }
            connection.setInstanceFollowRedirects(true);
            HttpURLConnection.setFollowRedirects(true);
            statusCode = connection.getResponseCode();
            // handle redirect manually if follow redirect is not working
            while(statusCode == HttpURLConnection.HTTP_MOVED_PERM
                    || statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                    /* || statusCode == HttpURLConnection.HTTP_MULT_CHOICE */) {
                // get redirect url from "location" header field
                String newUrl = connection.getHeaderField("Location");

                // open the new connnection again
                connection = (HttpURLConnection)new URL(newUrl).openConnection();
                connection.addRequestProperty("Connection", "keep-alive");
                connection.addRequestProperty("User-Agent", "stagefright/1.2 (Linux;Android 4.4.2)");

                statusCode = connection.getResponseCode();
            }
        } catch(IOException e) {
            if(connection != null) connection.disconnect();
            return null;
        }

        switch(statusCode) {
            case 200:
                try {
                    InputStream is = connection.getInputStream();
                    int read;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    do {
                        read = is.read(buffer);
                        if(read > 0) {
                            baos.write(buffer, 0, read);
                        }
                    } while(read != -1);

                    String ret = baos.toString();
//                Log.d(TAG, ret);
                    baos.close();
                    is.close();
                    connection.disconnect();
                    return ret;
                } catch(IOException e) {
                    connection.disconnect();
                    return null;
                }

            case 400:
            case 401:
            case 404:
            case 500:
//            response.getEntity().consumeContent();
                if(DEBUG) Log.d(TAG, "HTTP Code: " + statusCode);
                connection.disconnect();

            default:
                if(DEBUG) Log.d(TAG, "Default case for status code reached: " + statusCode);
                connection.disconnect();
//            response.getEntity().consumeContent();
//            throw new QianxunException("Error connecting to Qianxun: " + statusCode + ". Try again later.");
        }
        return null;
    }

    @CalledByNative
    public static boolean download(HttpRequest request, File file, File tmpFile) {
        if(request == null || file == null) return false;

//        if(request.headers == null || request.headers.isEmpty()) {
//            // set default header
//            request.addHeader("Connection", "keep-alive");
//            request.addHeader("User-Agent", "stagefright/1.2 (Linux;Android 4.2.2)");
//        }

        HttpURLConnection connection = null;
        int statusCode;
        try {
            URL aryURI = new URL(request.getUriStr());
            connection = (HttpURLConnection)aryURI.openConnection();
            if(HttpRequest.METHOD_POST.equalsIgnoreCase(request.method)) {
                connection.setRequestMethod(HttpRequest.METHOD_POST);
            }
            if(request.headers == null) {
                connection.setRequestProperty("Connection", "keep-alive");
                connection.setRequestProperty("User-Agent", "stagefright/1.2 (Linux;Android 4.4.2)");
            } else {
//                Set<HashMap.Entry<String, String>> headers = request.headers.entrySet();
//                for(HashMap.Entry<String, String> entry : headers) {
//                    connection.setRequestProperty(entry.getKey(), entry.getValue());
//                }
                HttpHeader header = request.headers;
                while(header != null) {
                    connection.setRequestProperty(header.key, header.value);
                    header = header.next;
                }
            }
            long size;
            if(tmpFile != null) {
                size = tmpFile.length();
            } else {
                size = file.length();
            }
            if(size > 0) {
                connection.setRequestProperty("Range", "bytes=" + size + "-");
            }

            if(request.multiPartList != null) {
                String charset = "UTF-8";
                String boundary = Long.toHexString(System.currentTimeMillis());
                String CRLF = "\r\n";
                connection.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + boundary);

                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                for(HttpRequest.MultiPart part : request.multiPartList) {
                    if(part == null) continue;
                    if(part.file == null) {
                        writer.append("--").append(boundary).append(CRLF);
                        writer.append("Content-Disposition: form-data; name=\"").append(part.name).append(
                                "\"").append(CRLF);
                        writer.append("Content-Type: text/plain; charset=");
                        if(part.charset != null) {
                            writer.append(part.charset).append(CRLF);
                            writer.append(CRLF).flush();
                            //                            writer.append(part.value);
                            output.write(part.value.getBytes());
                            output.flush();
                            writer.append(CRLF).flush();
                        } else {
                            writer.append(charset).append(CRLF);
                            writer.append(CRLF).append(part.value).append(CRLF).flush();
                        }
                    } else {
                        // Send binary file.
                        writer.append("--").append(boundary).append(CRLF);
                        writer.append("Content-Disposition: form-data; name=\"").append(part.name).append(
                                "\"; filename=\"").append(part.file.getName()).append("\"").append(
                                CRLF);
                        writer.append("Content-Type: ").append(part.type).append(CRLF);
                        writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                        writer.append(CRLF).flush();
                        copyFile(part.file, output);
                        output.flush();
                        writer.append(CRLF).flush();
                    }
                }
                writer.append("--").append(boundary).append("--").append(CRLF).flush();
            } else if(request.bodyData != null) {
                if(request.bodyDataLength > 0) {
                    connection.getOutputStream().write(request.bodyData, request.bodyDataOffset,
                            request.bodyDataLength);
                } else {
                    connection.getOutputStream().write(request.bodyData);
                }
            } else if(!TextUtils.isEmpty(request.body)) {
                connection.getOutputStream().write(request.body.getBytes());
            } else if(HttpRequest.METHOD_POST.equalsIgnoreCase(request.method)) {
                String query = request.getQuery();
                if(!TextUtils.isEmpty(query)) {
                    connection.getOutputStream().write(query.getBytes());
                }
            }
            if(request.timeout > 0) {
                connection.setConnectTimeout(request.timeout * 1000);
                connection.setReadTimeout(request.timeout * 1000);
            } else {
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(HTTP_TIMEOUT);
            }
            connection.setInstanceFollowRedirects(true);
            HttpURLConnection.setFollowRedirects(true);
            statusCode = connection.getResponseCode();
            // handle redirect manually if follow redirect is not working
            while(statusCode == HttpURLConnection.HTTP_MOVED_PERM || statusCode == HttpURLConnection.HTTP_MOVED_TEMP
            /* || statusCode == HttpURLConnection.HTTP_MULT_CHOICE */) {
                // get redirect url from "location" header field
                String newUrl = connection.getHeaderField("Location");

                // open the new connnection again
                connection = (HttpURLConnection)new URL(newUrl).openConnection();
                connection.addRequestProperty("Connection", "keep-alive");
                connection.addRequestProperty("User-Agent", "stagefright/1.2 (Linux;Android 4.4.2)");

                statusCode = connection.getResponseCode();
            }
        } catch(IOException e) {
            if(connection != null) connection.disconnect();
            return false;
        }

        switch(statusCode) {
            case 200:
            case 206:
                try {
                    InputStream is = connection.getInputStream();
                    int read;
                    byte[] buffer = new byte[BUFFER_SIZE];

//                RandomAccessFile os = new RandomAccessFile(tmpFile == null ? file : tmpFile, "rw");
                    FileOutputStream os;
                    if(tmpFile != null) {
                        os = new FileOutputStream(tmpFile, true);
                    } else {
                        os = new FileOutputStream(file, true);
                    }
                    do {
                        read = is.read(buffer);
                        if(read > 0) {
                            os.write(buffer, 0, read);
                        }
                    } while(read != -1);

                    os.close();
                    is.close();

                    return tmpFile == null || tmpFile.renameTo(file);
                } catch(IOException e) {
                    connection.disconnect();
                    return false;
                }

            case 400:
            case 401:
            case 404:
            case 500:
//            response.getEntity().consumeContent();
                if(DEBUG) Log.d(TAG, "HTTP Code: " + statusCode);

            default:
                if(DEBUG) Log.d(TAG, "Default case for status code reached: " + statusCode);
//            response.getEntity().consumeContent();
//            throw new QianxunException("Error connecting to Qianxun: " + statusCode + ". Try again later.");
        }
        return false;
    }

    private static void copyFile(File file, OutputStream outputStream) throws IOException {
        InputStream in = new FileInputStream(file.getPath());
        byte[] buf = new byte[1024];
        int len;
        while((len = in.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
        }
        in.close();
    }
}
