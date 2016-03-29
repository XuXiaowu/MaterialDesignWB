package truecolor.webdataloader.cache;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import truecolor.webdataloader.annotations.LocalCache;
import truecolor.webdataloader.annotations.MemoryCache;
import truecolor.webdataloader.annotations.PageCache;
import truecolor.webdataloader.annotations.PageData;
import truecolor.webdataloader.annotations.PageTotalCount;
import truecolor.webdataloader.fastxml.XML;
import truecolor.webdataloader.fastxml.annotations.XMLType;
import truecolor.webdataloader.util.PreferenceUtils;

public class WebCacheUtils {

    private static final String PREFERENCES_NAME = "local_cache_preferences";
    private static final String LOCAL_CACHE_TIME = "_time";

    /*
     * local cache
     */
    public static boolean isLocalCacheNeedRefresh(Class clazz) {
        if(clazz != null && clazz.isAnnotationPresent(LocalCache.class)) {
            int time = PreferenceUtils.getIntPref(PREFERENCES_NAME, clazz.getCanonicalName() + LOCAL_CACHE_TIME, -1);
            if(time >= 0) {
                int curTime = (int)(System.currentTimeMillis() / 1000);
                LocalCache lc = (LocalCache)clazz.getAnnotation(LocalCache.class);
                return curTime > time + lc.refresh_time();
            }
        }
        return true;
    }

    public static Object loadLocalCache(Class clazz) {
        if(clazz == null) return null;

        if(clazz.isAnnotationPresent(LocalCache.class)) {
            String str = PreferenceUtils.getStringPref(PREFERENCES_NAME, clazz.getCanonicalName(), null);
            if(!TextUtils.isEmpty(str)) {
                if(clazz.isAnnotationPresent(JSONType.class)) {
                    return JSON.parseObject(str, clazz);
                } else if(clazz.isAnnotationPresent(XMLType.class)) {
                    return XML.parseObject(str, clazz);
                }
            }
        }
        return null;
    }

    public static void saveLocalCache(Class clazz, String data) {
        if(clazz == null || data == null) return;

        if(clazz.isAnnotationPresent(LocalCache.class)) {
            PreferenceUtils.setStringPref(PREFERENCES_NAME, clazz.getCanonicalName(), data);
            int curTime = (int)(System.currentTimeMillis() / 1000);
            PreferenceUtils.setIntPref(PREFERENCES_NAME, clazz.getCanonicalName() + LOCAL_CACHE_TIME, curTime);
        }
    }

    /*
     * memory cache
     */
    public static void resetMemoryCache(Class clazz, String key) {
        if(clazz == null || key == null) return;

        DataWrapper cache = mDataCache.get(key);
        if(cache == null) return;
        cache.reset();
    }

    public static Object loadMemoryCache(Class clazz, String key) {
        if(clazz == null || key == null) return null;

        if(clazz.isAnnotationPresent(PageCache.class)) {
            DataWrapper cache = mDataCache.get(key);
            if(cache == null) return null;
            PageCache pc = (PageCache)clazz.getAnnotation(PageCache.class);
            return cache.get(pc.invalid_time());
        } else if(clazz.isAnnotationPresent(MemoryCache.class)) {
            DataWrapper cache = mDataCache.get(key);
            if(cache == null) return null;
            MemoryCache mc = (MemoryCache)clazz.getAnnotation(MemoryCache.class);
            return cache.get(mc.invalid_time());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Object saveMemoryCache(Class clazz, String key, Object data) {
        if(clazz == null || key == null || data == null) return data;

        if(!clazz.isAnnotationPresent(PageCache.class)
                && !clazz.isAnnotationPresent(MemoryCache.class)) {
            return data;
        }


        Field dataField = null;
        Field countField = null;
        if(clazz.isAnnotationPresent(PageCache.class)) {
            Field[] fields = clazz.getFields();
            if(fields != null) {
                for(Field f : fields) {
                    if(f.isAnnotationPresent(PageData.class)) {  //获取注解PageData的属性
                        dataField = f;
                    } else if(f.isAnnotationPresent(PageTotalCount.class)) {
                        countField = f;
                    }
                    if(dataField != null && countField != null) break;
                }
            }
        }

        DataWrapper cache = mDataCache.get(key);
        if(dataField != null) {
            if(cache == null) {
                cache = new DataWrapper(new ArrayList(), 0);
                mDataCache.put(key, cache);
            } else if(cache.page < 0) {
                cache.page = 0;
            }
            if(cache.data == null) cache.data = new ArrayList();
            ArrayList list = (ArrayList) cache.data;
            try {
                dataField.setAccessible(true);
                Object array = dataField.get(data); //根据PageData注解的属性 取得对象的Array
                if(array.getClass().getComponentType() != null) { //如果当前属性为数组类型
                    int size = Array.getLength(array);
                    if(size > 0) {
                        for(int i = 0; i < size; i++) {
                            Object obj = Array.get(array, i);
                            list.add(obj);
                        }
                    } else {
                        cache.hasMore = false;
                    }
                } else {
                    list.add(array);
                }
                dataField.setAccessible(false);

                if(countField != null) {
                    countField.setAccessible(true);
                    int count = countField.getInt(data);
                    cache.hasMore = count > list.size();
                    countField.setAccessible(false);
                }
            } catch(IllegalAccessException ignore) {
            }
            cache.page++;
        } else {
            if(cache != null) {
                cache.set(data);
            } else {
                cache = new DataWrapper(data);
                mDataCache.put(key, cache);
            }
        }
        return cache.data;
    }

    public static int getPageNum(Class clazz, String key) {
        if(clazz == null || key == null) return -1;

        if(clazz.isAnnotationPresent(PageCache.class)) {
            DataWrapper cache = mDataCache.get(key);
            if(cache != null) return cache.page;
        }
        return -1;
    }

    public static boolean hasMorePageData(Class clazz, String key) {
        if(clazz == null || key == null) return false;

        if(clazz.isAnnotationPresent(PageCache.class)) {
            DataWrapper cache = mDataCache.get(key);
            if(cache != null) return cache.hasMore;
        }
        return false;
    }

    /*
     * Memory Cache
     */
//    private static final int VALID_TIME = 60 * 60 * 1000;   // 1 hour
    private static final HashMap<String, DataWrapper> mDataCache = new HashMap<String, DataWrapper>();

    private static class DataWrapper {
        private Object data;
        private int page;
        private boolean hasMore;
        private int time;

        public DataWrapper(Object data) {
            this(data, -1);
        }

        public DataWrapper(Object data, int page) {
            this.data = data;
            this.page = page;
            this.hasMore = true;
            this.time = (int)(System.currentTimeMillis() / 1000);
        }

        public void reset() {
            data = null;
            page = -1;
            time = -1;
        }

        public void set(Object data) {
            this.data = data;
            this.time = (int)(System.currentTimeMillis() / 1000);
        }

        public Object get(int validTime) {
            if(validTime > 0) {
                int curTime = (int)(System.currentTimeMillis() / 1000);//当前时间
                if(curTime > time + validTime) {
                    page = -1;
                    data = null;
                }
            }
            return data;
        }
    }
}
