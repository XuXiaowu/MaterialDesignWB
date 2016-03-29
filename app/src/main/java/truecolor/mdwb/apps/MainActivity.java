package truecolor.mdwb.apps;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import truecolor.imageloader.HttpImageDecoder;
import truecolor.imageloader.ImageLoader;
import truecolor.imageloader.ImageViewDisplayer;
import truecolor.mdwb.R;
import truecolor.mdwb.api.StatusesAPI;
import truecolor.mdwb.fragments.main.info.InfoFragment;
import truecolor.mdwb.fragments.main.status.FavoritesFragment;
import truecolor.mdwb.fragments.main.status.MainFragment;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.global.WebServiceConfigure;
import truecolor.mdwb.logics.http.HttpService;
import truecolor.mdwb.logics.publish.UploadService;
import truecolor.mdwb.model.EmotionsResult;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.model.UserInfoResult;
import truecolor.mdwb.utils.Utils;
import truecolor.mdwb.view.CircleImageView;
import truecolor.webdataloader.WebListener;


public class MainActivity extends ActionBarActivity {
    private String TAG = "MainActivity";

//    @ViewInject(R.id.drawer_view)
    private DrawerLayout mDrawerLayout;
    private FrameLayout mContentLayout;
//    @ViewInject(R.id.tab_view)
    private TabLayout mTabLayout;
//    @ViewInject(R.id.viewpager)
    private ViewPager mViewPager;
    private CircleImageView mHeadView;
    private TextView mUserNameView;
    private TextView mDescription;
//    public static FloatingActionButton sWhiteFab;
//    public static AppBarLayout sAppBarlayout;
    private FavoritesFragment mFavoritesFragment;
    private MainFragment mMainFragment;
    private InfoFragment mInfoFragment;
    private FragmentManager mFragmentManager;

    private DbUtils mDbUtils;

    public static FriendsTimelineResult.FriendsTimeline EXTRA_FRIENDS_TIMELINE;


//    private static final String[] tabTitles = new String[]{"关注微博", "互相关注", "公共微博"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mFragmentManager = getSupportFragmentManager();
        Utils.setDisplayResolution(getWindowManager());
        NavigationView navigationView = (NavigationView) findViewById(R.id.nv_sample_drawer_navigation);
        navigationView.setEnabled(true);

//        setupDrawerContent(navigationView);
        navigationView.setNavigationItemSelectedListener(mNavigationViewNavigationItemSelectedListener);

        HttpService.getUser(Utils.accessToken, mWebListener);

        android.support.v4.app.FragmentTransaction transaction = mFragmentManager.beginTransaction();
        mMainFragment = new MainFragment(MainActivity.this);
        transaction.add(R.id.main_content, mMainFragment);
        transaction.commit();

        UploadService.startService(this);
        getEmotion();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }else if(id == R.id.ab_search_user){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this , SearchActivity.class);
            intent.setType(Constant.GO_TO_SEARCH_USER);
            startActivity(intent);
        }else if (id == R.id.ab_search_statuses){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this , SearchActivity.class);
            intent.setType(Constant.GO_TO_SEARCH_STATUSES);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView(){
        mContentLayout = (FrameLayout) findViewById(R.id.main_content);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_view);
        mHeadView = (CircleImageView) findViewById(R.id.user_head_view);
        mUserNameView = (TextView) findViewById(R.id.user_name_view);
        mDescription = (TextView) findViewById(R.id.user_description);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            if (s != ""){
                UserInfoResult userInfoResult = JSON.parseObject(s, UserInfoResult.class);
                if (userInfoResult != null){
                    mUserNameView.setText(userInfoResult.name);
                    mDescription.setText(userInfoResult.description);
                    ImageLoader.loadImage(userInfoResult.avatar_large, HttpImageDecoder.getInstance(), ImageViewDisplayer.getInstance(),
                            mHeadView, R.mipmap.ic_user);
                }
            }
        }

        @Override
        public void onIOException(IOException e) {
            Log.e(TAG, e.getMessage());
        }

        @Override
        public void onError(WeiboException e) {
            Log.e(TAG, e.getMessage());
        }
    };

    private RequestListener mEmotionsRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            try {
//                db.deleteAll(EmotionsResult.class);
                List<EmotionsResult> lists = JSON.parseArray(s, EmotionsResult.class);
                List<EmotionsResult> emotionsResultList = mDbUtils.findAll(Selector.from(EmotionsResult.class));
                if (emotionsResultList == null || emotionsResultList.size() == 0){
                    List<EmotionsResult> list = JSON.parseArray(s, EmotionsResult.class);
                    Log.e("", list.size() + "");
                    for (int i = 0; i <= 112; i++) {
                        EmotionsResult er = list.get(i);
                        String[] strarray=er.getUrl().split("/");
                        String img_name = strarray[strarray.length - 1];
                        er.url = img_name;
                        if (er.url.equals("88_org.gif")){
                            er.url = "baibai_org.gif";
                        }
                        mDbUtils.save(er);
                    }
                }

                List<EmotionsResult> ls = mDbUtils.findAll(Selector.from(EmotionsResult.class));
                MaterialDesignWBApps.EMOTIONS = ls;
                Log.e("", ls.size() + "");

//                FileUtils.createFile("/storage/sdcard0/MDWB_EM/");
//                for (int i = 0; i < ls.size(); i++) {
//                    EmotionsResult er = ls.get(i);
//                    String[] strarray=er.getUrl().split("/");
//                    String img_name = strarray[strarray.length - 1];
//                    Log.e("MAIN", img_name);
//
//                    URL url =  new URL(er.getUrl());
//                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setReadTimeout(5 * 1000);
//                    if(conn.getResponseCode() == 200){
//                        InputStream is = conn.getInputStream();
//                        byte[] data = readStream(is);
//                        String paht = "/storage/sdcard0/MDWB_EM/" + img_name;
////                        FileUtils.createFile(paht);
//                        File file = new File(paht);
//                        FileOutputStream fs = new FileOutputStream(file);
//                        fs.write(data);
//                        fs.close();
//                    }else{
//                        System.out.println("请求失败");
//                    }
//                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onIOException(IOException e) {

        }

        @Override
        public void onError(WeiboException e) {

        }
    };

    public byte[] readStream(InputStream is) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len = 0;
        while((len = is.read(buffer)) != -1){
            os.write(buffer,0,len);
        }
        is.close();
        return os.toByteArray();
    }

    private WebListener mWebListener = new WebListener() {
        @Override
        public void onDataLoadFinished(int service, Bundle params, Object result) {
            if (service == WebServiceConfigure.GET_USER){
                if (result != null){
                    UserInfoResult userInfoResult = (UserInfoResult) result;
                    if (userInfoResult != null){
                        bingUserInfoData(userInfoResult);
                    }
                }
            }
        }
    };

    private NavigationView.OnNavigationItemSelectedListener mNavigationViewNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            int id = menuItem.getItemId();
            switch (id){
                case R.id.nav_home:
                    replaceMainFragment();
                    break;
                case R.id.nav_favorites:
                    replaceFavoritesFragment();
                    break;
                case R.id.nav_info:
                    replaceInfoFragment();
                    break;
            }
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            return true;
        }
    };

    public View.OnClickListener mGoodViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout goodView = (LinearLayout)v;
            TextView goodNumView = (TextView) goodView.getChildAt(1);
            goodNumView.setText(String.valueOf(Integer.parseInt(goodNumView.getText().toString()) + 1));
            Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.good_statue_scale_anim);
            animation.setInterpolator(new DecelerateInterpolator(1));
            v.startAnimation(animation);
        }
    };

    public View.OnClickListener mCommentViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType(Constant.PUBLISH_COMMENTS_CREATE);
            intent.putExtra(Constant.EXTRA_STATUESE_ID, v.getTag().toString());
            intent.setClass(MainActivity.this, PublishActivity.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener mRepostViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType(Constant.PUBLISH_STATUESE_REPOST);
            intent.putExtra(Constant.EXTRA_STATUESE_ID, v.getTag().toString());
            intent.setClass(MainActivity.this, PublishActivity.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener mCardViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FriendsTimelineResult.FriendsTimeline ft = (FriendsTimelineResult.FriendsTimeline)v.getTag();
            EXTRA_FRIENDS_TIMELINE = ft;
            Intent intent = new Intent();
            intent.putExtra(Constant.DETALI_FRIENDS_TIMELINE_TYPE, Constant.DETAIL_FRIENDS_TIMELINE_TYPE_MAIN);
            intent.setClass(MainActivity.this, DetailActivity.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener mImageGroupViewItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = v.getId();
            EXTRA_FRIENDS_TIMELINE = (FriendsTimelineResult.FriendsTimeline) v.getTag();
            Intent intent = new Intent();
            intent.setType(Constant.GO_TO_PHOTOS_BROWSE_TYPE_MIAN);
            intent.putExtra(Constant.EXTRA_IMAGER_GROUP_POS, pos);
            intent.setClass(MainActivity.this, PhotoBrowseActivity.class);
            startActivity(intent);
        }
    };

    /**
     * 绑定用户信息数据
     */
    private void bingUserInfoData(UserInfoResult userInfoResult){
        mUserNameView.setText(userInfoResult.name);
        mDescription.setText(userInfoResult.description);
        ImageLoader.loadImage(userInfoResult.avatar_large, HttpImageDecoder.getInstance(), ImageViewDisplayer.getInstance(),
                mHeadView, R.mipmap.ic_user);
    }

    private void replaceFavoritesFragment(){
        android.support.v4.app.FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mFavoritesFragment == null) {
            mFavoritesFragment = new FavoritesFragment(MainActivity.this);
            transaction.add(R.id.main_content, mFavoritesFragment);
        }
        transaction.show(mFavoritesFragment);
        transaction.hide(mMainFragment);
        if (mInfoFragment != null) transaction.hide(mInfoFragment);
        transaction.commit();
    }

    private void replaceMainFragment(){
        android.support.v4.app.FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        if (mMainFragment == null) {
//            mMainFragment = new MainFragment(MainActivity.this);
//            transaction.add(R.id.main_content, mMainFragment);
//        }
        if (mFavoritesFragment != null) transaction.hide(mFavoritesFragment);
         if (mInfoFragment != null) transaction.hide(mInfoFragment);
        transaction.show(mMainFragment);
        transaction.commit();
    }

    private void replaceInfoFragment(){
        android.support.v4.app.FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mInfoFragment == null) {
            mInfoFragment = new InfoFragment();
            transaction.add(R.id.main_content, mInfoFragment);
        }
        transaction.show(mInfoFragment);
        transaction.hide(mMainFragment);
        if (mFavoritesFragment != null) transaction.hide(mFavoritesFragment);
        transaction.commit();
    }

    public static FriendsTimelineResult.FriendsTimeline getExtraFriendsTimeline() {
        return EXTRA_FRIENDS_TIMELINE;
    }

    private void getEmotion(){
        try {
            mDbUtils = DbUtils.create(MainActivity.this);
            List<EmotionsResult> emotionsResultList = mDbUtils.findAll(Selector.from(EmotionsResult.class));
            if (emotionsResultList != null && emotionsResultList.size() != 0) {
                MaterialDesignWBApps.EMOTIONS = emotionsResultList;
            }else {
                StatusesAPI statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                statusesAPI.emotions(WeiboAPI.EMOTION_TYPE.FACE, WeiboAPI.LANGUAGE.cnname, mEmotionsRequestListener);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
