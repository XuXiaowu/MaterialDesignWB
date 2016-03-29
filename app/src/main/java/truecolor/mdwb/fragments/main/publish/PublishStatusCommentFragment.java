package truecolor.mdwb.fragments.main.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;

import truecolor.mdwb.R;
import truecolor.mdwb.apps.DetailActivity;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.logics.publish.UploadManager;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.model.PublishStatus;

/**
 * Created by xiaowu on 15/10/12.
 */
public class PublishStatusCommentFragment extends APulishStatusFragment{

    private View mActionBarView;
    private ProgressBar mActionBarLoadingView;
    private TextView mActionBarTitleView;
    private String mStatueseId;
    private String mCommendId;

    private final int PUBLISH_SUCCESS = 1;
    private final int PUBLISH_FAIL = 2;

    public static FriendsTimelineResult.FriendsTimeline EXTRA_FRIENDS_TIMELINE;

    public static PublishStatusCommentFragment newInstance(View actionBarView, String statueseId, String commendId) {
        PublishStatusCommentFragment fragment = new PublishStatusCommentFragment();
        fragment.mActionBarView = actionBarView;
        fragment.mStatueseId = statueseId;
        fragment.mCommendId = commendId;
        return fragment;
    }

    @Override
    public void initView() {
        sendBtn.setOnClickListener(sendBtnClickListener);
        photoBtn.setVisibility(View.GONE);
        editContent.setHint(getResources().getString(R.string.publish_status_comment_hint));
        checkBox.setVisibility(View.GONE);
        initActionBarView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private View.OnClickListener mSnackbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().finish();
            Intent intent = new Intent();
            intent.putExtra(Constant.DETALI_FRIENDS_TIMELINE_TYPE, Constant.DETAIL_FRIENDS_TIMELINE_TYPE_CREATE_COMMENT);
            intent.setClass(getActivity(), DetailActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener sendBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String comment = editContent.getText().toString();
            if (comment.equals("")){
                Snackbar.make(mActionBarView, getResources().getString(R.string.publish_content_no_empty), Snackbar.LENGTH_SHORT).show();
                return;
            }
            mActionBarLoadingView.setVisibility(View.VISIBLE);
            PublishStatus publishStatus = new PublishStatus();
            publishStatus.status = comment;
            publishStatus.id = mStatueseId;
            publishStatus.cid = mCommendId;
            publishStatus.requestListener = mRequestListener;
            publishStatus.view = mActionBarView;
            publishStatus.context = getActivity();
            publishStatus.type = mCommendId==null? Constant.PUBLISH_STATUS_TYPE_COMMENT_CREATE: Constant.PUBLISH_STATUS_TYPE_COMMENT_REPLY;
            UploadManager.addTask(publishStatus);
            Snackbar.make(mActionBarView, getResources().getString(R.string.publish_status_commtnt_uploading), Snackbar.LENGTH_SHORT).show();

        }
    };

    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            EXTRA_FRIENDS_TIMELINE = JSON.parseObject(s, FriendsTimelineResult.FriendsTimeline.class);
            mHandler.sendEmptyMessage(PUBLISH_SUCCESS);
        }

        @Override
        public void onIOException(IOException e) {
            mHandler.sendEmptyMessage(PUBLISH_FAIL);
        }

        @Override
        public void onError(WeiboException e) {
            mHandler.sendEmptyMessage(PUBLISH_FAIL);
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PUBLISH_SUCCESS:
                    mActionBarLoadingView.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= 19 && mActionBarView.isAttachedToWindow()){
                        Snackbar.make(mActionBarView, getResources().getString(R.string.publish_comment_success),
                                Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.publish_go_to_see),
                                mSnackbarClickListener).show();
                    }else {
                        Toast.makeText(getActivity(),getResources().getString(R.string.publish_comment_success), Toast.LENGTH_SHORT).show();
                    }
                    return;
                case PUBLISH_FAIL:
                    mActionBarLoadingView.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= 19 && mActionBarView.isAttachedToWindow()){
                        Snackbar.make(mActionBarView, getResources().getString(R.string.publish_comment_fail),
                                Snackbar.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.publish_comment_fail), Toast.LENGTH_SHORT).show();
                    }
                    return;
            }
        }
    };

    private void initActionBarView(){
        mActionBarTitleView = (TextView) mActionBarView.findViewById(R.id.action_bar_title_view);
        mActionBarLoadingView = (ProgressBar) mActionBarView.findViewById(R.id.action_bar_loading_view);
        mActionBarLoadingView.setVisibility(View.GONE);
        if (mCommendId == null){
            mActionBarTitleView.setText(getResources().getString(R.string.publish_status_comment));
        }else {
            mActionBarTitleView.setText(getResources().getString(R.string.publish_status_comment_reply));
        }

    }

    public static FriendsTimelineResult.FriendsTimeline getExtraFriendsTimeline(){
        return EXTRA_FRIENDS_TIMELINE;
    }


}
