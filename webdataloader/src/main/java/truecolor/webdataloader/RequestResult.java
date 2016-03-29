package truecolor.webdataloader;

import android.os.Bundle;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

@JSONType
public class RequestResult {

    private final static String SUCCESS = "success";

    public int mServiceCode;
    public Bundle mParams;

    @JSONField(name = "status")
    public String mStatus;

    @JSONField(name = "message")
    public String mMessage;

    @JSONField(name = "timestamp")
    public long mTimestamp;

    public boolean isSuccess() {
        return SUCCESS.equals(mStatus);
    }


}
