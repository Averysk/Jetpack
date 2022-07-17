package com.oulive.jetpack.callback.livedata

import android.util.Log
import androidx.lifecycle.*

abstract class ObForever<T>(val liveData: UnPeekLiveData<T>) : Observer<T>,
    DefaultLifecycleObserver {

//    private  val TAG = "ObForever"
//
//    override fun onCreate(owner: LifecycleOwner) {
//        super.onCreate(owner)
//        Log.i(TAG, "onCreate: ")
//    }
//
//    override fun onStart(owner: LifecycleOwner) {
//        super.onStart(owner)
//        Log.i(TAG, "onStart: ")
//    }
//
//    override fun onResume(owner: LifecycleOwner) {
//        super.onResume(owner)
//        Log.i(TAG, "onResume: ")
//    }
//
//    override fun onPause(owner: LifecycleOwner) {
//        super.onPause(owner)
//        Log.i(TAG, "onPause: ")
//    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
//        Log.i(TAG, "onDestroy: ")
        liveData.removeObserver(this)
    }

}