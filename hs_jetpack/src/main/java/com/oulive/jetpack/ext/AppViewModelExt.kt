package com.oulive.jetpack.ext

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oulive.jetpack.base.activity.ActivityManager
import com.oulive.jetpack.base.activity.BaseJetpackActivity
import com.oulive.jetpack.base.fragment.BaseJetpackFragment
import com.oulive.jetpack.base.viewmodel.AppViewModel
import com.oulive.jetpack.ext.util.loge
import com.oulive.jetpack.network.AppException
import com.oulive.jetpack.network.BaseResponse
import com.oulive.jetpack.network.ExceptionHandle
import com.oulive.jetpack.state.AppResultState
import com.oulive.jetpack.state.paresException
import com.oulive.jetpack.state.paresResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * 作者　: Averysk
 * 描述　:BaseViewModel请求协程封装
 */


/**
 * 显示页面状态，这里有个技巧，成功回调在第一个，其后两个带默认值的回调可省
 * @param onLoading 加载中
 * @param onSuccess 成功回调
 * @param onError 失败回调
 * @param onComplete 完成结束回调
 *
 */
fun <T> AppResultState<T>.parseState(
    onSuccess: (T?) -> Unit = {},
    onError: ((AppException) -> Unit)? = null,
    onComplete: ((Boolean) -> Unit)? = null,
    onLoading: ((message: String) -> Unit)? = null,
) {
    var isSuccess = false
    when (this) {
        is AppResultState.Loading -> {
            onLoading?.run { this(loadingMessage) }
        }
        is AppResultState.Success -> {
            onSuccess(data)
            isSuccess = true
        }
        is AppResultState.Error -> {
            onError?.run { this(error) }
            isSuccess = false
        }
        else -> {
            onComplete?.run { this(isSuccess) }
        }
    }
    onComplete?.run { this(isSuccess) }
}

/**
 * 显示页面状态，这里有个技巧，成功回调在第一个，其后两个带默认值的回调可省
 * @param resultState 接口返回值
 * @param onLoading 加载中
 * @param onSuccess 成功回调
 * @param onError 失败回调
 *
 */
fun <T> BaseJetpackActivity.parseState(
    resultState: AppResultState<T>,
    onSuccess: (T) -> Unit,
    onError: ((AppException) -> Unit)? = null,
    onComplete: ((Boolean) -> Unit)? = null,
    onLoading: ((message: String) -> Unit)? = null,
) {
    var isSuccess = false
    when (resultState) {
        is AppResultState.Loading -> {
            if (onLoading == null) {
                showLoading(resultState.loadingMessage)
            } else {
                onLoading.run { this(resultState.loadingMessage) }
            }
        }
        is AppResultState.Success -> {
            dismissLoading()
            onSuccess(resultState.data)
            isSuccess = true
        }
        is AppResultState.Error -> {
            dismissLoading()
            onError?.run { this(resultState.error) }
            isSuccess = false
        }
        else -> {
            onComplete?.run { this(isSuccess) }
        }
    }
    onComplete?.run { this(isSuccess) }
}

/**
 * 显示页面状态，这里有个技巧，成功回调在第一个，其后两个带默认值的回调可省
 * @param resultState 接口返回值
 * @param onLoading 加载中
 * @param onSuccess 成功回调
 * @param onError 失败回调
 *
 */
fun <T> BaseJetpackFragment.parseState(
    resultState: AppResultState<T>,
    onSuccess: (T) -> Unit,
    onError: ((AppException) -> Unit)? = null,
    onComplete: ((Boolean) -> Unit)? = null,
    onLoading: ((message: String) -> Unit)? = null
) {
    var isSuccess = false
    when (resultState) {
        is AppResultState.Loading -> {
            if (onLoading == null) {
                showLoading(resultState.loadingMessage)
            } else {
                onLoading.run { this(resultState.loadingMessage) }
            }
        }
        is AppResultState.Success -> {
            dismissLoading()
            onSuccess(resultState.data)
            isSuccess = true
        }
        is AppResultState.Error -> {
            dismissLoading()
            onError?.run { this(resultState.error) }
            isSuccess = false
        }
        else -> {
            onComplete?.run { this(isSuccess) }
        }
    }
    onComplete?.run { this(isSuccess) }
}

/**
 * 多网络数据的返回模型进行判断
 * 如果返回的code 是200 那么我们就认为业务数据为正常的，
 * 否则直接抛出Result.Error为异常。
 *
 */
suspend fun <T> getResult(block: suspend () -> BaseResponse<T>): AppResultState<T> {
    val result = block()
    when {
        result.isSuccess() -> {
            return AppResultState.onAppSuccess(result.getResponseData())
        }
        else -> {
            throw AppException(
                result.getResponseCode(),
                result.getResponseMsg()
            )
        }
    }
}

fun <T> flowOfResult(
    isShowDialog: Boolean = false,
    loadingMessage: String = "加载中...",
    block: suspend () -> BaseResponse<T>,
) = flow {
    val result = getResult { block() }
    emit(result)
}.flowOn(
    Dispatchers.IO
).onStart {
    // loading动画
    if (isShowDialog) {
        ActivityManager.INSTANCE.currentActivity()?.showLoadingExt(loadingMessage)
    }
    emit(AppResultState.onAppLoading(loadingMessage))
}.catch {
    when (it) {
        is AppException -> {
            emit(AppResultState.onAppError(it))
        }
        else -> {
            emit(AppResultState.onAppError(ExceptionHandle.handleException(it)))
        }
    }

}.onCompletion {
    // cancel动画 和 log打印
    if (isShowDialog) {
        ActivityManager.INSTANCE.currentActivity()?.dismissLoadingExt()
    }
    emit(AppResultState.onComplete())
}.flowOn(Dispatchers.Main)

/**
 * net request 不校验请求结果数据是否是成功
 * @param block 请求体方法
 * @param appResultState 请求回调的ResultState数据
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> AppViewModel.request(
    block: suspend () -> BaseResponse<T>,
    appResultState: MutableLiveData<AppResultState<T>>,
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中..."
): Job {
    return viewModelScope.launch {
        runCatching {
            if (isShowDialog) appResultState.value = AppResultState.onAppLoading(loadingMessage)
            //请求体
            block()
        }.onSuccess {
            appResultState.paresResult(it)
        }.onFailure {
            it.message?.loge()
            //打印错误栈信息
            it.printStackTrace()
            appResultState.paresException(it)
        }
    }
}

/**
 * net request 不校验请求结果数据是否是成功
 * @param block 请求体方法
 * @param appResultState 请求回调的ResultState数据
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> AppViewModel.requestNoCheck(
    block: suspend () -> T,
    appResultState: MutableLiveData<AppResultState<T>>,
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中..."
): Job {
    return viewModelScope.launch {
        runCatching {
            if (isShowDialog) appResultState.value = AppResultState.onAppLoading(loadingMessage)
            //请求体
            block()
        }.onSuccess {
            appResultState.paresResult(it)
        }.onFailure {
            it.message?.loge()
            //打印错误栈信息
            it.printStackTrace()
            appResultState.paresException(it)
        }
    }
}

/**
 * 过滤服务器结果，失败抛异常
 * @param block 请求体方法，必须要用suspend关键字修饰
 * @param success 成功回调
 * @param error 失败回调 可不传
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> AppViewModel.request(
    block: suspend () -> BaseResponse<T>,
    success: (T) -> Unit,
    error: (AppException) -> Unit = {},
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中..."
): Job {
    //如果需要弹窗 通知Activity/fragment弹窗
    return viewModelScope.launch {
        runCatching {
            if (isShowDialog) loadingChange.showDialog.postValue(loadingMessage)
            //请求体
            block()
        }.onSuccess {
            //网络请求成功 关闭弹窗
            loadingChange.dismissDialog.postValue(false)
            runCatching {
                //校验请求结果码是否正确，不正确会抛出异常走下面的onFailure
                executeResponse(it) { t ->
                    success(t)
                }
            }.onFailure { e ->
                //打印错误消息
                e.message?.loge()
                //打印错误栈信息
                e.printStackTrace()
                //失败回调
                error(ExceptionHandle.handleException(e))
            }
        }.onFailure {
            //网络请求异常 关闭弹窗
            loadingChange.dismissDialog.postValue(false)
            //打印错误消息
            it.message?.loge()
            //打印错误栈信息
            it.printStackTrace()
            //失败回调
            error(ExceptionHandle.handleException(it))
        }
    }
}

/**
 *  不过滤请求结果
 * @param block 请求体 必须要用suspend关键字修饰
 * @param success 成功回调
 * @param error 失败回调 可不给
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> AppViewModel.requestNoCheck(
    block: suspend () -> T,
    success: (T) -> Unit,
    error: (AppException) -> Unit = {},
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中..."
): Job {
    //如果需要弹窗 通知Activity/fragment弹窗
    if (isShowDialog) loadingChange.showDialog.postValue(loadingMessage)
    return viewModelScope.launch {
        runCatching {
            //请求体
            block()
        }.onSuccess {
            //网络请求成功 关闭弹窗
            loadingChange.dismissDialog.postValue(false)
            //成功回调
            success(it)
        }.onFailure {
            //网络请求异常 关闭弹窗
            loadingChange.dismissDialog.postValue(false)
            //打印错误消息
            it.message?.loge()
            //打印错误栈信息
            it.printStackTrace()
            //失败回调
            error(ExceptionHandle.handleException(it))
        }
    }
}

/**
 * 请求结果过滤，判断请求服务器请求结果是否成功，不成功则会抛出异常
 */
suspend fun <T> executeResponse(
    response: BaseResponse<T>,
    success: suspend CoroutineScope.(T) -> Unit
) {
    coroutineScope {
        when {
            response.isSuccess() -> {
                success(response.getResponseData())
            }
            else -> {
                throw AppException(
                    response.getResponseCode(),
                    response.getResponseMsg(),
                    response.getResponseMsg()
                )
            }
        }
    }
}

/**
 *  调用携程
 * @param block 操作耗时操作任务
 * @param success 成功回调
 * @param error 失败回调 可不给
 */
fun <T> AppViewModel.launch(
    block: () -> T,
    success: (T) -> Unit,
    error: (Throwable) -> Unit = {}
) {
    viewModelScope.launch {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                block()
            }
        }.onSuccess {
            success(it)
        }.onFailure {
            error(it)
        }
    }
}
