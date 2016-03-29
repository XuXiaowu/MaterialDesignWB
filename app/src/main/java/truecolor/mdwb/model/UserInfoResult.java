package truecolor.mdwb.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import truecolor.webdataloader.annotations.MemoryCache;

/**
 * Created by xiaowu on 15/8/16.
 */
@MemoryCache
@JSONType
public class UserInfoResult {

    @JSONField(name = "name")
    public String name;
    @JSONField(name = "description")
    public String description;
    @JSONField(name = "profile_image_url")
    public String profile_image_url;//50x50小头像
    @JSONField(name = "avatar_large")
    public String avatar_large;
}
