package me.wcy.music.net

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.JsonObject
import top.wangchenyan.common.CommonApp
import me.wcy.music.account.service.UserServiceModule.Companion.userService
import me.wcy.music.net.NetUtils.toJsonBody
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import okio.IOException

/**
 * Created by wcy on 2018/7/15.
 */
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val cookie = CommonApp.app.userService().getCookie()
        if (cookie.isNotEmpty() && request.method == "POST") {
            val body = request.body
            if (body == null || body.contentLength() <= 0) {
                val newBody = mapOf("cookie" to cookie).toJsonBody()
                val newRequest = request.newBuilder()
                    .post(newBody)
                    .build()
                return chain.proceed(newRequest)
            } else if (body.contentType().toString()
                    .contains(NetUtils.CONTENT_TYPE_JSON.toString())
            ) {
                val bodyString = try {
                    var oldBodyString = body.bodyToString()
                    if (oldBodyString.isEmpty()) {
                        oldBodyString = "{}"
                    }
                    val jsonObject = GsonUtils.fromJson(oldBodyString, JsonObject::class.java)
                    jsonObject.addProperty("cookie", cookie)
                    jsonObject.toString()
                } catch (e: Exception) {
                    Log.e(TAG, "add cookie to body error")
                    "{}"
                }
                val newRequest = request.newBuilder()
                    .post(bodyString.toRequestBody(NetUtils.CONTENT_TYPE_JSON))
                    .build()
                return chain.proceed(newRequest)
            }
        }
        return chain.proceed(request)
    }

    @Throws(IOException::class)
    private fun RequestBody.bodyToString(): String {
        val buffer = Buffer()
        this.writeTo(buffer)
        return buffer.readUtf8()
    }

    companion object {
        private const val TAG = "HeaderInterceptor"
    }
}