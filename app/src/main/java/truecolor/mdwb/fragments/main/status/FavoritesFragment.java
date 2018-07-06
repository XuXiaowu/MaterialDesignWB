package truecolor.mdwb.fragments.main.status;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import truecolor.mdwb.R;
import truecolor.mdwb.adapter.BaseRecycleAdapter;
import truecolor.mdwb.adapter.StatusAdapter;
import truecolor.mdwb.api.StatusesAPI;
import truecolor.mdwb.apps.MainActivity;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.global.WebServiceConfigure;
import truecolor.mdwb.logics.http.HttpService;
import truecolor.mdwb.model.FavoritesResult;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.utils.Utils;
import truecolor.webdataloader.WebListener;

/**
 * Created by xiaowu on 15/10/30.
 */
public class FavoritesFragment extends Fragment {

    private MainActivity mActivity;
    private View mView;
    private RecyclerView mListView;
    private LinearLayoutManager mLayoutManager;
    private StatusAdapter mStatusAdapter;
    private ArrayList<Object> mFriendsTimelineResultList;
    private int mPage = 1;
    private boolean mIsLoadMoreData = true;
    private int mRemovedStatusesPos;

    public FavoritesFragment(MainActivity activity){
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.favorites_fragment, container, false);
        initView();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        HttpService.getFavorites(Utils.accessToken, mWebListener, true, mPage);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorites, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(mView, "真要点我?", Snackbar.LENGTH_SHORT)
                    .show();
            return true;
        }else if(id == android.R.id.home){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private WebListener mWebListener = new WebListener() {
        @Override
        public void onDataLoadFinished(int service, Bundle params, Object result) {
            if (service == WebServiceConfigure.GET_FAVORITES){
                if (result != null){
                    List<FavoritesResult.Status> friendsTimelineList = (List<FavoritesResult.Status>)result;
                    int dataSize = friendsTimelineList.size();

                    if (dataSize == 0 && mFriendsTimelineResultList.size() == 0){
                        mStatusAdapter.setState(BaseRecycleAdapter.STATE_EMPTY);
                        return;
                    }

                    for (int i = 0; i < dataSize; i++) {
                        mFriendsTimelineResultList.add(friendsTimelineList.get(i).status);
                    }
                    mStatusAdapter.setDataByNet(mFriendsTimelineResultList);
                    mStatusAdapter.setState(BaseRecycleAdapter.STATE_NONE);
                    if (friendsTimelineList.size() == 50) {
                        mIsLoadMoreData = false;
                    }
                }else {
                    if (mPage == 1){
                        mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_ERROR);
                    }else {
                        mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE_ERROR);
                    }
                }

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
            mHandler.sendEmptyMessage(Constant.FAVORITES_DESTROY_FAIL);
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.FAVORITES_DESTROY_SUCCESS:
                    mFriendsTimelineResultList.remove(mRemovedStatusesPos);
                    mStatusAdapter.notifyItemRemoved(mRemovedStatusesPos);
                    break;

            }
        }
    };

    private RecyclerView.OnScrollListener mListViewScrollListener = new RecyclerView.OnScrollListener() {

        private int lastVisibleItem;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mStatusAdapter.getItemCount() && !mIsLoadMoreData) {
                mIsLoadMoreData = true;
                mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE);
                mPage++;
                HttpService.getFavorites(Utils.accessToken, mWebListener, true, mPage);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
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

    private View.OnClickListener mLoadingErrorViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HttpService.getFavorites(Utils.accessToken, mWebListener, false, mPage);
            if (mPage == 1){
                mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING);
            }else {
                mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING_MORE);
            }
        }
    };

    private void doMoreMenuAction(View v){
        final FriendsTimelineResult.FriendsTimeline ftl = (FriendsTimelineResult.FriendsTimeline) v.getTag();
        mRemovedStatusesPos = v.getId();
        String items[] = getResources().getStringArray(R.array.favorites_more_menus);

        new MaterialDialog.Builder(getActivity())
                .title(R.string.publish_select_photo_sources)
                .items(R.array.favorites_more_menus)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        StatusesAPI statusesAPI;
                        switch (position){
                            case 0:
                                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
                                statusesAPI.favoritesDestroy(ftl.idstr, mFavoritesDestroyRequestListener);
                                break;
                            case 1:
                                Utils.copy(ftl.text, getActivity());
                                Snackbar.make(mView, getResources().getString(R.string.delete_comment_copy),
                                        Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                }).show();

//        new AlertDialogWrapper.Builder(getActivity())
//                .setItems(items, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        StatusesAPI statusesAPI;
//                        switch (which){
//                            case 0:
//                                statusesAPI = new StatusesAPI(Utils.getWeiboAccessToken());
//                                statusesAPI.favoritesDestroy(ftl.idstr, mFavoritesDestroyRequestListener);
//                                break;
//                            case 1:
//                                Utils.copy(ftl.text, getActivity());
//                                Snackbar.make(mView, getResources().getString(R.string.delete_comment_copy),
//                                        Snackbar.LENGTH_LONG).show();
//                                break;
//                        }
//                    }
//                })
//                .show();
    }


    private void initView(){
        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar);
        mActivity.setSupportActionBar(toolbar);

        ActionBar actionBar = mActivity.getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.favorites));

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mFriendsTimelineResultList = new ArrayList<>();
        mListView = (RecyclerView) mView.findViewById(R.id.list_view);
        mStatusAdapter = new StatusAdapter(mActivity, mFriendsTimelineResultList, 0);
        mStatusAdapter.setGoodViewClickListener(mActivity.mGoodViewClickListener);
        mStatusAdapter.setCommentViewClickListener(mActivity.mCommentViewClickListener);
        mStatusAdapter.setRepostViewClickListener(mActivity.mRepostViewClickListener);
        mStatusAdapter.setCardViewClickListener(mActivity.mCardViewClickListener);
        mStatusAdapter.setCardViewLongClickListener(mCardViewLongClickListener);
        mStatusAdapter.setMenuMoreViewClickListener(mMenuMoreViewClickListener);
        mStatusAdapter.setImageGroupViewItemClickListener(mActivity.mImageGroupViewItemClickListener);
//        mStatusAdapter.setLoadingErrorViewClickListener(mLoadingErrorViewClickListener);
//        mStatusAdapter.setLoadingMoreErrorViewClickListener(mLoadingErrorViewClickListener);
        mStatusAdapter.setLoadingErrorListener(mLoadingErrorViewClickListener);
        mStatusAdapter.setLoadingMoreErrorListener(mLoadingErrorViewClickListener);
        mStatusAdapter.setShowHeadMargin(false);
        mStatusAdapter.setState(BaseRecycleAdapter.STATE_LOADING);
        mListView.setAdapter(mStatusAdapter);
        mListView.setLayoutManager(mLayoutManager);
        mListView.setOnScrollListener(mListViewScrollListener);
    }
}
