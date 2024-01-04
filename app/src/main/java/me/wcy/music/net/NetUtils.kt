package me.wcy.music.net

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.JsonObject
import top.wangchenyan.common.model.CommonResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

/**
 * Created by wangchenyan.top on 2023/8/28.
 */
object NetUtils {
    val CONTENT_TYPE_JSON = "application/json".toMediaType()

    fun Map<String, String>.toJsonBody(): RequestBody {
        return GsonUtils.toJson(this).toRequestBody(CONTENT_TYPE_JSON)
    }

    fun parseErrorResponse(
        exception: Throwable?,
        codeField: String = "code",
        msgField: String = "message"
    ): CommonResult<Unit> {
        var code = -1
        var msg = exception?.message
        if (exception is HttpException) {
            code = exception.code()
            msg = exception.message()
            val body = exception.response()?.errorBody()?.string().orEmpty()
            if (body.isNotEmpty()) {
                kotlin.runCatching {
                    val json = GsonUtils.fromJson(body, JsonObject::class.java)
                    if (json.has(codeField)) {
                        code = json.get(codeField).asInt
                    }
                    if (json.has(msgField)) {
                        msg = json.get(msgField).asString
                    }
                }
            }
        }
        return CommonResult.fail(code, msg)
    }
}