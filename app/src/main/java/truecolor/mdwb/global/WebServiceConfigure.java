package truecolor.mdwb.global;

public class WebServiceConfigure {

    public static final String STATUS_SUCCESS = "success";

    private static int BASE_TASK_ID = 1000;

    public static final int GET_USER = BASE_TASK_ID++;
    public static final int GET_FRIENDS_TIMELINE = BASE_TASK_ID++;
    public static final int GET_PUBLIC_TIMELINE = BASE_TASK_ID++;
    public static final int GET_BILATERA_TIMELINE = BASE_TASK_ID++;
    public static final int GET_MORE_FRIENDS_TIMELINE = BASE_TASK_ID++;
    public static final int GET_COMMENTS = BASE_TASK_ID++;
    public static final int GET_REPOST = BASE_TASK_ID++;
    public static final int GET_FAVORITES = BASE_TASK_ID++;
    public static final int POST_STATUESE = BASE_TASK_ID++;
    public static final int GET_EMOTIONS = BASE_TASK_ID++;

    public static final String MOD_OAUTH = "Oauth";
    public static final String MOD_WEIBO_STATUES = "WeiboStatuses";

    public static final String ACT_AUTHORIZE = "authorize";
    public static final String ACT_FRIENDS_TIME_LINE = "friends_timeline";

    public static final String HTTP_PARAM_PASSWORD = "passwd";
    public static final String HTTP_PARAM_PAUTH_TOKEN = "oauth_token";
    public static final String HTTP_PARAM_PAUTH_TOKEN_SECRET = "oauth_token_secret";


    public static final String CONSUMER_KEY = "3236949863";
    public static final String AISEN_KEY = "2362431378";
    public static final String SINA_CONSUMER_KEY = "966056985";

    public static final String WEICO_TOKEN = "2.00ZvCCAC06XASO71f64d14d607Nx6N"; //Weico.Android Token



    /************************************* sina weibo **********************************************/
    public static final String REDIRECT_URL = "http://com.mirahome.mlily";
    public static final String BASE_URL = "https://api.weibo.com/2/";
    public static final String HTTP_METHOD_GET = "GET";
    public static final int  HTTP_PARAM_DEFAULT_COUNT = 20;

    public static final String HTTP_URL_GET_USER = BASE_URL + "users/show.json";
    public static final String HTTP_URL_GET_FRIENDS_TIMELINE = BASE_URL + "statuses/friends_timeline.json";
    public static final String HTTP_URL_GET_PUBLIC_TIMELINE = BASE_URL + "statuses/public_timeline.json";
    public static final String HTTP_URL_GET_BILATERA_TIMELINE = BASE_URL + "statuses/bilateral_timeline.json";
    public static final String HTTP_URL_GET_COMMENTS = BASE_URL + "comments/show.json";
    public static final String HTTP_URL_GET_REPOST = BASE_URL + "statuses/repost_timeline.json";
    public static final String HTTP_URL_UPDATE_STATUSES = BASE_URL + "statuses/update.json";
    public static final String HTTP_URL_UPLOAD_PIC = BASE_URL + "statuses/upload_pic.json";
    public static final String HTTP_URL_GET_FAVORITES = BASE_URL + "favorites.json";
    public static final String HTTP_URL_GET_EMOTIONS = BASE_URL + "emotions.json";

    public static final String HTTP_PARAM_ACCESS_TOKEN = "access_token";
    public static final String HTTP_PARAM_UID = "uid";
    public static final String HTTP_PARAM_PAGE = "page";
    public static final String HTTP_PARAM_COUNT = "count";
    public static final String HTTP_PARAM_WEIBO_ID = "id";
    public static final String HTTP_PARAM_SINCE_ID = "since_id";
    public static final String HTTP_PARAM_STATUES = "status";

    public static final String HTTP_GET_FRIENDS_TIMELINE_IS_REFRESH = "get_friends_timeline_is_refresh";
    public static final String HTTP_GET_PUBLIC_TIMELINE_IS_REFRESH = "get_public_timeline_is_refresh";
    public static final String HTTP_GET_BILATERAL_TIMELINE_IS_REFRESH = "get_bilateral_timeline_is_refresh";
    public static final String HTTP_GET_COMMENTS_IS_REFERSH = "get_comments_is_refresh";
    public static final String HTTP_GET_REPOST_IS_REFERSH = "get_repost_is_refresh";
    public static final String HTTP_GET_PUBLIC_TIMELINE_TYPE = "get_friends_timeline_type";

    public static final int HTTP_GET_PUBLIC_TIMELINE_TYPE_INIT = 0;
    public static final int HTTP_GET_PUBLIC_TIMELINE_TYPE_REFRESH = 1;
    public static final int HTTP_GET_PUBLIC_TIMELINE_TYPE_MORE = 2;



}
