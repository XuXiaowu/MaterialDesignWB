package truecolor.mdwb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

import java.util.ArrayList;

import truecolor.mdwb.R;
import truecolor.mdwb.ViewHolder.AbsItemHolder;
import truecolor.mdwb.ViewHolder.EmptyViewHolder;
import truecolor.mdwb.ViewHolder.FriendsViewHolder;
import truecolor.mdwb.ViewHolder.LoadingErrorViewHolder;
import truecolor.mdwb.ViewHolder.LoadingViewHolder;
import truecolor.mdwb.ViewHolder.SearchUsersViewHolder;
import truecolor.mdwb.model.FriendsTimelineResult;

/**
 * Created by xiaowu on 15/11/23.
 */
public class FriendsAdapter extends BaseRecycleAdapter {

    private int mCurNum;
    private View mLoadingView;
    private View mEmptyView;
    private View mLoadingErrorView;

    private BitmapUtils mBitmapUtils;
    private BitmapDisplayConfig mDisplayConfig;
    private int mLoadingViewHeight;

    private View.OnClickListener mItemClickListener;

    public FriendsAdapter(Context mContext, ArrayList<Object> data, View.OnClickListener itemClickListener, BitmapUtils
            bitmapUtils, BitmapDisplayConfig displayConfig, int loadingViewHeight) {
        super(mContext);
        mItems = data;
        mItemClickListener = itemClickListener;
        mBitmapUtils = bitmapUtils;
        mDisplayConfig = displayConfig;
        mLoadingViewHeight = loadingViewHeight;
    }

    @Override
    public void reset() {

    }

    @Override
    public int getItemViewType(int position) {
        if(mState == BaseRecycleAdapter.STATE_LOADING_MORE && position == getItemNum() - 1){
            return BaseRecycleAdapter.STATE_LOADING_MORE;
        } else if (mState == BaseRecycleAdapter.STATE_LOADING_MORE_ERROR  && position == getItemNum() - 1){
            return BaseRecycleAdapter.STATE_LOADING_MORE_ERROR;
        }else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public AbsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout.LayoutParams layoutParams;
        switch (viewType) {
            case STATE_LOADING:
                if (mLoadingView == null) {
                    mLoadingView = LayoutInflater.from(mContext).inflate(R.layout.main_fragment_section_recycler_item_loading_view, null);
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mLoadingViewHeight);
                    mLoadingView.setLayoutParams(layoutParams);
                }
                LoadingViewHolder loadingHolder = new LoadingViewHolder(mLoadingView);
                return loadingHolder;
            case STATE_EMPTY:
                if (mEmptyView == null) {
                    mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.loading_empty_view, null);
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mLoadingViewHeight);
                    mEmptyView.setLayoutParams(layoutParams);
                }
                EmptyViewHolder emptyViewHolder = new EmptyViewHolder(mEmptyView);
                emptyViewHolder.mTextView.setText(mEmptyText);
                return emptyViewHolder;
            case STATE_LOADING_ERROR:
                if (mLoadingErrorView == null){
                    mLoadingErrorView = LayoutInflater.from(mContext).inflate(R.layout.main_fragment_section_recycler_item_loading_error_view, null);
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mLoadingViewHeight);
                    mLoadingErrorView.setLayoutParams(layoutParams);
                }
                LoadingErrorViewHolder loadingErrorViewHolder = new LoadingErrorViewHolder(mLoadingErrorView);
                loadingErrorViewHolder.mErrorView.setOnClickListener(mLoadingErrorListener);
                return loadingErrorViewHolder;
            default:
                return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public boolean hasHeadView() {
        return false;
    }

    @Override
    public AbsItemHolder bindViewHolder() {
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.friends_item_view, null);
        FriendsViewHolder searchUsersViewHolder = new FriendsViewHolder(view);
        return searchUsersViewHolder;
    }

    @Override
    public void bindData(AbsItemHolder holder, int position) {
        FriendsViewHolder friendsViewHolder = (FriendsViewHolder) holder;
        Object object = mItems.get(position);
        if(object != null){
            FriendsTimelineResult.User user = (FriendsTimelineResult.User)object;
            friendsViewHolder.mUserNameView.setText(user.name);
            mBitmapUtils.display(friendsViewHolder.mHeadView, user.avatar_large, mDisplayConfig);
            friendsViewHolder.mRootView.setTag(user.name);
            friendsViewHolder.mRootView.setOnClickListener(mItemClickListener);
        }
    }

    @Override
    public int getItemNum() {
        if (mState != BaseRecycleAdapter.STATE_NONE){
            return mCurNum + 1;
        }else {
            return mCurNum;
        }
    }

    @Override
    public void setDataNone(ArrayList<Object> data) {
        mCurNum = data.size();
        mItems = data;
    }
}
