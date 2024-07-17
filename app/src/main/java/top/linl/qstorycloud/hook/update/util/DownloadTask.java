package top.linl.qstorycloud.hook.update.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.linl.qstorycloud.R;
import top.linl.qstorycloud.db.UpdateInfoDAO;
import top.linl.qstorycloud.hook.HookEnv;
import top.linl.qstorycloud.hook.update.model.UpdateInfo;
import top.linl.qstorycloud.hook.util.ActivityTools;
import top.linl.qstorycloud.hook.util.ToastTool;

public class DownloadTask {

    private static final String channelId = "QStoryCloud";
    private final int notificationFlag = (int) (System.currentTimeMillis() / 2);
    private final Context context;
    private final UpdateInfo updateInfo;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private NotificationClickReceiver clickReceiver;

    public DownloadTask(Context context) {
        this.context = context;
        updateInfo = UpdateInfoDAO.getLastUpdateInfo();
        initializeNotification();
    }

    private static boolean isAppForeground(Context context) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList =
                activityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList == null) {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.processName.equals(context.getPackageName())
                    && (processInfo.importance ==
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)) {
                return true;
            }
        }
        return false;
    }

    private void initializeNotification() {
        // 创建一个通知频道 NotificationChannel
        NotificationChannel channel = new NotificationChannel(channelId, "QStoryCloud", NotificationManager.IMPORTANCE_DEFAULT);
        //桌面小红点
        channel.enableLights(false);
        //通知显示
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private void sendProgressNotification(int size) {
        if (updateInfo == null) {
            return;
        }
        builder = new NotificationCompat.Builder(context, channelId);
        ActivityTools.injectResourcesToContext(context);
        String contentText = "下载中 大小" + getNetFileSizeDescription(size) + " 切换QQ到后台可查看具体进度";
        if (size == 0) {
            contentText = "准备开始下载";
        }
        builder.setContentTitle("QStory正在云更新到" + updateInfo.getLatestVersionName()) //设置标题
                .setSmallIcon(R.mipmap.icon) //设置小图标
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false) //设置通知被点击一次不自动取消
                .setOngoing(true)
                .setSound(null)
                .setContentText(contentText); //设置内容;
        notificationManager.notify(notificationFlag, builder.build());
    }

    private void updateNotification(int max, int progress) {
        //qq在前台时通知被查看后会自动消失 因此只让qq在后台时通知进度
        if (builder == null) {
            return;
        }
        //应用在前台不更新进度
        if (isAppForeground(context)) return;

        if (progress >= 0) {
            builder.setContentText("进度:" + getNetFileSizeDescription(progress) + "/" + getNetFileSizeDescription(max));
            builder.setProgress(max, progress, false);
        }
        if (progress == max) {
            builder.setContentText("下载完成");
            builder.setAutoCancel(true);
            builder.setOngoing(false);
        }
        notificationManager.notify(notificationFlag, builder.build());
    }

    private void sendDownloadSuccessNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("QStory已更新到" + updateInfo.getLatestVersionName()) //设置标题
                .setSmallIcon(R.mipmap.icon) //设置小图标
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级
                .setAutoCancel(false) //设置通知被点击一次不自动取消
                .setOngoing(true)
                .setContentText("请手动重启QQ,模块将会生效") //设置内容
                ;
        notificationManager.notify(notificationFlag + 0xFF, builder.build());
    }

    private String getNetFileSizeDescription(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }


    public void download(String url, String path) throws IOException {
        File downloadPath = new File(path);
        if (!downloadPath.getParentFile().exists()) {
            downloadPath.getParentFile().mkdirs();
        }
        if (downloadPath.exists()) {
            downloadPath.delete();
        }
        if (!downloadPath.exists()) {
            downloadPath.createNewFile();
        }
        sendProgressNotification(0);

        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("User-Agent", "Android")
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .get()
                .build();
        Call call = client.newCall(request);
        try (Response response = call.execute();
             BufferedInputStream bufIn = new BufferedInputStream(response.body().byteStream());
             BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(downloadPath))) {
            //总字节数
            long size = response.body().contentLength();
            sendProgressNotification((int) size);
            //发送通知
            long downloadSize = 0;
            int len;
            byte[] buf = new byte[2048];//2k
            while ((len = bufIn.read(buf)) != -1) {
                bufOut.write(buf, 0, len);
                downloadSize += len;
                updateNotification((int) size, (int) downloadSize);
            }
            bufOut.flush();
        }
        sendDownloadSuccessNotification();
    }

    /**
     * 处理通知栏点击事件
     */
    public class NotificationClickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ToastTool.show("点击了通知");
            //点击退出
            ActivityTools.killAppProcess(HookEnv.getHostAppContext());
        }
    }
}
