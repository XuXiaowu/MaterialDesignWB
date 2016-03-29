package truecolor.mdwb.logics.publish;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import truecolor.mdwb.model.PublishStatus;

/**
 * Created by xiaowu on 15/9/23.
 */
public class UploadManager {

    private static List<UploadTask> MANAGER_TASKS = new ArrayList<>();

    public static void addTask(PublishStatus publishStatus){
        UploadTask uploadTask = new UploadTask(publishStatus);
        MANAGER_TASKS.add(uploadTask);
        UploadService.THREAD_POOL.execute(uploadTask);
    }

    public static void stopAllTask(){

    }
}
