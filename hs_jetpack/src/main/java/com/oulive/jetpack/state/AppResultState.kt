package com.oulive.jetpack.state
import androidx.lifecycle.MutableLiveData
import com.oulive.jetpack.network.AppException
import com.oulive.jetpack.network.BaseResponse
import com.oulive.jetpack.network.ExceptionHandle

/**
 * 作者　: Averysk
 * 描述　: 自定义结果集封装类
 */
sealed class AppResultState<out T> {
    companion object {
        fun <T> onAppLoading(loadingMessage: String): AppResultState<T> = Loading(loadingMessage)
        fun <T> onAppSuccess(data: T): AppResultState<T> = Success(data)
        fun <T> onAppError(error: AppException): AppResultState<T> = Error(error)
        fun <T> onComplete(isSuccess :Boolean = true): AppResultState<T> = Complete(isSuccess)
    }

    data class Loading(val loadingMessage: String) : AppResultState<Nothing>()
    data class Success<out T>(val data: T) : AppResultState<T>()
    data class Error(val error: AppException) : AppResultState<Nothing>()
    data class Complete(val isSuccess :Boolean) : AppResultState<Nothing>()
}

/**
 * 处理返回值
 * @param result 请求结果
 */
fun <T> MutableLiveData<AppResultState<T>>.paresResult(result: BaseResponse<T>) {
    value = when {
        result.isSuccess() -> {
            AppResultState.onAppSuccess(result.getResponseData())
        }
        else -> {
            AppResultState.onAppError(AppException(result.getResponseCode(), result.getResponseMsg()))
        }
    }
}

/**
 * 不处理返回值 直接返回请求结果
 * @param result 请求结果
 */
fun <T> MutableLiveData<AppResultState<T>>.paresResult(result: T) {
    value = AppResultState.onAppSuccess(result)
}

/**
 * 异常转换异常处理
 */
fun <T> MutableLiveData<AppResultState<T>>.paresException(e: Throwable) {
    this.value = AppResultState.onAppError(ExceptionHandle.handleException(e))
}

/**
 * 处理全部完成状态
 */
fun <T> MutableLiveData<AppResultState<T>>.paresComplete(isSuccess :Boolean) {
    this.value = AppResultState.onComplete(isSuccess)
}


