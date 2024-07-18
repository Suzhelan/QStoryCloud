package top.linl.qstorycloud.util;

import android.os.Handler;
import android.os.Looper;

public class TaskManager {
    public static void addTask(Runnable runnable)
    {
        new Handler(Looper.getMainLooper())
                .post(runnable);
    }
}
