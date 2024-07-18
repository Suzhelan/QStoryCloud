package top.linl.qstorycloud.hook.update;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.alibaba.fastjson2.JSON;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.linl.qstorycloud.db.LocalModuleInfoDAO;
import top.linl.qstorycloud.db.helper.UpdateDBHelper;
import top.linl.qstorycloud.hook.HookEnv;
import top.linl.qstorycloud.hook.moduleloader.model.LocalModuleInfo;
import top.linl.qstorycloud.hook.update.model.UpdateInfo;
import top.linl.qstorycloud.hook.util.ToastTool;
import top.linl.qstorycloud.log.QSLog;

public class UpdateChecker {
    private final String detectUpdatesUrl = "https://qstory.linl.top/update/detectUpdates";

    private UpdateInfo lastUpdateInfo;

    private void run() {
        //这里用到了跨进程通讯
        //获取内容提供者
        ContentResolver contentResolver = HookEnv.getHostAppContext().getContentResolver();
        //查询是否开启了安全模式
        Uri uri = Uri.parse("content://qstorycloud.linl.top/cleanData");
        Cursor cursor = contentResolver.query(uri, new String[]{"value"}, "name=?", new String[]{"clean_data"}, null);
        String isCleanData = null;
        if (cursor != null && cursor.moveToNext()) {
            isCleanData = cursor.getString(0);
            cursor.close();
        }
        if (isCleanData != null) {
            UpdateDBHelper dbHelper = UpdateDBHelper.getInstance();
            dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 1);
            ToastTool.show("收到重置指令");
        }
    }
    public void startObservingUpdates() {
        if (!HookEnv.isMainProcess()) {
            return;
        }
        run();
        //观察者
        Disposable disposable = Observable.create((ObservableOnSubscribe<UpdateInfo>) emitter -> {
            //先去查询一次之前的版本
            //循环检测更新
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    UpdateInfo updateInfo = getUpdateInfo();
                    if (updateInfo == null) return;
                    if (updateInfo.equals(lastUpdateInfo)) return;
                    lastUpdateInfo = updateInfo;
                    emitter.onNext(updateInfo);
                }
            }, 0, 10 * 1000);//每十分钟轮询一次
        }).subscribe(new UpdateObserver());
    }

    /**
     * 请求最新版本
     */
    private UpdateInfo getUpdateInfo() {
        LocalModuleInfo localModuleInfo = LocalModuleInfoDAO.getLastModuleInfo();
        int moduleVersionCode = 0;
        if (localModuleInfo != null) {
            moduleVersionCode = localModuleInfo.getModuleVersionCode();
        }
        OkHttpClient client = new OkHttpClient.Builder().build();
        FormBody formBody = new FormBody.Builder()
                .add("versionCode", String.valueOf(moduleVersionCode))
                .build();
        Request request = new Request
                .Builder()
                .url(detectUpdatesUrl)
                .post(formBody)
                .addHeader("User-Agent", "Android")
                .addHeader("Content-Type", "text/plain")
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .build();
        try (Response response = client.newCall(request).execute()) {
            String data = response.body().string();
            return JSON.parseObject(data, UpdateInfo.class);
        } catch (Exception e) {
            QSLog.e("DetectUpdates", e);
            return null;
        }

    }
}
