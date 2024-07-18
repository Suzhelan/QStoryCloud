package top.linl.qstorycloud.util;

import android.os.Handler;
import android.os.Looper;

public class TaskManager {
    public static void addTask(Runnable runnable)
    {
        new Handler(Looper.getMainLooper())
                .post(runnable);
    }
    //添加延时任务
    public static void addDelayTask(Runnable runnable, long delay)
    {
        new Handler(Looper.getMainLooper())
                .postDelayed(runnable, delay);
    }
}
