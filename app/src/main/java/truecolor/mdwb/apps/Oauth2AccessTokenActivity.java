package truecolor.mdwb.apps;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

import truecolor.mdwb.R;
import truecolor.mdwb.logics.SsoHandler;
import truecolor.mdwb.global.WebServiceConfigure;
import truecolor.mdwb.logics.AccessTokenKeeper;
import truecolor.mdwb.model.Oauth2AccessToken;
import truecolor.mdwb.utils.Utils;

public class Oauth2AccessTokenActivity extends ActionBarActivity {

    private Button mLoginButton;
    SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth2_access_token);

        mLoginButton = (Button) findViewById(R.id.login_btn);
        mLoginButton.setOnClickListener(mLoginButtonClickListener);

        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
        if (!accessToken.getAccessToken().equals("")) {
            Utils.accessToken = accessToken;
            Utils.setWeiboAccessToken(accessToken.getAccessToken(), accessToken.getRefreshToken(), accessToken.getExpiresTime());
            Utils.startActivity(Oauth2AccessTokenActivity.this, MainActivity.class);
        }
    }


    View.OnClickListener mLoginButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // test
//            Weibo weibo = Weibo.getInstance("2362431378", "http://boyqiang520.s8.csome.cn/oauth2/");
            Weibo weibo = Weibo.getInstance(WebServiceConfigure.CONSUMER_KEY, WebServiceConfigure.REDIRECT_URL); // me app

//            weibo.authorize(Oauth2AccessTokenActivity.this, mWeiboAuthListener);

            //sso验证  若未安装微博 则自动换到网页登陆
            mSsoHandler = new SsoHandler(Oauth2AccessTokenActivity.this, weibo);
            mSsoHandler.authorize(new AuthDialogListener());
        }
    };

    WeiboAuthListener mWeiboAuthListener = new WeiboAuthListener() {
        @Override
        public void onComplete(Bundle bundle) {
            String token = bundle.getString("access_token");
            String expires_in = bundle.getString("expires_in");
            String uid = bundle.getString("uid");
            Oauth2AccessToken oauth2AccessToken = new Oauth2AccessToken(token, Long.parseLong(expires_in), uid);
            AccessTokenKeeper.keepAccessToken(Oauth2AccessTokenActivity.this, oauth2AccessToken);
            Utils.accessToken = oauth2AccessToken;
            Utils.startActivity(Oauth2AccessTokenActivity.this, MainActivity.class);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(),
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(WeiboDialogError weiboDialogError) {
            Toast.makeText(getApplicationContext(),
                    "Auth error : " + weiboDialogError.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {

        }
    };

    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            String token = values.getString("access_token");
            String refresh_token = values.getString("refresh_token");
            String expires_in = values.getString("expires_in");
            String uid = values.getString("uid");
            Oauth2AccessToken oauth2AccessToken = new Oauth2AccessToken(token, Long.parseLong(expires_in), uid);
            AccessTokenKeeper.keepAccessToken(Oauth2AccessTokenActivity.this, oauth2AccessToken);
            Utils.accessToken = oauth2AccessToken;
            Utils.setWeiboAccessToken(token, refresh_token, Long.parseLong(expires_in));
            Utils.startActivity(Oauth2AccessTokenActivity.this, MainActivity.class);
        }

        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(getApplicationContext(),
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "Auth cancel",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(),
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * 仅当sdk支持sso时有效，
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


}
