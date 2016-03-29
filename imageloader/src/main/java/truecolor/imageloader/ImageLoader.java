package truecolor.imageloader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xiaowu on 15/7/15.
 */
public class ImageLoader {
    private static final int MESSAGE_UPDATE_IMAGE = 0x10000;

    private static final Handler sHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_IMAGE:
                    ImageLoaderTask task = (ImageLoaderTask) msg.obj;
                    finishLoader(task);
                    break;
            }
        }
    };

    private static ReentrantLock mLock = new ReentrantLock();
//    private static final Object mLock = new Object();

    /*
     * call this in main loop
     */
    static {
        TaskUtils.setTaskMaxSize(ImageConfigure.TASK_IMAGE_TAG, ImageConfigure.TASK_MAX_NUM);
        TaskUtils.setTaskMaxSize(ImageConfigure.TASK_WEB_IMAGE_TAG, ImageConfigure.TASK_WEB_MAX_NUM);
    }

    /*
     * load image
     */
    public static Bitmap loadImage(String uri) {
        return loadImage(uri, null, null, null);
    }

    public static Bitmap loadImage(String uri, ImageDecoder decoder) {
        return loadImage(uri, decoder, null, null);
    }

    public static Bitmap loadImage(String uri, ImageLoaderListener listener) {
        return loadImage(uri, null, null, listener);
    }

    private static Bitmap loadImage(String uri, ImageDecoder decoder, ImageConverter converter,
                                    ImageLoaderListener listener) {
        Bitmap image = getImageCache(uri, converter);
        if(image != null) {
            if(listener != null) listener.onImageLoaded(uri, image);
            return image;
        }

        startLoader(uri, decoder, converter, listener);
        return null;
    }

    /*
     * load image for ImageView or other view
     */
    public static Bitmap loadImage(String uri, ImageView view, int resId) {
        return loadImage(uri, null, null, ImageViewDisplayer.getInstance(), view, resId);
    }

    public static Bitmap loadImage(String uri, ImageDisplayer displayer, Object view, int resId) {
        return loadImage(uri, null, null, displayer, view, resId);
    }

//    public static Bitmap loadImage(String uri, ImageConverter converter, ImageLoaderListener listener){
//        return loadImage(uri, null,converter,null,null,-1,listener);
//    }

    public static Bitmap loadImage(String uri, ImageConverter converter, Object view, int resId) {
        return loadImage(uri, null, converter, ImageViewDisplayer.getInstance(), view, resId);
    }

    public static Bitmap loadImage(String uri, ImageConverter converter, ImageDisplayer displayer,
                                   Object view, int resId) {
        return loadImage(uri, null, converter, displayer, view, resId);
    }

    public static Bitmap loadImage(String uri, ImageDecoder decoder, ImageDisplayer displayer,
                                   Object view, int resId) {
        return loadImage(uri, decoder, null, displayer, view, resId);
    }

    private static Bitmap loadImage(String uri, ImageDecoder decoder, ImageConverter converter,
                                    ImageDisplayer displayer, Object view, int resId) {
        Bitmap image = getImageCache(uri, converter);//加载内存缓存
        if (image != null) {
            closeRequest(view);
            if (displayer != null) displayer.displayImage(view, image); //把图片显示在View控件上
            return image;
        }
        if (displayer != null && resId > 0) {
            displayer.displayImage(view, resId); //显示默认图片
        }

        startLoader(uri, decoder, converter, displayer, view); //继续加载 获取本地文件缓存图片或请求网络图片
        return null;
    }

    /*
     * cache image
     */
    private static final HashMap<String, SoftReference<Bitmap>> mNoConvertCache = new HashMap<String, SoftReference<Bitmap>>();
    private static final HashMap<ImageConverter, HashMap<String, SoftReference<Bitmap>>> mCacheTable = new HashMap<ImageConverter, HashMap<String, SoftReference<Bitmap>>>();
    private static Bitmap getImageCache(String uri, ImageConverter converter) {
        HashMap<String, SoftReference<Bitmap>> cache = getCache(converter);
        if (cache.containsKey(uri)) {
            SoftReference<Bitmap> reference = cache.get(uri);
            Bitmap image = reference.get();
            if (image != null) {
                return image;
            }
            cache.remove(uri);
        }
        return null;
    }

    private static void putImageCache(String uri, ImageConverter converter, Bitmap image) {
        HashMap<String, SoftReference<Bitmap>> cache = getCache(converter);
        cache.put(uri, new SoftReference<Bitmap>(image));
    }

    private static HashMap<String, SoftReference<Bitmap>> getCache(ImageConverter converter) {
        if (converter == null) {
            return mNoConvertCache;
        }

        HashMap<String, SoftReference<Bitmap>> cache = mCacheTable.get(converter);
        if (cache == null) {
            cache = new HashMap<String, SoftReference<Bitmap>>();
            mCacheTable.put(converter, cache);
        }
        return cache;
    }

    // image display request pool
    private static final LinkedList<ImageDisplayRequest> mImageRequestPool = new LinkedList<ImageDisplayRequest>();
    private static final LinkedList<ImageDisplayRequest> mImageRequests = new LinkedList<ImageDisplayRequest>();

    private static ImageDisplayRequest getDisplayRequest(Object view) {
        if(view == null) return null;
        Iterator<ImageDisplayRequest> it = mImageRequests.listIterator();
        while(it.hasNext()) {
            ImageDisplayRequest request = it.next();
            Object v = request.view == null ? null : request.view.get();
            if(view == v) {
                return request;
            }
//            if(v == null && request.listener == null) {
//                if(request.pre != null) request.pre.next = request.next;
//                if(request.next != null) request.next.pre = request.pre;
//                request.clear();
//
//                mImageRequestPool.addLast(request);
//                mImageRequests.remove(request);
//                it.remove();
//            }
        }
        return null;
    }

    // image loader pool
    private static final LinkedList<ImageLoaderTask> mImageLoaderPool = new LinkedList<ImageLoaderTask>();
    private static ImageLoaderTask getLoaderTask(String uri, ImageDecoder decoder,
                                                 ImageConverter converter, ImageDisplayRequest request) {
        ImageLoaderTask loader = mImageLoaderPool.poll();
        if(loader != null) {
            loader.set(uri, decoder, converter, request);
            return loader;
        }
        return new ImageLoaderTask(uri, decoder, converter, request);
    }

    private static void releaseLoaderTask(ImageLoaderTask loader) {
        if (loader == null) return;

        loader.clear();
        mImageLoaderPool.addLast(loader);
    }

    // filter duplicate request
    private static final HashMap<String, ImageLoaderTask> mNoConvertTask = new HashMap<String, ImageLoaderTask>();
    private static final HashMap<ImageConverter, HashMap<String, ImageLoaderTask>> mTaskTable = new HashMap<ImageConverter, HashMap<String, ImageLoaderTask>>();
    private static ImageLoaderTask getLoadingTask(String uri, ImageConverter converter) {
        HashMap<String, ImageLoaderTask> cache = getTaskCache(converter);
        return cache.get(uri);
    }

    private static void addLoadingTask(String uri, ImageConverter converter, ImageLoaderTask task) {
        HashMap<String, ImageLoaderTask> cache = getTaskCache(converter);
        cache.put(uri, task);
    }

    private static void removeLoadingTask(String uri, ImageConverter converter) {
        HashMap<String, ImageLoaderTask> cache = getTaskCache(converter);
        cache.remove(uri);
    }

    private static HashMap<String, ImageLoaderTask> getTaskCache(ImageConverter converter) {
        if (converter == null) {
            return mNoConvertTask;
        }

        HashMap<String, ImageLoaderTask> cache = mTaskTable.get(converter);
        if (cache == null) {
            cache = new HashMap<String, ImageLoaderTask>();
            mTaskTable.put(converter, cache);
        }
        return cache;
    }

    /*
     *
     */
    private static void startLoader(String uri, ImageDecoder decoder, ImageConverter converter,
                                    ImageDisplayer displayer, Object view) {
//        synchronized(mLock) {
        mLock.lock();
        ImageDisplayRequest request = getDisplayRequest(view);
        if(request != null) {
            if(request.pre != null) request.pre.next = request.next;
            if(request.next != null) request.next.pre = request.pre;
            request.set(view, displayer);
        } else {
            request = mImageRequestPool.poll();
            if (request != null) {
                request.set(view, displayer);
            } else {
                request = new ImageDisplayRequest(view, displayer);
            }
            if(view != null) mImageRequests.addFirst(request);
        }

        ImageLoaderTask loader = getLoadingTask(uri, converter);
        if(loader != null) {
            loader.addDisplayRequest(request);
        } else {
            ImageLoaderTask task = getLoaderTask(uri, decoder, converter, request);
            addLoadingTask(uri, converter, task);
            TaskUtils.executeTask(ImageConfigure.TASK_IMAGE_TAG, task);
        }
        mLock.unlock();
//        }
    }

    private static void startLoader(String uri, ImageDecoder decoder, ImageConverter converter,
                                    ImageLoaderListener listener) {
//        synchronized(mLock) {
        mLock.lock();
        ImageDisplayRequest request = mImageRequestPool.poll();
        if(request != null) {
            request.set(listener);
        } else {
            request = new ImageDisplayRequest(listener);
        }

        ImageLoaderTask loader = getLoadingTask(uri, converter);
        if (loader != null) {
            loader.addDisplayRequest(request);
        } else {
            ImageLoaderTask task = getLoaderTask(uri, decoder, converter, request);
            addLoadingTask(uri, converter, task);
            TaskUtils.executeTask(ImageConfigure.TASK_IMAGE_TAG, task);
        }
        mLock.unlock();
//        }
    }

    private static void finishLoader(ImageLoaderTask loader) {
        if (loader == null) return;

//        synchronized(mLock) {
        mLock.lock();
        removeLoadingTask(loader.uri, loader.converter);

        ImageDisplayRequest request = loader.request_header.next;
        while(request != null) {
            ImageDisplayRequest next = request.next;
            mImageRequests.remove(request);
//            if(loader.image != null) {
            if(request.displayer != null) {
                Object v = request.view == null ? null : request.view.get();
                request.displayer.displayImage(v, loader.image); //显示图片
            }
            if(request.listener != null) {
                request.listener.onImageLoaded(loader.uri, loader.image);
            }
//            }
            request.clear();
            mImageRequestPool.addLast(request);
            request = next;
        }

        releaseLoaderTask(loader);
        mLock.unlock();
//        }
    }

    private static void closeRequest(Object view) {
        if(view == null) return;

//        synchronized(mLock) {
        mLock.lock();
        ImageDisplayRequest request = getDisplayRequest(view);
        if(request != null) {
            if(request.pre != null) request.pre.next = request.next;
            if(request.next != null) request.next.pre = request.pre;

            request.clear();
            mImageRequestPool.addLast(request);
            mImageRequests.remove(request);
        }
        mLock.unlock();
//        }
    }

    // image display request
    private static class ImageDisplayRequest {
        public WeakReference<Object> view;
        public ImageDisplayer displayer;

        public ImageLoaderListener listener;

        public ImageDisplayRequest pre;
        public ImageDisplayRequest next;

        public ImageDisplayRequest() {
        }

        public ImageDisplayRequest(Object view, ImageDisplayer displayer) {
            this.view = new WeakReference<Object>(view);
            this.displayer = displayer;
        }

        public ImageDisplayRequest(ImageLoaderListener listener) {
            this.listener = listener;
        }

        public void set(Object view, ImageDisplayer displayer) {
            this.view = new WeakReference<Object>(view);
            this.displayer = displayer;
            this.listener = null;
            pre = null;
            next = null;
        }

        public void set(ImageLoaderListener listener) {
            this.view = null;
            this.displayer = null;
            this.listener = listener;
            pre = null;
            next = null;
        }

//        public void copy(ImageDisplayRequest request) {
//            if (request == null || request == this) return;
//
//            view = request.view;
//            displayer = request.displayer;
//            listener = request.listener;
//            pre = request.pre;
//            next = request.next;
//        }

        public void clear() {
            view = null;
            displayer = null;
            listener = null;
            pre = null;
            next = null;
        }
    }

    // image request
    private static class ImageLoaderTask extends AbstractTask {
        public String uri;
        public ImageDecoder decoder;
        public ImageConverter converter;

        public Bitmap image;
        public ImageDisplayRequest request_header;

        public ImageLoaderTask(String uri, ImageDecoder decoder, ImageConverter converter,
                               ImageDisplayRequest request) {
            request_header = new ImageDisplayRequest();
            set(uri, decoder, converter, request);
        }

        public void set(String uri, ImageDecoder decoder, ImageConverter converter,
                        ImageDisplayRequest request) {
            this.uri = uri;
            this.decoder = decoder;
            this.converter = converter;
            request_header.next = request;
            if (request != null) {
                request.pre = request_header;
            }
        }

        public void addDisplayRequest(ImageDisplayRequest request) {
            if (request == null) return;

            request.next = request_header.next;
            request_header.next = request;
            request.pre = request_header;
            if (request.next != null) request.next.pre = request;
        }

        public void clear() {
            uri = null;
            decoder = null;
            converter = null;
            image = null;
            request_header.next = null;
        }

        @Override
        protected void work() {
            if (decoder == null) {
                image = LocalImageDecoder.getInstance().decodeImage(uri);
                if(image == null) {
                    String uri = this.uri;
                    ImageConverter converter = this.converter;
                    ImageDisplayRequest request = request_header.next;
                    request_header.next = null;
                    finishLoader(this);

                    mLock.lock();
                    //实列化HttpImageDecoder对象
                    ImageLoaderTask task = getLoaderTask(uri, HttpImageDecoder.getInstance(),
                            converter, request);
                    addLoadingTask(uri, converter, task);
                    TaskUtils.executeTask(ImageConfigure.TASK_WEB_IMAGE_TAG, task);
                    mLock.unlock();
                    return;
                }
//                image = HttpImageDecoder.getInstance().decodeImage(uri);
            } else {
                image = decoder.decodeImage(uri);//加载图片
            }

            if (image != null && converter != null) image = converter.convertImage(image);
            if (image == null) {
                finishLoader(this);
                return;
            }

            putImageCache(uri, converter, image); //把图片保存到内存缓存中
            Message msg = sHandler.obtainMessage(MESSAGE_UPDATE_IMAGE);
            msg.obj = this;
            sHandler.sendMessage(msg);
        }
    }

    /*
     * interface
     */
    public interface ImageDecoder {
        public Bitmap decodeImage(String uri);
    }

    public interface ImageConverter {
        public Bitmap convertImage(Bitmap image);
    }

    public interface ImageDisplayer {
        public void displayImage(Object view, Bitmap image);
        public void displayImage(Object view, int resId);
    }

    public interface ImageLoaderListener {
        public void onImageLoaded(String uri, Bitmap image);
    }
}
