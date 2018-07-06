package truecolor.mdwb.apps;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import truecolor.mdwb.R;
import truecolor.mdwb.api.StatusesAPI;
import truecolor.mdwb.fragments.main.publish.PublishStatusCommentFragment;
import truecolor.mdwb.fragments.main.publish.PublishStatusFragment;
import truecolor.mdwb.fragments.main.publish.PublishStatusRepostFragment;
import truecolor.mdwb.fragments.main.status.PageSectionFragment;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.global.WebServiceConfigure;
import truecolor.mdwb.logics.http.HttpService;
import truecolor.mdwb.model.CommentsResult;
import truecolor.mdwb.model.DetailMessage;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.model.RepostTimelineResult;
import truecolor.mdwb.utils.Utils;
import truecolor.mdwb.view.CircleImageView;
import truecolor.mdwb.view.ImageGroupView;
import truecolor.mdwb.view.MyNestedScrollView;
import truecolor.webdataloader.WebListener;

public class DetailActivity extends AppCompatActivity {

    public static FriendsTimelineResult.FriendsTimeline mFriendsTimeline = null;
    public static FriendsTimelineResult.FriendsTimeline mRepostFriendsTimeline = null;
    private final int FUNCTION_POS_LEFT = 0;
    private final int FUNCTION_POS_MIDDLE = 1;
    private final int FUNCTION_POST_RIGHT = 2;

    private TextView mUserNameView;
    private TextView mUserSubInfoView;
    private TextView mWeiboContentView;
    private CircleImageView mHeandView;
    private ImageGroupView mImageGroupView;
    private TextView mGoodNumView;
    private TextView mRepostNunView;
    private TextView mCommentNumView;
    private TextView mPinGoodNumView;
    private TextView mPinRepostNunView;
    private TextView mPinCommentNumView;
    private RelativeLayout mPinFunctionView;
    private LinearLayout mGoodView;
    private LinearLayout mRepostView;
    private LinearLayout mCommentView;
    private TextView mRepostWeiboContentView;
    private View mCutOffLineView;
    private ImageGroupView mRepostImageGroupView;
    private LinearLayout mRepostContainerView;
    //    private LinearLayout mFunctionView;
    private LinearLayout mCommentListView;
    private LinearLayout mRepostListView;
    private MyNestedScrollView mScrollView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toolbar mToobar;
    private FloatingActionButton mFunctionFab;
    private View mCommentErrorView;
    private View mRepostErrorView;
    private View mRepostListLoadingView;
    private View mCommetnListLoadingView;

//    private LinearLayout.LayoutParams mTopBlockViewLayoutParams;

    private BitmapUtils mBitmapUtils;
    private BitmapDisplayConfig mDisplayConfig;
    private int mCommentPage = 1;
    private int mRepostPage = 1;
    private int mIndex = 0;
    private int mFunctionPos;
    private long mCommentSinceId = 0;
    private long mRepostSinceId = 0;
    private int mInitialOffset;
    private boolean mHidden;
    private int mStatusBarHeight;
    //    private int mCommentNumViewLocaltionOfY;
    private int mPinFunctionViewLocationOnY;

    private boolean mLockGetMoreComment;
    private boolean mLockGetMoreRepost;
    private boolean mCommentHasMore;
    private boolean mRepostHasMore;

    private int mDeletedPos;
    private ProgressDialog mProgressDialog;
    //
    private static final int REPOST_DELETE_SUCCESS = 10001;
    private static final int REPOST_DELETE_FAIL = 10002;
    private static final int COMMENT_DELETE_SUCCESS = 10003;
    private static final int COMMENT_DELETE_FAIL = 10004;

    private List mCommentDatas = new ArrayList();
    private List<FriendsTimelineResult.FriendsTimeline> mRepostDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mToobar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToobar);

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.weibo_content));

//        mFriendsTimeline = PageSectionFragment.getExtraFriendsTimeline();
        getFriendsTimeline();
        initView();
        initBitmapUtils();
        bindData();
        getComment(false, false);
        mInitialOffset = Utils.getActionBarHeight(this);
//        computeCommentNumViewLocaltionOfY();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_destroy_favorite) {
            StatusesAPI statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
            statusesAPI.favoritesDestroy(mFriendsTimeline.idstr, mFavoritesDestroyRequestListener);
            return true;
        } else if (id == R.id.action_copy) {
            Utils.copy(mFriendsTimeline.text, DetailActivity.this);
            Snackbar.make(mUserNameView, getResources().getString(R.string.delete_comment_copy),
                    Snackbar.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_favorite) {
            StatusesAPI statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
            statusesAPI.favoritesCreate(mFriendsTimeline.idstr, mFavoritesCreateRequestListener);
            return true;
        } else if (id == R.id.action_repost) {
            String commentEdit = "//@" + mFriendsTimeline.user.screen_name + ":" + mFriendsTimeline.text;
            Intent intent = new Intent();
            intent.setType(Constant.PUBLISH_STATUESE_REPOST);
            intent.putExtra(Constant.EXTRA_STATUESE_ID, mFriendsTimeline.idstr);
            intent.putExtra(Constant.EXTRA_COMMENT, commentEdit);
            intent.setClass(DetailActivity.this, PublishActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mUserNameView = (TextView) findViewById(R.id.user_name_view);
        mUserSubInfoView = (TextView) findViewById(R.id.user_sub_info);
        mWeiboContentView = (TextView) findViewById(R.id.weibo_content_view);
        mHeandView = (CircleImageView) findViewById(R.id.head_view);
        mImageGroupView = (ImageGroupView) findViewById(R.id.image_gropu_view);
        mGoodNumView = (TextView) findViewById(R.id.goog_num_tv);
        mRepostNunView = (TextView) findViewById(R.id.repost_num_tv);
        mCommentNumView = (TextView) findViewById(R.id.comment_num_tv);
        mPinGoodNumView = (TextView) findViewById(R.id.pin_goog_num_tv);
        mPinRepostNunView = (TextView) findViewById(R.id.pin_repost_num_tv);
        mPinCommentNumView = (TextView) findViewById(R.id.pin_comment_num_tv);
        mPinFunctionView = (RelativeLayout) findViewById(R.id.pin_function_view);
        mGoodView = (LinearLayout) findViewById(R.id.good_view);
        mRepostView = (LinearLayout) findViewById(R.id.reword_view);
        mCommentView = (LinearLayout) findViewById(R.id.comment_view);
        mRepostWeiboContentView = (TextView) findViewById(R.id.weibo_repost_content_view);
        mCutOffLineView = findViewById(R.id.cut_off_line);
        mRepostImageGroupView = (ImageGroupView) findViewById(R.id.repost_image_gropu_view);
        mRepostContainerView = (LinearLayout) findViewById(R.id.repost_container_view);
        mCommentListView = (LinearLayout) findViewById(R.id.comment_list_view);
        mRepostListView = (LinearLayout) findViewById(R.id.repost_list_view);
        mFunctionFab = (FloatingActionButton) findViewById(R.id.function_fab);
        mScrollView = (MyNestedScrollView) findViewById(R.id.detail_scroll_view);
        mScrollView.setOnTouchListener(mScrollViewTouchListener);
        mScrollView.setOnScrollListener(mScrollViewScrollListener);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.detail_swipe_refresh);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_light,//加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setOnRefreshListener(mSwipeRefreshLayoutRefreshListener);
        mCommentErrorView = LayoutInflater.from(this.getApplication()).inflate(R.layout.item_error_view, null, false);
        mRepostErrorView = LayoutInflater.from(this.getApplication()).inflate(R.layout.item_error_view, null, false);
        mCommetnListLoadingView = LayoutInflater.from(this.getApplication()).inflate(R.layout.item_loading_view, null, false);
        mRepostListLoadingView = LayoutInflater.from(this.getApplication()).inflate(R.layout.item_loading_view, null, false);
        mCommentListView.addView(mCommetnListLoadingView);

        mFunctionPos = FUNCTION_POS_MIDDLE;
        mLockGetMoreComment = true;
//        mInitialOffset = DipConvertPxUtil.dip2px(this, 55);
        mCommentNumView.setSelected(true);
        mPinCommentNumView.setSelected(true);
        mRepostNunView.setOnClickListener(mRepostNumViewClickListener);
        mCommentNumView.setOnClickListener(mCommentNumViewClickListener);
        mGoodNumView.setOnClickListener(mGooodNumViewClickListener);
        mPinCommentNumView.setOnClickListener(mPinCommentNumViewClickListener);
        mPinRepostNunView.setOnClickListener(mPinRepostNumViewClickListener);
        mPinGoodNumView.setOnClickListener(mPinGoodNumViewClickListener);
        mFunctionFab.setOnClickListener(mFunctionFabClickListener);
        mCommentErrorView.setOnClickListener(mCommentErroeViewClickListener);
        mRepostErrorView.setOnClickListener(mRepostErrorViewClickListener);

        mProgressDialog = new ProgressDialog(DetailActivity.this);
        mProgressDialog.setTitle("删除中......");

    }

    private void bindData() {
        mBitmapUtils.display(mHeandView, mFriendsTimeline.user.avatar_large, mDisplayConfig);
        mUserNameView.setText(mFriendsTimeline.user.name);
        mUserSubInfoView.setText(mFriendsTimeline.created_at.substring(0, 20) + Html.fromHtml(mFriendsTimeline.source));
        Editable editable = Utils.getEmotionsContent(mWeiboContentView, mFriendsTimeline.text, this);
        mWeiboContentView.setText(editable);
        mGoodNumView.setText(getString(R.string.detaile_weibo_likes, mFriendsTimeline.attitudes_count));
        mRepostNunView.setText(getString(R.string.detaile_weibo_repost, mFriendsTimeline.reposts_count));
        mCommentNumView.setText(getString(R.string.detaile_weibo_comment, mFriendsTimeline.comments_count));
        mPinGoodNumView.setText(getString(R.string.detaile_weibo_likes, mFriendsTimeline.attitudes_count));
        mPinRepostNunView.setText(getString(R.string.detaile_weibo_repost, mFriendsTimeline.reposts_count));
        mPinCommentNumView.setText(getString(R.string.detaile_weibo_comment, mFriendsTimeline.comments_count));

        if (mFriendsTimeline.pic_urls == null || mFriendsTimeline.pic_urls.length == 0) {
            mImageGroupView.setVisibility(View.GONE);
        } else {
            mImageGroupView.setImageGroupViewItemClickListener(mImageGroupViewItemClickListener);
            mImageGroupView.bindImage(mFriendsTimeline, mBitmapUtils);
            mImageGroupView.setVisibility(View.VISIBLE);
        }

        if (mFriendsTimeline.retweeted_status != null) {
            mRepostFriendsTimeline = mFriendsTimeline.retweeted_status;
            mCutOffLineView.setVisibility(View.VISIBLE);
            Editable repostEditable = Utils.getEmotionsContent(mRepostWeiboContentView, mFriendsTimeline.retweeted_status.text, this);
            mRepostWeiboContentView.setText("@" + mFriendsTimeline.retweeted_status.user.name + ":" + repostEditable);
            mRepostWeiboContentView.setOnClickListener(mRepostWeiboContentViewClickListener);
            if (mFriendsTimeline.retweeted_status != null && mFriendsTimeline.retweeted_status.pic_urls.length != 0) {
                mRepostImageGroupView.setImageGroupViewItemClickListener(mImageGroupViewItemClickListener);
                mRepostImageGroupView.bindImage(mFriendsTimeline.retweeted_status, mBitmapUtils);
                mRepostImageGroupView.setVisibility(View.VISIBLE);
                mRepostContainerView.setOnClickListener(mRepostContainerViewClickListener);
            } else {
                mRepostImageGroupView.setVisibility(View.GONE);
            }
        } else {
            mRepostImageGroupView.setVisibility(View.GONE);
            mRepostWeiboContentView.setVisibility(View.GONE);
            mCutOffLineView.setVisibility(View.GONE);
        }
    }

    private void initBitmapUtils() {
        mBitmapUtils = new BitmapUtils(this);
        mDisplayConfig = new BitmapDisplayConfig();
        mDisplayConfig.setLoadingDrawable(this.getResources().getDrawable(R.mipmap.ic_user));
        Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        alphaAnimation.setFillAfter(true);
        mDisplayConfig.setAnimation(alphaAnimation);
    }

    private void getComment(boolean isRefresh, boolean isGetMore) {
        if (isGetMore) {
            HttpService.getComments(Utils.accessToken, mWebListener, mFriendsTimeline.idstr, mCommentPage, "0", isRefresh);
        } else {
            HttpService.getComments(Utils.accessToken, mWebListener, mFriendsTimeline.idstr, mCommentPage, String.valueOf(mCommentSinceId), isRefresh);
        }
    }

    private void getMoreComment() {
        mLockGetMoreComment = true;
        mCommentPage++;
        mCommentListView.removeView(mCommentErrorView);
        mCommentListView.addView(mCommetnListLoadingView);
        getComment(false, true);
    }

    private void getRefreshComment() {
        getComment(true, false);
    }

    private void getRepost(boolean isRefresh, boolean isGetMore) {
        if (isGetMore) {
            HttpService.getRepost(Utils.accessToken, mWebListener, mFriendsTimeline.idstr, mRepostPage, "0", isRefresh);
        } else {
            HttpService.getRepost(Utils.accessToken, mWebListener, mFriendsTimeline.idstr, mRepostPage, String.valueOf(mRepostSinceId), isRefresh);
        }
    }

    private void getMoreRepost() {
        mLockGetMoreRepost = true;
        mRepostPage++;
        mRepostListView.removeView(mRepostErrorView);
        mRepostListView.addView(mRepostListLoadingView);
        getRepost(false, true);
    }

    private void getRefrshRepost() {
        getRepost(true, false);
    }

    private WebListener mWebListener = new WebListener() {
        @Override
        public void onDataLoadFinished(int service, Bundle params, Object result) {
            if (service == WebServiceConfigure.GET_COMMENTS) {
                boolean isRefresh = params.getBoolean(WebServiceConfigure.HTTP_GET_COMMENTS_IS_REFERSH);
                if (isRefresh) {
                    if (result != null) {
                        CommentsResult comments = (CommentsResult) result;
                        List commentResultList = Arrays.asList(comments.comments);
                        mCommentDatas.addAll(0, commentResultList);
                        if (commentResultList.size() > 0) {
                            addCommentItemView(commentResultList, true);
                            mCommentSinceId = Long.valueOf(comments.comments[0].id);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    if (result != null && ((CommentsResult) result).comments != null && ((CommentsResult) result).comments.length > 0) {
                        CommentsResult comments = (CommentsResult) result;
                        List commentResultList = Arrays.asList(comments.comments);
                        mCommentDatas.addAll(commentResultList);
                        addCommentItemView(commentResultList, false);
                        setCommentHasMore(comments);
                        if (Long.valueOf(comments.comments[0].id) > mCommentSinceId) {
                            mCommentSinceId = Long.valueOf(comments.comments[0].id);
                        }
                        mLockGetMoreComment = false;
                        mSwipeRefreshLayout.setEnabled(true);
                        mCommentListView.removeView(mCommetnListLoadingView);
                    } else if (result != null && ((CommentsResult) result).comments != null && ((CommentsResult) result).comments.length == 0) {
                        mCommentListView.removeView(mCommetnListLoadingView);
                        mSwipeRefreshLayout.setEnabled(true);
                    } else {
                        if (mCommentPage > 1) mCommentPage--;
                        mLockGetMoreComment = false;
                        mCommentListView.removeView(mCommetnListLoadingView);
                        mCommentListView.addView(mCommentErrorView);
                        mSwipeRefreshLayout.setEnabled(true);
                    }

                }

            } else if (service == WebServiceConfigure.GET_REPOST) {
                boolean isRefresh = params.getBoolean(WebServiceConfigure.HTTP_GET_REPOST_IS_REFERSH);
                if (isRefresh) {
                    if (result != null) {
                        RepostTimelineResult rtr = (RepostTimelineResult) result;
                        List<FriendsTimelineResult.FriendsTimeline> commentResultList = Arrays.asList(rtr.statuses);
                        mRepostDatas.addAll(0, commentResultList);
                        if (rtr.statuses.length > 0) {
                            addRepostItemView(rtr.statuses, true);
                            mRepostSinceId = Long.valueOf(rtr.statuses[0].idstr);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    if ((result != null && ((RepostTimelineResult) result).statuses != null && ((RepostTimelineResult) result).statuses.length > 0)) {
                        RepostTimelineResult rtr = (RepostTimelineResult) result;
                        List<FriendsTimelineResult.FriendsTimeline> commentResultList = Arrays.asList(rtr.statuses);
                        mRepostDatas.addAll(commentResultList);
                        addRepostItemView(rtr.statuses, false);
                        setRepostHasMore(rtr);
                        if (Long.valueOf(rtr.statuses[0].idstr) > mRepostSinceId) {
                            mRepostSinceId = Long.valueOf(rtr.statuses[0].idstr);
                        }
                        mRepostListView.removeView(mRepostListLoadingView);
                        mLockGetMoreRepost = false;
                    } else if ((result != null && ((RepostTimelineResult) result).statuses != null && ((RepostTimelineResult) result).statuses.length == 0)) {
                        mRepostListView.removeView(mRepostListLoadingView);
                    } else {
                        if (mRepostPage > 1) mRepostPage--;
                        mLockGetMoreRepost = false;
                        mRepostListView.addView(mRepostErrorView);
                        mRepostListView.removeView(mRepostListLoadingView);
                    }
                }
            }
        }
    };

    private View.OnClickListener mGooodNumViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener mRepostNumViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mFunctionPos != FUNCTION_POS_LEFT) {
                mFunctionPos = FUNCTION_POS_LEFT;

                selectedRepostView();
                showRepostListView();
                if (mRepostListView.getChildCount() == 0) {
                    mRepostListView.addView(mRepostListLoadingView);
                    getRepost(false, false);
                }

            }
        }
    };

    private View.OnClickListener mCommentNumViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mFunctionPos != FUNCTION_POS_MIDDLE) {
                mFunctionPos = FUNCTION_POS_MIDDLE;
                selectedCommentView();
                showCommentListView();
            }
        }
    };

    private View.OnClickListener mPinCommentNumViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCommentNumView.performClick();
        }
    };

    private View.OnClickListener mPinRepostNumViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRepostNunView.performClick();
        }
    };

    private View.OnClickListener mPinGoodNumViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mGoodNumView.performClick();
        }
    };


    private View.OnClickListener mFunctionFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType(Constant.PUBLISH_COMMENTS_CREATE);
            intent.putExtra(Constant.EXTRA_STATUESE_ID, mFriendsTimeline.idstr);
            intent.setClass(DetailActivity.this, PublishActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener mCommentErroeViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getMoreComment();
        }
    };

    private View.OnClickListener mRepostErrorViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getMoreRepost();
        }
    };

    private View.OnClickListener mCommentItemViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final CommentsResult.Comment comment = (CommentsResult.Comment) v.getTag();
            final String[] commentMenuArr = getResources().getStringArray(R.array.comment_menus);
            List<String> commentMenuArrList = new ArrayList<>();
            final boolean isCurrentUser = comment.user.idstr.equals(Utils.accessToken.getUid());
            if (isCurrentUser) {
                commentMenuArrList.add(commentMenuArr[0]);
                commentMenuArrList.add(commentMenuArr[1]);
                commentMenuArrList.add(commentMenuArr[2]);
                commentMenuArrList.add(commentMenuArr[3]);
            } else {
                commentMenuArrList.add(commentMenuArr[0]);
                commentMenuArrList.add(commentMenuArr[1]);
                commentMenuArrList.add(commentMenuArr[3]);
            }

            new MaterialDialog.Builder(DetailActivity.this)
                    .title(comment.user.screen_name)
                    .items(commentMenuArrList.toArray(new String[commentMenuArrList.size()]))
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                            Intent intent;
                            switch (position) {
                                case 0:
                                    intent = new Intent();
                                    intent.setType(Constant.PUBLISH_COMMENTS_REPLY);
                                    intent.putExtra(Constant.EXTRA_STATUESE_ID, mFriendsTimeline.idstr);
                                    intent.putExtra(Constant.EXTRA_COMMENT_ID, comment.id);
                                    intent.setClass(DetailActivity.this, PublishActivity.class);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    intent = new Intent();
                                    String commentEdit = "//@" + comment.user.screen_name + ":" + comment.text;
                                    intent.setType(Constant.PUBLISH_COMMENTS_REPOST);
                                    intent.putExtra(Constant.EXTRA_STATUESE_ID, mFriendsTimeline.idstr);
                                    intent.putExtra(Constant.EXTRA_COMMENT, commentEdit);
                                    intent.setClass(DetailActivity.this, PublishActivity.class);
                                    startActivity(intent);
                                    break;
                                case 2:
                                    if (isCurrentUser) {
                                        mProgressDialog.show();
                                        mDeletedPos = mCommentDatas.indexOf(comment);
                                        StatusesAPI statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                                        statusesAPI.commentsDestroy(comment.id, mCommentsDestroyRequestListener);
                                    } else {
                                        Utils.copy(comment.text, DetailActivity.this);
                                        Toast.makeText(DetailActivity.this, R.string.delete_comment_copy, Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 3:
                                    Utils.copy(comment.text, DetailActivity.this);
                                    Toast.makeText(DetailActivity.this, R.string.delete_comment_copy, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }).show();

//            new AlertDialogWrapper.Builder(DetailActivity.this)
//                    .setTitle(comment.user.screen_name)
//                    .setItems(commentMenuArrList.toArray(new String[commentMenuArrList.size()]), new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent;
//                            switch (which) {
//                                case 0:
//                                    intent = new Intent();
//                                    intent.setType(Constant.PUBLISH_COMMENTS_REPLY);
//                                    intent.putExtra(Constant.EXTRA_STATUESE_ID, mFriendsTimeline.idstr);
//                                    intent.putExtra(Constant.EXTRA_COMMENT_ID, comment.id);
//                                    intent.setClass(DetailActivity.this, PublishActivity.class);
//                                    startActivity(intent);
//                                    break;
//                                case 1:
//                                    intent = new Intent();
//                                    String commentEdit = "//@" + comment.user.screen_name + ":" + comment.text;
//                                    intent.setType(Constant.PUBLISH_COMMENTS_REPOST);
//                                    intent.putExtra(Constant.EXTRA_STATUESE_ID, mFriendsTimeline.idstr);
//                                    intent.putExtra(Constant.EXTRA_COMMENT, commentEdit);
//                                    intent.setClass(DetailActivity.this, PublishActivity.class);
//                                    startActivity(intent);
//                                    break;
//                                case 2:
//                                    if (isCurrentUser) {
//                                        mProgressDialog.show();
//                                        mDeletedPos = mCommentDatas.indexOf(comment);
//                                        StatusesAPI statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//                                        statusesAPI.commentsDestroy(comment.id, mCommentsDestroyRequestListener);
//                                    } else {
//                                        Utils.copy(comment.text, DetailActivity.this);
//                                        Toast.makeText(DetailActivity.this, R.string.delete_comment_copy, Toast.LENGTH_SHORT).show();
//                                    }
//                                    break;
//                                case 3:
//                                    Utils.copy(comment.text, DetailActivity.this);
//                                    Toast.makeText(DetailActivity.this, R.string.delete_comment_copy, Toast.LENGTH_SHORT).show();
//                                    break;
//                            }
//                        }
//
//                    })
//                    .show();
        }
    };

    private View.OnClickListener mRepostItemViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final FriendsTimelineResult.FriendsTimeline status = (FriendsTimelineResult.FriendsTimeline) v.getTag();
            String idstr = status.user.idstr;

            final String[] commentMenuArr = getResources().getStringArray(R.array.repost_menus);
            List<String> commentMenuArrList = new ArrayList<>();
            final boolean isCurrentUser = idstr.equals(Utils.accessToken.getUid());
            if (isCurrentUser) {
                commentMenuArrList.add(commentMenuArr[0]);
                commentMenuArrList.add(commentMenuArr[1]);
                commentMenuArrList.add(commentMenuArr[2]);
                commentMenuArrList.add(commentMenuArr[3]);
                commentMenuArrList.add(commentMenuArr[4]);
            } else {
                commentMenuArrList.add(commentMenuArr[0]);
                commentMenuArrList.add(commentMenuArr[1]);
                commentMenuArrList.add(commentMenuArr[2]);
                commentMenuArrList.add(commentMenuArr[4]);
            }

            new MaterialDialog.Builder(DetailActivity.this)
                    .items(commentMenuArr)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                            Intent intent;
                            switch (position) {
                                case 0:
                                    intent = new Intent();
                                    intent.putExtra(Constant.DETALI_FRIENDS_TIMELINE_TYPE, Constant.DETAIL_FRIENDS_TIMELINE_TYPE_DETAIL_REPOST);
                                    MaterialDesignWBApps.EXTRA_STATUS = status;
                                    intent.setClass(DetailActivity.this, DetailActivity.class);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    intent = new Intent();
                                    intent.setType(Constant.PUBLISH_COMMENTS_CREATE);
                                    intent.putExtra(Constant.EXTRA_STATUESE_ID, status.idstr);
                                    intent.setClass(DetailActivity.this, PublishActivity.class);
                                    startActivity(intent);
                                    break;
                                case 2:
                                    intent = new Intent();
                                    String commentEdit = "//@" + status.user.screen_name + ":" + status.text;
                                    intent.setType(Constant.PUBLISH_STATUESE_REPOST);
                                    intent.putExtra(Constant.EXTRA_STATUESE_ID, status.idstr);
                                    intent.putExtra(Constant.EXTRA_COMMENT, commentEdit);
                                    intent.setClass(DetailActivity.this, PublishActivity.class);
                                    startActivity(intent);
                                    break;
                                case 3:
                                    if (isCurrentUser) {
                                        mProgressDialog.show();
                                        mDeletedPos = mRepostDatas.indexOf(status);
                                        StatusesAPI statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                                        statusesAPI.destroy(Long.parseLong(status.idstr), mStatusesDestroyRequestListener);
                                    } else {
                                        Utils.copy(status.text, DetailActivity.this);
                                        Toast.makeText(DetailActivity.this, R.string.delete_comment_copy, Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 4:
                                    Utils.copy(status.text, DetailActivity.this);
                                    Toast.makeText(DetailActivity.this, R.string.delete_comment_copy, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }).show();

//            new AlertDialogWrapper.Builder(DetailActivity.this)
//                    .setItems(commentMenuArrList.toArray(new String[commentMenuArrList.size()]), new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent;
//                            switch (which) {
//                                case 0:
//                                    intent = new Intent();
//                                    intent.putExtra(Constant.DETALI_FRIENDS_TIMELINE_TYPE, Constant.DETAIL_FRIENDS_TIMELINE_TYPE_DETAIL_REPOST);
//                                    MaterialDesignWBApps.EXTRA_STATUS = status;
//                                    intent.setClass(DetailActivity.this, DetailActivity.class);
//                                    startActivity(intent);
//                                    break;
//                                case 1:
//                                    intent = new Intent();
//                                    intent.setType(Constant.PUBLISH_COMMENTS_CREATE);
//                                    intent.putExtra(Constant.EXTRA_STATUESE_ID, status.idstr);
//                                    intent.setClass(DetailActivity.this, PublishActivity.class);
//                                    startActivity(intent);
//                                    break;
//                                case 2:
//                                    intent = new Intent();
//                                    String commentEdit = "//@" + status.user.screen_name + ":" + status.text;
//                                    intent.setType(Constant.PUBLISH_STATUESE_REPOST);
//                                    intent.putExtra(Constant.EXTRA_STATUESE_ID, status.idstr);
//                                    intent.putExtra(Constant.EXTRA_COMMENT, commentEdit);
//                                    intent.setClass(DetailActivity.this, PublishActivity.class);
//                                    startActivity(intent);
//                                    break;
//                                case 3:
//                                    if (isCurrentUser) {
//                                        mProgressDialog.show();
//                                        mDeletedPos = mRepostDatas.indexOf(status);
//                                        StatusesAPI statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//                                        statusesAPI.destroy(Long.parseLong(status.idstr), mStatusesDestroyRequestListener);
//                                    } else {
//                                        Utils.copy(status.text, DetailActivity.this);
//                                        Toast.makeText(DetailActivity.this, R.string.delete_comment_copy, Toast.LENGTH_SHORT).show();
//                                    }
//                                    break;
//                                case 4:
//                                    Utils.copy(status.text, DetailActivity.this);
//                                    Toast.makeText(DetailActivity.this, R.string.delete_comment_copy, Toast.LENGTH_SHORT).show();
//                                    break;
//                            }
//                        }
//
//                    })
//                    .show();
        }
    };

    private View.OnClickListener mImageGroupViewItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = v.getId();
            mFriendsTimeline = (FriendsTimelineResult.FriendsTimeline) v.getTag();
            Intent intent = new Intent();
            intent.setType(Constant.GO_TO_PHOTOS_BROWSE_TYPE_DETAIL);
            intent.putExtra(Constant.EXTRA_IMAGER_GROUP_POS, pos);
            intent.setClass(DetailActivity.this, PhotoBrowseActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener mRepostContainerViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mFriendsTimeline = mFriendsTimeline.retweeted_status;
            Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
            intent.putExtra(Constant.DETALI_FRIENDS_TIMELINE_TYPE, Constant.DETAIL_FRIENDS_TIMELINE_TYPE_DETAIL);
            startActivity(intent);
        }
    };

    private View.OnClickListener mRepostWeiboContentViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRepostContainerView.performClick();
        }
    };

    private View.OnTouchListener mScrollViewTouchListener = new View.OnTouchListener() {

        private int startY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mIndex++;

                    int dy = (int) (event.getRawY() - startY);
                    if (dy > mInitialOffset) {
                        showFabView(mFunctionFab);
                    } else if (dy < -mInitialOffset) {
                        hideFabView(mFunctionFab);
                    }
                    break;
                default:
                    break;
            }
            if (event.getAction() == MotionEvent.ACTION_UP && mIndex > 0) {
                mIndex = 0;
                View view = ((NestedScrollView) v).getChildAt(0);
                if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
                    if (mFunctionPos == FUNCTION_POS_MIDDLE && !mLockGetMoreComment && mCommentHasMore) {
                        getMoreComment();
                    } else if (mFunctionPos == FUNCTION_POS_LEFT && !mLockGetMoreRepost && mRepostHasMore) {
                        getMoreRepost();
                    }
                }
            }
            return false;
        }
    };

    private MyNestedScrollView.OnScrollListener mScrollViewScrollListener = new MyNestedScrollView.OnScrollListener() {
        @Override
        public void onScroll(int scrollY) {
            int locationOnScreenXY[] = new int[2];
            mCommentNumView.getLocationOnScreen(locationOnScreenXY);

            int mCommentNumViewScrollLoactionOnY = locationOnScreenXY[1];

            if (mPinFunctionViewLocationOnY == 0) {
                int commentlocationOnScreenXY[] = new int[2];
                mPinCommentNumView.getLocationOnScreen(commentlocationOnScreenXY);
                mPinFunctionViewLocationOnY = commentlocationOnScreenXY[1];
            }

            if (mCommentNumViewScrollLoactionOnY < mPinFunctionViewLocationOnY) {
                mPinFunctionView.setVisibility(View.VISIBLE);
            } else {
                mPinFunctionView.setVisibility(View.GONE);
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mSwipeRefreshLayoutRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (mFunctionPos == FUNCTION_POS_MIDDLE) {
                getRefreshComment();
            } else if (mFunctionPos == FUNCTION_POS_LEFT) {
                getRefrshRepost();
            }

        }
    };

    private RequestListener mCommentsDestroyRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            mCommentDatas.remove(mDeletedPos);
            mHandler.sendEmptyMessage(COMMENT_DELETE_SUCCESS);
        }

        @Override
        public void onIOException(IOException e) {
            mHandler.sendEmptyMessage(COMMENT_DELETE_FAIL);
        }

        @Override
        public void onError(WeiboException e) {
            mHandler.sendEmptyMessage(COMMENT_DELETE_FAIL);
        }
    };

    private RequestListener mStatusesDestroyRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            mHandler.sendEmptyMessage(REPOST_DELETE_SUCCESS);
        }

        @Override
        public void onIOException(IOException e) {
            mHandler.sendEmptyMessage(REPOST_DELETE_FAIL);
        }

        @Override
        public void onError(WeiboException e) {
            mHandler.sendEmptyMessage(REPOST_DELETE_FAIL);
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
            if (detailMessage.error_code.equals("20704")) {
                mHandler.sendEmptyMessage(Constant.FAVORITES_CREATE_FAIL_HAVA_COLLECTED);
            } else {
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMMENT_DELETE_SUCCESS:
                    mCommentDatas.remove(mDeletedPos);
                    mCommentListView.removeViewAt(mDeletedPos);
                    mProgressDialog.dismiss();
                    break;
                case COMMENT_DELETE_FAIL:
                    Snackbar.make(mUserNameView, getResources().getString(R.string.delete_comment_fail),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case REPOST_DELETE_SUCCESS:
                    mRepostDatas.remove(mDeletedPos);
                    mRepostListView.removeViewAt(mDeletedPos);
                    mProgressDialog.dismiss();
                    break;
                case REPOST_DELETE_FAIL:
                    Snackbar.make(mUserNameView, getResources().getString(R.string.delete_repost_fail),
                            Snackbar.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    break;
                case Constant.FAVORITES_CREATE_SUCCESS:
                    Snackbar.make(mUserNameView, getResources().getString(R.string.favorites_create_success),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case Constant.FAVORITES_CREATE_FAIL:
                    Snackbar.make(mUserNameView, getResources().getString(R.string.favorites_create_fail),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case Constant.FAVORITES_CREATE_FAIL_HAVA_COLLECTED:
                    Snackbar.make(mUserNameView, getResources().getString(R.string.have_collected),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case Constant.FAVORITES_DESTROY_SUCCESS:
                    Snackbar.make(mUserNameView, getResources().getString(R.string.favorites_destroy_success),
                            Snackbar.LENGTH_LONG).show();
                    return;
                case Constant.FAVORITES_DESTROY_FAIL:
                    Snackbar.make(mUserNameView, getResources().getString(R.string.favorites_destroy_fail),
                            Snackbar.LENGTH_LONG).show();
                    return;
                case Constant.FAVORITES_DESTROY_FAIL_NO_COLLECTED:
                    Snackbar.make(mUserNameView, getResources().getString(R.string.no_collected),
                            Snackbar.LENGTH_LONG).show();
                    return;
            }
        }
    };


    private void selectedCommentView() {
        mCommentNumView.setSelected(true);
        mPinCommentNumView.setSelected(true);
        mRepostNunView.setSelected(false);
        mPinRepostNunView.setSelected(false);
    }

    private void selectedRepostView() {
        mCommentNumView.setSelected(false);
        mPinCommentNumView.setSelected(false);
        mRepostNunView.setSelected(true);
        mPinRepostNunView.setSelected(true);
    }

    private void showRepostListView() {
        mRepostListView.setVisibility(View.VISIBLE);
        mCommentListView.setVisibility(View.GONE);
    }

    private void showCommentListView() {
        mRepostListView.setVisibility(View.GONE);
        mCommentListView.setVisibility(View.VISIBLE);
    }

    public void hideFabView(View v) {
        if (!mHidden) {
            mHidden = true;
            hideView(v);
        }
    }

    private void showFabView(View v) {
        if (mHidden) {
            mHidden = false;
            showView(v);
        }
    }

    private void hideView(View view) {
        int height = calculateTranslation(view);
        int translateY = height;
        runTranslateAnimation(view, translateY, new AccelerateInterpolator(3));
    }

    /**
     * Takes height + margins
     *
     * @param view View to translate
     * @return translation in pixels
     */
    private int calculateTranslation(View view) {
        int height = view.getHeight();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int margins = params.topMargin + params.bottomMargin;

        return height + margins;
    }

    private void showView(View view) {
        runTranslateAnimation(view, 0, new DecelerateInterpolator(3));
    }

    private void runTranslateAnimation(View view, int translateY, Interpolator interpolator) {
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.setDuration(view.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.start();
    }

    private void addCommentItemView(List<CommentsResult.Comment> commentList, boolean isAddToHead) {
        for (int i = 0; i < commentList.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.detail_comment_item, null, false);
            CommentViewHolder commentViewHolder = new CommentViewHolder(view);
            commentViewHolder.mUserNameView.setText(commentList.get(i).user.name);
            commentViewHolder.mDataAndSourceView.setText(commentList.get(i).created_at.substring(0, 20) + Html.fromHtml(commentList.get(i).source == null ? "" : commentList.get(i).source));
            commentViewHolder.mContentView.setText(commentList.get(i).text);
            Linkify.addLinks(commentViewHolder.mContentView, Pattern.compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]"), "");//匹配所有网址链接

            commentViewHolder.mContentView.setMovementMethod(LinkMovementMethod.getInstance());
            commentViewHolder.mRootView.setTag(commentList.get(i));
            commentViewHolder.mRootView.setId(i);
            commentViewHolder.mRootView.setOnClickListener(mCommentItemViewClickListener);
            mBitmapUtils.display(commentViewHolder.mHeandView, commentList.get(i).user.avatar_large, mDisplayConfig);
            if (isAddToHead) {
                mCommentListView.addView(commentViewHolder.getCommentView(), 0);
            } else {
                mCommentListView.addView(commentViewHolder.getCommentView());
            }
        }
    }

    private void addRepostItemView(FriendsTimelineResult.FriendsTimeline status[], boolean isAddToHead) {
        for (int i = 0; i < status.length; i++) {
            View view = LayoutInflater.from(this.getApplication()).inflate(R.layout.detail_comment_item, null, false);
            FriendsTimelineResult.FriendsTimeline sta = status[i];
            CommentViewHolder commentViewHolder = new CommentViewHolder(view);
            commentViewHolder.mUserNameView.setText(sta.user.name);
            commentViewHolder.mDataAndSourceView.setText(sta.created_at.substring(0, 20) + Html.fromHtml(sta.source));
            commentViewHolder.mContentView.setText(sta.text);
            commentViewHolder.mRootView.setTag(sta);
            commentViewHolder.mRootView.setOnClickListener(mRepostItemViewClickListener);
            mBitmapUtils.display(commentViewHolder.mHeandView, sta.user.avatar_large, mDisplayConfig);
            if (isAddToHead) {
                mRepostListView.addView(commentViewHolder.getCommentView(), 0);
            } else {
                mRepostListView.addView(commentViewHolder.getCommentView());
            }
        }
    }

    private void setCommentHasMore(CommentsResult commentsResult) {
        if (commentsResult.next_cursor.equals("0")) {
            mCommentHasMore = false;
        } else {
            mCommentHasMore = true;
        }
    }

    private void setRepostHasMore(RepostTimelineResult rtr) {
        if (rtr.next_cursor.equals("0")) {
            mRepostHasMore = false;
        } else {
            mRepostHasMore = true;
        }
    }

    private static class CommentViewHolder {

        private View mView;
        private TextView mUserNameView;
        private CircleImageView mHeandView;
        private TextView mDataAndSourceView;
        private TextView mContentView;
        private RelativeLayout mRootView;

        public CommentViewHolder(View view) {
            mView = view;
            mRootView = (RelativeLayout) mView.findViewById(R.id.root_view);
            mUserNameView = (TextView) mView.findViewById(R.id.user_name_view);
            mHeandView = (CircleImageView) mView.findViewById(R.id.head_view);
            mDataAndSourceView = (TextView) mView.findViewById(R.id.data_and_source);
            mContentView = (TextView) mView.findViewById(R.id.comment_content);
        }

        public View getCommentView() {
            return mView;
        }
    }

    private void getFriendsTimeline() {
        Intent intent = getIntent();
        if (intent != null) {
            int friendsTimelineType = intent.getIntExtra(Constant.DETALI_FRIENDS_TIMELINE_TYPE, -1);
            if (friendsTimelineType != -1) {
                switch (friendsTimelineType) {
                    case Constant.DETAIL_FRIENDS_TIMELINE_TYPE_LIST:
                        mFriendsTimeline = PageSectionFragment.getExtraFriendsTimeline();
                        break;
                    case Constant.DETAIL_FRIENDS_TIMELINE_TYPE_PUBLISH:
                        mFriendsTimeline = PublishStatusFragment.getPublishFriendsTimeline();
                        break;
                    case Constant.DETAIL_FRIENDS_TIMELINE_TYPE_CREATE_COMMENT:
                        mFriendsTimeline = PublishStatusCommentFragment.getExtraFriendsTimeline();
                        break;
                    case Constant.DETAIL_FRIENDS_TIMELINE_TYPE_REPOST:
                        mFriendsTimeline = PublishStatusRepostFragment.getExtraFriendsTimeline();
                        break;
                    case Constant.DETAIL_FRIENDS_TIMELINE_TYPE_MAIN:
                        mFriendsTimeline = MainActivity.getExtraFriendsTimeline();
                        break;
                    case Constant.DETAIL_FRIENDS_TIMELINE_TYPE_SEARCH:
                        mFriendsTimeline = SearchActivity.getExtraFriendsTimeline();
                    case Constant.DETAIL_FRIENDS_TIMELINE_TYPE_DETAIL:
                        mFriendsTimeline = DetailActivity.getRepostExtraFriendsTimeline();
                        break;
                    case Constant.DETAIL_FRIENDS_TIMELINE_TYPE_DETAIL_REPOST:
                        mFriendsTimeline = MaterialDesignWBApps.EXTRA_STATUS;
                        break;
                    default:
                        return;
                }
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    public static FriendsTimelineResult.FriendsTimeline getExtraFriendsTimeline() {
        return mFriendsTimeline;
    }

    public static FriendsTimelineResult.FriendsTimeline getRepostExtraFriendsTimeline() {
        return mRepostFriendsTimeline;
    }

//    private void computeCommentNumViewLocaltionOfY(){
//        mCommentNumViewLocaltionOfY = Utils.getStatusBarHeight(this) + Utils.getActionBarHeight(this) * 2;
//    }


}
