package truecolor.mdwb.ViewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import truecolor.mdwb.R;
import truecolor.mdwb.view.CircleImageView;
import truecolor.mdwb.view.ImageGroupView;

/**
 * Created by xiaowu on 15/11/2.
 */
public class StatusViewHolder extends AbsItemHolder {
    public final View mView;
    public CardView mCardView;
    public TextView mUserNameView;
    public TextView mUserSubInfoView;
    public ImageView mMenuMoreView;
    public TextView mWeiboContentView;
    public CircleImageView mHeandView;
    public ImageGroupView mImageGroupView;
    public TextView mGooodNumView;
    public TextView mRepostNunView;
    public TextView mCommentNumView;
    public LinearLayout mGoodView;
    public LinearLayout mRepostView;
    public LinearLayout mCommentView;
    public View mHeadMarginView;
    public TextView mRepostWeiboContentView;
    public View mCutOffLineView;
    public ImageGroupView mRepostImageGroupView;
    public RelativeLayout mErrorView;

    public StatusViewHolder(View view) {
        super(view);
        mView = view;
        mCardView = (CardView) mView.findViewById(R.id.card_view);
        mUserNameView = (TextView) mView.findViewById(R.id.user_name_view);
        mUserSubInfoView = (TextView) mView.findViewById(R.id.user_sub_info);
        mWeiboContentView = (TextView) mView.findViewById(R.id.weibo_content_view);
        mMenuMoreView = (ImageView) mView.findViewById(R.id.menu_more_view);
        mHeandView = (CircleImageView) mView.findViewById(R.id.head_view);
        mImageGroupView = (ImageGroupView) mView.findViewById(R.id.image_gropu_view);
        mGooodNumView = (TextView) view.findViewById(R.id.goog_num_tv);
        mRepostNunView = (TextView) view.findViewById(R.id.repost_num_tv);
        mCommentNumView = (TextView) view.findViewById(R.id.comment_num_tv);
        mGoodView = (LinearLayout) view.findViewById(R.id.good_view);
        mRepostView = (LinearLayout) view.findViewById(R.id.reword_view);
        mCommentView = (LinearLayout) view.findViewById(R.id.comment_view);
        mHeadMarginView =  mView.findViewById(R.id.heand_margin);
        mRepostWeiboContentView = (TextView) mView.findViewById(R.id.weibo_repost_content_view);
        mCutOffLineView = mView.findViewById(R.id.cut_off_line);
        mRepostImageGroupView = (ImageGroupView) mView.findViewById(R.id.repost_image_gropu_view);
        mErrorView = (RelativeLayout) mView.findViewById(R.id.root_view);
    }
}
