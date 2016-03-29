package truecolor.mdwb.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

/**
 * Created by xiaowu on 15/9/22.
 */
@JSONType
public class UploadPicResult {

    @JSONField(name = "pic_id")
    public String pic_id;
    @JSONField(name = "thumbnail_pic")
    public String thumbnail_pic;
    @JSONField(name = "bmiddle_pic")
    public String bmiddle_pic;
    @JSONField(name = "original_pic")
    public String original_pic;
}
