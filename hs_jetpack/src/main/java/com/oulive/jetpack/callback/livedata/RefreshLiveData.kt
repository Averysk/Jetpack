package com.oulive.jetpack.callback.livedata

import androidx.lifecycle.MutableLiveData

class RefreshLiveData<T> : MutableLiveData<T> {

    constructor() : super()
    constructor(value: T) : super(value)

    fun refresh() {
        value = value
    }
}