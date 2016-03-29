package truecolor.webdataloader.task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import truecolor.webdataloader.AbstractTask;

public class TaskUtils {

//    private static final int KANKAN_THREAD_POOL_SIZE        = 4;
//    private static final int WEB_IMAGE_THREAD_POOL_SIZE        = 2;
//    private static final int LOCAL_TASK_THREAD_POOL_SIZE    = 2;
//    private static final int OTHER_TASK_THREAD_POOL_SIZE    = 1;
//    private static final int USER_SERVICE_TASK_POOL_SIZE    = 4;
//    private static final int CLOUD_THREAD_POOL_SIZE         = 2;
    // tag: "Kankan", "VideoUrl", "WebImage", "Local", "Other", "UserService", "CloudService"

    private static final String DEFAULT_TAG = "default_tag";

    private static final int MAX_TASK_THREAD = 2;

    private static ExecutorService mExecutor = Executors.newCachedThreadPool();
    private static HashMap<String, TaskList> mTables = new HashMap<String, TaskList>();

//    private static final LinkedList<AbstractTask> mTaskList = new LinkedList<AbstractTask>();
//    private static int mTaskNum = 0;

    private static TaskList getTaskList(String tag) {
        TaskList list = mTables.get(tag);
        if(list == null) {
            list = new TaskList();
            mTables.put(tag, list);
        }
        return list;
    }

//    private static TaskList getTaskList(String tag, int maxNum) {
//        TaskList list = mTables.get(tag);
//        if(list == null) {
//            list = new TaskList(maxNum);
//            mTables.put(tag, list);
//        }
//        return list;
//    }

    public static void setTaskMaxSize(String tag, int maxNum) {
        TaskList list = mTables.get(tag);
        if(list != null) {
            list.setMaxNum(maxNum);
        } else {
            mTables.put(tag, new TaskList(maxNum));
        }
//        if(!mTables.containsKey(tag)) {
//            mTables.put(tag, new TaskList(maxNum));
//        }
    }

    public static void executeTask(AbstractTask task) {
        executeTask(DEFAULT_TAG, task);
    }

    public static void executeTask(String tag, AbstractTask task) {  //用一个TaskList管理一个类型的网络请求
        TaskList list = getTaskList(tag);//获取一个任务列表,先从mTables取出，如果mTables里没有的话就new一个TaskList
        list.addTask(task); //把Task加进TaskList并运行
    }

    public static boolean cancelTask(AbstractTask task) {
        return cancelTask(DEFAULT_TAG, task);
    }

    public static boolean cancelTask(String tag, AbstractTask task) {
        TaskList list = getTaskList(tag);
        return list.removeTask(task);
    }

    private static class TaskList extends LinkedList<AbstractTask> {
        private int mMaxNum;
        private int mTaskNum;

        public TaskList() {
            mMaxNum = MAX_TASK_THREAD;
            mTaskNum = 0;
        }

        public TaskList(int maxNum) {
            mMaxNum = maxNum;
            mTaskNum = 0;
        }

        public void setMaxNum(int maxNum) {
            mMaxNum = maxNum;
            int num = size();
            while(num > 0 && (maxNum < 0 || mTaskNum < maxNum)) {
                num--;
                mTaskNum++;
                mExecutor.execute(new TaskRunner(this));
            }
        }

        public synchronized void addTask(AbstractTask task) {
            if(task == null) return;

//            add(task);
            addFirst(task);
            /**
             * 最大Task数量大于0，而且当前Task数量小于最大Task数量
             * 如果当前TaskList数量小于mMaxNum数量，直接运行Task，如果不小于，把Task加入TaskList里，在TaskRunner循环取出TaskList
             * 里的Task并运行
             */
            if(mMaxNum < 0 || mTaskNum < mMaxNum) {
                mTaskNum++;
                mExecutor.execute(new TaskRunner(this)); //把Runnabler放入线程池运行
            }
        }

        public synchronized AbstractTask pollTask() {
            AbstractTask task = poll();
            if(task == null) mTaskNum--;
            return task;
        }

        public synchronized boolean removeTask(AbstractTask task) {
            if(task == null) return false;

            if(contains(task)) {
                remove(task);
                return true;
            } else {
                task.cancel();
                return false;
            }
        }
    }

    private static class TaskRunner implements Runnable {
        private TaskList mList;

        public TaskRunner(TaskList list) {
            mList = list;
        }

        @Override
        public void run() {
            if(mList == null) return;

            while(true) {
                Runnable task = mList.pollTask(); //取出TaskList里的Task来执行
                if(task == null) {
                    break;
                }

                task.run();
            }
        }
    }


//    private static final int MAX_TASK_THREAD = 2;
//
//    private static HashMap<String, ExecutorService> mTables = new HashMap<String, ExecutorService>();
//
//    private static ExecutorService getExecutor(String tag) {
//        return getExecutor(tag, MAX_TASK_THREAD);
//    }
//
//    private static ExecutorService getExecutor(String tag, int max) {
//        ExecutorService executor = mTables.get(tag);
//        if(executor == null) {
//            executor = Executors.newFixedThreadPool(max);
//            mTables.put(tag, executor);
//        }
//        return executor;
//    }
//
//    public static void newTaskList(String tag, int maxNum) {
//        if(!mTables.containsKey(tag)) {
//            mTables.put(tag, Executors.newFixedThreadPool(maxNum));
//        }
//    }
//
//    public static void executeTask(String tag, AbstractTask task) {
//        ExecutorService executor = getExecutor(tag);
//        executor.execute(task);
//    }
}
