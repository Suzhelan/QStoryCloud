package top.linl.qstorycloud.util

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import com.tencent.mmkv.MMKV

/**
 * mmkv封装类
 */
class SpHelper(id: String) {
    private val mmkv = MMKV.mmkvWithID(id)

    companion object {
        fun getMMKV(id: String): SpHelper {
            return SpHelper(id)
        }
    }

    /**
     * 保存数据的方法
     * @param key
     * @param value
     */
    fun encode(key: String, value: Any) {
        when (value) {
            is String -> {
                mmkv.encode(key, value)
            }

            is Int -> {
                mmkv.encode(key, value)
            }

            is Boolean -> {
                mmkv.encode(key, value)
            }

            is Float -> {
                mmkv.encode(key, value)
            }

            is Long -> {
                mmkv.encode(key, value)
            }

            is Double -> {
                mmkv.encode(key, value)
            }

            is ByteArray -> {
                mmkv.encode(key, value)
            }

            else -> {
                mmkv.encode(key, JSON.toJSONString(value))
            }
        }
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    fun decodeInt(key: String, def: Int = 0): Int {
        return mmkv.decodeInt(key, def)
    }

    fun decodeDouble(key: String, def: Double = 0.00): Double {
        return mmkv.decodeDouble(key, def)
    }

    fun decodeLong(key: String, def: Long = 0L): Long {
        return mmkv.decodeLong(key, def)
    }

    fun decodeBoolean(key: String, def: Boolean): Boolean {
        return mmkv.decodeBool(key, def)
    }

    fun decodeFloat(key: String, def: Float = 0f): Float {
        return mmkv.decodeFloat(key, def)
    }

    fun decodeBytes(key: String): ByteArray {
        return mmkv.decodeBytes(key) ?: byteArrayOf()
    }

    fun decodeString(key: String, def: String = ""): String {
        return mmkv.decodeString(key, def) ?: ""
    }


    fun <T> decodeObject(key: String, type: Class<T>): T? {
        val data = mmkv.decodeString(key)
        if (data.isNullOrEmpty()) {
            return null
        }
        return JSON.parseObject(data, type)
    }

    fun <T> decodeType(key: String, def: T): T {
        val data = mmkv.decodeString(key)
        if (data.isNullOrEmpty()) {
            return def
        }
        return JSON.parseObject(data, object : TypeReference<T>() {})
    }

    fun <T> decodeType(key: String, type: TypeReference<T>): T? {
        val data = mmkv.decodeString(key)
        if (data.isNullOrEmpty()) {
            return null
        }
        return JSON.parseObject(data, type)
    }
    /**
     * 清除所有key
     */
    fun clearAll() {
        mmkv.clearAll()
    }

    fun remove(key: String) {
        mmkv.removeValueForKey(key)
    }

    /**
     * 获取所有key
     */
    fun getAllKeys(): List<String> {
        return mmkv.allKeys()?.toList() ?: listOf()
    }

    /**
     * 是否包含某个key
     */
    fun containKey(key: String): Boolean {
        return mmkv.containsKey(key)
    }

    /**
     * 移除指定key的value
     */
    fun removeValueForKey(key: String) {
        mmkv.removeValueForKey(key)
    }

    /**
     * 移除指定key集合的value
     */
    fun removeValuesForKeys(keys: Array<String>) {
        mmkv.removeValuesForKeys(keys)
    }
}