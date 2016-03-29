package truecolor.mdwb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

import java.util.ArrayList;
import java.util.List;

import truecolor.mdwb.R;
import truecolor.mdwb.ViewHolder.AbsItemHolder;
import truecolor.mdwb.ViewHolder.EmptyViewHolder;
import truecolor.mdwb.ViewHolder.LoadingErrorViewHolder;
import truecolor.mdwb.ViewHolder.LoadingViewHolder;
import truecolor.mdwb.ViewHolder.StatusViewHolder;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.utils.Utils;

/**
 * Created by xiaowu on 15/11/2.
 */
public class StatusAdapter extends BaseRecycleAdapter {

    protected ArrayList<Object> mItems;
    private int mCurNum;
    private Context mContext;
    private BitmapUtils mBitmapUtils;
    private View.OnClickListener mGoodViewClickListener;
    private View.OnClickListener mRepostViewClickListener;
    private View.OnClickListener mCommentViewClickListener;
    private View.OnClickListener mCardViewClickListener;
    private View.OnLongClickListener mCardViewLongClickListener;
    private View.OnClickListener mMenuMoreViewClickListener;
    private View.OnClickListener mImageGroupViewItemClickListener;
    private BitmapDisplayConfig mBitmapDisplayConfig;

    private int mLoadingViewHeight;

    private View mLoadingView;
    private View mEmptyView;
    private View mLoadingErrorView;

    private boolean mIsShowHeadMargin;

    public StatusAdapter(Context mContext, ArrayList<Object> mFriendsTimelineResultList, int loadingViewHeight) {
        super(mContext);
        this.mContext = mContext;
        this.mItems = mFriendsTimelineResultList;
        mBitmapDisplayConfig = new BitmapDisplayConfig();
        mBitmapDisplayConfig.setLoadingDrawable(mContext.getResources().getDrawable(R.mipmap.ic_user));
        mBitmapUtils = new BitmapUtils(mContext);
        mLoadingViewHeight = loadingViewHeight;
    }

    public void setGoodViewClickListener(View.OnClickListener mGoodViewClickListener) {
        this.mGoodViewClickListener = mGoodViewClickListener;
    }

    public void setRepostViewClickListener(View.OnClickListener mRepostViewClickListener) {
        this.mRepostViewClickListener = mRepostViewClickListener;
    }

    public void setCommentViewClickListener(View.OnClickListener mCommentViewClickListener) {
        this.mCommentViewClickListener = mCommentViewClickListener;
    }

    public void setCardViewClickListener(View.OnClickListener mCardViewClickListener) {
        this.mCardViewClickListener = mCardViewClickListener;
    }

    public void setCardViewLongClickListener(View.OnLongClickListener mCardViewLongClickListener) {
        this.mCardViewLongClickListener = mCardViewLongClickListener;
    }

    public void setMenuMoreViewClickListener(View.OnClickListener mMenuMoreViewClickListener) {
        this.mMenuMoreViewClickListener = mMenuMoreViewClickListener;
    }

    public void setImageGroupViewItemClickListener(View.OnClickListener mImageGroupViewItemClickListener) {
        this.mImageGroupViewItemClickListener = mImageGroupViewItemClickListener;
    }

    private View.OnClickListener mWeiboContentViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = (View) v.getTag();
            view.performClick();
        }
    };

    private View.OnLongClickListener mWeiboContentViewLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            View view = (View) v.getTag();
            view.performLongClick();
            return false;
        }
    };

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
                inflate(R.layout.main_fragment_section_recycler_view_item, null);
        StatusViewHolder viewHolder = new StatusViewHolder(view);
        return viewHolder;
    }

    @Override
    public AbsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout.LayoutParams layoutParams;
        switch (viewType) {

            case STATE_LOADING:
                if (mLoadingView == null) {
                    mLoadingView = LayoutInflater.from(mContext).inflate(R.layout.main_fragment_section_recycler_item_loading_view, null);
                    int loadingViewHeight;
                    if (mLoadingViewHeight == 0) {
                        loadingViewHeight = Utils.getScreenHeight();
                    } else {
                        loadingViewHeight = mLoadingViewHeight;
                    }
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, loadingViewHeight);
                    mLoadingView.setLayoutParams(layoutParams);
                }
                LoadingViewHolder loadingHolder = new LoadingViewHolder(mLoadingView);
                return loadingHolder;

            case STATE_EMPTY:
                if (mEmptyView == null) {
                    mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.loading_empty_view, null);
                    int loadingViewHeight;
                    if (mLoadingViewHeight == 0) {
                        loadingViewHeight = Utils.getScreenHeight();
                    } else {
                        loadingViewHeight = mLoadingViewHeight;
                    }
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, loadingViewHeight);
                    mEmptyView.setLayoutParams(layoutParams);
                }
                EmptyViewHolder emptyViewHolder = new EmptyViewHolder(mEmptyView);
                emptyViewHolder.mTextView.setText(mEmptyText);
                return emptyViewHolder;

            case STATE_LOADING_ERROR:
                if (mLoadingErrorView == null) {
                    mLoadingErrorView = LayoutInflater.from(mContext).inflate(R.layout.main_fragment_section_recycler_item_loading_error_view, null);
                    int loadingViewHeight;
                    if (mLoadingViewHeight == 0) {
                        loadingViewHeight = Utils.getScreenHeight();
                    } else {
                        loadingViewHeight = mLoadingViewHeight;
                    }
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, loadingViewHeight);
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
    public void bindData(AbsItemHolder holder, int position) {

        if (mState == BaseRecycleAdapter.STATE_LOADING_MORE) {
            return;
        } else if (mState == BaseRecycleAdapter.STATE_LOADING) {
            return;
        } else if (mState == BaseRecycleAdapter.STATE_LOADING_ERROR) {
//            LoadingErrorViewHolder loadingErrorViewHolder = (LoadingErrorViewHolder) holder;
//            loadingErrorViewHolder.mErrorView.setOnClickListener(mLoadingErrorViewClickListener);
            return;
        } else if (mState == BaseRecycleAdapter.STATE_LOADING_MORE_ERROR) {
//            LoadingErrorViewHolder loadingErrorViewHolder = (LoadingErrorViewHolder) holder;
////            if(loadingErrorViewHolder.mErrorView != null){
//                loadingErrorViewHolder.mErrorView.setOnClickListener(mLoadingMoreErrorViewClickListener);
////            }
            return;
        } else if (mState == BaseRecycleAdapter.STATE_EMPTY) {
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            emptyViewHolder.mTextView.setText(mEmptyText);
            return;
        } else if (mState == BaseRecycleAdapter.STATE_NONE) {
            Object object = mItems.get(position);
            StatusViewHolder itemViewHoler = (StatusViewHolder) holder;
            if (object != null) {
                FriendsTimelineResult.FriendsTimeline ft = (FriendsTimelineResult.FriendsTimeline) object;
                bindStatusItemData(itemViewHoler, ft, position);
            }
        }
    }

    @Override
    public int getItemNum() {

        if (mState != BaseRecycleAdapter.STATE_NONE) {
            return mCurNum + 1;
        } else {
            return mCurNum;
        }
    }

    @Override
    public void setDataNone(ArrayList<Object> data) {
        mCurNum = data.size();
        mItems = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (mState == BaseRecycleAdapter.STATE_LOADING_MORE && position == getItemNum() - 1) {
            return BaseRecycleAdapter.STATE_LOADING_MORE;
        } else if (mState == BaseRecycleAdapter.STATE_LOADING_MORE_ERROR && position == getItemNum() - 1) {
            return BaseRecycleAdapter.STATE_LOADING_MORE_ERROR;
        } else {
            return super.getItemViewType(position);
        }
    }

    public void setShowHeadMargin(boolean mIsShowHeadMargin) {
        this.mIsShowHeadMargin = mIsShowHeadMargin;
    }

    private void bindStatusItemData(StatusViewHolder itemViewHoler, FriendsTimelineResult.FriendsTimeline ft, int position) {
        if (ft.user == null) {
            itemViewHoler.mUserNameView.setText("");
            itemViewHoler.mUserSubInfoView.setText(ft.created_at.substring(0, 20));
            itemViewHoler.mHeandView.setImageResource(R.mipmap.ic_user);
        } else {
            itemViewHoler.mUserNameView.setText(ft.user.name);
            itemViewHoler.mUserSubInfoView.setText(ft.created_at.substring(0, 20) + Html.fromHtml(ft.source));
            mBitmapUtils.display(itemViewHoler.mHeandView, ft.user.avatar_large, mBitmapDisplayConfig);
        }

        Editable editable = Utils.getEmotionsContent(itemViewHoler.mWeiboContentView, ft.text, mContext);
        itemViewHoler.mWeiboContentView.setText(editable);
        itemViewHoler.mWeiboContentView.setTag(itemViewHoler.mCardView);
        itemViewHoler.mWeiboContentView.setOnClickListener(mWeiboContentViewClickListener);
        itemViewHoler.mWeiboContentView.setOnLongClickListener(mWeiboContentViewLongClickListener);
        itemViewHoler.mGooodNumView.setText(String.valueOf(ft.attitudes_count));
        itemViewHoler.mRepostNunView.setText(String.valueOf(ft.reposts_count));
        itemViewHoler.mCommentNumView.setText(String.valueOf(ft.comments_count));
        itemViewHoler.mGoodView.setOnClickListener(mGoodViewClickListener);
        itemViewHoler.mRepostView.setTag(ft.idstr);
        itemViewHoler.mRepostView.setOnClickListener(mRepostViewClickListener);
        itemViewHoler.mCommentView.setTag(ft.idstr);
        itemViewHoler.mCommentView.setOnClickListener(mCommentViewClickListener);
        itemViewHoler.mCardView.setTag(ft);
        itemViewHoler.mCardView.setId(position);
        itemViewHoler.mCardView.setOnClickListener(mCardViewClickListener);
        itemViewHoler.mCardView.setOnLongClickListener(mCardViewLongClickListener);
        itemViewHoler.mMenuMoreView.setTag(ft);
        itemViewHoler.mMenuMoreView.setId(position);
        itemViewHoler.mMenuMoreView.setOnClickListener(mMenuMoreViewClickListener);
        if (ft.pic_urls != null && ft.pic_urls.length != 0) {
            itemViewHoler.mImageGroupView.setImageGroupViewItemClickListener(mImageGroupViewItemClickListener);
            itemViewHoler.mImageGroupView.bindImage(ft, mBitmapUtils);
            itemViewHoler.mImageGroupView.setVisibility(View.VISIBLE);
        } else {
            itemViewHoler.mImageGroupView.setVisibility(View.GONE);
        }

        if (ft.retweeted_status != null) {
            itemViewHoler.mCutOffLineView.setVisibility(View.VISIBLE);
            itemViewHoler.mRepostWeiboContentView.setVisibility(View.VISIBLE);
            itemViewHoler.mRepostWeiboContentView.setTag(itemViewHoler.mCardView);
            itemViewHoler.mRepostWeiboContentView.setOnClickListener(mWeiboContentViewClickListener);
            itemViewHoler.mRepostWeiboContentView.setOnLongClickListener(mWeiboContentViewLongClickListener);
            Editable repostEditable = Utils.getEmotionsContent(itemViewHoler.mRepostWeiboContentView, ft.retweeted_status.text, mContext);
            itemViewHoler.mRepostWeiboContentView.setText("@" + ft.retweeted_status.user.name + ":" + repostEditable);
            if (ft.retweeted_status.pic_urls != null && ft.retweeted_status.pic_urls.length != 0) {
                itemViewHoler.mRepostImageGroupView.setImageGroupViewItemClickListener(mImageGroupViewItemClickListener);
                itemViewHoler.mRepostImageGroupView.bindImage(ft.retweeted_status, mBitmapUtils);
                itemViewHoler.mRepostImageGroupView.setVisibility(View.VISIBLE);
            } else {
                itemViewHoler.mRepostImageGroupView.setVisibility(View.GONE);
            }
        } else {
            itemViewHoler.mCutOffLineView.setVisibility(View.GONE);
            itemViewHoler.mRepostWeiboContentView.setVisibility(View.GONE);
            itemViewHoler.mRepostImageGroupView.setVisibility(View.GONE);
        }

        if (mIsShowHeadMargin) {
            if (position == 0) {
                itemViewHoler.mHeadMarginView.setVisibility(View.VISIBLE);
            } else {
                itemViewHoler.mHeadMarginView.setVisibility(View.GONE);
            }
        } else {
            itemViewHoler.mHeadMarginView.setVisibility(View.GONE);
        }
    }


}
