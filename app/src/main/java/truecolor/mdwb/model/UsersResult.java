package truecolor.mdwb.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

/**
 * Created by xiaowu on 15/11/9.
 */
@JSONType
public class UsersResult {

    @JSONField(name = "screen_name")
    public String screen_name;
    @JSONField(name = "followers_count")
    public String followers_count;
    @JSONField(name = "uid")
    public String uid;
}
