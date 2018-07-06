package truecolor.mdwb.fragments.main.status;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import truecolor.mdwb.R;
import truecolor.mdwb.adapter.BaseRecycleAdapter;
import truecolor.mdwb.adapter.StatusAdapter;
import truecolor.mdwb.api.StatusesAPI;
import truecolor.mdwb.apps.MainActivity;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.global.WebServiceConfigure;
import truecolor.mdwb.logics.http.HttpService;
import truecolor.mdwb.model.DetailMessage;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.utils.DipConvertPxUtil;
import truecolor.mdwb.utils.Utils;
import truecolor.webdataloader.WebListener;

/**
 * Created by xiaowu on 15/8/13.
 */
public class PageSectionFragment extends Fragment{

    public static List<WeakReference<PageSectionFragment>> mFragments = new ArrayList<>();
    public static FriendsTimelineResult.FriendsTimeline EXTRA_FRIENDS_TIMELINE;
    public static FriendsTimelineResult.FriendsTimeline mFriendsTimeline;
    private final int REFRESH_LISTENER_TYPE_FRINENDS = 0;
    private final int REFRESH_LISTENER_TYPE_PUBLIC = 1;
    private final int REFRESH_LISTENER_TYPE_BILATERA = 2;
    private final int SCROLL_LISTENER_TYPE_FRIENDS = 0;
    private final int SCROLL_LISTENER_TYPE_PUBLIC = 1;
    private final int SCROLL_LISTENER_TYPE_BILATERA = 2;
    private View mView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mCategoryId;
    private MainActivity mActivity;
    private StatusAdapter mStatusAdapter;
    private LinearLayoutManager mLayoutManager;
    private PageSectionFragment mPageSectionFragment;
    private ArrayList<Object> mFriendsTimelineResultList;
    private long mFriendsTimelineSinceId;
    private long mBiatusesTimelineSinceId;
    private boolean isLoadMoreData = true;
    private int page = 1;
    private BitmapUtils bitmapUtils;
    private BitmapDisplayConfig displayConfig;
    private AppBarLayout mAppBarLayout;
    private FloatingActionButton mEditFab;
    private int mRemovedStatusesPos;
    private WebListener mWebListener = new WebListener() {
        @Override
        public void onDataLoadFinished(int service, Bundle params, Object result) {
            if (service == WebServiceConfigure.GET_FRIENDS_TIMELINE){
                bindFriendsTimelineData(params, result);
            }else if(service == WebServiceConfigure.GET_PUBLIC_TIMELINE){
                bindPublicTimelineData(params, result);
            }else if (service == WebServiceConfigure.GET_BILATERA_TIMELINE){
                bindBilateraTimelineData(params, result);
            }
        }
    };
//    private boolean mHidden;
//    private int mAccummulatedDy;
//    private int mTotalDy;
//    private int mInitialOffset = 50;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.FAVORITES_CREATE_SUCCESS:
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.favorites_create_success),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case Constant.FAVORITES_CREATE_FAIL:
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.favorites_create_fail),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case Constant.FAVORITES_CREATE_FAIL_HAVA_COLLECTED:
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.have_collected),
                            Snackbar.LENGTH_LONG).show();
                    break;
                case Constant.FAVORITES_DESTROY_SUCCESS:
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.favorites_destroy_success),
                            Snackbar.LENGTH_LONG).show();
                    return;
                case Constant.FAVORITES_DESTROY_FAIL:
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.favorites_destroy_fail),
                            Snackbar.LENGTH_LONG).show();
                    return;
                case Constant.FAVORITES_DESTROY_FAIL_NO_COLLECTED:
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.no_collected),
                            Snackbar.LENGTH_LONG).show();
                    return;
                case Constant.STATUSES_DESTROY_SUCCESS:
                    mFriendsTimelineResultList.remove(mRemovedStatusesPos);
                    mStatusAdapter.notifyItemRemoved(mRemovedStatusesPos);
                    return;
                case Constant.STATUSES_DESTROY_FAIL:
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.statuses_destroy_fail),
                            Snackbar.LENGTH_LONG).show();
                    return;
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

    private View.OnClickListener mFriendsLoadingErrorViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING);
            HttpService.getFriendsTimeline(Utils.accessToken, mWebListener, page, true, true, null);
        }
    };

    private View.OnClickListener mFriendsLoadingMoreErrorViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE);
            HttpService.getFriendsTimeline(Utils.accessToken, mWebListener, page, false, true, null);
        }
    };

    private View.OnClickListener mPublicLoadingErrorViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING);
            HttpService.getPublicTimeline(Utils.accessToken, mWebListener, page, true, false);
        }
    };

    private View.OnClickListener mPublicLoadingMoreErrorViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE);
            HttpService.getPublicTimeline(Utils.accessToken, mWebListener, page, false, false);
        }
    };

    private View.OnClickListener mBiatusesLoadingErrorViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING);
            HttpService.getBiatusesTimeline(Utils.accessToken, mWebListener, page, true, true, null);
        }
    };

    private View.OnClickListener mBiatusesLoadingMoreErrorViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE);
            HttpService.getBiatusesTimeline(Utils.accessToken, mWebListener, page, false, true, null);
        }
    };


    private RequestCallBack requestCallBack = new RequestCallBack() {
        @Override
        public void onSuccess(ResponseInfo responseInfo) {
            Log.e("", "");
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            Log.e("","");
        }
    };

    public static PageSectionFragment newInstance(int categoryId, MainActivity mActivity) {
        PageSectionFragment fragment = new PageSectionFragment();
        fragment.mCategoryId = categoryId;
        fragment.mActivity = mActivity;
        WeakReference<PageSectionFragment> weakReference = new WeakReference<>(fragment);
        mFragments.add(weakReference);
        return fragment;
    }

    public static FriendsTimelineResult.FriendsTimeline getExtraFriendsTimeline(){
        return EXTRA_FRIENDS_TIMELINE;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PageSectionFragment psf = mFragments.get(mCategoryId).get();
        if (psf != null && psf.mView != null){
            return psf.mView;
        }else {
            psf.mView = inflater.inflate(R.layout.main_fragment_section, container, false);
            psf.mRecyclerView = (RecyclerView) mView.findViewById(R.id.main_fragment_section_recycler_view);
            psf.mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_container);

            psf.mFriendsTimelineResultList = new ArrayList<>();
            psf.mLayoutManager = new LinearLayoutManager(this.getActivity());

            psf.displayConfig = new BitmapDisplayConfig();
            psf.displayConfig.setLoadingDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_user));
            psf.bitmapUtils = new BitmapUtils(getActivity());

            psf.mStatusAdapter = new StatusAdapter(getActivity(), psf.mFriendsTimelineResultList, 0);
            psf.mStatusAdapter.setGoodViewClickListener(mActivity.mGoodViewClickListener);
            psf.mStatusAdapter.setRepostViewClickListener(mActivity.mRepostViewClickListener);
            psf.mStatusAdapter.setCommentViewClickListener(mActivity.mCommentViewClickListener);
            psf.mStatusAdapter.setCardViewClickListener(mActivity.mCardViewClickListener);
            psf.mStatusAdapter.setCardViewLongClickListener(mCardViewLongClickListener);
            psf.mStatusAdapter.setMenuMoreViewClickListener(mMenuMoreViewClickListener);
            psf.mStatusAdapter.setImageGroupViewItemClickListener(mActivity.mImageGroupViewItemClickListener);
            psf.mStatusAdapter.setShowHeadMargin(true);
            psf.mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING);

            psf.mRecyclerView.setAdapter(psf.mStatusAdapter);
            psf.mRecyclerView.setLayoutManager(psf.mLayoutManager);
            psf.mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_light,//加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light, android.R.color.holo_red_light);
            psf.mSwipeRefreshLayout.setProgressViewOffset(false, 0, DipConvertPxUtil.dip2px(getActivity(), 130));
            psf.mSwipeRefreshLayout.setEnabled(false);

            if (mCategoryId == Constant.PAGE_SECETION_FRAGEMENT_TYPE_FRIENDS) {
                HttpService.getFriendsTimeline(Utils.accessToken, mWebListener, page, false, false, null);
                psf.mSwipeRefreshLayout.setOnRefreshListener(new MyOnRefreshListener(REFRESH_LISTENER_TYPE_FRINENDS));
                psf.mRecyclerView.setOnScrollListener(new MyOnScrollListener(SCROLL_LISTENER_TYPE_FRIENDS));
                psf.mStatusAdapter.setLoadingErrorListener(mFriendsLoadingErrorViewClickListener);
                psf.mStatusAdapter.setLoadingMoreErrorListener(mFriendsLoadingMoreErrorViewClickListener);
            } else if (mCategoryId == Constant.PAGE_SECETION_FRAGEMENT_TYPE_BILATERA) {
                HttpService.getBiatusesTimeline(Utils.accessToken, mWebListener, page, false, false, null);
                psf.mSwipeRefreshLayout.setOnRefreshListener(new MyOnRefreshListener(REFRESH_LISTENER_TYPE_BILATERA));
                psf.mRecyclerView.setOnScrollListener(new MyOnScrollListener(SCROLL_LISTENER_TYPE_BILATERA));
                psf.mStatusAdapter.setLoadingErrorListener(mBiatusesLoadingErrorViewClickListener);
                psf.mStatusAdapter.setLoadingMoreErrorListener(mBiatusesLoadingMoreErrorViewClickListener);
            } else if (mCategoryId == Constant.PAGE_SECETION_FRAGEMENT_TYPE_PUBILE) {
                HttpService.getPublicTimeline(Utils.accessToken, mWebListener, 1, false, true);
                psf.mSwipeRefreshLayout.setOnRefreshListener(new MyOnRefreshListener(REFRESH_LISTENER_TYPE_PUBLIC));
                psf.mRecyclerView.setOnScrollListener(new MyOnScrollListener(SCROLL_LISTENER_TYPE_PUBLIC));
                psf.mStatusAdapter.setLoadingErrorListener(mPublicLoadingErrorViewClickListener);
                psf.mStatusAdapter.setLoadingMoreErrorListener(mPublicLoadingMoreErrorViewClickListener);
            }

            return psf.mView;
        }
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

        new MaterialDialog.Builder(getContext())
                .items(R.array.status_card_more_menus)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        StatusesAPI statusesAPI;
                        switch (position){
                            case 0:
                                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                                statusesAPI.favoritesCreate(ftl.idstr, mFavoritesCreateRequestListener);
                                break;
                            case 1:
                                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                                statusesAPI.favoritesDestroy(ftl.idstr, mFavoritesDestroyRequestListener);
                                break;
                            case 2:
                                if (isCurrentUser){
                                    statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                                    statusesAPI.destroy(Long.parseLong(ftl.idstr), mStatusesDestroyRequestListener);
                                }else {
                                    Utils.copy(ftl.text, getActivity());
                                    Snackbar.make(mRecyclerView, getResources().getString(R.string.delete_comment_copy),
                                            Snackbar.LENGTH_LONG).show();
                                }
                                break;
                            case 3:
                                Utils.copy(ftl.text, getActivity());
                                Snackbar.make(mRecyclerView, getResources().getString(R.string.delete_comment_copy),
                                        Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                }).show();

//        new AlertDialogWrapper.Builder(getActivity())
//                .setItems(itmeList.toArray(new String[itmeList.size()]), new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        StatusesAPI statusesAPI;
//                        switch (which){
//                            case 0:
//                                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//                                statusesAPI.favoritesCreate(ftl.idstr, mFavoritesCreateRequestListener);
//                                break;
//                            case 1:
//                                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//                                statusesAPI.favoritesDestroy(ftl.idstr, mFavoritesDestroyRequestListener);
//                                break;
//                            case 2:
//                                if (isCurrentUser){
//                                    statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//                                    statusesAPI.destroy(Long.parseLong(ftl.idstr), mStatusesDestroyRequestListener);
//                                }else {
//                                    Utils.copy(ftl.text, getActivity());
//                                    Snackbar.make(mRecyclerView, getResources().getString(R.string.delete_comment_copy),
//                                            Snackbar.LENGTH_LONG).show();
//                                }
//                                break;
//                            case 3:
//                                Utils.copy(ftl.text, getActivity());
//                                Snackbar.make(mRecyclerView, getResources().getString(R.string.delete_comment_copy),
//                                        Snackbar.LENGTH_LONG).show();
//                                break;
//                        }
//                    }
//
//                })
//                .show();
    }

   private void bindFriendsTimelineData(Bundle params, Object result){

       PageSectionFragment fragment = mFragments.get(Constant.PAGE_SECETION_FRAGEMENT_TYPE_FRIENDS).get();
       if (fragment == null) fragment = PageSectionFragment.newInstance(Constant.PAGE_SECETION_FRAGEMENT_TYPE_FRIENDS, mActivity);
       boolean isRefresh = params.getBoolean(WebServiceConfigure.HTTP_GET_FRIENDS_TIMELINE_IS_REFRESH);
       // test code
//       result = null;
//       if (fragment.mFriendsTimelineResultList.size()>0){
//           result = null;
//       }
       if (result != null){
           List<FriendsTimelineResult.FriendsTimeline> friendsTimelineList =
                   (List<FriendsTimelineResult.FriendsTimeline>)result;
           int dataSize = friendsTimelineList.size();
//           dataSize = 0;  // test code

           if (dataSize == 0 && mFriendsTimelineResultList.size() == 0){
               fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_EMPTY);
               return;
           }

           if (isRefresh){
               if (dataSize > 0) {
                   fragment.mFriendsTimelineResultList.addAll(0, friendsTimelineList);
                   fragment.mStatusAdapter.setDataByNet(fragment.mFriendsTimelineResultList);
                   fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_NONE);
               }
               fragment.mSwipeRefreshLayout.setRefreshing(false);
           }else {
               fragment.isLoadMoreData = false;
               fragment.mFriendsTimelineResultList.addAll(friendsTimelineList);
               fragment.mStatusAdapter.setDataByNet(fragment.mFriendsTimelineResultList);
               fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_NONE);
           }

           if (dataSize > 0){
               long newSinceId = Long.parseLong(friendsTimelineList.get(0).idstr);
               if (newSinceId > mFriendsTimelineSinceId){
                   mFriendsTimelineSinceId = newSinceId;
               }
           }
           fragment.mSwipeRefreshLayout.setEnabled(true);
       }else {
           if (fragment.mFriendsTimelineSinceId == 0){
               fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_ERROR);
           }else if (!isRefresh && mFriendsTimelineSinceId != 0){
               fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE_ERROR);
           }else if (isRefresh && mFriendsTimelineSinceId != 0){
               fragment.mSwipeRefreshLayout.setRefreshing(false);
           }
       }

   }

    private void bindPublicTimelineData(Bundle params, Object result){
        List<FriendsTimelineResult.FriendsTimeline> publicTimelineList =
                (List<FriendsTimelineResult.FriendsTimeline>)result;
        PageSectionFragment fragment = mFragments.get(Constant.PAGE_SECETION_FRAGEMENT_TYPE_PUBILE).get();
        if (fragment == null) fragment = PageSectionFragment.newInstance(Constant.PAGE_SECETION_FRAGEMENT_TYPE_PUBILE, mActivity);
        boolean isRefresh = params.getBoolean(WebServiceConfigure.HTTP_GET_PUBLIC_TIMELINE_IS_REFRESH);
        if(result != null){
            if (isRefresh){
                fragment.mFriendsTimelineResultList.addAll(0, publicTimelineList);
                fragment.mStatusAdapter.setDataByNet(fragment.mFriendsTimelineResultList);
                fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_NONE);
                fragment.mSwipeRefreshLayout.setRefreshing(false);
            } else {
                fragment.mFriendsTimelineResultList.addAll(publicTimelineList);
                fragment.mStatusAdapter.setDataByNet(fragment.mFriendsTimelineResultList);
                fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_NONE);
                fragment.isLoadMoreData = false;
            }
            fragment.mSwipeRefreshLayout.setEnabled(true);
        }else {
            if (fragment.mFriendsTimelineSinceId == 0){
               fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_ERROR);
            }else if (!isRefresh && mFriendsTimelineSinceId != 0){
                fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE_ERROR);
            }else if (isRefresh && mFriendsTimelineSinceId != 0){
                fragment.mSwipeRefreshLayout.setRefreshing(false);
            }
        }

    }

    private void bindBilateraTimelineData(Bundle params, Object result){
        List<FriendsTimelineResult.FriendsTimeline> bilateraTimelineList =
                (List<FriendsTimelineResult.FriendsTimeline>)result;
        PageSectionFragment fragment = mFragments.get(Constant.PAGE_SECETION_FRAGEMENT_TYPE_BILATERA).get();
        if (fragment == null) fragment = PageSectionFragment.newInstance(Constant.PAGE_SECETION_FRAGEMENT_TYPE_BILATERA, mActivity);
        boolean isRefresh = params.getBoolean(WebServiceConfigure.HTTP_GET_BILATERAL_TIMELINE_IS_REFRESH);
        if (result != null){
            int dataSize = bilateraTimelineList.size();
            if (dataSize == 0 && mFriendsTimelineResultList.size() == 0){
                fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_EMPTY);
                return;
            }

            if (isRefresh){
                if (dataSize > 0) {
                    fragment.mFriendsTimelineResultList.addAll(0, bilateraTimelineList);
                    fragment.mStatusAdapter.setDataByNet(fragment.mFriendsTimelineResultList);
                    fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_NONE);
                }
                fragment.mSwipeRefreshLayout.setRefreshing(false);
            }else {
                fragment.isLoadMoreData = false;
                fragment.mFriendsTimelineResultList.addAll(bilateraTimelineList);
                fragment.mStatusAdapter.setDataByNet(fragment.mFriendsTimelineResultList);
                fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_NONE);
            }

            if (dataSize > 0){
                long newSinceId = Long.parseLong(bilateraTimelineList.get(0).idstr);
                if (newSinceId > mBiatusesTimelineSinceId){
                    mBiatusesTimelineSinceId = newSinceId;
                }
            }
            fragment.mSwipeRefreshLayout.setEnabled(true);
        }else {
            if (fragment.mFriendsTimelineSinceId == 0){
                fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_ERROR);
            }else if (!isRefresh && mFriendsTimelineSinceId != 0){
                fragment.mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE_ERROR);
            }else if (isRefresh && mFriendsTimelineSinceId != 0){
                fragment.mSwipeRefreshLayout.setRefreshing(false);
            }
        }

    }

    public enum Direction {UP, DOWN}

    private class  MyOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener{

        private int type;

        public MyOnRefreshListener(int type){
            this.type = type;
        }

        @Override
        public void onRefresh() {
            if (type == REFRESH_LISTENER_TYPE_FRINENDS){
                HttpService.getFriendsTimeline(Utils.accessToken, mWebListener, 1,true, true, String.valueOf(mFriendsTimelineSinceId));
            }else if (type == REFRESH_LISTENER_TYPE_PUBLIC){
                HttpService.getPublicTimeline(Utils.accessToken, mWebListener, 1, true, false);
            }else if (type == REFRESH_LISTENER_TYPE_BILATERA){
                HttpService.getBiatusesTimeline(Utils.accessToken, mWebListener, 1, true, true, String.valueOf(mBiatusesTimelineSinceId));
            }
        }
    }

    private class MyOnScrollListener extends RecyclerView.OnScrollListener{
        private static final int MIN_SCROLL_TO_HIDE = 10;
        public HashMap<View, Direction> viewsToHide = new HashMap<>();
        private int lastVisibleItem;
        private int type;
        private boolean hidden;
        private int accummulatedDy;
        private int totalDy;
        private int initialOffset;


        public MyOnScrollListener(int type){
            this.type = type;
            viewsToHide.put(MainFragment.sAppBarlayout, Direction.UP);
            viewsToHide.put(MainFragment.sWhiteFab, Direction.DOWN);
            initialOffset = DipConvertPxUtil.dip2px(getActivity(), 90);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView,
                                         int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mStatusAdapter.getItemCount() && !isLoadMoreData) {
                isLoadMoreData = true;
                mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE);
                page++;
                if (type == SCROLL_LISTENER_TYPE_FRIENDS){
                    HttpService.getFriendsTimeline(Utils.accessToken, mWebListener, page, false, true, null);
                }else if (type == SCROLL_LISTENER_TYPE_PUBLIC){
                    HttpService.getPublicTimeline(Utils.accessToken, mWebListener, 1, false, false);
                }else if (type == SCROLL_LISTENER_TYPE_BILATERA) {
                    HttpService.getBiatusesTimeline(Utils.accessToken, mWebListener, page, false, true, null);
                }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

            totalDy += dy;

            if (totalDy < initialOffset) {
                return;
            }

            if (dy > 0) {
                accummulatedDy = accummulatedDy > 0 ? accummulatedDy + dy : dy;
                if (accummulatedDy > MIN_SCROLL_TO_HIDE) {
                    hideViews();
                }
            } else if (dy < 0) {
                accummulatedDy = accummulatedDy < 0 ? accummulatedDy + dy : dy;
                if (accummulatedDy < -MIN_SCROLL_TO_HIDE) {
                    showViews();
                }
            }
        }

        public void hideViews() {
            if (!hidden) {
                hidden = true;
                for (View view : viewsToHide.keySet()) {
                    hideView(view, viewsToHide.get(view));
                }
            }
        }

        private void showViews() {
            if (hidden) {
                hidden = false;
                for (View view : viewsToHide.keySet()) {
                    showView(view);
                }
            }
        }

        private void hideView(View view, Direction direction) {
            int height = calculateTranslation(view);
            int translateY = direction == Direction.UP ? -height : height;
            runTranslateAnimation(view, translateY, new AccelerateInterpolator(3));
        }

        /**
         * Takes height + margins
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
    }




}
