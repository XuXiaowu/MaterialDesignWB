package truecolor.mdwb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import truecolor.mdwb.R;
import truecolor.mdwb.ViewHolder.AbsItemHolder;
import truecolor.mdwb.ViewHolder.EmptyViewHolder;
import truecolor.mdwb.ViewHolder.LoadingErrorViewHolder;
import truecolor.mdwb.ViewHolder.LoadingMoreViewHolder;
import truecolor.mdwb.ViewHolder.LoadingViewHolder;
import truecolor.mdwb.utils.Utils;

/**
 * Created by xiaowu on 15-7-20.
 */
public abstract class BaseRecycleAdapter extends RecyclerView.Adapter<AbsItemHolder>{

    public static final int STATE_NONE = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_LOADING_MORE = 2;
    public static final int STATE_EMPTY = 3;
    public static final int STATE_LOADING_ERROR = 4;
    public static final int STATE_LOADING_MORE_ERROR = 5;
    public static final int STATE_HEAD = 6;

//    public static final int STATE_NUMBER = 5;

    protected Context mContext;
    protected int mState;
    protected boolean mHasMore;
    protected ArrayList<Object> mItems;

    private View mHeadView;
    private View mLoadingView;
    private View mLoadingMoreView;
    private View mLoadingErrorView;
    private View mLoadingMoreErrorView;
    private View mEmptyView;

    public String mEmptyText;
    protected View.OnClickListener mLoadingErrorListener;
    protected View.OnClickListener mLoadingMoreErrorListener;
    protected View.OnClickListener mItemClickListener;

    public BaseRecycleAdapter(Context context) {
        super();
        mContext = context;
        mState = STATE_HEAD;
    }

    public final void setItemClickListener(View.OnClickListener listener){
        mItemClickListener = listener;
    }

    public final void setLoadingErrorListener(View.OnClickListener listener){
        mLoadingErrorListener = listener;
    }

    public final void setLoadingMoreErrorListener(View.OnClickListener listener){
        mLoadingMoreErrorListener = listener;
    }

    public final void setState(int state) {
        mState = state;
        notifyDataSetChanged();
    }

    public final int getState(){
        return mState;
    }

    public final boolean hasMore(){
        return mState == STATE_NONE && mHasMore;
    };

    public final void setEmptyText(String emptyText){
        mEmptyText = emptyText;
    }

    public final void setEmptyText(int emptyId){
        mEmptyText = mContext.getResources().getString(emptyId);
    }

    public final void setDataByNet(ArrayList<Object> data){
        if(data == null){
            if(mItems == null) setState(STATE_LOADING_ERROR);
            else setState(STATE_LOADING_ERROR);
        }
        else{
            setData(data);
        }
    }


    private final void setData(ArrayList<Object> data){
        if(mItems == null && data.size() == 0 ){
            setDataNone(data);
            setState(STATE_EMPTY);
        }
        else{
            setDataNone(data);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(hasHeadView()) {
            if (position == 0) {
                return STATE_HEAD;
            } else if ((position == 1 || position == getItemNum() - 1) && mState != STATE_NONE) {
                return mState;
            }
        }
        else{
            if((position == 0 || position == getItemNum() - 1) && mState != STATE_NONE) return mState;
        }
        return STATE_NONE;
    }

    @Override
    public int getItemCount() {
        return getItemNum();
    }

    @Override
    public void onBindViewHolder(AbsItemHolder holder, int position) {
        int itemStatus = getItemViewType(position);
        if(itemStatus == STATE_NONE || itemStatus == STATE_EMPTY){
            bindData(holder, position);
        }
    }

    @Override
    public AbsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout.LayoutParams layoutParams;
        switch (viewType){
//            case STATE_HEAD:
//                return headHolder;


            case STATE_LOADING:
                if(mLoadingView == null){
                    mLoadingView = LayoutInflater.from(mContext).inflate(R.layout.main_fragment_section_recycler_item_loading_view, null);
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.getScreenHeight());
                    mLoadingView.setLayoutParams(layoutParams);
                }
                LoadingViewHolder loadingHolder = new LoadingViewHolder(mLoadingView);
                return loadingHolder;

            case STATE_LOADING_MORE:
                if(mLoadingMoreView == null){
                    mLoadingMoreView = LayoutInflater.from(mContext).inflate(R.layout.main_fragment_section_recycler_item_loading_more_view, null);
                    layoutParams = new RelativeLayout.LayoutParams(Utils.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
                    mLoadingMoreView.setLayoutParams(layoutParams);
                }
                LoadingMoreViewHolder loadingMoreViewHolder = new LoadingMoreViewHolder(mLoadingMoreView);
                return loadingMoreViewHolder;

            case STATE_LOADING_ERROR:
                if(mLoadingErrorView == null){
                    mLoadingErrorView = LayoutInflater.from(mContext).inflate(R.layout.main_fragment_section_recycler_item_loading_error_view, null);
                    mLoadingErrorView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            Utils.getScreenHeight()));
                }
                LoadingErrorViewHolder loadingErrorViewHolder = new LoadingErrorViewHolder(mLoadingErrorView);
                loadingErrorViewHolder.mErrorView.setOnClickListener(mLoadingErrorListener);
                return loadingErrorViewHolder;

            case STATE_LOADING_MORE_ERROR:
                if(mLoadingMoreErrorView == null){
                    mLoadingMoreErrorView = LayoutInflater.from(mContext).inflate(R.layout.main_fragment_section_recycler_item_loading_error_view, null);
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            mContext.getResources().getDimensionPixelSize(R.dimen.loading_error_view_height));
                    mLoadingMoreErrorView.setLayoutParams(layoutParams);
                }
                LoadingErrorViewHolder loadingMoreErrorViewHolder = new LoadingErrorViewHolder(mLoadingMoreErrorView);
                loadingMoreErrorViewHolder.mErrorView.setOnClickListener(mLoadingMoreErrorListener);
                return loadingMoreErrorViewHolder;

            case STATE_EMPTY:
                if(mEmptyView == null){
                    mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.loading_empty_view, null);
                    layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.getScreenHeight());
                    mEmptyView.setLayoutParams(layoutParams);
                }
                EmptyViewHolder emptyViewHolder = new EmptyViewHolder(mEmptyView);
                emptyViewHolder.mTextView.setText(mEmptyText);
                return emptyViewHolder;

            case STATE_NONE:
                return bindViewHolder();
        }
        return null;
    }


    public final boolean isEmpty(){
        if(mItems == null || mItems.isEmpty()) return true;
        return false;
    }

    public abstract void reset();

    public abstract boolean hasHeadView();

    public abstract AbsItemHolder bindViewHolder();

    public abstract void bindData(AbsItemHolder holder, int  position);

    public abstract int getItemNum();

    public abstract void setDataNone(ArrayList<Object> data);


}
