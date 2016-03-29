package truecolor.mdwb.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

import java.util.ArrayList;
import java.util.List;

import truecolor.imageloader.ImageLoader;
import truecolor.imageloader.ImageViewDisplayer;
import truecolor.mdwb.R;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.utils.DipConvertPxUtil;

/**
 * Created by xiaowu on 15/8/14.
 */
public class ImageGroupView extends AbsViewGroup{

    private ClickShadeImageView mImageViewItem1;
    private ClickShadeImageView mImageViewItem2;
    private ClickShadeImageView mImageViewItem3;
    private ClickShadeImageView mImageViewItem4;
    private ClickShadeImageView mImageViewItem5;
    private ClickShadeImageView mImageViewItem6;
    private ClickShadeImageView mImageViewItem7;
    private ClickShadeImageView mImageViewItem8;
    private ClickShadeImageView mImageViewItem9;

    private Rect mItem1Rect;
    private Rect mItem2Rect;
    private Rect mItem3Rect;
    private Rect mItem4Rect;
    private Rect mItem5Rect;
    private Rect mItem6Rect;
    private Rect mItem7Rect;
    private Rect mItem8Rect;
    private Rect mItem9Rect;

    private List<ClickShadeImageView> mImageViews;
    private BitmapDisplayConfig mDisplayConfig;

    private int margin;
    private int mItemWidth;
    private FriendsTimelineResult.PicUrl mPicUrls[];

    private OnClickListener mImageGroupViewItemClickListener;

    private final int MARGIN = 8;
    public static final int PIC_ID_LENGTH = 36;
    public static final int PIC_TYPE_MIDDLE = 0;
    public static final int PIC_TYPE_ORIGIN = 1;

    public ImageGroupView(Context context) {
        super(context);
    }

    public ImageGroupView(Context context, AttributeSet attr) {
        super(context, attr);
//        setBackgroundResource(R.color.cover_click_shade_color);
    }

    @Override
    public void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.image_group_view, this);
        mImageViewItem1 = (ClickShadeImageView) findViewById(R.id.image_item_view1);
        mImageViewItem2 = (ClickShadeImageView) findViewById(R.id.image_item_view2);
        mImageViewItem3 = (ClickShadeImageView) findViewById(R.id.image_item_view3);
        mImageViewItem4 = (ClickShadeImageView) findViewById(R.id.image_item_view4);
        mImageViewItem5 = (ClickShadeImageView) findViewById(R.id.image_item_view5);
        mImageViewItem6 = (ClickShadeImageView) findViewById(R.id.image_item_view6);
        mImageViewItem7 = (ClickShadeImageView) findViewById(R.id.image_item_view7);
        mImageViewItem8 = (ClickShadeImageView) findViewById(R.id.image_item_view8);
        mImageViewItem9 = (ClickShadeImageView) findViewById(R.id.image_item_view9);

        mImageViews = new ArrayList<>();
        mImageViews.add(mImageViewItem1);
        mImageViews.add(mImageViewItem2);
        mImageViews.add(mImageViewItem3);
        mImageViews.add(mImageViewItem4);
        mImageViews.add(mImageViewItem5);
        mImageViews.add(mImageViewItem6);
        mImageViews.add(mImageViewItem7);
        mImageViews.add(mImageViewItem8);
        mImageViews.add(mImageViewItem9);

        if (mDisplayConfig == null){
            mDisplayConfig = new BitmapDisplayConfig();
            mDisplayConfig.setLoadingDrawable(mContext.getResources().getDrawable(R.color.holo_gray_bright));
            Animation alphaAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alpha_anim);
            alphaAnimation.setFillAfter(true);
            mDisplayConfig.setAnimation(alphaAnimation);
        }
    }

    @Override
    public void initSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);

        margin = DipConvertPxUtil.dip2px(context,MARGIN);
        mViewWidth = metrics.widthPixels - (4 * margin);
//        mViewHeight = mViewWidth;
        mItemWidth = (mViewWidth - margin * 2) / 3;

    }

    @Override
    public void initPadding(Context context) {
//        margin = (int) context.getResources().getDimension(R.dimen.normal_margin);
    }

    @Override
    public void initRect(Context context) {
        mItem1Rect = new Rect();
        mItem2Rect = new Rect();
        mItem3Rect = new Rect();
        mItem4Rect = new Rect();
        mItem5Rect = new Rect();
        mItem6Rect = new Rect();
        mItem7Rect = new Rect();
        mItem8Rect = new Rect();
        mItem9Rect = new Rect();
    }

    @Override
    protected void onMeasure(int mViewWidthwidthMeasureSpec, int heightMeasureSpec) {
//        if (mViewWidth == 0 || mViewHeight == 0) {

//            bindImage(null);
//        }
//        mViewHeight = mViewWidth;
        if (mPicUrls == null){
            mViewHeight = mViewWidth;
        }else if (mPicUrls.length == 6){
            mViewHeight = mViewWidth - mItemWidth - margin;
        }else if (mPicUrls.length == 3 || mPicUrls.length == 2){
            mViewHeight = mItemWidth;
        } else {
            mViewHeight = mViewWidth;
        }
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mImageViewItem1.layout(mItem1Rect.left, mItem1Rect.top, mItem1Rect.right, mItem1Rect.bottom);
        mImageViewItem2.layout(mItem2Rect.left, mItem2Rect.top, mItem2Rect.right, mItem2Rect.bottom);
        mImageViewItem3.layout(mItem3Rect.left, mItem3Rect.top, mItem3Rect.right, mItem3Rect.bottom);
        mImageViewItem4.layout(mItem4Rect.left, mItem4Rect.top, mItem4Rect.right, mItem4Rect.bottom);
        mImageViewItem5.layout(mItem5Rect.left, mItem5Rect.top, mItem5Rect.right, mItem5Rect.bottom);
        mImageViewItem6.layout(mItem6Rect.left, mItem6Rect.top, mItem6Rect.right, mItem6Rect.bottom);
        mImageViewItem7.layout(mItem7Rect.left, mItem7Rect.top, mItem7Rect.right, mItem7Rect.bottom);
        mImageViewItem8.layout(mItem8Rect.left, mItem8Rect.top, mItem8Rect.right, mItem8Rect.bottom);
        mImageViewItem9.layout(mItem9Rect.left, mItem9Rect.top, mItem9Rect.right, mItem9Rect.bottom);
    }

    public void bindImage(FriendsTimelineResult.FriendsTimeline ft, BitmapUtils bitmapUtil){
        mPicUrls = ft.pic_urls;
        if (ft.pic_urls.length == 1){
            mItemWidth = mViewWidth;
            mItem1Rect.left = 0;
            mItem1Rect.right = mViewWidth;
            mItem1Rect.top = 0;
            mItem1Rect.bottom = mViewWidth;
        }else if (ft.pic_urls.length == 4 || ft.pic_urls.length == 2){
            mItemWidth = (mViewWidth - margin) / 2;
            mItem1Rect.left = 0;
            mItem1Rect.right = mItemWidth;
            mItem1Rect.top = 0;
            mItem1Rect.bottom = mItemWidth;

            mItem2Rect.left = mItem1Rect.right + margin;
            mItem2Rect.right = mItem2Rect.left + mItemWidth;
            mItem2Rect.top = 0;
            mItem2Rect.bottom = mItemWidth;

            mItem3Rect.left = 0;
            mItem3Rect.right = mItemWidth;
            mItem3Rect.top = mItem1Rect.bottom + margin;
            mItem3Rect.bottom = mItem3Rect.top + mViewWidth;

            mItem4Rect.left = mItem2Rect.left;
            mItem4Rect.right = mItem2Rect.right;
            mItem4Rect.top = mItem3Rect.top;
            mItem4Rect.bottom = mItem3Rect.bottom;

        }else if (ft.pic_urls.length > 4 || ft.pic_urls.length == 3){
            mItemWidth = (mViewWidth - margin * 2) / 3;
//            mViewHeight = mViewWidth;

            mItem1Rect.left = 0;
            mItem1Rect.right = mItem1Rect.left + mItemWidth;
            mItem1Rect.top = 0;
            mItem1Rect.bottom = mItem1Rect.top + mItemWidth;

            mItem2Rect.left = mItem1Rect.right + margin;
            mItem2Rect.right = mItem2Rect.left + mItemWidth;
            mItem2Rect.top = 0;
            mItem2Rect.bottom = mItem2Rect.top + mItemWidth;

            mItem3Rect.left = mItem2Rect.right + margin;
            mItem3Rect.right = mItem3Rect.left + mItemWidth;
            mItem3Rect.top = 0;
            mItem3Rect.bottom = mItem3Rect.top + mItemWidth;

            mItem4Rect.left = 0;
            mItem4Rect.right = mItem4Rect.left + mItemWidth;
            mItem4Rect.top = mItem1Rect.bottom + margin;
            mItem4Rect.bottom = mItem4Rect.top + mItemWidth;

            mItem5Rect.left = mItem2Rect.left;
            mItem5Rect.right = mItem2Rect.right;
            mItem5Rect.top = mItem4Rect.top;
            mItem5Rect.bottom = mItem4Rect.bottom;

            mItem6Rect.left = mItem3Rect.left;
            mItem6Rect.right = mItem3Rect.right;
            mItem6Rect.top = mItem4Rect.top;
            mItem6Rect.bottom = mItem4Rect.bottom;

            mItem7Rect.left = mItem1Rect.left;
            mItem7Rect.right = mItem1Rect.right;
            mItem7Rect.top = mItem4Rect.bottom + margin;
            mItem7Rect.bottom = mItem7Rect.top + mItemWidth;

            mItem8Rect.left = mItem2Rect.left;
            mItem8Rect.right = mItem2Rect.right;
            mItem8Rect.top = mItem7Rect.top;
            mItem8Rect.bottom = mItem7Rect.bottom;

            mItem9Rect.left = mItem6Rect.left;
            mItem9Rect.right = mItem6Rect.right;
            mItem9Rect.top = mItem7Rect.top;
            mItem9Rect.bottom = mItem7Rect.bottom;

//            if (ft.pic_urls.length == 6) mViewHeight = mViewWidth - mItemWidth;

        }

        for (int i = 0; i < ft.pic_urls.length; i++) {
//            ImageLoader.loadImage(picUrl[i].thumbnail_pic, ImageViewDisplayer.getInstance(),
//                    mImageViews.get(i), R.mipmap.ic_user);
            String url;
            if (ft.pic_urls.length == 1 || ft.pic_urls.length == 4){
                url = getPicUrl(PIC_TYPE_MIDDLE, ft.pic_urls[i].thumbnail_pic, ft);
            }else {
//                url = ft.pic_urls[i].thumbnail_pic;
                url = getPicUrl(PIC_TYPE_MIDDLE, ft.pic_urls[i].thumbnail_pic, ft);
            }
            bitmapUtil.display(mImageViews.get(i), url, mDisplayConfig);
            mImageViews.get(i).setVisibility(VISIBLE);
            mImageViews.get(i).setId(i);
            mImageViews.get(i).setTag(ft);
            mImageViews.get(i).setOnClickListener(mImageGroupViewItemClickListener);
        }
        for (int i = ft.pic_urls.length; i < 9; i++) {
            mImageViews.get(i).setVisibility(GONE);
        }

        reMeasure();

    }

    public static String getPicUrl(int picType, String thumbnail, FriendsTimelineResult.FriendsTimeline friendsTimeline){
        String picUrl;
        String picId;
        String baseBmiddleUrl;
        String baseOriginaUrl;
        int bmiddlePicLength = friendsTimeline.bmiddle_pic.length();
        int originaLength = friendsTimeline.original_pic.length();
        int thumbnailLength = thumbnail.length();

        picId = thumbnail.substring(thumbnailLength - PIC_ID_LENGTH);

        if (picType == PIC_TYPE_MIDDLE){
            baseBmiddleUrl = friendsTimeline.bmiddle_pic.substring(0, bmiddlePicLength - PIC_ID_LENGTH);
            return baseBmiddleUrl + picId;
        }else if (picType == PIC_TYPE_ORIGIN){
            baseOriginaUrl = friendsTimeline.original_pic.substring(0, originaLength - PIC_ID_LENGTH);
            return baseOriginaUrl + picId;
        }else {
            return "";
        }
    }

    public void setImageGroupViewItemClickListener(OnClickListener mImageGroupViewItemClickListener){
        this.mImageGroupViewItemClickListener = mImageGroupViewItemClickListener;
    }


}
