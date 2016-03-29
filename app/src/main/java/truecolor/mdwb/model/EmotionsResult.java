package truecolor.mdwb.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import truecolor.webdataloader.annotations.LocalCache;

/**
 * Created by xiaowu on 15/11/16.
 */
@JSONType
public class EmotionsResult {

    public int id;
    @JSONField(name = "category")
    public String category;
    @JSONField(name = "common")
    public String common;
//    @JSONField(name = "icon")
//    public String icon;
//    @JSONField(name = "phrase")
//    public String phrase;
//    @JSONField(name = "picid")
//    public String picid;
    @JSONField(name = "type")
    public String type;
    @JSONField(name = "url")
    public String url;
    @JSONField(name = "value")
    public String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCommon() {
        return common;
    }

    public void setCommon(String common) {
        this.common = common;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
