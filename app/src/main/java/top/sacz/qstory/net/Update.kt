package top.sacz.qstory.net

import java.util.Date


data class HasUpdate(
    var hasUpdate: Boolean,
    var isForceUpdate: Boolean,
    var version: Int
)

data class UpdateInfo(
    var fileName: String?=null,
    var updateLog: String?=null,
    var versionCode: Int?=null,
    var versionName: String?=null,
    var time: Date = Date(),
    var forceUpdate: Boolean = false
)