package truecolor.webdataloader;


import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import truecolor.webdataloader.annotations.CalledByNative;
import truecolor.webdataloader.util.SecurityUtils;

/**
 *
 */
public class HttpRequest {
    public static final String METHOD_POST  = "POST";

    private String url;
    private String query;
//    public HashMap<String, String> headers;
    public HttpHeader headers;
    public String body;
    public byte[] bodyData;
    public int bodyDataOffset;
    public int bodyDataLength;
    public String method;
    public int timeout = -1;
    public boolean sign = false;

    private boolean defaultParams;

    public ArrayList<MultiPart> multiPartList;

    public static class MultiPart {
        public String name;

        public String value;
        public String charset;

        public String type;
        public File file;

        public MultiPart(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public MultiPart(String name, String value, String charset) {
            this.name = name;
            this.value = value;
            this.charset = charset;
        }

        public MultiPart(String name, File file, String type) {
            this.name = name;
            this.file = file;
            this.type = type;
        }
    }

    public static HttpRequest createDefaultRequest(String url) {
        return new HttpRequest(url).setDefaultHeaders().addDefaultQuery();

    }

    public static HttpRequest createDefaultPostRequest(String url) {
        return new HttpRequest(url).setMethod(METHOD_POST).setDefaultHeaders().addDefaultQuery();
    }

    public static HttpRequest createDefaultRequestWithSign(String url) {
        return new HttpRequest(url).setDefaultHeaders().addSignQuery().addDefaultQuery();
    }

    public static HttpRequest createDefaultPostRequestWithSign(String url) {
        return new HttpRequest(url).setMethod(METHOD_POST).setDefaultHeaders().addDefaultQuery().addSignQuery();
    }

//    public HttpRequest() {
//    }

    public HttpRequest(String url) {
        this.url = url;
    }

    @CalledByNative
    public String getUriStr() {
        String queryStr;
        if(query == null || METHOD_POST.equalsIgnoreCase(method)) {
            queryStr = HttpConnectUtils.getDefaultParams(!defaultParams);
        } else {
            queryStr = String.format("%s&%s", query, HttpConnectUtils.getDefaultParams(!defaultParams));
        }
        if(sign) {
            String signValue = SecurityUtils.getSign(url, queryStr);
            queryStr = String.format("%s&sign=%s", queryStr, signValue);
        }
        if(url.contains("?")) return String.format("%s&%s", url, queryStr);
        return String.format("%s?%s", url, queryStr);
    }

    @CalledByNative
    public String getUriKey() {
        String defaultQueryStr = HttpConnectUtils.getDefaultParamsKey();
        String queryStr;
        if(defaultQueryStr == null) {
            queryStr = query;
        } else if(query == null || METHOD_POST.equalsIgnoreCase(method)) {
            queryStr = defaultQueryStr;
        } else {
            queryStr = String.format("%s&%s", query, defaultQueryStr);
        }
        if(queryStr == null) return url;
        if(url.contains("?")) return String.format("%s&%s", url, queryStr);
        return String.format("%s?%s", url, queryStr);
    }

    @CalledByNative
    public String getUrl() {
        return url;
    }

    @CalledByNative
    public HttpRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    @CalledByNative
    public String getQuery() {
        return query;
    }

    @CalledByNative
    public HttpRequest setQuery(String query) {
        this.query = query;
        return this;
    }

    @CalledByNative
    public HttpRequest addQuery(String name, int value) {
        if(query == null) {
            query = String.format("%s=%d", name, value);
        } else {
            query = String.format("%s&%s=%d", query, name, value);
        }
        return this;
    }

    @CalledByNative
    public HttpRequest addQuery(String name, long value) {
        if(query == null) {
            query = String.format("%s=%s", name, Long.toString(value));
        } else {
            query = String.format("%s&%s=%s", query, name, Long.toString(value));
        }
        return this;
    }

    @CalledByNative
    public HttpRequest addQuery(String name, float value) {
        if(query == null) {
            query = String.format("%s=%f", name, value);
        } else {
            query = String.format("%s&%s=%f", query, name, value);
        }
        return this;
    }

    @CalledByNative
    public HttpRequest addQuery(String name, boolean value) {
        if(query == null) {
            query = String.format("%s=%b", name, value);
        } else {
            query = String.format("%s&%s=%b", query, name, value);
        }
        return this;
    }

    @CalledByNative
    public HttpRequest addQuery(String name, String value) {
        if(value == null) return this;
        String v;
        try {
            v = URLEncoder.encode(value, HTTP.UTF_8);
        } catch (UnsupportedEncodingException problem) {
            v = value;
        }
        if(query == null) {
            query = String.format("%s=%s", name, v);
        } else {
            query = String.format("%s&%s=%s", query, name, v);
        }
        return this;
    }

    @CalledByNative
    public HttpRequest addQuery(HashMap<String, String> params) {
        if(params != null) {
            for(HashMap.Entry<String, String> entry : params.entrySet()) {
                addQuery(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    @CalledByNative
    public HttpRequest addSignQuery() {
        sign = true;
        return this;
    }

    @CalledByNative
    public HttpRequest addDefaultQuery() {
//        HttpConnectUtils.setDefaultQuery(this);
        defaultParams = true;
        return this;
    }

//    @CalledByNative
//    public HttpRequest setHeaders(HashMap<String, String> headers) {
//        this.headers = headers;
//        return this;
//    }

    @CalledByNative
    public HttpRequest setHeaders(HttpHeader headers) {
        this.headers = headers;
        return this;
    }

    @CalledByNative
    public HttpRequest setDefaultHeaders() {
        HttpConnectUtils.setDefaultHeaders(this);
        return this;
    }

    @CalledByNative
    public HttpRequest addHeader(String key, String value) {
        if(key != null && value != null) {
//            if(headers == null) headers = new HashMap<String, String>();
//            headers.put(key, value);
            headers = HttpHeader.addHeader(headers, key, value);
        }
        return this;
    }

    @CalledByNative
    public HttpRequest setBody(String body) {
        this.body = body;
        return this;
    }

    @CalledByNative
    public HttpRequest setBody(byte[] body) {
        this.bodyData = body;
        this.bodyDataOffset = 0;
        this.bodyDataLength = -1;
        return this;
    }

    @CalledByNative
    public HttpRequest setBody(byte[] body, int length) {
        return setBody(body, 0, length);
    }

    @CalledByNative
    public HttpRequest setBody(byte[] body, int offset, int length) {
        this.bodyData = body;
        this.bodyDataOffset = offset;
        this.bodyDataLength = length;
        return this;
    }

    @CalledByNative
    public HttpRequest addMultiPart(String name, String value) {
        if(name == null || value == null) return this;

        if(multiPartList == null) multiPartList = new ArrayList<MultiPart>();
        multiPartList.add(new MultiPart(name, value));
        return this;
    }

    @CalledByNative
    public HttpRequest addMultiPart(String name, String value, String charset) {
        if(name == null || value == null) return this;

        if(multiPartList == null) multiPartList = new ArrayList<MultiPart>();
        multiPartList.add(new MultiPart(name, value, charset));
        return this;
    }

    @CalledByNative
    public HttpRequest addMultiPart(String name, File file, String type) {
        if(name == null || file == null || type == null) return this;

        if(multiPartList == null) multiPartList = new ArrayList<MultiPart>();
        multiPartList.add(new MultiPart(name, file, type));
        return this;
    }

//    @CalledByNative
//    public HttpRequest setBase64Body(String str) {
//        this.bodyData = Base64.decode(str, Base64.DEFAULT);
//        return this;
//    }

    @CalledByNative
    public HttpRequest setMethod(String method) {
        this.method = method;
        return this;
    }

    @CalledByNative
    public HttpRequest setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
}
