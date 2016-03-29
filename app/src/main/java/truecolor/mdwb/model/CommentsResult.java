package truecolor.mdwb.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import truecolor.webdataloader.annotations.MemoryCache;
import truecolor.webdataloader.annotations.PageCache;
import truecolor.webdataloader.annotations.PageData;

/**
 * Created by xiaowu on 15/8/30.
 */
@MemoryCache
@JSONType
public class CommentsResult {

    @JSONField(name = "comments")
    public Comment comments[];
    @JSONField(name = "next_cursor")
    public String next_cursor;

    @JSONType
    public static class Comment{
        @JSONField(name = "created_at")
        public String created_at; //评论创建时间
        @JSONField(name = "id")
        public String id; //评论的ID
        @JSONField(name = "text")
        public String text; //评论的内容
        @JSONField(name = "source")
        public String source; //评论的来源
        @JSONField(name = "mid")
        public String mid;
        @JSONField(name = "user")
        public FriendsTimelineResult.User user; //评论作者的用户信息字段
    }
}
