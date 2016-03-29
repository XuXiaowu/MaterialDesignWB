package truecolor.mdwb.fragments.main.publish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;

import truecolor.mdwb.R;
import truecolor.mdwb.apps.DetailActivity;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.logics.publish.UploadManager;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.model.PublishStatus;
import truecolor.mdwb.utils.NotificationUtils;

/**
 * Created by xiaowu on 15/10/15.
 */
public class PublishStatusRepostFragment extends APulishStatusFragment {

    private View mActionBarView;
    private ProgressBar mActionBarLoadingView;
    private TextView mActionBarTitleView;
    private String mStatueseId;
    private String mRepostCommentContent;

    public static FriendsTimelineResult.FriendsTimeline EXTRA_FRIENDS_TIMELINE;

    private final int PUBLISH_SUCCESS = 1;
    private final int PUBLISH_FAIL = 2;

    public static PublishStatusRepostFragment newInstance(View actionBarView, String statueseId, String repostCommentContent) {
        PublishStatusRepostFragment fragment = new PublishStatusRepostFragment();
        fragment.mActionBarView = actionBarView;
        fragment.mStatueseId = statueseId;
        fragment.mRepostCommentContent = repostCommentContent;
        return fragment;
    }

    @Override
    public void initView() {
        sendBtn.setOnClickListener(sendBtnClickListener);
        photoBtn.setVisibility(View.GONE);
        editContent.setHint(getResources().getString(R.string.publish_status_repost_hint));
        setRepostCommentEditContent();
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
            intent.putExtra(Constant.DETALI_FRIENDS_TIMELINE_TYPE, Constant.DETAIL_FRIENDS_TIMELINE_TYPE_REPOST);
            intent.setClass(getActivity(), DetailActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener sendBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String comment = editContent.getText().toString();
            mActionBarLoadingView.setVisibility(View.VISIBLE);
            PublishStatus publishStatus = new PublishStatus();
            publishStatus.id = mStatueseId;
            publishStatus.requestListener = mRequestListener;
            publishStatus.view = mActionBarView;
            publishStatus.context = getActivity();
            publishStatus.status = comment;
            publishStatus.type = Constant.PUBLISH_STATUS_TYPE_REPOST;
            if (checkBox.isChecked()) {
                publishStatus.isComment = WeiboAPI.COMMENTS_TYPE.CUR_STATUSES;
            } else {
                publishStatus.isComment = WeiboAPI.COMMENTS_TYPE.NONE;
            }
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PUBLISH_SUCCESS:
                    mActionBarLoadingView.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= 19 && mActionBarView.isAttachedToWindow()) {
                        Snackbar.make(mActionBarView, getResources().getString(R.string.publish_repost_success),
                                Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.publish_go_to_see),
                                mSnackbarClickListener).show();
                    }else {
                        Toast.makeText(getActivity(), R.string.publish_repost_success, Toast.LENGTH_SHORT).show();
                    }
                    return;
                case PUBLISH_FAIL:
                    mActionBarLoadingView.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= 19 && mActionBarView.isAttachedToWindow()) {
                        Snackbar.make(mActionBarView, getResources().getString(R.string.publish_repost_fail),
                                Snackbar.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getActivity(), R.string.publish_repost_fail, Toast.LENGTH_SHORT).show();
                    }
                    return;
            }
        }
    };

    private void initActionBarView() {
        mActionBarTitleView = (TextView) mActionBarView.findViewById(R.id.action_bar_title_view);
        mActionBarLoadingView = (ProgressBar) mActionBarView.findViewById(R.id.action_bar_loading_view);
        mActionBarLoadingView.setVisibility(View.GONE);
        mActionBarTitleView.setText(getResources().getString(R.string.publish_status_repost));
    }

    public static FriendsTimelineResult.FriendsTimeline getExtraFriendsTimeline() {
        return EXTRA_FRIENDS_TIMELINE;
    }

    private void setRepostCommentEditContent() {
        if (mRepostCommentContent != null) {
            editContent.setText(mRepostCommentContent);
            CharSequence text = editContent.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, 0);
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            //显示软键盘
            imm.showSoftInputFromInputMethod(editContent.getWindowToken(), 0);
            editContent.setFocusable(true);

        }
    }

}
