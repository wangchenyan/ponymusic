package me.wcy.music.net

import com.blankj.utilcode.util.CacheDiskUtils
import com.blankj.utilcode.util.GsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by wangchenyan.top on 2024/1/4.
 */
class NetCache(name: String) {
    private val cache by lazy {
        CacheDiskUtils.getInstance(name)
    }

    suspend fun getString(key: String): String {
        return withContext(Dispatchers.IO) {
            cache.getString(key, "")
        }
    }

    suspend fun <T> getJsonObject(key: String, clazz: Class<T>): T? {
        return withContext(Dispatchers.IO) {
            val json = getString(key)
            kotlin.runCatching {
                GsonUtils.fromJson(json, clazz)
            }.getOrNull()
        }
    }

    suspend fun <T> getJsonArray(key: String, clazz: Class<T>): List<T>? {
        return withContext(Dispatchers.IO) {
            val json = getString(key)
            top.wangchenyan.common.utils.GsonUtils.fromJsonList(json, clazz)
        }
    }

    suspend fun putString(key: String, value: String) {
        withContext(Dispatchers.IO) {
            cache.put(key, value)
        }
    }

    suspend fun putJson(key: String, json: Any) {
        withContext(Dispatchers.IO) {
            cache.put(key, GsonUtils.toJson(json))
        }
    }

    suspend fun remove(key: String) {
        withContext(Dispatchers.IO) {
            cache.remove(key)
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            cache.clear()
        }
    }

    companion object {
        val userCache by lazy {
            NetCache("net/user")
        }
        val globalCache by lazy {
            NetCache("net/global")
        }
    }
}