package truecolor.mdwb.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

/**
 * Created by xiaowu on 15/10/28.
 */
@JSONType
public class DetailMessage {

    @JSONField(name = "error")
    public String error;
    @JSONField(name = "error_code")
    public String error_code;
}
