package truecolor.mdwb.apps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import truecolor.mdwb.R;
import truecolor.mdwb.adapter.BaseRecycleAdapter;
import truecolor.mdwb.adapter.SearchUsersAdapter;
import truecolor.mdwb.adapter.StatusAdapter;
import truecolor.mdwb.api.SearchAPI;
import truecolor.mdwb.api.StatusesAPI;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.global.WebServiceConfigure;
import truecolor.mdwb.model.DetailMessage;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.model.UsersResult;
import truecolor.mdwb.utils.Utils;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView mListView;
    private EditText mEditView;

    private String mFromSearchType;
    private SearchAPI mSearchAPI;
    private SearchUsersAdapter mSearchUsersAdapter;
    private StatusAdapter mSearchStatusAdapter;
    private ArrayList<Object> mData;
    private LinearLayoutManager mLayoutManager;

    private int mPage = 1;
    private boolean mIsLoadMoreData = true;
    private int mLastVisibleItem;
    private String mSerachKey = "";
    private boolean mIsRefreshData;
    private int mRemovedStatusesPos;

    public static FriendsTimelineResult.FriendsTimeline EXTRA_FRIENDS_TIMELINE;

    private static final int SEARCH_USERS_SUCCESS = 1;
    private static final int SEARCH_USERS_ERROR = 2;
    private static final int SEARCH_USERS_MORE_ERROR = 3;
    private static final int SEARCH_USERS_EMPTY = 4;
    private static final int SEARCH_STATUS_SUCCESS = 5;
    private static final int SEARCH_STATUS_ERROR = 6;
    private static final int SEARCH_STATUS_EMPTY = 7;
    private static final int SEARCH_STATUS_MORE_ERROR = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mData = new ArrayList<>();
        getFromSearchType();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListView.removeOnScrollListener(mListViewScrollListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    private RequestListener mSearchRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            if(mFromSearchType.equals(Constant.GO_TO_SEARCH_USER)) {
                List<UsersResult> list = new ArrayList(JSONArray.parseArray(s, UsersResult.class));
                if(list != null){
                    if (list.size() == 0) {
                        mData.clear();
                        mHandler.sendEmptyMessage(SEARCH_USERS_EMPTY);
                        return;
                    }
                    mData.addAll(list);
                    mHandler.sendEmptyMessage(SEARCH_USERS_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(SEARCH_USERS_ERROR);
                }

            } else if (mFromSearchType.equals(Constant.GO_TO_SEARCH_STATUSES)){
                FriendsTimelineResult friendsTimelineResult = JSON.parseObject(s, FriendsTimelineResult.class);
                if (friendsTimelineResult != null){
                    List friendsTimelineList = Arrays.asList(friendsTimelineResult.statuses);
                    if (mIsRefreshData) {
                        mData.clear();
                        mIsRefreshData = false;
                        if(friendsTimelineList.size() == 0){
                            mHandler.sendEmptyMessage(SEARCH_STATUS_EMPTY);
                            return;
                        }
                    }
                    mData.addAll(friendsTimelineList);
                    if (mData.size() > 0 && friendsTimelineResult.total_number - mData.size() > 0){
                        mIsLoadMoreData = false;
                    }
                    mHandler.sendEmptyMessage(SEARCH_STATUS_SUCCESS);
                }else {
                    if (mPage > 1){
                        mHandler.sendEmptyMessage(SEARCH_STATUS_MORE_ERROR);
                    }else {
                        mData.clear();
                        mHandler.sendEmptyMessage(SEARCH_STATUS_ERROR);
                    }
                }
            }

        }

        @Override
        public void onIOException(IOException e) {
            mHandler.sendEmptyMessage(SEARCH_USERS_ERROR);
        }

        @Override
        public void onError(WeiboException e) {
            if (mFromSearchType.equals(Constant.GO_TO_SEARCH_USER)) {
                mHandler.sendEmptyMessage(SEARCH_USERS_ERROR);
            }else if (mFromSearchType.equals(Constant.GO_TO_SEARCH_STATUSES)){
                mHandler.sendEmptyMessage(SEARCH_STATUS_ERROR);
            }
        }
    };

    private RequestListener mFavoritesCreateRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            mHandler.sendEmptyMessage(Constant.FAVORITES_CREATE_SUCCESS);
        }

        @Override
        public void onIOException(IOException e) {
            mHandler.sendEmptyMessage(Constant.FAVORITES_CREATE_FAIL);
        }

        @Override
        public void onError(WeiboException e) {

            DetailMessage detailMessage = JSON.parseObject(e.getMessage(), DetailMessage.class);
            if (detailMessage.error_code.equals("20704")){
                mHandler.sendEmptyMessage(Constant.FAVORITES_CREATE_FAIL_HAVA_COLLECTED);
            }else {
                mHandler.sendEmptyMessage(Constant.FAVORITES_CREATE_FAIL);
            }
        }
    };
    private RequestListener mFavoritesDestroyRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            mHandler.sendEmptyMessage(Constant.FAVORITES_DESTROY_SUCCESS);
        }

        @Override
        public void onIOException(IOException e) {
            mHandler.sendEmptyMessage(Constant.FAVORITES_DESTROY_FAIL);
        }

        @Override
        public void onError(WeiboException e) {
            DetailMessage detailMessage = JSON.parseObject(e.getMessage(), DetailMessage.class);
            if (detailMessage.error_code.equals("20705")) {
                mHandler.sendEmptyMessage(Constant.FAVORITES_DESTROY_FAIL_NO_COLLECTED);
            } else {
                mHandler.sendEmptyMessage(Constant.FAVORITES_DESTROY_FAIL);
            }
        }

    };
    private RequestListener mStatusesDestroyRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            mHandler.sendEmptyMessage(Constant.STATUSES_DESTROY_SUCCESS);
        }

        @Override
        public void onIOException(IOException e) {
            mHandler.sendEmptyMessage(Constant.STATUSES_DESTROY_FAIL);
        }

        @Override
        public void onError(WeiboException e) {
            mHandler.sendEmptyMessage(Constant.STATUSES_DESTROY_FAIL);
        }
    };

    private View.OnClickListener mUsersListItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String id = (String) v.getTag();
            Log.e(">>>", id + "");
        }
    };

    private View.OnClickListener mLoadingUsersErrorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSearchUsersAdapter.setState(BaseRecycleAdapter.STATE_LOADING);
            mSearchAPI.users(mSerachKey, 200, mSearchRequestListener);
        }
    };

    private View.OnClickListener mLoadingStatusMoreErrorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSearchStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE);
            mSearchAPI.topics(mSerachKey, 20, mPage, mSearchRequestListener);
        }
    };

    private View.OnClickListener mLoadingStatusErrorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSearchStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING);
            mSearchAPI.topics(mSerachKey, 20, mPage, mSearchRequestListener);
        }
    };

    public View.OnClickListener mGoodViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout goodView = (LinearLayout)v;
            TextView goodNumView = (TextView) goodView.getChildAt(1);
            goodNumView.setText(String.valueOf(Integer.parseInt(goodNumView.getText().toString()) + 1));
            Animation animation = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.good_statue_scale_anim);
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
            intent.setClass(SearchActivity.this, PublishActivity.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener mRepostViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType(Constant.PUBLISH_STATUESE_REPOST);
            intent.putExtra(Constant.EXTRA_STATUESE_ID, v.getTag().toString());
            intent.setClass(SearchActivity.this, PublishActivity.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener mCardViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FriendsTimelineResult.FriendsTimeline ft = (FriendsTimelineResult.FriendsTimeline)v.getTag();
            EXTRA_FRIENDS_TIMELINE = ft;
            Intent intent = new Intent();
            intent.putExtra(Constant.DETALI_FRIENDS_TIMELINE_TYPE, Constant.DETAIL_FRIENDS_TIMELINE_TYPE_SEARCH);
            intent.setClass(SearchActivity.this, DetailActivity.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener mImageGroupViewItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = v.getId();
            EXTRA_FRIENDS_TIMELINE = (FriendsTimelineResult.FriendsTimeline) v.getTag();
            Intent intent = new Intent();
            intent.setType(Constant.GO_TO_PHOTOS_BROWSE_TYPE_SEARCH);
            intent.putExtra(Constant.EXTRA_IMAGER_GROUP_POS, pos);
            intent.setClass(SearchActivity.this, PhotoBrowseActivity.class);
            startActivity(intent);
        }
    };

    private View.OnLongClickListener mCardViewLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            doMoreMenuAction(v);
            return true;
        }
    };

    private View.OnClickListener mMenuMoreViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doMoreMenuAction(v);
        }
    };


    private RecyclerView.OnScrollListener mListViewScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mSearchStatusAdapter.getItemCount() && !mIsLoadMoreData) {
                mIsLoadMoreData = true;
                mPage++;
                mSearchStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE);
                mSearchAPI.topics(mSerachKey, 20, mPage, mSearchRequestListener);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        }
    };


    View.OnKeyListener onKey=new View.OnKeyListener() {

        @Override

        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if(keyCode == KeyEvent.KEYCODE_ENTER){
                String serachKey = mEditView.getText().toString();
                if (serachKey.equals("")){
                    Snackbar.make(mListView, getResources().getString(R.string.sreach_key_not_empty),
                            Snackbar.LENGTH_LONG).show();
                }else {
                    if (!mSerachKey.equals(serachKey)) {
                        mSerachKey = serachKey;
                        mIsRefreshData = true;
                        mData.clear();
                        mPage = 1;
                        if (mFromSearchType.equals(Constant.GO_TO_SEARCH_USER)) {
                            mSearchAPI.users(serachKey, 200, mSearchRequestListener);
                            mSearchUsersAdapter.setDataByNet(mData);
                            mSearchUsersAdapter.setState(BaseRecycleAdapter.STATE_LOADING);
                        } else if (mFromSearchType.equals(Constant.GO_TO_SEARCH_STATUSES)) {
                            mSearchAPI.topics(serachKey, 20, mPage, mSearchRequestListener);
                            mSearchStatusAdapter.setDataByNet(mData);
                            mSearchStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING);
                        }

                        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(imm.isActive()){
                            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );
                        }
                    }
                }
                return true;
            }
            return false;
        }

    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_USERS_SUCCESS:
                    mSearchUsersAdapter.setDataByNet(mData);
                    mSearchUsersAdapter.setState(BaseRecycleAdapter.STATE_NONE);
                    break;
                case SEARCH_USERS_ERROR:
                    mSearchUsersAdapter.setDataByNet(mData);
                    mSearchUsersAdapter.setState(BaseRecycleAdapter.STATE_LOADING_ERROR);
                    break;
                case SEARCH_USERS_EMPTY:
                    mSearchUsersAdapter.setDataByNet(mData);
                    mSearchUsersAdapter.setEmptyText(R.string.empty_data);
                    mSearchUsersAdapter.setState(BaseRecycleAdapter.STATE_EMPTY);
                    break;
                case SEARCH_STATUS_SUCCESS:
                    mSearchStatusAdapter.setDataByNet(mData);
                    mSearchStatusAdapter.setState(BaseRecycleAdapter.STATE_NONE);
                    break;
                case SEARCH_STATUS_ERROR:
                    mSearchStatusAdapter.setDataByNet(mData);
                    mSearchStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_ERROR);
                    break;
                case SEARCH_STATUS_MORE_ERROR:
                    mSearchStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE_ERROR);
                    break;
                case SEARCH_STATUS_EMPTY:
                    mSearchStatusAdapter.setEmptyText(R.string.empty_data);
                    mSearchStatusAdapter.setDataByNet(mData);
                    break;
                case Constant.FAVORITES_CREATE_SUCCESS:
                    Snackbar.make(mListView, getResources().getString(R.string.favorites_create_success),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case Constant.FAVORITES_CREATE_FAIL:
                    Snackbar.make(mListView, getResources().getString(R.string.favorites_create_fail),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case Constant.FAVORITES_CREATE_FAIL_HAVA_COLLECTED:
                    Snackbar.make(mListView, getResources().getString(R.string.have_collected),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case Constant.FAVORITES_DESTROY_SUCCESS:
                    Snackbar.make(mListView, getResources().getString(R.string.favorites_destroy_success),
                            Snackbar.LENGTH_LONG).show();
                    return;
                case Constant.FAVORITES_DESTROY_FAIL:
                    Snackbar.make(mListView, getResources().getString(R.string.favorites_destroy_fail),
                            Snackbar.LENGTH_LONG).show();
                    return;
                case Constant.FAVORITES_DESTROY_FAIL_NO_COLLECTED:
                    Snackbar.make(mListView, getResources().getString(R.string.no_collected),
                            Snackbar.LENGTH_LONG).show();
                    return;
                case Constant.STATUSES_DESTROY_SUCCESS:
                    mData.remove(mRemovedStatusesPos);
                    mSearchStatusAdapter.notifyItemRemoved(mRemovedStatusesPos);
                    return;
                case Constant.STATUSES_DESTROY_FAIL:
                    Snackbar.make(mListView, getResources().getString(R.string.statuses_destroy_fail),
                            Snackbar.LENGTH_LONG).show();
                    return;

            }
        }
    };

    private void initView(){
        mLayoutManager = new LinearLayoutManager(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_white);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mListView = (RecyclerView) findViewById(R.id.list_view);
        mEditView = (EditText) findViewById(R.id.edit_serach_view);
        mListView.setLayoutManager(mLayoutManager);
        mEditView.setOnKeyListener(onKey);

        int loadinViewHeight = Utils.getScreenHeight()
                - getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)
                - getResources().getDimensionPixelSize(R.dimen.search_button_height)
                - getResources().getDimensionPixelSize(R.dimen.medium_margin)
                - getResources().getDimensionPixelSize(R.dimen.navigation_bar_height)
                - getResources().getDimensionPixelSize(R.dimen.status_bar_height);

        if (mFromSearchType.equals(Constant.GO_TO_SEARCH_USER)) {
            actionBar.setTitle(getResources().getString(R.string.search_user));
            mSearchUsersAdapter = new SearchUsersAdapter(this, mData, mUsersListItemClickListener, loadinViewHeight);
            mListView.setAdapter(mSearchUsersAdapter);
            mSearchUsersAdapter.setLoadingErrorListener(mLoadingUsersErrorClickListener);
            mSearchUsersAdapter.setEmptyText(getResources().getString(R.string.sreach_user_hint));
            mSearchUsersAdapter.setState(BaseRecycleAdapter.STATE_EMPTY);
            mSearchAPI = new SearchAPI(Utils.getWeiboAccessToken());
        }else if (mFromSearchType.equals(Constant.GO_TO_SEARCH_STATUSES)){
            actionBar.setTitle(getResources().getString(R.string.search_statuses));
            mEditView.setHint(getResources().getString(R.string.sreach_status_hint));
            mSearchStatusAdapter = new StatusAdapter(this, mData, loadinViewHeight);
            mListView.setAdapter(mSearchStatusAdapter);
            mListView.addOnScrollListener(mListViewScrollListener);
            mSearchStatusAdapter.setLoadingMoreErrorListener(mLoadingStatusMoreErrorClickListener);
            mSearchStatusAdapter.setLoadingErrorListener(mLoadingStatusErrorClickListener);

            mSearchStatusAdapter.setGoodViewClickListener(mGoodViewClickListener);
            mSearchStatusAdapter.setRepostViewClickListener(mRepostViewClickListener);
            mSearchStatusAdapter.setCommentViewClickListener(mCommentViewClickListener);
            mSearchStatusAdapter.setCardViewClickListener(mCardViewClickListener);
            mSearchStatusAdapter.setCardViewLongClickListener(mCardViewLongClickListener);
            mSearchStatusAdapter.setMenuMoreViewClickListener(mMenuMoreViewClickListener);
            mSearchStatusAdapter.setImageGroupViewItemClickListener(mImageGroupViewItemClickListener);

            mSearchStatusAdapter.setEmptyText("");
            mSearchStatusAdapter.setState(BaseRecycleAdapter.STATE_EMPTY);

            Oauth2AccessToken token = new Oauth2AccessToken();
            token.setToken(WebServiceConfigure.WEICO_TOKEN);
            mSearchAPI = new SearchAPI(token);
        }
    }

    private void getFromSearchType(){
        mFromSearchType = getIntent().getType();
        if (mFromSearchType.equals(null)) finish();
    }

    private void doMoreMenuAction(View v){
        final FriendsTimelineResult.FriendsTimeline ftl = (FriendsTimelineResult.FriendsTimeline) v.getTag();
        final boolean isCurrentUser = ftl.user.idstr.equals(Utils.accessToken.getUid());
        mRemovedStatusesPos = v.getId();
        String items[] = getResources().getStringArray(R.array.status_card_more_menus);
        List<String> itmeList = new ArrayList();
        if (isCurrentUser){
            itmeList.add(items[0]);
            itmeList.add(items[1]);
            itmeList.add(items[2]);
            itmeList.add(items[3]);
        }else {
            itmeList.add(items[0]);
            itmeList.add(items[1]);
            itmeList.add(items[3]);
        }

        new MaterialDialog.Builder(this)
                .items(R.array.status_card_more_menus)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        StatusesAPI statusesAPI;
                        switch (position) {
                            case 0:
                                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                                statusesAPI.favoritesCreate(ftl.idstr, mFavoritesCreateRequestListener);
                                break;
                            case 1:
                                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                                statusesAPI.favoritesDestroy(ftl.idstr, mFavoritesDestroyRequestListener);
                                break;
                            case 2:
                                if (isCurrentUser) {
                                    statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                                    statusesAPI.destroy(Long.parseLong(ftl.idstr), mStatusesDestroyRequestListener);
                                } else {
                                    Utils.copy(ftl.text, SearchActivity.this);
                                    Snackbar.make(mListView, getResources().getString(R.string.delete_comment_copy),
                                            Snackbar.LENGTH_LONG).show();
                                }
                                break;
                            case 3:
                                Utils.copy(ftl.text, SearchActivity.this);
                                Snackbar.make(mListView, getResources().getString(R.string.delete_comment_copy),
                                        Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                })

//                .setItems(itmeList.toArray(new String[itmeList.size()]), new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        StatusesAPI statusesAPI;
//                        switch (which) {
//                            case 0:
//                                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//                                statusesAPI.favoritesCreate(ftl.idstr, mFavoritesCreateRequestListener);
//                                break;
//                            case 1:
//                                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//                                statusesAPI.favoritesDestroy(ftl.idstr, mFavoritesDestroyRequestListener);
//                                break;
//                            case 2:
//                                if (isCurrentUser) {
//                                    statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//                                    statusesAPI.destroy(Long.parseLong(ftl.idstr), mStatusesDestroyRequestListener);
//                                } else {
//                                    Utils.copy(ftl.text, SearchActivity.this);
//                                    Snackbar.make(mListView, getResources().getString(R.string.delete_comment_copy),
//                                            Snackbar.LENGTH_LONG).show();
//                                }
//                                break;
//                            case 3:
//                                Utils.copy(ftl.text, SearchActivity.this);
//                                Snackbar.make(mListView, getResources().getString(R.string.delete_comment_copy),
//                                        Snackbar.LENGTH_LONG).show();
//                                break;
//                        }
//                    }
//
//                })
                .show();
    }

    public static FriendsTimelineResult.FriendsTimeline getExtraFriendsTimeline(){
        return EXTRA_FRIENDS_TIMELINE;
    }


}
