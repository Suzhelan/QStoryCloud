package top.sacz.qstory.config

import com.alibaba.fastjson2.TypeReference
import top.linl.qstorycloud.util.SpHelper
import top.sacz.qstory.net.UpdateInfo
import top.sacz.qstory.net.bean.ModuleInfo

object ModuleConfig {
    private val sp = SpHelper("UpdateInfoConfig")


    fun getUpdateInfoList(): List<UpdateInfo> {
        val type = object : TypeReference<List<UpdateInfo>>() {}
        return sp.getType("updateInfoList", type) ?: listOf()
    }

    fun setUpdateInfoList(updateInfoList: List<UpdateInfo>) {
        sp.put("updateInfoList", updateInfoList)
    }

    fun clear() {
        sp.clearAll()
    }

    fun getModuleInfo(): ModuleInfo {
        return sp.getObject("moduleInfo", ModuleInfo::class.java) ?: ModuleInfo().apply {
            versionCode = 0
        }
    }

    fun setModuleInfo(moduleInfo: ModuleInfo) {
        sp.put("moduleInfo", moduleInfo)
    }

    fun isReadUpdateLog(): Boolean {
        return sp.getBoolean("isReadUpdateLog:${getModuleInfo().versionCode}", false)
    }

    fun setReadUpdateLog() {
        sp.put("isReadUpdateLog:${getModuleInfo().versionCode}", true)
    }

}