package truecolor.mdwb.apps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import org.w3c.dom.Text;

import java.io.File;

import truecolor.imageloader.HttpImageDecoder;
import truecolor.imageloader.ImageLoader;
import truecolor.imageloader.ImageViewDisplayer;
import truecolor.mdwb.R;
import truecolor.mdwb.fragments.main.status.PageSectionFragment;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.utils.FileUtils;
import truecolor.mdwb.utils.Utils;
import truecolor.mdwb.view.ImageGroupView;
import uk.co.senab.photoview.PhotoView;

public class PhotoBrowseActivity extends ActionBarActivity {

    private ViewPager mViewPager;

    private RelativeLayout mActionBarViwe;
    private ImageView mBackView;
    private TextView mIndexView;
    private TextView mSaveView;
    private TextView mCopyUrlView;

    private Animation mAlphaAnimation;

    private FriendsTimelineResult.FriendsTimeline mFriendsTimeline;
    private int mPicPos;

    private BitmapUtils mBitmapUtils;
    private BitmapDisplayConfig mDisplayConfig;

    private String mBaseSavePicPath = "/storage/sdcard0/MagerialDesignWB/Images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_browse);

        getExtarData();
        initView();
        setTarnsparentStatusBar();
        initBitmapUtil();
        initViewPager();

    }

    private ViewPager.OnPageChangeListener mViewPagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mPicPos = position;
            mIndexView.setText((position + 1) + "\\" +mFriendsTimeline.pic_urls.length);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.action_back_view:
                    finish();
                    break;
                case R.id.action_save_view:
                    saveImage();
                    break;
                case R.id.action_copy_url_view:
                    copyImagerUrl();
                    break;
            }
        }
    };

    private View.OnClickListener photoViewClickListene = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private class MbitmapLoadCallBack extends BitmapLoadCallBack{

        private WebView mWebView;

        public MbitmapLoadCallBack(WebView mWebView) {
            super();
            this.mWebView = mWebView;
        }

        @Override
        public void onLoadCompleted(View container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
            File gifFile = mBitmapUtils.getBitmapFileFromDiskCache(uri);
            readGifPicture(mWebView, gifFile);
        }

        @Override
        public void onLoadFailed(View container, String uri, Drawable drawable) {

        }
    }

    private void initView(){
        mViewPager = (ViewPager) findViewById(R.id.photos_viewpager);
        mActionBarViwe = (RelativeLayout) findViewById(R.id.action_bar_view);
        mBackView = (ImageView) findViewById(R.id.action_back_view);
        mIndexView = (TextView) findViewById(R.id.action_index_view);
        mSaveView = (TextView) findViewById(R.id.action_save_view);
        mCopyUrlView = (TextView) findViewById(R.id.action_copy_url_view);

        mBackView.setOnClickListener(mClickListener);
        mSaveView.setOnClickListener(mClickListener);
        mCopyUrlView.setOnClickListener(mClickListener);
        mIndexView.setText((mPicPos + 1) + "\\" + mFriendsTimeline.pic_urls.length);
    }

    private void getExtarData(){
        Intent intent = getIntent();
        if (intent != null){
            mPicPos = intent.getIntExtra(Constant.EXTRA_IMAGER_GROUP_POS, -1);
        }
        String type = intent.getType();
        if (type.equals(Constant.GO_TO_PHOTOS_BROWSE_TYPE_MIAN)){
            mFriendsTimeline = MainActivity.getExtraFriendsTimeline();
        } else if (type.equals(Constant.GO_TO_PHOTOS_BROWSE_TYPE_DETAIL)){
            mFriendsTimeline = DetailActivity.getExtraFriendsTimeline();
        } else if (type.equals(Constant.GO_TO_PHOTOS_BROWSE_TYPE_SEARCH)){
            mFriendsTimeline = SearchActivity.getExtraFriendsTimeline();
        }
    }

    private void initViewPager(){
        PhotosViewpagerAdapter viewpagerAdapter = new PhotosViewpagerAdapter();
        mViewPager.setAdapter(viewpagerAdapter);
        mViewPager.setCurrentItem(mPicPos);
        mViewPager.setOnPageChangeListener(mViewPagerChangeListener);
    }

    private void initBitmapUtil(){
        mBitmapUtils = new BitmapUtils(this);
        mDisplayConfig = new BitmapDisplayConfig();
        mDisplayConfig.setLoadingDrawable(getResources().getDrawable(android.R.color.transparent));
        mAlphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        mAlphaAnimation.setFillAfter(true);
        mDisplayConfig.setAnimation(mAlphaAnimation);
    }

    private class PhotosViewpagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = null;
            WebView webview;
            String picUrl = ImageGroupView.getPicUrl(ImageGroupView.PIC_TYPE_ORIGIN,
                    mFriendsTimeline.pic_urls[position].thumbnail_pic, mFriendsTimeline);

            if (picUrl.endsWith("gif")){
                webview = new WebView(PhotoBrowseActivity.this);
                webview.setBackgroundResource(R.color.photo_view_background);
                mBitmapUtils.display(webview, picUrl, mDisplayConfig, new MbitmapLoadCallBack(webview));
                container.addView(webview);
                return webview;
            }else if (picUrl.endsWith("jpg")){
                photoView = new PhotoView(PhotoBrowseActivity.this);
                photoView.setOnClickListener(photoViewClickListene);
                mBitmapUtils.display(photoView, picUrl, mDisplayConfig);
                container.addView(photoView);
                return photoView;
            }
            return  null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return mFriendsTimeline.pic_urls.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void readGifPicture(final WebView large, File file) {

        large.getSettings().setJavaScriptEnabled(true);
        large.getSettings().setUseWideViewPort(true);
        large.getSettings().setLoadWithOverviewMode(true);
        large.getSettings().setBuiltInZoomControls(true);
        large.getSettings().setDisplayZoomControls(false);

        large.setVerticalScrollBarEnabled(false);
        large.setHorizontalScrollBarEnabled(false);
        large.addJavascriptInterface(new PictureJavaScriptInterface(), "picturejs");

        String str1 = "file://" + file.getAbsolutePath().replace("/mnt/sdcard/", "/sdcard/");
        String str2 = "<html>\n<head>\n     <style>\n          html,body{background:#3b3b3b;margin:0;padding:0;}          *{-webkit-tap-highlight-color:rgba(0, 0, 0, 0);}\n     </style>\n     <script type=\"text/javascript\">\n     var imgUrl = \""
                + str1
                + "\";"
                + "     var objImage = new Image();\n"
                + "     var realWidth = 0;\n"
                + "     var realHeight = 0;\n"
                + "\n"
                + "     function onLoad() {\n"
                + "          objImage.onload = function() {\n"
                + "               realWidth = objImage.width;\n"
                + "               realHeight = objImage.height;\n"
                + "\n"
                + "               document.gagImg.src = imgUrl;\n"
                + "               onResize();\n"
                + "          }\n"
                + "          objImage.src = imgUrl;\n"
                + "     }\n"
                + "\n"
                + "     function imgOnClick() {\n"
                + "			window.picturejs.onClick();"
                + "     }\n"
                + "     function onResize() {\n"
                + "          var scale = 1;\n"
                + "          var newWidth = document.gagImg.width;\n"
                + "          if (realWidth > newWidth) {\n"
                + "               scale = realWidth / newWidth;\n"
                + "          } else {\n"
                + "               scale = newWidth / realWidth;\n"
                + "          }\n"
                + "\n"
                + "          hiddenHeight = Math.ceil(30 * scale);\n"
                + "          document.getElementById('hiddenBar').style.height = hiddenHeight + \"px\";\n"
                + "          document.getElementById('hiddenBar').style.marginTop = -hiddenHeight + \"px\";\n"
                + "     }\n"
                + "     </script>\n"
                + "</head>\n"
                + "<body onload=\"onLoad()\" onresize=\"onResize()\" onclick=\"Android.toggleOverlayDisplay();\">\n"
                + "     <table style=\"width: 100%;height:100%;\">\n"
                + "          <tr style=\"width: 100%;\">\n"
                + "               <td valign=\"middle\" align=\"center\" style=\"width: 100%;\">\n"
                + "                    <div style=\"display:block\">\n"
                + "                         <img name=\"gagImg\" src=\"\" width=\"100%\" style=\"\" onclick=\"imgOnClick()\" />\n"
                + "                    </div>\n"
                + "                    <div id=\"hiddenBar\" style=\"position:absolute; width: 0%; background: #3b3b3b;\"></div>\n"
                + "               </td>\n" + "          </tr>\n" + "     </table>\n" + "</body>\n" + "</html>";
        large.loadDataWithBaseURL("file:///android_asset/", str2, "text/html", "utf-8", null);
    }

    final class PictureJavaScriptInterface {

        public PictureJavaScriptInterface() {

        }

        @JavascriptInterface
        public void onClick() {
            finish();
        }

    }

    /**
     * 5.0及以上系统设置状态栏透明
     */
    private void setTarnsparentStatusBar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.action_bar_height));
            layoutParams.setMargins(0, 0, 0, 0);
            mActionBarViwe.setLayoutParams(layoutParams);
        }
    }

    private void saveImage(){
        boolean isSaved = false;
        String picUrl = ImageGroupView.getPicUrl(ImageGroupView.PIC_TYPE_ORIGIN,
                mFriendsTimeline.pic_urls[mPicPos].thumbnail_pic, mFriendsTimeline);
        Bitmap picBitmap = mBitmapUtils.getBitmapFromMemCache(picUrl, mDisplayConfig);
        String mSavePicPath = "";
        if (picUrl.endsWith("gif")){
            File gifFile = mBitmapUtils.getBitmapFileFromDiskCache(picUrl);
            mSavePicPath = mBaseSavePicPath + System.currentTimeMillis() + ".gif";
            boolean isCreated = FileUtils.createFile(mSavePicPath);
            if (isCreated){
                File target = new File(mSavePicPath);
                isSaved = FileUtils.copyFile(gifFile, target);
            }else {
                isSaved = false;
            }
        } else if (picUrl.endsWith("jpg")){
            mSavePicPath = mBaseSavePicPath + System.currentTimeMillis() + ".jgp";
            isSaved = FileUtils.saveImageFile(picBitmap, mSavePicPath);
        }

        if (isSaved){
            Snackbar.make(mViewPager, getResources().getString(R.string.pic_save_success) + mSavePicPath,
                    Snackbar.LENGTH_LONG).show();
        }else {
            Snackbar.make(mViewPager, getResources().getString(R.string.pic_save_fail) + mSavePicPath,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void copyImagerUrl(){
        Utils.copy(mFriendsTimeline.text, PhotoBrowseActivity.this);
        Snackbar.make(mViewPager, getResources().getString(R.string.copy_url_success),
                Snackbar.LENGTH_LONG).show();
    }


}
