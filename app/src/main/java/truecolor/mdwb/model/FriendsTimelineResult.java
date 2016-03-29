package truecolor.mdwb.model;

import android.graphics.Bitmap;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;

import truecolor.webdataloader.annotations.MemoryCache;
import truecolor.webdataloader.annotations.PageCache;
import truecolor.webdataloader.annotations.PageData;

@PageCache
@JSONType
public class FriendsTimelineResult {

	@PageData
	@JSONField(name = "statuses")
	public FriendsTimeline statuses[];

	@JSONField(name = "total_number")
	public int total_number;

	@JSONType
	public static class FriendsTimeline {
		@JSONField(name = "created_at")
		public String created_at;
		@JSONField(name = "idstr")
		public String idstr;
		@JSONField(name = "text")
		public String text;
		@JSONField(name = "source")
		public String source;
		@JSONField(name = "favorited")
		public boolean favorited; //是否已收藏，true：是，false：否
//		@JSONField(name = "thumbnail_pic")
//		public String thumbnail_pic; //缩略图片地址，没有时不返回此字段
		@JSONField(name = "bmiddle_pic")
		public String bmiddle_pic; //中等尺寸图片地址，没有时不返回此字段
		@JSONField(name = "original_pic")
		public String original_pic;	//原始图片地址，没有时不返回此字段
//		geo	object	地理信息字段 详细
		@JSONField(name = "user")
		public User user; //微博作者的用户信息字段 详细
		@JSONField(name = "retweeted_status")
		public FriendsTimeline retweeted_status;	//被转发的原微博信息字段，当该微博为转发微博时返回 详细
        @JSONField(name = "reposts_count")
		public int reposts_count; //转发数
		@JSONField(name = "comments_count")
		public int comments_count; //评论数
		@JSONField(name = "attitudes_count")
		public int attitudes_count; //表态数
//		visible	object	微博的可见性及指定可见分组信息。该object中type取值，0：普通微博，1：私密微博，3：指定分组微博，4：密友微博；list_id为分组的组号
        @JSONField(name = "pic_urls")
		public PicUrl pic_urls[]; //微博配图ID。多图时返回多图ID，用来拼接图片url。用返回字段thumbnail_pic的地址配上该返回字段的图片ID，即可得到多个图片url。
	}

	@JSONType
	public static class User{

		@JSONField(name = "idstr")
		public String idstr; //字符串型的用户UID
		@JSONField(name = "screen_name")
		public String screen_name; //用户昵称
		@JSONField(name = "name")
		public String name;	//友好显示名称
//		province	int	用户所在省级ID
//		city	int	用户所在城市ID
        @JSONField(name = "location")
        public String location;	//用户所在地
		@JSONField(name = "description")
		public String description;	//用户个人描述
//		url	string	用户博客地址
        @JSONField(name = "profile_image_url")
        public String profile_image_url;//用户头像地址（中图），50×50像素
//		profile_url	string	用户的微博统一URL地址
//		domain	string	用户的个性化域名
//		weihao	string	用户的微号
//		gender	string	性别，m：男、f：女、n：未知
//		followers_count	int	粉丝数
//		friends_count	int	关注数
//		statuses_count	int	微博数
//		favourites_count	int	收藏数
//		created_at	string	用户创建（注册）时间
//		allow_all_act_msg	boolean	是否允许所有人给我发私信，true：是，false：否
//		geo_enabled	boolean	是否允许标识用户的地理位置，true：是，false：否
//		verified	boolean	是否是微博认证用户，即加V用户，true：是，false：否
//		verified_type	int	暂未支持
//		remark	string	用户备注信息，只有在查询用户关系时才返回此字段
//		status	object	用户的最近一条微博信息字段 详细
//		allow_all_comment	boolean	是否允许所有人对我的微博进行评论，true：是，false：否
        @JSONField(name = "avatar_large")
        public String avatar_large; //用户头像地址（大图），180×180像素
		@JSONField(name = "avatar_hd")
		public String avatar_hd; //用户头像地址（高清），高清头像原图
//		verified_reason	string	认证原因
//		follow_me	boolean	该用户是否关注当前登录用户，true：是，false：否
//		online_status	int	用户的在线状态，0：不在线、1：在线
//		bi_followers_count	int	用户的互粉数
	}

	@JSONType
	public static class PicUrl{
		@JSONField(name = "thumbnail_pic")
		public String thumbnail_pic;
	}
}
