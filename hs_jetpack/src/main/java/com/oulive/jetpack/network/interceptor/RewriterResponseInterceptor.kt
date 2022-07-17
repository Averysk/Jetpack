package com.oulive.jetpack.network.interceptor

import com.oulive.jetpack.base.appContext
import com.oulive.jetpack.network.NetworkUtil
import okhttp3.Interceptor

/**
 * 描述　: 改写请求回复缓存头
 */
// 拦截Cache-Control(正常有网络时)
val rewriterResponseInterceptor = Interceptor { chain ->
    val originalResponse = chain.proceed(chain.request())
    //val cacheControl = originalResponse.header("cache-control")
    val cacheControl = originalResponse.header("Cache-Control")
    if (cacheControl == null
        //|| cacheControl.contains("no-store")
        //|| cacheControl.contains("no-cache")
        //|| cacheControl.contains("must-revalidate")
        || cacheControl.contains("max-age")) {
        //val maxAge = 60 * 60
        originalResponse.newBuilder()
            .removeHeader("Pragma")
            .header("Cache-Control", "public, max-age=" + 5000)
            .build()
    } else {
        originalResponse
    }
}
// 设置Cache-Control(正常无网络时)
val rewriterResponseInterceptorOffline = Interceptor { chain ->
    var request = chain.request()
    if (!NetworkUtil.isNetworkAvailable(appContext)) {
        request = request.newBuilder()
            .removeHeader("Pragma")
            .header("Cache-Control", "public, only-if-cached")
            .build()
    }
    chain.proceed(request)
}