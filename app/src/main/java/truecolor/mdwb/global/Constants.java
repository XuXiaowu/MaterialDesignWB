package truecolor.mdwb.global;

/**
 * Created by xuxiaowu on 2016/11/20.
 */

public class Constants {

    /**
     * Common
     */
    public final static int PLATFORM_ID = 16;
    public final static int CACHE_MAX_AGE = 86400000; //24h(缓存失效时间)
    public final static int CREATE_ITEM = 0xffffffff;
    public final static long PLAY_MUSIC_DEFAULT_COUNT_DOWN_TIME = 30 * 60 * 1000; //播放音乐默认时间
    public final static String LOGIN_URL = "nls.zc-jp.mirahome.com:6779";
    public final static String HTTP_URL = "http://newapi.zc-jp.mirahome.com:11086";
    public final static String STATEMENT_URL = "http://www.mirahome.me/service.html";
    public final static String ACCESS_KEY = "7a759524b956e69b8daa235286a127a5";
    public final static String SECRET_KEY = "3502297e97405f24262c7455bea8e294";
    public final static String DEFAULT_IMAGE_URL = "http://global.cdn.mirahome.net/"; //默认图片地址
    public final static String SHARE_URL = "http://www.mlily.com"; //分享的网站
    public final static String CHUNYU_PARTNER_KEY = "zySfgjCQWkNqKOwP"; //   REyfkdmazXTqrjLH
    public final static String SINGLE_USER_DEVICE = "1"; //单人毯
    public final static String DOUBLE_USER_DEVICE = "2"; //双人毯
    public final static String TOKEN_PATH = "?token={token}"; //get形式传入token参数
    public final static String TOKEN_EXPIRED_ERROR = "token_expired"; //token过期
    public final static String SESSION_EXPIRED_ERROR = "permission_denied"; //session过期
    public final static String USER_AGENT = "mlily/1.1.0 (Android; %s)"; //user-agent
    public final static String DOWNLOAD_APK_URL = "http://ounmrmz5g.bkt.clouddn.com/mlily2_";
    public final static String GUIDE_URL = "http://web.cn.mirahome.net/mlily-instructions/index-v3.html"; //操作指南地址
    public final static String USER_AGREEMENT_URL = "http://web.cn.mirahome.net/mlily-use-terms/index"; //服务声明地址
    public final static String FEEDBACK_URL = "http://web.cn.mirahome.net/mlily-feedback/index.html"; //意见反馈地址
    public final static String ACCOUNT_QRCODE_PREFIX = "Mlily_User_"; //用户二维码的前缀

    /**
     * sharedPreferences文件信息
     */
    public final static String SP_USERNAME = "username";//用户名
    public final static String SP_PASSWORD = "password";//密码
    public final static String SP_ZC_USERNAME = "zc_username";//智城云登录用户名
    public final static String SP_ZC_PASSWORD = "zc_password";//智城云登录密码
    public final static String SP_MAIN_USER_ID = "main_user_id";//存储当前账号本身的user_id(parent)
    public final static String SP_SESSION_ID = "session_ID";//session_ID
    public final static String SP_IS_HAS_INIT_USER = "is_has_init_user";//是否初始化过当前登录账号(已经有User存在与当前账号)
    public final static String DEVICE_ID = "deviceId";//设备ID
    public final static String WAY_OF_LOGIN = "way_of_login";//记录登录方式(1:手机,2:email,3:第三方),防止第三登录退出登录后,在登录界面显示用户名尴尬
    public final static String SP_PLATFORM_NAME = "platformName"; //第三方登录平台名称
    public final static String SP_UUID = "uuid"; //用户唯一标识
    public final static String SP_NICK = "nick"; //昵称
    public final static String SP_MOBILE = "mobile"; //手机号
    public final static String SP_EMAIL = "email"; //邮箱
    public final static String SP_AVATAR = "avatar"; //头像
    public final static String SP_IS_NOT_FIRST_LAUNCHER = "is_not_first_launch";//是否第一次启动APP
    public final static String SP_IS_SHOWED_GUIDE = "is_showed_guide"; //是否显示过新手引导
    public final static String SP_SHOW_USE_PILLOW_EXPLAIN_DAY = "show_use_pillow_explain_day"; //显示止鼾枕使用说明的日期
    public final static String SP_IS_CN_SERVICE = "is_cn_service"; //是否是国服
    public final static String SP_IS_NEED_CONNECT_ZC = "is_need_connect_zc"; //是否需要连接智城云
    public final static String SP_USE_DEVICE = "usr_device"; //使用的设备

    /**
     * Intent Extra Name
     */
    public final static String EXTRA_IS_NEW_DEVICE = "is_new_device";//是否新版设备
    public final static String EXTRA_IS_PERFORM_LOGIN = "is_perform_login";//是否执行登录
    //    public final static String EXTRA_ACTIVITY_ENTRANCE_FLAG = "activity_entrance_flag"; //activity类型标示
    public final static String EXTRA_VERIFY_CODE = "verify_code"; //验证码
    public final static String EXTRA_SUB_USER = "sub_user"; //子用户
    public final static String EXTRA_DEVICE_USER = "device_user"; //设备的用户
    public final static String EXTRA_IS_HIDE_SKIP_BTN = "is_hide_skip_btn"; //是否隐藏跳过按钮
    //    public final static String EXTRA_IS_SHOW_TITLE_BAR = "is_show_title_bar"; //是否隐藏titlebar
    public final static String EXTRA_DATA = "extra_data"; //intent附加数据
    public final static String EXTRA_IS_SHOW_UPDATE_FIRMWARE_BTN = "is_show_update_firmware_btn"; //是否显示固件升级按钮
    public final static String EXTRA_FROM_PAGE = "from_page"; //从哪个页面跳转过来
    public final static String EXTRA_DEVICE_ENABLE_CHANGE_FLAG = "device_enable_change_flag"; //设备可用状态改变
    public final static String EXTRA_DEVICE_ONLINE = "device_online"; //设备是否在线
    public final static String EXTRA_ENTER_FLAG = "enter_flag"; //页面进入标示
    public final static String EXTRA_SIDE = "side"; //左右边
    public final static String EXTRA_SUB_USER_ID = "sub_user_id";//子用户ID
    public final static String EXTRA_DEVICE_USER_NUM = "device_user_num";//设备用户数
    public final static String EXTRA_DEVICE_ID = "device_id";//设备ID
    public final static String EXTRA_DEVICE_TYPE = "device_tupe";//设备类型
    public final static String EXTRA_DEVICE_MARK = "device_mark";//设备别名
    public final static String EXTRA_DEVICE_LIST_CHANGE_FALG = "device_list_change_flag"; //设备列表是否改变
    public final static String EXTRA_IS_DOUBLE_DEVICE = "is_double_device";//是否双人设备
    public final static String EXTRA_FIRMWARE_VERSION = "firmware_version";//固件版本
    public final static String EXTRA_USER_ID = "user_id";//账号
    public final static String EXTRA_USER_PWD = "user_pwd";//密码
    public final static String EXTRA_IS_SHOW_TITLE_BAR = "is_show_title_bar";//是否显示标题栏
    public final static String EXTRA_IS_SHOW_BACK_BTN = "is_show_back_btn";//是否显示返回按钮

    /**
     * 以下全部对应设备属性值(Device Status)
     */
    public final static String ATTR_TIME_ZONE = "122";//时区

    /**
     * 服务器地址,以及接口详情
     */
    public final static String SERVICE_URL_CN = "https://api.mirahome.me/2.1/";
    public final static String SERVICE_URL_JP = "https://api.prod.mirahome.net/2.1/";//服务器地址 国服.me 日服.com
    //    public final static String SERVICE_URL = "https://api.mirahome.net/2.1/";//服务器地址 国服.me 日服.com http://api.tmp.mirahome.net/2.1/
//    public final static String SERVICE_HOST = "api.prod.mirahome.net";
//    public final static String SERVICE_HOST = "api.tmp.mirahome.net"; //国服域名
//    public final static String GET_TOKEN = SERVICE_URL + "app-key/get-token";//获取Token
    public final static String GET_TOKEN = "app-key/get-token";//获取Token
    public final static String CHUNYU_LOGIN_URL = "https://www.chunyuyisheng.com/cooperation/wap/login/?user_id=";//春雨医生登录地址
    public final static String HELP_CN_URL = "http://web.cn.mirahome.net/video-course/video-cn.html";
    public final static String HELP_US_URL = "http://web.cn.mirahome.net/video-course/video-us.html";
    public final static String BASE_BLE_FIRMWARE_URL = "http://p29zg9hvx.bkt.clouddn.com/";

    //用户接口
    public final static String LOGIN = "user/login";//登录
    public final static String LOGOUT = "user/logout";//退出登录
    public final static String REGISTER_MOBILE = "user/regist-mobile";//用户手机注册
    public final static String REGISTER_EMAIL = "user/regist-email";//用户邮箱注册
    public final static String REGISTER_WITH_MOBILE = "user/regist-email-with-code";//用户邮箱注册
    public final static String SEND_MOBILE_CODE = "user/send-mobile-code";//发送手机验证码
    public final static String SEND_EMAIL_CODE = "user/send-email-code";//发送邮箱验证码
    public final static String SEND_RESET_EMAIL = "user/send-reset-email";//发送重置密码邮件
    public final static String CHECK_MOBILE_CODE = "user/check-mobile-code"; //检查验证码是否正确
    public final static String CHECK_EMAIL_CODE = "user/check-email-code"; //检查验证码是否正确
    public final static String CHECK_EMAIL_EXISTS = "user/check-email-exists"; //检查邮箱是否存在
    public final static String CHECK_MOBILE_EXISTS = "user/check-mobile-exists"; //检查手机号是否存在
    public final static String CHANGE_PASSWORD_BY_MOBILE = "user/change-password-by-mobile-code";//验证码改密码
    public final static String CHANGE_PASSWORD_BY_EMAIL = "user/reset-password-by-email-for-app";//验证码改密码
    public final static String THIRD_PART_LOGIN = "user/third-part-login";//第三方登录
    public final static String UPDATE_LOCATION = "user/update-location"; //上传位置信息
    public final static String UPDATE_USER_EXTRA_COLUMN = "user/update-user-extra-column"; //更新用户额外的字段(language, timezone, longitude, latitude)
    public final static String GET_UPLOAD_TOKEN = "user/get-upload-token";//获取上传文件token
    public final static String GET_UPLOAD_FILE_TOKEN = "user/get-upload-file-token";//获取上传文件token(用于上次睡眠报告)
    public final static String USER_EDIT = "user/edit";//修改用户信息
    public final static String ADD_FEEDBACK = "user/add-feedback";//增加用户反馈

    //子用户接口
    public final static String SUB_USER_CREATE = "sub-user/create";
    public final static String SUB_USER_MODIFY = "sub-user/modify"; //修改子用户信息
    public final static String SUB_USER_GET_LIST = "sub-user/get-list-include-self";
    public final static String SUB_USER_MOVE_IN = "sub-user/move-in";//子用户加入
    public final static String SUB_USER_MOVE_OUT = "sub-user/move-out";//子用户移出
    public final static String GET_SUB_USER = "sub-user/get-list-include-self"; //获取包含自身的子用户列表

    //报表接口
    public final static String GET_DAILY = "report/daily";//获取日报
    public final static String GET_MONTHLY = "report/monthly";//获取月报
    public final static String GET_CHUNYU_TEXT = "report-daily/get-chunyu-text-by-report";//获取春雨文案
    public final static String SET_USER_REPORT_TYPE = "report/set-user-report-type";//设置用户报告类型
    public final static String CHECK_USER_SYNC_DATA = "report/check-user-sync-data";//判断用户及他的子用户的3天止鼾枕数据同步情况
    public final static String HANDLER_CLASSIC_DATA_FILE = "report/deal-classic-data-file";//处理七牛云文件
    public final static String UPDATE_SYNC_TIME = "report/update-sync-time";//更新止鼾枕同步报告时间
    public final static String CHECK_HANDLER_CLASSIC_DATA_FILE_STATUS = "report/check-deal-classic-data-file-status";//验证处理文件数据的状态
    public final static String GET_SUB_USER_SCORE = "report-daily/get-sub-user-score"; //获取用户和子用户列表的睡眠分数信息
    public final static String GET_DAILY_SUMMARY = "report-daily/summary";//日报概要
    public final static String GET_DAILY_RADAR_MAP = "report-daily/get-radar-map";//雷达图
    public final static String GET_DAILY_SLEEP_CYCLE = "report-daily/sleep-cycle";//睡眠周期图
    public final static String GET_DAILY_BODY_SIGN_HEART = "report-daily/body-sign-heartrate";//心跳详情
    public final static String GET_DAILY_BODY_SIGN_BREATH = "report-daily/body-sign-breathrate";//呼吸详情
    public final static String GET_DAILY_BODY_SIGN_MOVE = "report-daily/body-sign-body-move";//体动详情
    public final static String GET_DAILY_SNORE_CYCLE = "report-daily/get-snore-cycle";//打鼾图
    public final static String GET_WEEK_SUMMARY = "report-weekly/summary";//周报概要
    public final static String GET_WEEK_SLEEP_DURATION = "report-weekly/sleep-duration";//周报-睡眠时间分布
    public final static String GET_WEEK_SLEEP_CYCLE = "report-weekly/sleep-cycle";//周报-睡眠周期
    public final static String GET_WEEK_SNORE_CYCLE = "report-weekly/get-snore-cycle";//周报-打鼾
    public final static String GET_WEEK_HARTE_RATE = "report-weekly/heartrate";//周报-心率
    public final static String GET_WEEK_BREATH_RATE = "report-weekly/breathrate";//周报-呼吸频率
    public final static String GET_WEEK_BODY_MOVE = "report-weekly/body-move";//周报-体动
    public final static String GET_MONTH_SUMMARY = "report-monthly/summary";//月报概要
    public final static String GET_MONTH_SLEEP_DURATION = "report-monthly/sleep-duration";//月报睡眠时间分布
    public final static String GET_MONTH_SLEEP_CYCLE = "report-monthly/sleep-cycle";//月报-睡眠周期
    public final static String GET_MONTH_SNORE_CYCLE = "report-monthly/get-snore-cycle";//月报-打鼾
    public final static String GET_MONTH_HEART_RATE = "report-monthly/heartrate";//月报-心率
    public final static String GET_MONTH_BREATH_RATE = "report-monthly/breathrate";//月报-呼吸频率
    public final static String GET_MONTH_BODY_MOVE = "report-monthly/body-move";//月报-体动

    //设备操作接口
    public final static String DEVICE_BIND = "device/bind"; //绑定设备
    public final static String DEVICE_UNBIND = "device/unbind"; //解绑设备
    public final static String DEVICE_SET_SIDE = "device/set-side"; //设置睡毯左右边
    public final static String GET_USER_DEVICE = "device/get-user-device"; //设置睡毯左右边
    public final static String REMOVE_USER_FORM_SIDE = "device/remove-user-from-side"; //将用户移出睡毯
    public final static String EXCHANGE_SIDE = "device/exchange-side"; //切换睡毯左右边
    public final static String ADD_DEVICE_MARK = "device/add-device-mark"; //修改设备备注名
    public final static String GET_USER_DEVICE_MARK = "device/get-user-device-mark"; //获取用户的设备备注名
    public final static String GET_DOUBLE_DEVICE = "device/set-double-device"; //设置单人和双人设备
    public final static String GET_SINGLE_DEVICE_INFO = "device/get-single-device-info"; //获取单个设备信息
    public final static String UPLOAD_ANTISNORE_CLASSICE_DATA = "device/upload-antisnore-classic-data"; //上传止鼾枕原始数据
    public final static String DEVICE_FORCE_BIND = "device/force-unbind-and-bind"; //强制绑定设备
    public final static String GET_FORCE_DEVICE_UNBIND_NOTICE = "device/get-force-unbind-notice"; //强制绑定设备

    //文章接口
    public final static String ARTICLE_GET_LIST = "article/get-list"; //获取文章列表
    public final static String ARTICLE_COLLECT = "article/collect"; //收藏文章
    public final static String ARTICLE_UNCOLLECT = "article/uncollect"; //取消收藏
    public final static String ARTICLE_GET_COLLECTED_LIST = "article/get-list-collected"; //获取收藏文章列表
    public final static String ARTICLE_BASE_URL = "http://web.cn.mirahome.net/article/index.html"; //获取收藏文章详情

    //配置接口
    public final static String GET_PLATFORM_VERSION = "org-config/get-platform-version"; //
    public final static String GET_ANTI_SNORE_VERSION = "org-config/get-anti-snore-version"; //获取止鼾枕固件版本

    //关注接口
    public final static String GET_USER_FOLLOW_LIST = "follow/get-user-follow-list"; //获取用户关注列表和信息
    public final static String ADD_USER_FOLLOW = "follow/add-user-follow"; //添加用户关注
    public final static String CANCEL_USER_FOLLOW = "follow/cancel-user-follow"; //取消关注

    /**
     * 以下全部对应设备属性值( AidStatus)
     */

    public final static String ATTR_LIGHT_INTENSITY = "105"; //灯亮度 1~3
    public final static String ATTR_LIGHT_STATUS = "106"; //灯开关 0关 1开
    public final static String ATTR_LEFT_HEART_RATE = "109"; //左心率 0~255，0次~255次
    public final static String ATTR_RIGHT_HEART_RATE = "110"; //右心率 0~255，0次~255次
    public final static String ATTR_LEFT_BREATH = "111"; //左呼吸 0~255，0次~255次
    public final static String ATTR_RIGHT_BREATH = "112"; //右呼吸 0~255，0次~255次
    public final static String ATTR_LIFT_MOVE = "113"; //左体动 0静，1动
    public final static String ATTR_LEFT_BED_STATUS = "115"; //左边是否在床 0不在床 1在床
    public final static String ATTR_RIGHT_MOVE = "114"; //右体动 0静，1动
    public final static String ATTR_RIGHT_BED_STATUS = "116"; //右边是否在床 0不在床 1在床
    public final static String ATTR_DEVICE_USER_NUM = "124"; //床上的人数 1单人 2双人
    public final static String ATTR_DEVICE_TYPE = "120"; //设备的单双人类型 1单人 2双人

    /**
     * 数据库名
     */
    public final static String DATABASE_NAME = "mlily_database";

    /**
     * EventBus的MessageEvent
     */
    public final static String MESSAGE_CHANGE_DEVICE_INFO_NO_BACK = "message_change_device_info_no_back"; //改变设备的信息(包含用户和设备别名)(不退回上一级页面)
    public final static String MESSAGE_CHANGE_DEVICE_AND_USER_INFO_NO_BACK = "message_change_device_and_user_info_no_back"; //改变设备的信息和用户信息(不退回上一级页面)
    public final static String MESSAGE_CHANGE_USER_INFO_NO_BACK = "message_change_user_info_no_back"; //改变用户信息(不退回上一级页面)
    public final static String MESSAGE_CHANGE_DEVICE_INFO = "message_change_device_info"; //改变设备的信息(包含用户和设备别名)
    public final static String MESSAGE_CHANGE_DEVICE_AND_USER_INFO = "message_device_and_user_info"; //改变设备的信息和用户信息
    public final static String MESSAGE_CHANGE_DEVICE_INFO_AND_USE_DEVICE = "message_device_info_and_use_device"; //改变设备的信息和使用的设备
    public final static String MESSAGE_CHANGE_USE_DEVICE = "message_change_use_device"; //改变使用的设备
    public final static String MESSAGE_CHANGE_USER_INFO = "message_change_user_info"; //改变用户信息
    public final static String MESSAGE_DEVICE_UPDATE_SUCCEED = "message_device_update_succeed";// 设备升级成功
    public final static String MESSAGE_DEVICE_UPDATE_FAILED = "message_device_update_failed";// 设备升级失败
    public final static String MESSAGE_ADD_DEVICE = "message_add_device"; //添加设备
    public final static String MESSAGE_CHANGE_DEVICE_LIST = "message_change_device_list"; //改变设备列表
    public final static String MESSAGE_USER_INFO_UPDATE = "message_user_info_update"; //用户信息更新
    public final static String MESSAGE_ADD_USER_TO_DEVICE = "message_add_user_to_device"; //添加用户到设备
    public final static String MESSAGE_BACK = "message_back"; //退回上一页
    public final static String MESSAGE_SELECT_SUB_USER = "message_select_sub_user"; //选择子用户
    public final static String MESSAGE_DEVICE_STATUS_CHANGE = "message_device_status_change"; //设备在线状态改变
    public final static String MESSAGE_SHOW_NEWS_MARK = "message_show_news_mark"; //显示收到消息标志
    public final static String MESSAGE_REFRESH_SUB_USER_SCORE = "message_refresh_sub_user_score"; //刷新子用户分数
    public final static String MESSAGE_REFRESH_FINISH = "message_refresh_finish"; //刷新完成
    public final static String MESSAGE_REFRESH_DEVICE_LIST = "message_refresh_device_list"; //刷新设备列表
    public final static String MESSAGE_SHOW_ARTICLE = "message_show_article"; //显示文章
    public final static String MESSAGE_REFRESH_ARTICLE = "message_refresh_article"; //刷新文章列表
    public final static String MESSAGE_GO_TO_DEVICE_OPERATION = "message_go_to_device_operation"; //跳转到准备详情页
    public final static String MESSAGE_GO_TO_SCAN_QR = "message_go_to_scan_qr"; //跳转到扫码页
    public final static String MESSAGE_GET_DAY_SUMMARY = "message_get_day_summary"; //获取睡眠晨报
    public final static String MESSAGE_GET_DAY_REPORT = "message_get_day_report"; //获取睡眠日报
    public final static String MESSAGE_GET_DAY_REPORT_FROM_DAY_PAGE = "message_get_day_report_from_day_page"; //获取睡眠日报,在睡眠日报页面调用
    public final static String MESSAGE_GO_TO_CHUNYU_DOCEOR = "message_go_to_chunyu_doctor"; //跳转到春雨在线问诊页面
    public final static String MESSAGE_GO_TO_DEVICE_LIST = "message_go_to_device_list"; //跳转到设备列表页面
    public final static String MESSAGE_REPEAT_LOGIN = "message_repeat_login"; //重复登录
    public final static String MESSAGE_REFRESH_USER_CARD_LIST = "message_refresh_user_list"; //刷新用户卡片列表
    public final static String MESSAGE_REFRESH_AND_SELECT_USER_CARD_LIST = "message_refresh_and_select_user_list"; //刷新并选中用户卡片列表
    public final static String MESSAGE_BACK_TO_LOGIN = "message_back_to_login"; //退出到登录页面
    public final static String MESSAGE_SHOW_USER_DAY_REPORT = "message_show_user_day_report"; //显示用户的睡眠日报
    public final static String MESSAGE_SHOW_FORCE_LOGOUT_DIALOG = "message_show_force_logout_dialog"; //显示强制退出对话框

    /**
     * Intent相关
     */
    public final static int ADD_USER_REQUEST = 0x01;//跳转添加用户
    public final static int ADD_USER_RESULT = 0x02;//添加用户finish标识
    public final static int EDIT_USER_REQUEST = 0x03;// 跳转修改用户
    public final static int EDIT_USER_RESULT = 0x04;// 修改用户finish标识
    public final static int CAMERA_PHOTO = 0x5;//跳转照相机
    public final static int GALLERY_PHOTO = 0x6;//跳转相册
    public final static int CROP_PHOTO = 0x7;// 剪裁图片
    public final static int PLACE_USER_RESULT = 0x08;// 放置用户finish标识
    public final static int COLLECT_DELETE_RESULT = 0x09;// 删除收藏文章标识
    public final static int COLLECT_FROM_SLEEP_INFO = 0x10;// 从睡眠资讯页面跳转过来
    public final static int COLLECT_FROM_COLLECT_LIST = 0x11;// 从收藏列表页面跳转过来
    public final static int COLLECT_CHANGE_COLLECTED_RESULT = 0x12;// 收藏标示
    public final static int COLLECT_CHANGE_UNCOLLECT_RESULT = 0x13;// 取消收藏标示
    public final static int SELECT_DEVICE_REQUEST = 0x14;// 选择设备
    public final static int UPDATE_FIRMWARE_RESULT = 0x15;// 固件升级完成标示
    public final static int DEVICE_ENABLE_CHANGE_RESULT = 0x16;// 固件升级完成标示
    //    public final static int UNBIND_DEVICE_RESULT = 0x17;// 解绑固件标示
    public final static int BIND_DEVICE_RESULT = 0x17;// 绑定固件标示
    public final static int INIT_USER_REQUEST = 0x18;// 跳转初始化用户资料
    public final static int INIT_DEVICE_REQUEST = 0x19;// 跳转初始化选择设备
    public final static int CHUNYU_REQUEST = 0x20;// 跳转去春雨医生
    public final static int SELECT_SUB_USER_REQUEST = 0x21;// 选择子用户
    public final static int EDIT_COLLECT_REQUEST = 0x23;// 修改收藏列表
    public final static int SELECT_DEVICE_TYPE_REQUEST = 0x24;// 选择设备类型
    public final static int DEVICE_OPERATION_REQUEST = 0x25;// 操作设备
    public final static int CHANGE_USE_DEVICE_RESULT = 0x26;// 改变使用的设备
    public final static int CHANGE_DEVICE_INFO_RESULT = 0x27;// 改变设备的信息(包含用户和设备别名)
    public final static int CHANGE_DEVICE_AND_USER_INFO_RESULT = 0x28;// 改变设备的信息和用户信息
    public final static int CHANGE_USER_INFO_RESULT = 0x29;// 改变用户信息
    public final static int CHANGE_USE_DEVICE_NO_BACK_RESULT = 0x30;// 改变使用的设备(不退回上一级页面)
    public final static int CHANGE_DEVICE_INFO_NO_BACK_RESULT = 0x31;// 改变设备的信息(包含用户和设备别名)(不退回上一级页面)
    public final static int CHANGE_DEVICE_AND_USER_INFO_NO_BACK_RESULT = 0x32;// 改变设备的信息和用户信息(不退回上一级页面)
    public final static int CHANGE_USER_INFO_NO_BACK_RESULT = 0x33;// 改变用户信息(不退回上一级页面)
    public final static int CHANGE_DEVICE_LIST_RESULT = 0x34;// 改变设备列表
    public final static int BACK_RESULT = 0x35;// 返回上一页
    public final static int DEVICE_UPDATE_REQUEST = 0x36;// 设备升级页面
    public final static int USER_INFO_REQUEST = 0x37;// 用户详情
    public final static int MUSIC_REQUEST = 0x38;// 音乐播放页面
    public final static int ENABLE_BLUETOOTH_REQUEST = 0x39; //打开蓝牙
    public final static int DEVICE_LIST_REQUEST = 0x40; //设备列表
    public final static int ADD_DEVICE_RESULT = 0x41; //添加设备
    public final static int INIT_USER_RESULT = 0x42; //初始化用户
    public final static int ADD_DEVICE_REQUEST = 0x43; //添加设备
    public final static int FIRST_LOGIN = 0x44;// 用户一次登录
    //    public final static int SYNC_REPORT_REQUEST = 0x45; //同步报告
    public final static int REGISTER_REQUEST = 0x46; //注册
    public final static int ADD_USER_FOR_SETTING_REQUEST = 0x47;// 从设置页面跳转过来的添加用户操作

    /**
     * 设备类别
     */
    public final static int DEVICE_TYPE_WIFI = 1; //WiFi设备
    public final static int DEVICE_TYPE_BLE = 2; //蓝牙设备
    public final static int DEVICE_TYPE_WIFI_AND_BLE = 3; //即在蓝牙设备又在WiFi设备上


    /**
     * 观察者标示
     */
    public final static int OBSERVABLE_USER_LIST_CHANGE = 1;// 用户列表数据改变
    public final static int OBSERVABLE_DEVICE_USER_CHANGE = 2;// 设备用户数据改变
    public final static int OBSERVABLE_DEVICE_STATUS = 3;// 设备状态改变


    /**
     * 字符串标识
     */
    public final static String M_PROVIDER = "com.mlily.mh.provider";


}
