package top.linl.qstorycloud.config

import com.alibaba.fastjson2.TypeReference
import top.linl.qstorycloud.model.LocalModuleInfo
import top.linl.qstorycloud.model.UpdateInfo
import top.linl.qstorycloud.util.SpHelper

object UpdateInfoData {
    private val spHelper = SpHelper("UpdateInfoData")

    fun getLastUpdateInfo(): UpdateInfo? {
        val data = getDataList()
        if (data.isEmpty()) {
            return null
        }
        return data.last()
    }

    fun updateLastUpdateInfo(updateInfo: UpdateInfo) {
        val data = getDataList()
        data.removeAt(data.size - 1)
        data.add(updateInfo)
        spHelper.put("dataList", data)
    }

    fun addUpdateInfo(updateInfo: UpdateInfo) {
        val data = getDataList()
        data.add(updateInfo)
        spHelper.put("dataList", data)
    }

    fun clear() {
        spHelper.put("dataList", ArrayList<LocalModuleInfo>())
    }

    private fun getDataList(): MutableList<UpdateInfo> {
        return spHelper.getType("dataList", object : TypeReference<MutableList<UpdateInfo>>() {})
            ?: mutableListOf()
    }
}