package truecolor.webdataloader;

import android.os.Bundle;

import com.alibaba.fastjson.annotation.JSONType;

@JSONType
public class RequestError {

    public RequestError(int serviceCode, Bundle params) {
        this.mServiceCode = serviceCode;
        this.mParams = params;
    }

    public int mServiceCode;
    public Bundle mParams;

}
