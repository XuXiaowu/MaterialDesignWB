package truecolor.webdataloader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;

import java.util.HashMap;
import java.util.LinkedList;

import truecolor.webdataloader.cache.WebCacheUtils;
import truecolor.webdataloader.fastxml.XML;
import truecolor.webdataloader.fastxml.annotations.XMLType;
import truecolor.webdataloader.task.TaskUtils;

/**
 * Created by xiaowu on 15/7/2.
 */
public class WebDataLoader {

    private static final String TASK_TAG = "web_task";
    public static final String HAS_MORE_DATA = "has_more_data";

    private static final LinkedList<WebDataLoaderTask> mTaskPool = new LinkedList<WebDataLoaderTask>();
    private static final HashMap<String, WebDataLoaderTask> mLoadingWebData = new HashMap<String, WebDataLoaderTask>();

    /**
     * load web data, if has cache (not local cache) and cache is valid, return cache data
     */
    public static void loadWebData(HttpRequest request, Class clazz, WebListener listener,
                                   int service, Bundle params) {
        startWebTask(request, clazz, false, false, listener, service, params);
    }

    public static void loadWebDataRefresh(HttpRequest request, Class clazz, WebListener listener,
                                          int service, Bundle params) {
        startWebTask(request, clazz, true, false, listener, service, params);
    }

    /**
     * load web data with parameter page, same as loadWebData
     */
    public static void loadMoreWebData(HttpRequest request, Class clazz, WebListener listener,
                                       int service, Bundle params) {
        startWebTask(request, clazz, false, true, listener, service, params);
    }

    private static void startWebTask(HttpRequest request, Class clazz, boolean refresh,
                                     boolean hasPage, WebListener listener, int service, Bundle params) {
        String taskKey = request != null ? request.getUriKey() : null;
        synchronized(mLoadingWebData) {
            WebDataLoaderTask loader = taskKey == null ? null : mLoadingWebData.get(taskKey);
            if(loader != null) {
                loader.setListener(listener);
            } else {
                WebDataLoaderTask task = mTaskPool.poll();
                if(task != null) {
                    task.set(request, clazz, refresh, hasPage, listener, service, params);
                } else {
                    task = new WebDataLoaderTask(request, clazz, refresh, hasPage, listener,
                            service, params);
                }
                if(taskKey != null) mLoadingWebData.put(taskKey, task);
                TaskUtils.executeTask(TASK_TAG, task);
            }
        }
    }

    private static class WebDataLoaderTask extends AbstractTask {

        public String key;
        public HttpRequest request;
        public Class clazz;

        public boolean refresh;
        public boolean hasPage;

        public Object data;

        public WebListener listener;
        public int service;
        public Bundle params;
        public boolean hasMore;

//        public WebDataLoaderTask(HttpRequest request, Class clazz, boolean refresh, boolean hasPage,
//                int service, Bundle params) {
//            set(request, clazz, refresh, hasPage, service, params);
//        }

        public WebDataLoaderTask(HttpRequest request, Class clazz, boolean refresh, boolean hasPage,
                                 WebListener listener, int service, Bundle params) {
            set(request, clazz, refresh, hasPage, listener, service, params);
        }

//        public void set(HttpRequest request, Class clazz, boolean refresh, boolean hasPage,
//                int service, Bundle params) {
//            this.request = request;
//            if(request != null) key = request.getUriKey();
//            this.clazz = clazz;
//            this.refresh = refresh;
//            this.hasPage = hasPage;
//            this.bus = null;
//            this.service = service;
//            this.params = params;
//        }

        public void set(HttpRequest request, Class clazz, boolean refresh, boolean hasPage,
                        WebListener listener, int service, Bundle params) {
            this.request = request;
            if(request != null) key = request.getUriKey();
            this.clazz = clazz;
            this.refresh = refresh;
            this.hasPage = hasPage;
            this.listener = listener;
            this.service = service;
            this.params = params;
        }

        public void setListener(WebListener listener) {
            this.listener = listener;
        }

        public void clear() {
            request = null;
            key = null;
            clazz = null;
            refresh = false;
            hasPage = false;
            listener = null;
            service = -1;
            params = null;
            data = null;
            hasMore = false;
        }

        @Override
        protected void work() {
            do {
                // load local cache
                if(!WebCacheUtils.isLocalCacheNeedRefresh(clazz)) {
                    data = WebCacheUtils.loadLocalCache(clazz);
                    if(data != null) break;
                }
                if(request == null) break;
//                String key = request.getUriKey();
                // load memory cache(include page cache)
                if(!refresh) {
                    if(hasPage) {
                        int page = WebCacheUtils.getPageNum(clazz, key);
                        if(page >= 0) {
                            request.addQuery("page", page);
                        }
                    } else {
                        data = WebCacheUtils.loadMemoryCache(clazz, key);
                        if(data != null) {
                            hasMore = WebCacheUtils.hasMorePageData(clazz, key);
                            break;
                        }
                    }
                } else {
                    WebCacheUtils.resetMemoryCache(clazz, key);
                }
                try {
//                    UrlRegularExpressionUtils.parseHttpRequest(request);
                    String content = HttpConnectUtils.connect(request);
                    if(clazz == null) {
                        data = content;
                    } else if(content != null) {
                        // save local cache
                        WebCacheUtils.saveLocalCache(clazz, content);
                        if(clazz.isAnnotationPresent(JSONType.class)) {
                            data = JSON.parseObject(content, clazz);
                        } else if(clazz.isAnnotationPresent(XMLType.class)) {
                            data = XML.parseObject(content, clazz);
                        }
                        // save memory cache(include page cache)
                        if(data != null) {
                            data = WebCacheUtils.saveMemoryCache(clazz, key, data);
                            hasMore = WebCacheUtils.hasMorePageData(clazz, key);
                        }
                    }
                } catch(Exception e) {
                    data = null;
                }
            } while(false);
            // call listener
//            if(bus != null) {
//                if(params == null) {
//                    params = new Bundle();
//                }
//                params.putBoolean(HAS_MORE_DATA, hasMore);
//                if(data == null) {
//                    data = new RequestError(service, params);
//                } else if(data instanceof RequestResult) {
//                    ((RequestResult)data).mParams = params;
//                    ((RequestResult)data).mServiceCode = service;
//                }
//                bus.post(data);
//                finishWebTask(this);
//            } else {
                Message message = sHandler.obtainMessage();
                message.obj = this;
                sHandler.sendMessage(message);
//            }
        }
    }

    private static Handler sHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            WebDataLoaderTask task = (WebDataLoaderTask)msg.obj;
            if(task != null) {
                if(task.listener != null) {
                    if(task.params == null) {
                        task.params = new Bundle();
                    }
                    task.params.putBoolean(HAS_MORE_DATA, task.hasMore);
                    task.listener.onDataLoadFinished(task.service, task.params, task.data);
                }
                finishWebTask(task);
            }
        }
    };

    private static void finishWebTask(WebDataLoaderTask task) {
        if(task == null) return;

        synchronized(mLoadingWebData) {
            mLoadingWebData.remove(task.key);
            task.clear();
            mTaskPool.addLast(task);
        }
    }

}
