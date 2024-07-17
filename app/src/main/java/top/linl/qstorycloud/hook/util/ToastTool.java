package top.linl.qstorycloud.hook.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import top.linl.qstorycloud.hook.HookEnv;


public class ToastTool {
    public static void show(Object content) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Context activity = HookEnv.getHostAppContext();
                try {
                    Toast.makeText(activity, String.valueOf(content), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    try {
                        Toast.makeText(activity, String.valueOf(content), Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                    }
                }
            }
        });
    }

}
