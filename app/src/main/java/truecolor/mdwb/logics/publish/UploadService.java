package truecolor.mdwb.logics.publish;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import truecolor.mdwb.global.Constant;

/**
 * Created by xiaowu on 15/9/23.
 */
public class UploadService extends Service{
    private static final int THREAD_SIZE = 2;
    public static ExecutorService THREAD_POOL = null;
//    public static FinalDb DOWNLOAD_DB = null;
//    public static FinalDb SUB_DOWNLOAD_DB = null;
    public static Map<Integer, UploadTask> TASKS = new LinkedHashMap<Integer, UploadTask>();

//    public static DownloadInfo DOWNLOAD_INFO;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            if (intent.getAction().equals(Constant.ACTION_START)){
//                createDb();
                //创建一个可重用固定线程数的线程池
                THREAD_POOL = Executors.newFixedThreadPool(THREAD_SIZE);
            }
            if (intent.getAction().equals(Constant.ACTION_STOP)){
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UploadManager.stopAllTask();
    }

//    private void createDb(){
//        if (DOWNLOAD_DB == null){
//            DOWNLOAD_DB = FinalDb.create(this, Constant.DB_DOWNLOAD_INFO_NAME, true);
//        }
//        if (SUB_DOWNLOAD_DB == null){
//            SUB_DOWNLOAD_DB = FinalDb.create(this, Constant.DB_SUB_DOWNLOAD_INFO_NAME, true);
//        }
//    }

//    public static void setDownloadIfo(DownloadInfo downloadIfo){
//        DOWNLOAD_INFO = downloadIfo;
//    }

    public static boolean isServiceRunning(Context context) {
        boolean isRunning = false;

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(UploadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static void startService(Context context) {
        if(! isServiceRunning(context)){
            Intent intent=new Intent(context,UploadService.class);
            intent.setAction(Constant.ACTION_START);
            context.startService(intent);
        }
    }
}
