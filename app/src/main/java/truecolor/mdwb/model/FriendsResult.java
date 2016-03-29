package truecolor.mdwb.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

/**
 * Created by xiaowu on 15/11/23.
 */
@JSONType
public class FriendsResult {

    @JSONField(name = "users")
    public FriendsTimelineResult.User users[];
    @JSONField(name = "next_cursor")
    public int next_cursor;
    @JSONField(name = "total_number")
    public int total_number;
}
