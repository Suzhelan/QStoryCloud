package top.linl.qstorycloud.hook.update

import android.net.Uri
import com.alibaba.fastjson2.JSON
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import top.linl.qstorycloud.config.LocalModuleData
import top.linl.qstorycloud.config.UpdateInfoData
import top.linl.qstorycloud.hook.HookEnv
import top.linl.qstorycloud.hook.util.ToastTool
import top.linl.qstorycloud.log.QSLog
import top.linl.qstorycloud.model.UpdateInfo

class UpdateObserver {

    private val detectUpdatesUrl = "https://qstory.linl.top/update/detectUpdates"


    private fun cleanData() {
        //这里用到了跨进程通讯
        //获取内容提供者
        val contentResolver = HookEnv.getHostAppContext().contentResolver
        val uri = Uri.parse("content://qstorycloud.linl.top/cleanData")
        val cursor =
            contentResolver.query(uri, arrayOf("value"), "name=?", arrayOf("clean_data"), null)
        var isCleanData: String? = null
        if (cursor != null && cursor.moveToNext()) {
            isCleanData = cursor.getString(0)
            cursor.close()
        }
        if (isCleanData != null) {
            LocalModuleData.clear()
            UpdateInfoData.clear()
            ToastTool.show("收到重置指令")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun runObserver() {
        GlobalScope.launch {
            cleanData()
            getUpdateTaskFlow().collect(UpdateChecker())
        }
    }

    private fun getUpdateTaskFlow(): Flow<UpdateInfo> {
        return flow {
            while (true) {
                //循环检测更新
                val updateInfo = getUpdateInfo()
                if (updateInfo != null && !updateInfo.updateUrl.isNullOrEmpty()) {
                    emit(updateInfo)
                }
                //延迟10分钟
                delay(60 * 1000 * 10)
            }
        }.flowOn(Dispatchers.IO)//在io线程运行协程
    }

    /**
     * 请求最新版本
     */
    private fun getUpdateInfo(): UpdateInfo? {
        val localModuleInfo = LocalModuleData.getLastModuleInfo()
        var moduleVersionCode = 0
        if (localModuleInfo != null) {
            moduleVersionCode = localModuleInfo.moduleVersionCode
        }
        val client: OkHttpClient = OkHttpClient.Builder().build()
        val formBody: FormBody = FormBody.Builder()
            .add("versionCode", moduleVersionCode.toString())
            .build()
        val request: Request = Request.Builder()
            .url(detectUpdatesUrl)
            .post(formBody)
            .addHeader("User-Agent", "Android")
            .addHeader("Content-Type", "text/plain")
            .addHeader("Accept", "*/*")
            .addHeader("Connection", "keep-alive")
            .build()
        try {
            client.newCall(request).execute().use { response ->
                val data = response.body.string()
                return JSON.parseObject(data, UpdateInfo::class.java)
            }
        } catch (e: Exception) {
            QSLog.e("DetectUpdates", e)
            return null
        }
    }
}