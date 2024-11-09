package top.linl.qstorycloud.hook.update

import kotlinx.coroutines.flow.FlowCollector
import top.linl.qstorycloud.config.LocalModuleData
import top.linl.qstorycloud.config.UpdateInfoData
import top.linl.qstorycloud.hook.HookEnv
import top.linl.qstorycloud.hook.PathTool
import top.linl.qstorycloud.hook.util.ToastTool
import top.linl.qstorycloud.log.QSLog
import top.linl.qstorycloud.model.LocalModuleInfo
import top.linl.qstorycloud.model.UpdateInfo
import top.linl.qstorycloud.util.SpHelper
import java.io.File

class UpdateChecker : FlowCollector<UpdateInfo> {

    val spHelper = SpHelper("UpdateChecker")

    override suspend fun emit(value: UpdateInfo) {
        QSLog.d(this, "UpdateInfo: $value")

        val lastModuleInfo = LocalModuleData.getLastModuleInfo()
        val lastUpdateInfo = UpdateInfoData.getLastUpdateInfo()
        //如果从未初始化过
        if (lastModuleInfo == null || lastUpdateInfo == null || !File(lastModuleInfo.moduleApkPath).exists()) {
            QSLog.d(this, "未初始化过last module:$lastModuleInfo, update info: $lastUpdateInfo")
            download(value)
            return
        }

        //如果云端版本大于本地更新版本
        if (value.latestVersionCode > lastUpdateInfo.latestVersionCode) {
            QSLog.d(this, "云端版本大于本地版本")
            download(value)
            return
        }

        //如果云端版本大于 本地版本
        if (lastUpdateInfo.latestVersionCode > lastModuleInfo.moduleVersionCode) {
            download(value)
            return
        }
    }

    private fun download(updateInfo: UpdateInfo) {
        UpdateInfoData.addUpdateInfo(updateInfo)
        QSLog.d(this, "开始下载")
        ToastTool.show("QStory开始更新")
        //下载
        val downloadTask = DownloadTask(
            HookEnv.getHostAppContext(),
            updateInfo
        )
        val downloadPath = PathTool.getDataPath() + "/" + updateInfo.latestVersionName
        downloadTask.download(updateInfo.updateUrl, downloadPath)
        //下载完成 初始化bean
        val localModuleInfo = LocalModuleInfo().apply {
            moduleApkPath = downloadPath
            moduleName = updateInfo.latestVersionName
            moduleVersionCode = updateInfo.latestVersionCode
            moduleVersionName = updateInfo.latestVersionName
        }
        LocalModuleData.addModuleInfo(localModuleInfo)
        ToastTool.show("QStory已更新完成，请重启QQ")
        QSLog.d(this, "下载完成")
    }
}