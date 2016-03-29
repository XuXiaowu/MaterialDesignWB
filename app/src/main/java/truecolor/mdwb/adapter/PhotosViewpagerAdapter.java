//package truecolor.mdwb.adapter;
//
//import android.content.Context;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.view.PagerAdapter;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
///**
// * Created by xiaowu on 15/10/29.
// */
//public class PhotosViewpagerAdapter extends PagerAdapter {
//
//    private String[] mPicUrls;
//
//    public PhotosViewpagerAdapter(String[] mPicUrls, Context mContext) {
//        this.mPicUrls = mPicUrls;
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        ImageView imageView = new ImageView(mActivity);
//        HomePosterListResult.HomePosterListData hpld  = mHomePosterListResult.data[position % mHomePosterListResult.data.length];
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setTag(hpld.link_url);
//        imageView.setId(hpld.id);
//        imageView.setOnClickListener(mPosterClickListener);
//        container.addView(imageView);
//        ImageLoader.loadImage(hpld.image_url,
//                HttpImageDecoder.getInstance(), ImageViewDisplayer.getInstance(), imageView,
//                R.drawable.home_poster_default_bg);
//        return super.instantiateItem(container, position);
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
//    }
//
//    @Override
//    public int getCount() {
//        return mPicUrls.length;
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == object;
//    }
//
//
//}
