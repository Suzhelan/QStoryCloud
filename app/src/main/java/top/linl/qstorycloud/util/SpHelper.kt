package top.linl.qstorycloud.util

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import io.fastkv.FastKV


/**
 * mmkv封装类
 */
class SpHelper(id: String = "default") {
    private var kv = FastKV.Builder(storePath, id).build()

    companion object {
        private var storePath = ""
        fun initialize(path: String) {
            storePath = path
        }
    }

    /**
     * 保存数据的方法
     * @param key
     * @param value
     */
    fun put(key: String, value: Any) {
        when (value) {
            is String -> {
                kv.putString(key, value)
            }

            is Int -> {
                kv.putInt(key, value)
            }

            is Boolean -> {
                kv.putBoolean(key, value)
            }

            is Float -> {
                kv.putFloat(key, value)
            }

            is Long -> {
                kv.putLong(key, value)
            }

            is Double -> {
                kv.putDouble(key, value)
            }

            is ByteArray -> {
                kv.putArray(key, value)
            }

            else -> {
                kv.putString(key, JSON.toJSONString(value))
            }
        }
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    fun getInt(key: String, def: Int = 0): Int {
        return kv.getInt(key, def)
    }

    fun getDouble(key: String, def: Double = 0.00): Double {
        return kv.getDouble(key, def)
    }

    fun getLong(key: String, def: Long = 0L): Long {
        return kv.getLong(key, def)
    }

    fun getBoolean(key: String, def: Boolean): Boolean {
        return kv.getBoolean(key, def)
    }

    fun getFloat(key: String, def: Float = 0f): Float {
        return kv.getFloat(key, def)
    }

    fun getBytes(key: String, def: ByteArray = byteArrayOf()): ByteArray {
        return kv.getArray(key, def)
    }

    fun getString(key: String, def: String = ""): String {
        return kv.getString(key, def) ?: ""
    }


    fun <T> getObject(key: String, type: Class<T>): T? {
        val data = kv.getString(key)
        if (data.isNullOrEmpty()) {
            return null
        }
        return JSON.parseObject(data, type)
    }

    fun <T> getType(key: String, def: T): T {
        val data = kv.getString(key)
        if (data.isNullOrEmpty()) {
            return def
        }
        val type = object : TypeReference<T>() {}
        return JSON.parseObject(data, type)
    }

    fun <T> getType(key: String, type: TypeReference<T>): T? {
        val data = kv.getString(key)
        if (data.isNullOrEmpty()) {
            return null
        }
        return JSON.parseObject(data, type)
    }

    /**
     * 清除所有key
     */
    fun clearAll() {
        kv.clear()
    }

    fun remove(key: String) {
        kv.remove(key)
    }

    /**
     * 获取所有key
     */
    fun getAllKeys(): MutableSet<String> {
        return kv.all.keys
    }

    /**
     * 是否包含某个key
     */
    fun containKey(key: String): Boolean {
        return kv.contains(key)
    }

}