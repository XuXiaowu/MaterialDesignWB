package truecolor.mdwb.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import truecolor.webdataloader.annotations.PageCache;
import truecolor.webdataloader.annotations.PageData;

/**
 * Created by xiaowu on 15/11/3.
 */
@PageCache
@JSONType
public class FavoritesResult {

    @PageData
    @JSONField(name = "favorites")
    public Status favorites[];
    @JSONField(name = "total_number")
    public int total_number;

    @JSONType
    public static class Status{
        @JSONField(name = "status")
        public FriendsTimelineResult.FriendsTimeline status;
    }
}
