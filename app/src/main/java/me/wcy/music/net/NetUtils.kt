package me.wcy.music.net

import com.blankj.utilcode.util.GsonUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Created by wangchenyan.top on 2023/8/28.
 */
object NetUtils {
    val CONTENT_TYPE_JSON = "application/json".toMediaType()

    fun Map<String, String>.toJsonBody(): RequestBody {
        return GsonUtils.toJson(this).toRequestBody(CONTENT_TYPE_JSON)
    }
}