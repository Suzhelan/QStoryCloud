package top.sacz.qstory.hook

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import top.linl.qstorycloud.hook.HookEnv
import top.linl.qstorycloud.hook.PathTool
import top.linl.qstorycloud.hook.util.ToastTool
import top.linl.qstorycloud.log.QSLog
import top.sacz.qstory.config.ModuleConfig
import top.sacz.qstory.net.DownloadTask
import top.sacz.qstory.net.HasUpdate
import top.sacz.qstory.net.bean.ModuleInfo
import top.sacz.qstory.net.UpdateInfo
import top.sacz.qstory.net.bean.QSResult


class UpdateTask {

    fun init() {
        start()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun start() {
        GlobalScope.launch {
            while (true) {
                //是否有更新
                hasUpdate()?.apply {

                    //有更新或强制更新
                    if (hasUpdate || isForceUpdate) {
                        ToastTool.show("[QStory]检测到有新版本")
                        doUpdate()
                    }
                }
                //延迟5分钟
                delay(10 * 60 * 1000)
            }
        }
    }

    /**
     * 执行更新操作
     */
    private fun doUpdate() {
        val updateInfoList = getUpdateInfoList()
        if (updateInfoList.isEmpty()) {
            return
        }
        //更新到本地
        ModuleConfig.setUpdateInfoList(updateInfoList)
        //获取最新一条
        val updateInfo = updateInfoList.first()
        val versionCode = updateInfo.versionCode
        //更新链接
        val downloadUrl = "https://qstory.sacz.top/update/download?version=$versionCode"
        //下载路径
        val downloadPath = PathTool.getDataPath() + "/" + updateInfo.fileName
        //构造更新
        val downloadTask = DownloadTask(HookEnv.getHostAppContext(),updateInfo)
        downloadTask.download(downloadUrl, downloadPath)

        //构造信息并存入本地对象
        val moduleInfo = ModuleInfo()
        moduleInfo.versionCode = versionCode!!
        moduleInfo.path = downloadPath

        ModuleConfig.setModuleInfo(moduleInfo)
        ToastTool.show("QStory已更新完成，请重启QQ")
    }

    private fun getUpdateInfoList(): List<UpdateInfo> {
        try {
            val client = OkHttpClient().newBuilder()
                .build()
            val formBody: FormBody = FormBody.Builder()
                .add("version", ModuleConfig.getModuleInfo().versionCode.toString())
                .build()
            val request: Request = Request.Builder()
                .url("https://qstory.sacz.top/update/getUpdateLog")
                .method("POST", formBody)
                .addHeader("User-Agent", "QStoryCloud/Android")
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .build()
            val response = client.newCall(request).execute()
            val type = object : TypeReference<QSResult<List<UpdateInfo>>>() {}
            return JSON.parseObject(
                response.body.string(),
                type
            ).data
        } catch (e: Exception) {
            ToastTool.show("更新失败$e")

            return mutableListOf()
        }
    }

    private fun hasUpdate(): HasUpdate? {
        try {
            val client = OkHttpClient().newBuilder()
                .build()
            val formBody: FormBody = FormBody.Builder()
                .add("version", ModuleConfig.getModuleInfo().versionCode.toString())
                .build()
            val request: Request = Request.Builder()
                .url("https://qstory.sacz.top/update/hasUpdate")
                .method("POST", formBody)
                .addHeader("User-Agent", "QStoryCloud/Android")
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .build()
            val response = client.newCall(request).execute()
            val result = JSON.parseObject(response.body.string())
            response.close()
            val data =result.getJSONObject("data")
            return JSON.parseObject(data.toString(), HasUpdate::class.java)
        } catch (e: Exception) {
            return null
        }
    }
}