package truecolor.mdwb.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import truecolor.mdwb.fragments.main.ABaseFragment;

/**
 * Created by xiaowu on 15/9/9.
 */
public class PhotoChoice {

    public static final int PHONE_IMAGE_REQUEST_CODE = 88888888;
    public static final int CAMERA_IMAGE_REQUEST_CODE = 99999999;

    private Activity mContext;

    /**
     * 临时文件目录
     */
    private String tempFilePath = "/sdcard/photoChoice/";
    /**
     * 临时文件的URI
     */
    private Uri tempFileUri;
    /**
     * 临时文件名
     */
    private String tempFileName = "photodata.o";

    public PhotoChoice(Activity context, String tempFilePath) {
//        this();
        this.mContext = context;
        this.tempFilePath = tempFilePath;
//        this.choiceListener = choiceListener;
//        setPhotoChoice();
    }

    public void start(ABaseFragment fragment, int position) {
        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (fragment == null)
                    mContext.startActivityForResult(Intent.createChooser(intent, "请选择文件..."), PHONE_IMAGE_REQUEST_CODE);
                else
                    fragment.startActivityForResult(Intent.createChooser(intent, "请选择文件..."), PHONE_IMAGE_REQUEST_CODE);
                break;
            case 1:
                // 准备启动相机
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 设置照片缓存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
                // 等待结果返回
                if (fragment == null)
                    mContext.startActivityForResult(intent, CAMERA_IMAGE_REQUEST_CODE);
                else
                    fragment.startActivityForResult(intent, CAMERA_IMAGE_REQUEST_CODE);
                break;
        }
    }

}
