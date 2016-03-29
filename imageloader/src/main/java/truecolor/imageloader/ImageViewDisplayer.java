package truecolor.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by xiaowu on 15/7/15.
 */
public class ImageViewDisplayer implements ImageLoader.ImageDisplayer {

    private static ImageViewDisplayer sDisplayer;

    public static ImageViewDisplayer getInstance() {
        if(sDisplayer == null) {
            sDisplayer = new ImageViewDisplayer();
        }
        return sDisplayer;
    }

    protected ImageViewDisplayer() {
    }

    @Override
    public void displayImage(Object view, Bitmap image) {
        if(image != null && view != null && view instanceof ImageView) {
            ((ImageView)view).setImageBitmap(image);
        }
    }

    @Override
    public void displayImage(Object view, int resId) {
        if(resId > 0 && view != null && view instanceof ImageView) {
            ((ImageView)view).setImageResource(resId);
        }
    }
}