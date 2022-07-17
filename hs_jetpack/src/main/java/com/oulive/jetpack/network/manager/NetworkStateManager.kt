package com.oulive.jetpack.network.manager

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.oulive.jetpack.callback.livedata.event.EventLiveData
import com.oulive.jetpack.util.Utils

/**
 * 作者　: Averysk
 * 描述　: 网络变化管理者
 */
class NetworkStateManager private constructor(): DefaultLifecycleObserver {

    val mNetworkStateCallback = EventLiveData<NetState>()
    private val mNetworkStateReceive: NetworkStateReceive by lazy { NetworkStateReceive() }

    companion object {
        val instance: NetworkStateManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkStateManager()
        }
    }

    //TODO tip：让 NetworkStateManager 可观察页面生命周期，
    // 从而在页面失去焦点时，
    // 及时断开本页面对网络状态的监测，以避免重复回调和一系列不可预期的问题。

    // 关于 Lifecycle 组件的存在意义，可详见《为你还原一个真实的 Jetpack Lifecycle》篇的解析
    // https://xiaozhuanlan.com/topic/3684721950

    override fun onResume(owner: LifecycleOwner) {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        Utils.getApp().applicationContext.registerReceiver(mNetworkStateReceive, filter)
    }

    override fun onPause(owner: LifecycleOwner) {
        Utils.getApp().applicationContext.unregisterReceiver(mNetworkStateReceive)
    }

}