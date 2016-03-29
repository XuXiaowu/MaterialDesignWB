package truecolor.webdataloader;

import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xiaowu on 15/7/2.
 */
public abstract class AbstractTask implements Runnable {

    private static AtomicInteger ExecutorId = new AtomicInteger();

    protected Context mContext;
    protected final int mTaskId;

    private boolean mIsRunning;
    private boolean mIsCanceled;

    public AbstractTask() {
        mContext = null;
        mTaskId = ExecutorId.getAndAdd(1);
        mIsRunning = false;
        mIsCanceled = false;
    }

    public AbstractTask(Context context) {
        mContext = context;
        mTaskId = ExecutorId.getAndAdd(1);
        mIsRunning = false;
        mIsCanceled = false;
    }

    public final int getTaskId() {
        return mTaskId;
    }

    public final boolean isRunning() {
        return mIsRunning;
    }

    public final boolean isCanceled() {
        return mIsCanceled;
    }

    public final void cancel() {
        mIsCanceled = true;
    }

    protected void onError() { }
    protected void onEnd() { }

    protected abstract void work();

    /**
     * Sets this Future to the result of its computation
     * unless it has been cancelled.
     */
    public void run() {
        if(mIsCanceled) return;

        try {
            mIsRunning = true;
            work();
            mIsRunning = false;
            onEnd();
        } catch (Throwable ex) {
            mIsRunning = false;
            onError();
        }
    }
}
