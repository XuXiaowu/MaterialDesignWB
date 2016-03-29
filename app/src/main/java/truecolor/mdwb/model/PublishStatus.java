package truecolor.mdwb.model;

import android.content.Context;
import android.view.View;

import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

import java.util.List;

import truecolor.mdwb.global.Constant;

/**
 * Created by xiaowu on 15/9/23.
 */
public class PublishStatus {

    public List<String> picPaths;
    public String status;
    public RequestListener requestListener;
    public View view;
    public Context context;
    public String id;
    public String cid;
    public int type;
    public WeiboAPI.COMMENTS_TYPE isComment;

}
