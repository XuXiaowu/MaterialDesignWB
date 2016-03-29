package truecolor.mdwb.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import truecolor.mdwb.R;
import truecolor.mdwb.global.Constant;

/**
 * Created by xiaowu on 15/9/24.
 */
public class NotificationUtils {

    public static void  showPuclishStatusNotification(Context context,int showType){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = null;
        if (showType == Constant.PUBLISH_STATUS_NOTIFICATION_UPLOADING) {
            notify = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("微博发送中......")
                    .setContentTitle("MaterialDesignWB")
                    .setContentText("微博发送中......")
//                    .setContentIntent(pendingIntent3)
//                .setNumber(1)
                    .build(); // 需要注意build()是在API // level16及之后增加的，API11可以使用getNotificatin()来替代
        } else if (showType == Constant.PUBLISH_STATUS_NOTIFICATION_SUCCESS){
            notify = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("微博发送成功")
                    .setContentTitle("MaterialDesignWB")
                    .setContentText("微博发送成功")
//                    .setContentIntent(pendingIntent3)
//                .setNumber(1)
                    .build(); // 需要注意build()是在API // level16及之后增加的，API11可以使用getNotificatin()来替代
        } else if (showType == Constant.PUBLISH_STATUS_NOTIFICATION_FAIL){
            notify = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("微博发送失败")
                    .setContentTitle("MaterialDesignWB")
                    .setContentText("微博发送失败")
//                    .setContentIntent(pendingIntent3)
//                .setNumber(1)
                    .build(); // 需要注意build()是在API // level16及之后增加的，API11可以使用getNotificatin()来替代
        }

        notify.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        manager.notify(1, notify);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加
    }
}
