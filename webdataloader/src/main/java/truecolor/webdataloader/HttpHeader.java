package truecolor.webdataloader;

/**
 *
 */
public class HttpHeader {
    public String key;
    public String value;
    public HttpHeader next;

    public HttpHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static HttpHeader addHeader(HttpHeader list, String key, String value) {
        if(key == null || value == null) return list;
        if(list == null) {
            return new HttpHeader(key, value);
        }
        HttpHeader header = list;
        HttpHeader parent = list;
        while(header != null) {
            if(key.equals(header.key)) {
                header.value = value;
                return list;
            }
            parent = header;
            header = header.next;
        }
        header = new HttpHeader(key, value);
        parent.next = header;
        return list;
    }

    public static HttpHeader removeDefaultHeader(HttpHeader list, String key) {
        if(key == null) return list;
        HttpHeader header = list;
        HttpHeader parent = null;
        while(header != null) {
            if(key.equals(header.key)) {
                if(parent != null) {
                    parent.next = header.next;
                    break;
                } else {
                    return null;
                }
            }
            parent = header;
            header = header.next;
        }
        return list;
    }
}
