package truecolor.mdwb.logics.http;


import android.content.Entity;
import android.os.Bundle;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.ContentBody;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.util.MimeTypeUtils;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.RequestListener;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import truecolor.mdwb.global.WebServiceConfigure;
import truecolor.mdwb.model.CommentsResult;
import truecolor.mdwb.model.EmotionsResult;
import truecolor.mdwb.model.FavoritesResult;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.model.Oauth2AccessToken;
import truecolor.mdwb.model.RepostTimelineResult;
import truecolor.mdwb.model.UserInfoResult;
import truecolor.mdwb.utils.OauthToken;
import truecolor.webdataloader.HttpRequest;
import truecolor.webdataloader.WebDataLoader;
import truecolor.webdataloader.WebListener;

/**
 * Created by XuXiaowu on 15-2-21.
 *
 */
public class HttpService {

    String uid="8pPuH882KfTXHhH2GapNsjP4ECsYDQ44+8EhWDtd+FE=";
    String password="TO02iqzqeuAf0qo5jTwCA48Vz5eaphdvupFzCSy8rTmBKIYLgsd/xw==";
    static String oauth_token = "57ff8f7e0ddfbaf62fc490c03a742448";
    static String oauth_token_secret = "3be6044a032c8e667102f47193b3db23";



    /**
     * 登陆验证
     */
    public static void getAuthorize( RequestCallBack requestCallBack, String uid, String password){
        String url = OauthToken.getBaseUriAPI(WebServiceConfigure.MOD_OAUTH, WebServiceConfigure.ACT_AUTHORIZE);
        RequestParams requestParams = new RequestParams();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(WebServiceConfigure.HTTP_PARAM_UID, uid));
        params.add(new BasicNameValuePair(WebServiceConfigure.HTTP_PARAM_PASSWORD, password));
        try {
            requestParams.setBodyEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }catch (Exception e){
            e.printStackTrace();
        }

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(com.lidroid.xutils.http.client.HttpRequest.HttpMethod.POST, url, requestParams, requestCallBack);
    }

    /**
     * 获取用户信息
     */
    public static void getUser(Oauth2AccessToken accessToken, RequestListener listener){
        WeiboParameters params = new WeiboParameters();
        params.add(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken());
        params.add(WebServiceConfigure.HTTP_PARAM_UID, accessToken.getUid());
        AsyncWeiboRunner.request(WebServiceConfigure.HTTP_URL_GET_USER, params, WebServiceConfigure.HTTP_METHOD_GET, listener);
    }

    /**
     * 获取用户信息
     */
    public static void getUser(Oauth2AccessToken accessToken, WebListener listener){
        HttpRequest httpRequest = HttpRequest.createDefaultRequest(WebServiceConfigure.HTTP_URL_GET_USER)
                .addQuery(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken())
                .addQuery(WebServiceConfigure.HTTP_PARAM_UID, accessToken.getUid());
        WebDataLoader.loadWebData(httpRequest, UserInfoResult.class, listener, WebServiceConfigure.GET_USER, null);
    }

    /**
     * 获取当前登录用户及其所关注用户的最新微博
     */
    public static void getFriendsTimeline(Oauth2AccessToken accessToken, WebListener listener, int page, boolean isRefreshTag, boolean isRefreshData, String since_id){
        HttpRequest httpRequest = HttpRequest.createDefaultRequest(WebServiceConfigure.HTTP_URL_GET_FRIENDS_TIMELINE)
                .addQuery(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken())
                .addQuery(WebServiceConfigure.HTTP_PARAM_UID, accessToken.getUid())
                .addQuery(WebServiceConfigure.HTTP_PARAM_PAGE, page);
        if (since_id != null) {
            httpRequest.addQuery(WebServiceConfigure.HTTP_PARAM_SINCE_ID, since_id);
        }
        Bundle bundle = new Bundle(1);
        bundle.putBoolean(WebServiceConfigure.HTTP_GET_FRIENDS_TIMELINE_IS_REFRESH, isRefreshTag);
        if (isRefreshData){
            WebDataLoader.loadWebDataRefresh(httpRequest, FriendsTimelineResult.class, listener, WebServiceConfigure.GET_FRIENDS_TIMELINE, bundle);
        }else {
            WebDataLoader.loadWebData(httpRequest, FriendsTimelineResult.class, listener, WebServiceConfigure.GET_FRIENDS_TIMELINE, bundle);

        }
    }

    /**
     * 获取双向关注用户的最新微博
     */
    public static void getBiatusesTimeline(Oauth2AccessToken accessToken, WebListener listener, int page, boolean isRefreshTag, boolean isRefreshData, String since_id){
        HttpRequest httpRequest = HttpRequest.createDefaultRequest(WebServiceConfigure.HTTP_URL_GET_BILATERA_TIMELINE)
                .addQuery(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken())
                .addQuery(WebServiceConfigure.HTTP_PARAM_UID, accessToken.getUid())
                .addQuery(WebServiceConfigure.HTTP_PARAM_PAGE, page);
        if (since_id != null) {
            httpRequest.addQuery(WebServiceConfigure.HTTP_PARAM_SINCE_ID, since_id);
        }
        Bundle bundle = new Bundle(1);
        bundle.putBoolean(WebServiceConfigure.HTTP_GET_BILATERAL_TIMELINE_IS_REFRESH, isRefreshTag);
        if (isRefreshData){
            WebDataLoader.loadWebDataRefresh(httpRequest, FriendsTimelineResult.class, listener, WebServiceConfigure.GET_BILATERA_TIMELINE, bundle);
        }else {
            WebDataLoader.loadWebData(httpRequest, FriendsTimelineResult.class, listener, WebServiceConfigure.GET_BILATERA_TIMELINE, bundle);

        }
    }

    /**
     * 获取公共微博
     */
    public static void getPublicTimeline(Oauth2AccessToken accessToken, WebListener listener, int page, boolean isRefresh, boolean isGetCache){
        HttpRequest httpRequest = HttpRequest.createDefaultRequest(WebServiceConfigure.HTTP_URL_GET_PUBLIC_TIMELINE)
                .addQuery(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken())
                .addQuery(WebServiceConfigure.HTTP_PARAM_COUNT, WebServiceConfigure.HTTP_PARAM_DEFAULT_COUNT)
                .addQuery(WebServiceConfigure.HTTP_PARAM_PAGE, page);
        Bundle bundle = new Bundle(1);
        bundle.putBoolean(WebServiceConfigure.HTTP_GET_PUBLIC_TIMELINE_IS_REFRESH, isRefresh);
        if (isGetCache){
            WebDataLoader.loadWebData(httpRequest, FriendsTimelineResult.class, listener, WebServiceConfigure.GET_PUBLIC_TIMELINE, bundle);
        }else{
            WebDataLoader.loadWebDataRefresh(httpRequest, FriendsTimelineResult.class, listener, WebServiceConfigure.GET_PUBLIC_TIMELINE, bundle);
        }
    }

    /**
     * 点赞
     */
    public static void   createAttitude(Oauth2AccessToken accessToken, WebListener listener, String id){
        HttpRequest httpRequest = HttpRequest.createDefaultPostRequest("https://api.weibo.com/2/attitudes/create.json")
                .addMultiPart(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken())
                .addMultiPart("id", id);
        WebDataLoader.loadWebDataRefresh(httpRequest, null, listener, WebServiceConfigure.GET_PUBLIC_TIMELINE, null);
    }

    /**
     * 点赞
     */
//    public static void createAttitude( RequestCallBack requestCallBack, String id, Oauth2AccessToken accessToken){
//        String url = "https://api.weibo.com/2/attitudes/create.jso";
//        RequestParams requestParams = new RequestParams();
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken()));
//        params.add(new BasicNameValuePair("id", id));
//        try {
//            requestParams.setBodyEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        HttpUtils httpUtils = new HttpUtils();
//        httpUtils.send(com.lidroid.xutils.http.client.HttpRequest.HttpMethod.POST, url, requestParams, requestCallBack);
//    }


    /**
     * 获取微博评论列表
     */
    public static void getComments(Oauth2AccessToken accessToken, WebListener listener,String id, int page, String sinceId, boolean isRefresh){
        HttpRequest httpRequest = HttpRequest.createDefaultRequest(WebServiceConfigure.HTTP_URL_GET_COMMENTS)
                .addQuery(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken())
                .addQuery(WebServiceConfigure.HTTP_PARAM_WEIBO_ID, id)
                .addQuery(WebServiceConfigure.HTTP_PARAM_PAGE, page)
                .addQuery(WebServiceConfigure.HTTP_PARAM_COUNT, 10)
                .addQuery(WebServiceConfigure.HTTP_PARAM_SINCE_ID, sinceId);
        Bundle bundle = new Bundle(1);
        if (isRefresh){
            bundle.putBoolean(WebServiceConfigure.HTTP_GET_COMMENTS_IS_REFERSH, true);
            WebDataLoader.loadWebDataRefresh(httpRequest, CommentsResult.class, listener, WebServiceConfigure.GET_COMMENTS, bundle);
        }else {
            bundle.putBoolean(WebServiceConfigure.HTTP_GET_COMMENTS_IS_REFERSH, false);
            WebDataLoader.loadWebDataRefresh(httpRequest, CommentsResult.class, listener, WebServiceConfigure.GET_COMMENTS, bundle);
        }
    }

    /**
     * 获取微博转发列表
     */
    public static void getRepost(Oauth2AccessToken accessToken, WebListener listener,String id, int page, String sinceId, boolean isRefresh){
        HttpRequest httpRequest = HttpRequest.createDefaultRequest(WebServiceConfigure.HTTP_URL_GET_REPOST)
                .addQuery(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken())
                .addQuery(WebServiceConfigure.HTTP_PARAM_WEIBO_ID, id)
                .addQuery(WebServiceConfigure.HTTP_PARAM_PAGE, page)
                .addQuery(WebServiceConfigure.HTTP_PARAM_SINCE_ID, sinceId);
        Bundle bundle = new Bundle(1);
        if (isRefresh){
            bundle.putBoolean(WebServiceConfigure.HTTP_GET_REPOST_IS_REFERSH, true);
            WebDataLoader.loadWebDataRefresh(httpRequest, RepostTimelineResult.class, listener, WebServiceConfigure.GET_REPOST, bundle);
        }else {
            bundle.putBoolean(WebServiceConfigure.HTTP_GET_REPOST_IS_REFERSH, false);
            WebDataLoader.loadWebData(httpRequest, RepostTimelineResult.class, listener, WebServiceConfigure.GET_REPOST, bundle);
        }
    }

    /**
     * 发布一条新微薄
     */
    public static void updateStatuses(Oauth2AccessToken accessToken, WebListener listener,String status){

        try {
            HttpRequest httpRequest = HttpRequest.createDefaultPostRequest(WebServiceConfigure.HTTP_URL_UPDATE_STATUSES)
                    .addMultiPart(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken())
                    .addMultiPart(WebServiceConfigure.HTTP_PARAM_STATUES, URLEncoder.encode(status,"UTF-8"));
            WebDataLoader.loadWebData(httpRequest, RepostTimelineResult.class, listener, WebServiceConfigure.POST_STATUESE, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void updateStatuses( RequestCallBack requestCallBack, Oauth2AccessToken accessToken, String status, String imageFile){
        RequestParams requestParams = new RequestParams();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken()));
        params.add(new BasicNameValuePair(WebServiceConfigure.HTTP_PARAM_STATUES, status));
        try {
            requestParams.setBodyEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

        }catch (Exception e){
            e.printStackTrace();
        }
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(com.lidroid.xutils.http.client.HttpRequest.HttpMethod.POST, WebServiceConfigure.HTTP_URL_UPDATE_STATUSES,
                requestParams, requestCallBack);
    }

    /**
     * 获取当前登录用户的收藏列表
     */
    public static void getFavorites(Oauth2AccessToken accessToken, WebListener listener, boolean isGetCache, int page){
        HttpRequest httpRequest = HttpRequest.createDefaultRequest(WebServiceConfigure.HTTP_URL_GET_FAVORITES)
                .addQuery(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken())
                .addQuery(WebServiceConfigure.HTTP_PARAM_PAGE, page);
        Bundle bundle = new Bundle(1);
        if (isGetCache) {
            WebDataLoader.loadWebData(httpRequest, FavoritesResult.class, listener, WebServiceConfigure.GET_FAVORITES, bundle);
        }else {
            WebDataLoader.loadWebDataRefresh(httpRequest, FavoritesResult.class, listener, WebServiceConfigure.GET_FAVORITES, bundle);
        }
    }

    /**
     * 获取微博官方表情的详细信息
     */
    public static void getEmotions(Oauth2AccessToken accessToken, WebListener listener){
        HttpRequest httpRequest = HttpRequest.createDefaultRequest(WebServiceConfigure.HTTP_URL_GET_EMOTIONS)
                .addQuery(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken());
        WebDataLoader.loadWebData(httpRequest, EmotionsResult.class, listener, WebServiceConfigure.GET_EMOTIONS, null);
    }

//    /**
//     * 获取用户的关注列表
//     */
//    public static void getFriends(Oauth2AccessToken accessToken, WebListener listener){
//        HttpRequest httpRequest = HttpRequest.createDefaultRequest(WebServiceConfigure.HTTP_URL_GET_EMOTIONS)
//                .addQuery(WebServiceConfigure.HTTP_PARAM_ACCESS_TOKEN, accessToken.getAccessToken());
//        WebDataLoader.loadWebData(httpRequest, EmotionsResult.class, listener, WebServiceConfigure.GET_EMOTIONS, null);
//    }



}
