package truecolor.mdwb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import truecolor.mdwb.R;
import truecolor.mdwb.ViewHolder.AbsItemHolder;
import truecolor.mdwb.ViewHolder.EmptyViewHolder;
import truecolor.mdwb.ViewHolder.LoadingViewHolder;
import truecolor.mdwb.ViewHolder.SearchUsersViewHolder;
import truecolor.mdwb.model.UsersResult;
import truecolor.mdwb.utils.Utils;

/**
 * Created by xiaowu on 15/11/9.
 */
public class SearchUsersAdapter extends BaseRecycleAdapter{

    private View.OnClickListener mItemClickListener;
    private int mCurNum;
    private int mLoadingViewHeight;

    private View mLoadingView;
    private View mEmptyView;

    public SearchUsersAdapter(Context mContext, ArrayList<Object> data, View.OnClickListener clickListener, int loadingViewHeight) {
        super(mContext);
        mItems = data;
        mItemClickListener = clickListener;
        mLoadingViewHeight = loadingViewHeight;
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

    @Override
    public void reset() {

    }

    @Override
    public boolean hasHeadView() {
        return false;
    }

    @Override
    public AbsItemHolder bindViewHolder() {
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.search_users_item_view, null);
        SearchUsersViewHolder searchUsersViewHolder = new SearchUsersViewHolder(view);
        return searchUsersViewHolder;
    }

    @Override
    public AbsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout.LayoutParams layoutParams;
        switch (viewType) {
            case STATE_LOADING:
                if(mLoadingView == null){
                    mLoadingView = LayoutInflater.from(mContext).inflate(R.layout.main_fragment_section_recycler_item_loading_view, null);
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mLoadingViewHeight);
                    mLoadingView.setLayoutParams(layoutParams);
                }
                LoadingViewHolder loadingHolder = new LoadingViewHolder(mLoadingView);
                return loadingHolder;

            case STATE_EMPTY:
                if(mEmptyView == null){
                    mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.loading_empty_view, null);
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mLoadingViewHeight);
                    mEmptyView.setLayoutParams(layoutParams);
                }
                EmptyViewHolder emptyViewHolder = new EmptyViewHolder(mEmptyView);
                emptyViewHolder.mTextView.setText(mEmptyText);
                return emptyViewHolder;
            default:
                return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void bindData(AbsItemHolder holder, int position) {
        if (mState == BaseRecycleAdapter.STATE_NONE) {
            SearchUsersViewHolder suvh = (SearchUsersViewHolder) holder;
            Object itemData = mItems.get(position);
            if (itemData != null) {
                UsersResult usersResult = (UsersResult) itemData;
                suvh.mRootView.setTag(usersResult.uid);
                suvh.mRootView.setOnClickListener(mItemClickListener);
                suvh.mUserNameView.setText(usersResult.screen_name);
                suvh.mFollowersCountView.setText(mContext.getResources().
                        getString(R.string.sreach_user_followers_count, usersResult.followers_count));
            }
        } else if (mState == BaseRecycleAdapter.STATE_EMPTY){
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            emptyViewHolder.mTextView.setText(mEmptyText);
        }
    }

}
