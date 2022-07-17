package com.oulive.jetpack.callback.livedata

import android.os.Looper
import androidx.lifecycle.LifecycleOwner

private val mBus = hashMapOf<String, UnPeekLiveData<Any>>()

/**
 * onResume时候监听
 */
fun <T> LifecycleOwner.addLDBObserve(channel: String, onObserve: (T) -> Unit) {
    if (!mBus.containsKey(channel)) {
        mBus[channel] = UnPeekLiveData()
    }
    mBus[channel]?.observe(this) {
        onObserve.invoke(mBus[channel]?.value as T)
    }
}

/**
 * 全生命周期监听
 */
fun <T> LifecycleOwner.addLDBObserveForever(channel: String, onObserve: (T) -> Unit) {
    if (!mBus.containsKey(channel)) {
        mBus[channel] = UnPeekLiveData()
    }
    mBus[channel]?.apply {
        val obForever = object : ObForever<Any>(this) {
            override fun onChanged(t: Any) {
                onObserve.invoke(value as T)
//                removeObserver(this)
            }
        }
        lifecycle.addObserver(obForever)
        observeForever(obForever)
    }
}

/**
 * 发送一个事件
 */
fun sendLDBObserve(channel: String, data: Any?) {
    if (!mBus.containsKey(channel)) {
        mBus[channel] = UnPeekLiveData()
    }
    if (isMainThread()) {
        mBus[channel]!!.value = data
    } else {
        mBus[channel]!!.postValue(data)
    }
}

private fun isMainThread() = Looper.getMainLooper().thread == Thread.currentThread()
