package top.linl.qstorycloud.hook.update;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.functions.Consumer;
import top.linl.qstorycloud.db.LocalModuleInfoDAO;
import top.linl.qstorycloud.db.UpdateInfoDAO;
import top.linl.qstorycloud.hook.HookEnv;
import top.linl.qstorycloud.hook.PathTool;
import top.linl.qstorycloud.hook.moduleloader.model.LocalModuleInfo;
import top.linl.qstorycloud.hook.update.model.UpdateInfo;
import top.linl.qstorycloud.hook.update.util.DownloadTask;
import top.linl.qstorycloud.hook.util.ActivityTools;
import top.linl.qstorycloud.hook.util.ToastTool;
import top.linl.qstorycloud.log.QSLog;
import top.linl.qstorycloud.util.TaskManager;

/**
 * 更新观察者类
 */
public class UpdateObserver implements Consumer<UpdateInfo> {

    public AtomicBoolean isDownloading = new AtomicBoolean();
    public AtomicBoolean isUpdateEnd = new AtomicBoolean();

    /**
     * 收到更新信息
     */
    @Override
    public void accept(UpdateInfo updateInfo) {
        if (updateInfo.getUpdateUrl() == null) return;

        //如果在下载中那不重复触发
        if (isDownloading.get()) return;
        //如果更新结束那不重复触发
        if (isUpdateEnd.get()) return;


        UpdateInfo localUpdateInfo = UpdateInfoDAO.getLastUpdateInfo();
        LocalModuleInfo localModuleInfo = LocalModuleInfoDAO.getLastModuleInfo();

        //判断更新信息是否已经存在于数据库,不存在则插入
        if (!updateInfo.equals(localUpdateInfo)) {
            UpdateInfoDAO.insertUpdateInfo(updateInfo);
        }

        //检测是否是首次使用或者有云端更新
        //本地模块信息为空说明是第一次使用 || 本地模块版本号小于最新云端版本号
        if (localModuleInfo == null || localModuleInfo.getModuleVersionCode() < updateInfo.getLatestVersionCode()) {
            sendUpdateTask(updateInfo);
        }

        //模块文件不存在也进行更新
        else if (!new File(localModuleInfo.getModuleApkPath()).exists()) {
            sendUpdateTask(updateInfo);
        }
    }


    /**
     * 发送更新任务
     */
    public void sendUpdateTask(UpdateInfo updateInfo) {
        isDownloading.set(true);
        try {

            ToastTool.show("QStory-云更新 开始执行更新任务");
            //下载
            String downloadPath = PathTool.getDataPath() + "/" + updateInfo.getLatestVersionName();
            DownloadTask downloadTask = new DownloadTask(HookEnv.getHostAppContext());
            downloadTask.download(updateInfo.getUpdateUrl(), downloadPath);
            //下载完成 开始写入数据库
            LocalModuleInfo localModuleInfo = new LocalModuleInfo();
            localModuleInfo.setModuleApkPath(downloadPath);
            localModuleInfo.setModuleName(updateInfo.getLatestVersionName());
            localModuleInfo.setModuleVersionCode(updateInfo.getLatestVersionCode());
            localModuleInfo.setModuleVersionName(updateInfo.getLatestVersionName());
            localModuleInfo.setUpdateLogHaveRead(false);
            //插入模块更新信息
            LocalModuleInfoDAO.insertModuleInfo(localModuleInfo);
            //数据库写入完成
            ToastTool.show("更新任务执行成功,重启QQ");

            isUpdateEnd.set(true);
            TaskManager.addDelayTask(() -> {
                ActivityTools.killAppProcess(HookEnv.getHostAppContext());
            }, 3000);
        } catch (Exception e) {
            ToastTool.show("更新失败：" + e);
            QSLog.e("更新失败", e);
        }
        isDownloading.set(false);
    }
}
