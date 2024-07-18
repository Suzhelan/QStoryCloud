package top.linl.qstorycloud.hook.update;

import java.io.File;

import io.reactivex.rxjava3.functions.Consumer;
import top.linl.qstorycloud.db.ModuleInfoDAO;
import top.linl.qstorycloud.db.UpdateInfoDAO;
import top.linl.qstorycloud.hook.HookEnv;
import top.linl.qstorycloud.hook.PathTool;
import top.linl.qstorycloud.hook.moduleloader.model.LocalModuleInfo;
import top.linl.qstorycloud.hook.update.model.UpdateInfo;
import top.linl.qstorycloud.hook.update.util.DownloadTask;
import top.linl.qstorycloud.hook.util.ToastTool;
import top.linl.qstorycloud.log.QSLog;

/**
 * 更新观察者类
 */
public class UpdateObserver implements Consumer<UpdateInfo> {


    /**
     * 收到更新信息
     */
    @Override
    public void accept(UpdateInfo updateInfo) {
        if (updateInfo.getUpdateUrl() == null) return;

        UpdateInfo localUpdateInfo = UpdateInfoDAO.getLastUpdateInfo();
        LocalModuleInfo localModuleInfo = ModuleInfoDAO.getLastModuleInfo();

        //判断更新信息是否已经存在于数据库,不存在则插入
        if (!updateInfo.equals(localUpdateInfo)) {
            UpdateInfoDAO.insertUpdateInfo(updateInfo);
        }


        //检测是否是首次使用或者有云端更新
        //本地模块信息为空说明是第一次使用 || 本地模块版本号小于最新云端版本号
        if (localModuleInfo == null || localModuleInfo.getModuleVersionCode() != updateInfo.getLatestVersionCode()) {
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
            localModuleInfo.setLoad(false);
            //插入模块更新信息
            ModuleInfoDAO.insertModuleInfo(localModuleInfo);
            //数据库写入完成
            ToastTool.show("更新任务执行成功,请重启QQ");
        } catch (Exception e) {
            ToastTool.show("更新失败：" + e);
            QSLog.e("更新失败", e);
        }
    }
}
