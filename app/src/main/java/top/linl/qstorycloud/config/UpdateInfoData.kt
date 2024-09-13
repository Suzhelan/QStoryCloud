package top.linl.qstorycloud.config

import com.alibaba.fastjson2.TypeReference
import top.linl.qstorycloud.model.LocalModuleInfo
import top.linl.qstorycloud.model.UpdateInfo
import top.linl.qstorycloud.util.SpHelper

object UpdateInfoData {
    private val spHelper = SpHelper.getMMKV("UpdateInfoData")

    fun getLastUpdateInfo(): UpdateInfo? {
        val data = getDataList()
        if (data.isEmpty()) {
            return null
        }
        return data.last()
    }

    fun updateLastUpdateInfo(updateInfo: UpdateInfo) {
        val data = getDataList()
        data.removeLast()
        data.add(updateInfo)
        spHelper.encode("dataList", data)
    }

    fun addUpdateInfo(updateInfo: UpdateInfo) {
        val data = getDataList()
        data.add(updateInfo)
        spHelper.encode("dataList", data)
    }

    fun clear() {
        spHelper.encode("dataList", ArrayList<LocalModuleInfo>())
    }

    private fun getDataList(): MutableList<UpdateInfo> {
        return spHelper.decodeType("dataList", object : TypeReference<MutableList<UpdateInfo>>() {})
            ?: mutableListOf()
    }
}