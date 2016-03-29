package truecolor.mdwb.logics.publish;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.weibo.sdk.android.Oauth2AccessToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import truecolor.mdwb.R;
import truecolor.mdwb.api.MSSLSocketFactory;
import truecolor.mdwb.api.StatusesAPI;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.global.WebServiceConfigure;
import truecolor.mdwb.logics.http.Params;
import truecolor.mdwb.model.PublishStatus;
import truecolor.mdwb.model.UploadPicResult;
import truecolor.mdwb.utils.FileUtils;
import truecolor.mdwb.utils.NotificationUtils;
import truecolor.mdwb.utils.ParamsUtil;
import truecolor.mdwb.utils.SystemUtils;
import truecolor.mdwb.utils.Utils;

/**
 * Created by xiaowu on 15/9/23.
 */
public class UploadTask extends Thread{

    private static final String boundary = "4a5b6c7d8e9f";
    private PublishStatus mPublishStatus;
    private List<String> mPicIds;

    private static final int UPLOAD_FIAL = 1;
    private static String TAG = "UploadTask";


    public UploadTask(PublishStatus mPublishStatus){
        this.mPicIds = new ArrayList<>();
        this.mPublishStatus = mPublishStatus;
    }

    @Override
    public void run() {

        StatusesAPI statusesAPI;
        switch (mPublishStatus.type){
            case Constant.PUBLISH_STATUS_TYPE_COMMENT_CREATE:
                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                statusesAPI.commentsCreate(mPublishStatus.status, mPublishStatus.id, mPublishStatus.requestListener);
                break;

            case Constant.PUBLISH_STATUS_TYPE_COMMENT_REPLY:
                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                statusesAPI.commentsReply(mPublishStatus.status, mPublishStatus.id, mPublishStatus.cid, mPublishStatus.requestListener);
                break;

            case Constant.PUBLISH_STATUS_TYPE_REPOST:
                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                statusesAPI.repost(Long.parseLong(mPublishStatus.id), mPublishStatus.status, mPublishStatus.isComment, mPublishStatus.requestListener);
                break;

            case Constant.PUBLISH_STATUS_TYPE_NEW:
                if (mPublishStatus.picPaths.size() > 1){
                    for (int i = 0; i < mPublishStatus.picPaths.size(); i++) {
                        File picFile = new File(mPublishStatus.picPaths.get(i));
                        FileUtils.getUploadFile(picFile, mPublishStatus.context);
                        uploadFile(picFile);
                        Log.v(TAG, "成功上传一直图片--"+ mPublishStatus.picPaths.get(i));
                    }
                    statusesAPI = new StatusesAPI(getToken());
                    statusesAPI.uploadUrlText(mPublishStatus.status, "", getPicId(), "0", "0.0", mPublishStatus.requestListener);
                }else if (mPublishStatus.picPaths.size() == 1){
                    statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                    File picFile = new File(mPublishStatus.picPaths.get(0));
                    FileUtils.getUploadFile(picFile, mPublishStatus.context);
                    statusesAPI.upload(mPublishStatus.status, picFile.getPath(), "", "", mPublishStatus.requestListener);
                } else {
                    statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                    statusesAPI.update(mPublishStatus.status, "0.0", "0.0", mPublishStatus.requestListener);
                }
                break;
        }

//        if (mPublishStatus.comment != null){
//            StatusesAPI statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//            statusesAPI.commentsCreate(mPublishStatus.comment, mPublishStatus.id, mPublishStatus.requestListener);
//            return;
//        }

//        if (mPublishStatus.picPaths.size() > 1){
//            for (int i = 0; i < mPublishStatus.picPaths.size(); i++) {
//                File picFile = new File(mPublishStatus.picPaths.get(i));
//                FileUtils.getUploadFile(picFile, mPublishStatus.context);
//                uploadFile(picFile);
//                Log.v(TAG, "成功上传一直图片--"+ mPublishStatus.picPaths.get(i));
//            }
//            StatusesAPI statusesAPI = new StatusesAPI(getToken());
//            statusesAPI.uploadUrlText(mPublishStatus.status, "", getPicId(), "0", "0.0", mPublishStatus.requestListener);
//        }else if (mPublishStatus.picPaths.size() == 1){
//            StatusesAPI statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
////            File picFile = new File(mPublishStatus.picPaths.get(0));
////            FileUtils.getUploadFile(picFile, mPublishStatus.context);
//            statusesAPI.upload(mPublishStatus.status, mPublishStatus.picPaths.get(0), "", "", mPublishStatus.requestListener);
//        } else {
//            StatusesAPI statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//            statusesAPI.update(mPublishStatus.status, "0.0", "0.0", mPublishStatus.requestListener);
//        }



        Log.e("","upload_____OV");
    }

    public void uploadFile(File file) {

        int soTimeout = 3 * 60 * 1000;// 上传文件连接时间改长一点
        int connectTimeout = 3 * 60 * 1000;

        Params params = new Params();
        configParams(params);

        HttpClient client = null;
        try {
            try {
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);

                // 如果不设置这里，会报no peer certificate错误
                SSLSocketFactory sf = new MSSLSocketFactory(keyStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("https", sf, 443));
                schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                HttpParams httpParams = new BasicHttpParams();
                HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
                SingleClientConnManager clientManager = new SingleClientConnManager(httpParams, schemeRegistry);
                BasicHttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, connectTimeout);
                HttpConnectionParams.setSoTimeout(httpParameters, soTimeout);
                client = new DefaultHttpClient(clientManager, httpParameters);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 添加代理
            HttpHost proxy = SystemUtils.getProxy();
            if (proxy != null)
                client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);


            HttpPost request = new HttpPost(WebServiceConfigure.HTTP_URL_UPLOAD_PIC);

            request.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            StringBuffer buffer = new StringBuffer();

            for (String key : params.getKeys()) {
                buffer.append("--").append(boundary);
                buffer.append("\r\n");
                buffer.append("Content-Disposition: form-data; name=" + "\"" + key + "\"");
                buffer.append("\r\n\r\n");// 每一行必须以\r\n结尾,包括最后一行
                buffer.append(ParamsUtil.encode(params.getParameter(key)));
                buffer.append("\r\n");
            }

            String imageKey = "pic";
            if (params.containsKey("imageKey")) {
                imageKey = params.getParameter("imageKey");
            }

            buffer.append("--" + boundary);
            buffer.append("\r\n");
            buffer.append("Content-Disposition: form-data; name=").append("\"").append(imageKey).append("\"").append(";").append("filename=")
                    .append("\"").append("lovesong.jpg").append("\"");
            buffer.append("Content-Type: " + "image/jpge");
            buffer.append("\r\n\r\n");
            out.write(buffer.toString().getBytes());
            // Logger.d(TAG, "form-data = " + buffer.toString());
            out.write(FileUtils.readFileToBytes(file));
            out.write(("\r\n--" + boundary + "--\r\n").getBytes());

            ByteArrayEntity entity = new ByteArrayEntity(out.toByteArray());
            request.setEntity(entity);
            try {
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            request.setHeader("Authorization", params.getParameter("access_token"));
            HttpResponse httpResponse = client.execute(request);
            String responseStr = readResponse(httpResponse);

            UploadPicResult picResult = JSON.parseObject(responseStr, UploadPicResult.class);
            mPicIds.add(picResult.pic_id);

            Log.v("", String.format("upload file's response body = %s", responseStr));

        } catch (SocketTimeoutException e) {
            mHandler.sendEmptyMessage(UPLOAD_FIAL);
            e.printStackTrace();
            if (client != null)
                client.getConnectionManager().shutdown();
        } catch (ConnectTimeoutException e) {
            mHandler.sendEmptyMessage(UPLOAD_FIAL);
            e.printStackTrace();
            if (client != null)
                client.getConnectionManager().shutdown();
        } catch (ClientProtocolException e) {
            mHandler.sendEmptyMessage(UPLOAD_FIAL);
            e.printStackTrace();
        } catch (UnknownHostException e) {
            mHandler.sendEmptyMessage(UPLOAD_FIAL);
            e.printStackTrace();
        } catch (IOException e) {
            mHandler.sendEmptyMessage(UPLOAD_FIAL);
            e.printStackTrace();
        }
    }

    private static String readResponse(HttpResponse response) throws IllegalStateException, IOException {
        String result = "";
        HttpEntity entity = response.getEntity();
        InputStream inputStream;

        inputStream = entity.getContent();
        ByteArrayOutputStream content = new ByteArrayOutputStream();

        int len = 0;
        byte[] sBuffer = new byte[256 * 1024];
        while ((len = inputStream.read(sBuffer)) != -1) {
            content.write(sBuffer, 0, len);
        }
        result = new String(content.toByteArray());

        content.close();

        Log.v("", result);
        return result;
    }

    private Params configParams(Params params) {
        if (params == null) {
            params = new Params();
        }

        if (!params.containsKey("source"))
            params.addParameter("source", WebServiceConfigure.AISEN_KEY);
        if (!params.containsKey("access_token"))
            params.addParameter("access_token", WebServiceConfigure.WEICO_TOKEN); //如果微博发送失败 token过期就更新WEICO_TOKEN

        return params;
    }

    private Oauth2AccessToken getToken(){
        Oauth2AccessToken token = new Oauth2AccessToken();
        token.setToken(WebServiceConfigure.WEICO_TOKEN);
        return token;
    }

    private String getPicId(){
        String picId = "";
        for (int i = 0; i < mPicIds.size(); i++) {
            picId = picId + mPicIds.get(i) + ",";
        }
        return picId;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPLOAD_FIAL:
                    Snackbar.make(mPublishStatus.view, mPublishStatus.context.getResources().
                            getString(R.string.publish_new_status_fail), Snackbar.LENGTH_LONG).show();
                    NotificationUtils.showPuclishStatusNotification(mPublishStatus.context, Constant.PUBLISH_STATUS_NOTIFICATION_FAIL);
                    return;
            }
        }
    };



}
