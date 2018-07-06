package truecolor.mdwb.fragments.main.publish;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
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
import java.util.Date;
import java.util.List;

import truecolor.mdwb.R;
import truecolor.mdwb.apps.DetailActivity;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.logics.publish.UploadManager;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.model.PublishStatus;
import truecolor.mdwb.utils.FileUtils;
import truecolor.mdwb.utils.NotificationUtils;
import truecolor.mdwb.view.ClickShadeImageView;

/**
 * Created by xiaowu on 15/9/8.
 */
public class PublishStatusFragment extends APulishStatusFragment{

    private View mActionBarView;
    private ProgressBar mActionBarLoadingView;
    private TextView mActionBarTitleView;

    private BitmapUtils mBitmapUtils;
    private BitmapDisplayConfig mDisplayConfig;

    public static FriendsTimelineResult.FriendsTimeline PUBLISH_FRIENDS_TIMELINE;

    private final int REQUEST_CODE_CAPTURE_IMAGES = 1000;
    private final int REQUEST_CODE_CAPTURE_CAMEIA = 1001;
    private final int PUBLISH_SUCCESS = 1;
    private final int PUBLISH_FAIL = 2;

//    private List<String> picList = new ArrayList<>();


    public static PublishStatusFragment newInstance(View actionBarView) {
        PublishStatusFragment fragment = new PublishStatusFragment();
        fragment.mActionBarView = actionBarView;
        return fragment;
    }

    @Override
    public void initView() {
        initBimapUitl();
        sendBtn.setOnClickListener(sendBtnClickListener);
        photoBtn.setOnClickListener(photoBtnClickListener);
        editContent.setHint(getResources().getString(R.string.publish_new_status_hint));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CAPTURE_IMAGES && data != null) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            String path = "";
            String[] pojo = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().managedQuery(uri, pojo, null, null, null);
            if(cursor != null )
            {
                int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                cursor.moveToFirst();
                path = cursor.getString(columnIndex);
                if (Build.VERSION.SDK_INT < 14){
                    cursor.close();
                }
            }
            addPhotoContainerItemView(path);
            setPhotoContainerShowStatus();
        }else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA ){
            Uri uri = data.getData();
//            Log.e("uri", uri.toString());
            if (uri == null){
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap  photo = (Bitmap) bundle.get("data"); //get bitmap
                    Log.e("", photo.toString());
                    String path = getCameraTempPhotoPaht();
                    FileUtils.saveImageFile(photo, path);
                    addPhotoContainerItemView(path);
                    setPhotoContainerShowStatus();
                } else {
                    Toast.makeText(getActivity(), "error****", Toast.LENGTH_LONG).show();
                    return;
                }
            }else {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener sendBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String status = editContent.getText().toString();
            if (status.equals("")){
                Snackbar.make(mActionBarView, getResources().getString(R.string.publish_content_no_empty),
                        Snackbar.LENGTH_SHORT).show();
                return;
            }
            mActionBarLoadingView.setVisibility(View.VISIBLE);
            PublishStatus publishStatus = new PublishStatus();
            publishStatus.picPaths = getPicPaths();
            publishStatus.status = status;
            publishStatus.requestListener = mRequestListener;
            publishStatus.view = mActionBarView;
            publishStatus.context = getActivity();
            publishStatus.type = Constant.PUBLISH_STATUS_TYPE_NEW;
            UploadManager.addTask(publishStatus);
            NotificationUtils.showPuclishStatusNotification(getActivity(), Constant.PUBLISH_STATUS_NOTIFICATION_UPLOADING);

        }
    };

    private View.OnClickListener photoBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //    指定下拉列表的显示数据
            final String[] cities = {
                    getResources().getString(R.string.publish_pictures),
                    getResources().getString(R.string.publish_camera)};

            new MaterialDialog.Builder(getActivity())
                    .title(R.string.publish_select_photo_sources)
                    .items(cities)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                            switch (position) {
                                case 0:
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGES);
                                    break;

                                case 1:
                                    Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                                    startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
                                    break;
                            }
                        }
                    }).show();

//            new AlertDialogWrapper.Builder(getActivity())
//                    .setTitle(R.string.publish_select_photo_sources)
//                    .setItems(cities, new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which) {
//                                case 0:
//                                    Intent intent = new Intent();
//                                    intent.setType("image/*");
//                                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                                    startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGES);
//                                    break;
//
//                                case 1:
//                                    Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
//                                    startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
//                                    break;
//                            }
//                        }
//                    }).show();
        }
    };

    private View.OnClickListener mSnackbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().finish();
            Intent intent = new Intent();
            intent.putExtra(Constant.DETALI_FRIENDS_TIMELINE_TYPE, Constant.DETAIL_FRIENDS_TIMELINE_TYPE_PUBLISH);
            intent.setClass(getActivity(), DetailActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener deleteViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View photoItemView = (View) v.getTag();
            photoContainer.removeView(photoItemView);
            setPhotoContainerShowStatus();
        }
    };

//    private WebListener mWebListener = new WebListener() {
//        @Override
//        public void onDataLoadFinished(int service, Bundle params, Object result) {
//            if (service == WebServiceConfigure.POST_STATUESE){
//                Log.e("","");
//            }
//        }
//    };

//    private RequestCallBack mRequestCallBack = new RequestCallBack() {
//        @Override
//        public void onSuccess(ResponseInfo responseInfo) {
//            Log.e("", "");
//            mActionBarLoadingView.setVisibility(View.GONE);
//            Snackbar.make(mActionBarView, getResources().getString(R.string.publish_new_status_success),
//                    Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.publish_go_to_see),
//                    mSnackbarClickListener).show();
//            PUBLISH_FRIENDS_TIMELINE = JSON.parseObject(responseInfo.result.toString(), FriendsTimelineResult.FriendsTimeline.class);
//        }
//
//        @Override
//        public void onFailure(HttpException error, String msg) {
//            Snackbar.make(mActionBarView, getResources().getString(R.string.publish_new_status_fail),
//                    Snackbar.LENGTH_LONG).show();
//        }
//    };

    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            PUBLISH_FRIENDS_TIMELINE = JSON.parseObject(s, FriendsTimelineResult.FriendsTimeline.class);
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

//    private void updateStatuses(String status, String imageFile){
//        HttpService.updateStatuses(mRequestCallBack, Utils.accessToken, status, imageFile);
//        mActionBarLoadingView.setVisibility(View.VISIBLE);
//    }

    private void initActionBarView(){
        mActionBarTitleView = (TextView) mActionBarView.findViewById(R.id.action_bar_title_view);
        mActionBarLoadingView = (ProgressBar) mActionBarView.findViewById(R.id.action_bar_loading_view);
        mActionBarLoadingView.setVisibility(View.GONE);
        mActionBarTitleView.setText(getResources().getString(R.string.publish_new_status));
    }

    public static FriendsTimelineResult.FriendsTimeline getPublishFriendsTimeline(){
        return PUBLISH_FRIENDS_TIMELINE;
    }

    private void initBimapUitl(){
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_anim);
        mDisplayConfig = new BitmapDisplayConfig();
        mBitmapUtils = new BitmapUtils(getActivity());
        mDisplayConfig.setAnimation(animation);
    }

    private List<String> getPicPaths(){
        List<String> paths = new ArrayList<>();
        for (int i = 0; i < photoContainer.getChildCount(); i++) {
            View v = photoContainer.getChildAt(i);
            String path = (String) v.getTag();
            paths.add(path);
        }
        return paths;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PUBLISH_SUCCESS:
                    mActionBarLoadingView.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= 19 && mActionBarView.isAttachedToWindow()){
                        Snackbar.make(mActionBarView, getResources().getString(R.string.publish_new_status_success),
                                Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.publish_go_to_see),
                                mSnackbarClickListener).show();
                    }

                    NotificationUtils.showPuclishStatusNotification(mActionBarLoadingView.getContext(), Constant.PUBLISH_STATUS_NOTIFICATION_SUCCESS);
                    return;
                case PUBLISH_FAIL:
                    mActionBarLoadingView.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= 19 && mActionBarView.isAttachedToWindow()){
                        Snackbar.make(mActionBarView, getResources().getString(R.string.publish_new_status_fail),
                                Snackbar.LENGTH_LONG).show();
                    }
                    NotificationUtils.showPuclishStatusNotification(mActionBarLoadingView.getContext(), Constant.PUBLISH_STATUS_NOTIFICATION_FAIL);
                    return;
            }
        }
    };

    private String getCameraTempPhotoPaht(){
        Date date = new Date();
        return Constant.CAMERA_PHOTO_TEMP_PATH + date.getTime() + ".jpg";
    }

    private void addPhotoContainerItemView(String path){
        View photoItem = LayoutInflater.from(getActivity()).inflate(R.layout.publish_statuses_photo_item, photoContainer, false);
        ClickShadeImageView photoView = (ClickShadeImageView) photoItem.findViewById(R.id.publish_statuese_photo_view);
        ClickShadeImageView deleteView = (ClickShadeImageView) photoItem.findViewById(R.id.publish_statuese_photo_delete_view);
        photoItem.setTag(path);
        deleteView.setTag(photoItem);
        deleteView.setOnClickListener(deleteViewClickListener);
        mBitmapUtils.display(photoView, path, mDisplayConfig);
        photoContainer.addView(photoItem);
    }

    private void setPhotoContainerShowStatus(){
        if (photoContainer.getChildCount() == 0){
            photoContainer.setVisibility(View.GONE);
        }else {
            photoContainer.setVisibility(View.VISIBLE);
        }
    }


}
