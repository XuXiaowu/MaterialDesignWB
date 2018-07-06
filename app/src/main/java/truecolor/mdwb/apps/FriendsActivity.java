package truecolor.mdwb.apps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;
import java.util.ArrayList;

import truecolor.mdwb.R;
import truecolor.mdwb.adapter.BaseRecycleAdapter;
import truecolor.mdwb.adapter.FriendsAdapter;
import truecolor.mdwb.api.FriendshipsAPI;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.model.FriendsResult;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.utils.PreferenceUtils;
import truecolor.mdwb.utils.Utils;

/**
 * Created by xiaowu on 15/11/23.
 */
public class FriendsActivity extends AppCompatActivity {

    private static final String FRIENDS_URL = "https://api.weibo.com/2/friendships/friends.json";
    private int mCursor;
    private String mPreferenceName;

    private BitmapUtils mBitmapUtils;
    private BitmapDisplayConfig mDisplayConfig;

    private ArrayList mDatas = new ArrayList();

    private RecyclerView mListView;
    private LinearLayoutManager mLayoutManager;
    private FriendsAdapter mFriendsAdapter;

    private static final int GET_FRIENDS_DATA_SUCCESS = 0;
    private static final int GET_FRIENDS_DATA_ERROR = 1;
    private static final int GET_MORE_FRIENDS_DATA_ERROR = 2;

    private boolean mIsLoadMoreData = true;
    private int mLastVisibleItem;

    private FriendshipsAPI mFriendshipsAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        initBitmapUtils();
        initView();
        initFriendshipsAPI();
        initAdapter();
        getFriendsData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListView.removeOnScrollListener(mListViewScrollListener);
    }

    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            PreferenceUtils.setStringPref(FriendsActivity.this, mPreferenceName, s);
            FriendsResult friendsResult = JSON.parseObject(s, FriendsResult.class);
            mCursor = friendsResult.next_cursor;
            for (int i = 0; i < friendsResult.users.length; i++) {
                FriendsTimelineResult.User user = friendsResult.users[i];
                mDatas.add(user);
            }
            mIsLoadMoreData = false;
            mHandler.sendEmptyMessage(GET_FRIENDS_DATA_SUCCESS);
        }

        @Override
        public void onIOException(IOException e) {
            Log.e("onIOException", e.getMessage());
            if (mDatas.size() == 0){
                mHandler.sendEmptyMessage(GET_FRIENDS_DATA_ERROR);
            }else {
                mHandler.sendEmptyMessage(GET_MORE_FRIENDS_DATA_ERROR);
            }
        }

        @Override
        public void onError(WeiboException e) {
            Log.e("onError", e.getMessage());
            if (mDatas.size() == 0){
                mHandler.sendEmptyMessage(GET_FRIENDS_DATA_ERROR);
            }else {
                mHandler.sendEmptyMessage(GET_MORE_FRIENDS_DATA_ERROR);
            }
        }
    };

    private RecyclerView.OnScrollListener mListViewScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mFriendsAdapter.getItemCount() && !mIsLoadMoreData && mCursor != 0) {
                mIsLoadMoreData = true;
                mFriendsAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE);
                getFriendsData();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        }
    };

    private View.OnClickListener mLoadingErrorViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFriendsData();
        }
    };

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String userName = (String)v.getTag();
            Intent intent = getIntent();
            intent.putExtra(Constant.EXTRA_FRIENDS_USER_NAME, userName);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_FRIENDS_DATA_SUCCESS:
                    mFriendsAdapter.setDataByNet(mDatas);
                    mFriendsAdapter.setState(BaseRecycleAdapter.STATE_NONE);
                    break;
                case GET_FRIENDS_DATA_ERROR:
                    mFriendsAdapter.setState(BaseRecycleAdapter.STATE_LOADING_ERROR);
                    break;
                case GET_MORE_FRIENDS_DATA_ERROR:
                    mFriendsAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE_ERROR);
                    break;


            }
        }
    };

    private void getFriendsData(){
        mPreferenceName = FRIENDS_URL + mCursor;
        String jsonStrCache = PreferenceUtils.getStringPref(this, mPreferenceName, "");
        if (jsonStrCache.equals("")) {
            mFriendshipsAPI.friends(Utils.accessToken.getUid(), mCursor, true, mRequestListener);
            if (mDatas.size() == 0){
                mFriendsAdapter.setState(BaseRecycleAdapter.STATE_LOADING);
            }else {
                mFriendsAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE);
            }
        }else {
            FriendsResult friendsResult = JSON.parseObject(jsonStrCache, FriendsResult.class);
            mCursor = friendsResult.next_cursor;
            for (int i = 0; i < friendsResult.users.length; i++) {
                FriendsTimelineResult.User user = friendsResult.users[i];
                mDatas.add(user);
            }
            mFriendsAdapter.setDataByNet(mDatas);
            mFriendsAdapter.setState(BaseRecycleAdapter.STATE_NONE);
            mIsLoadMoreData = false;
        }
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_white);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_friends);

        mListView = (RecyclerView) findViewById(R.id.list_view);
        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        mListView.addOnScrollListener(mListViewScrollListener);
    }

    private void initFriendshipsAPI(){
        mFriendshipsAPI = new FriendshipsAPI(Utils.getWeiboAccessToken());
    }

    private void initAdapter(){
        int loadinViewHeight = Utils.getScreenHeight()
                - getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)
                - getResources().getDimensionPixelSize(R.dimen.navigation_bar_height)
                - getResources().getDimensionPixelSize(R.dimen.status_bar_height);
        mFriendsAdapter = new FriendsAdapter(this, mDatas, mItemClickListener, mBitmapUtils, mDisplayConfig, loadinViewHeight);
        mFriendsAdapter.setLoadingErrorListener(mLoadingErrorViewClickListener);
        mFriendsAdapter.setLoadingMoreErrorListener(mLoadingErrorViewClickListener);
        mListView.setAdapter(mFriendsAdapter);
    }


    private void initBitmapUtils() {
        mBitmapUtils = new BitmapUtils(this);
        mDisplayConfig = new BitmapDisplayConfig();
        mDisplayConfig.setLoadingDrawable(this.getResources().getDrawable(R.mipmap.ic_user));
        Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        alphaAnimation.setFillAfter(true);
        mDisplayConfig.setAnimation(alphaAnimation);
    }


}
