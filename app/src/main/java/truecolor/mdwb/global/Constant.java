package truecolor.mdwb.global;

/**
 * Created by daiyinhua on 15-2-2.
 *
 */
public class Constant {

    public static final int PAGE_SECETION_FRAGEMENT_TYPE_FRIENDS = 0;
    public static final int PAGE_SECETION_FRAGEMENT_TYPE_BILATERA = 1;
    public static final int PAGE_SECETION_FRAGEMENT_TYPE_PUBILE = 2;

    public static final String EXTRA_FT = "fiendsTimeline";

    public static final String DETALI_FRIENDS_TIMELINE_TYPE = "detail_type";

    public static final int DETAIL_FRIENDS_TIMELINE_TYPE_LIST = 1000;
    public static final int DETAIL_FRIENDS_TIMELINE_TYPE_PUBLISH = 1001;
    public static final int DETAIL_FRIENDS_TIMELINE_TYPE_CREATE_COMMENT = 1002;
    public static final int DETAIL_FRIENDS_TIMELINE_TYPE_REPOST = 1003;
    public static final int DETAIL_FRIENDS_TIMELINE_TYPE_MAIN = 1004;
    public static final int DETAIL_FRIENDS_TIMELINE_TYPE_SEARCH = 1005;
    public static final int DETAIL_FRIENDS_TIMELINE_TYPE_DETAIL = 1006;
    public static final int DETAIL_FRIENDS_TIMELINE_TYPE_DETAIL_REPOST = 1007;


    public static final int PUBLISH_STATUS_NOTIFICATION_UPLOADING  = 1001;
    public static final int PUBLISH_STATUS_NOTIFICATION_FAIL  = 1002;
    public static final int PUBLISH_STATUS_NOTIFICATION_SUCCESS  = 1003;

    public static final int PUBLISH_STATUS_TYPE_NEW = 0;
    public static final int PUBLISH_STATUS_TYPE_COMMENT_CREATE = 1;
    public static final int PUBLISH_STATUS_TYPE_REPOST = 2;
    public static final int PUBLISH_STATUS_TYPE_COMMENT_REPLY = 3;

    public static final int FAVORITES_CREATE_SUCCESS = 100;
    public static final int FAVORITES_CREATE_FAIL = 101;
    public static final int FAVORITES_CREATE_FAIL_HAVA_COLLECTED = 102;
    public static final int FAVORITES_DESTROY_SUCCESS = 103;
    public static final int FAVORITES_DESTROY_FAIL = 104;
    public static final int FAVORITES_DESTROY_FAIL_NO_COLLECTED = 105;
    public static final int STATUSES_DESTROY_SUCCESS = 106;
    public static final int STATUSES_DESTROY_FAIL = 107;

    public static final String ACTION_START = "action_start";
    public static final String ACTION_STOP = "action_stop";
    public static final String CAMERA_PHOTO_TEMP_PATH = "/sdcard/MaterialDesingWB/temp/camera/";
    public static final String UPLOAD_IMAGE_TEMP_PATH = "/sdcard/MaterialDesignWB/temp/";

    public static final String PUBLISH_STATUESE_NEW = "publish_new_statuese";
    public static final String PUBLISH_STATUESE_REPOST = "publish_statuese_repost";
    public static final String PUBLISH_COMMENTS_CREATE = "publish_comment_create";
    public static final String PUBLISH_COMMENTS_REPOST = "publish_comment_repost";
    public static final String PUBLISH_COMMENTS_REPLY = "publish_comment_reply";

    public static final String EXTRA_STATUESE_ID = "statuese_id";
    public static final String EXTRA_COMMENT_ID = "commet_id";
    public static final String EXTRA_COMMENT = "commet";
    public static final String EXTRA_IMAGER_GROUP_POS = "imager_group_pos";

    public static final String GO_TO_PHOTOS_BROWSE_TYPE_MIAN = "go_to_photos_browse_type_mian";
    public static final String GO_TO_PHOTOS_BROWSE_TYPE_DETAIL = "go_to_photos_browse_type_detail";
    public static final String GO_TO_PHOTOS_BROWSE_TYPE_SEARCH = "go_to_photos_browse_type_search";

    public static final String GO_TO_SEARCH_USER= "go_to_search_user";
    public static final String GO_TO_SEARCH_STATUSES= "go_to_search_statuses";

    public static final String EXTRA_FRIENDS_USER_NAME = "extra_friends_user_name";



}
