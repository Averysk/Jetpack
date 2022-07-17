package com.oulive.jetpack.base.fragment

import androidx.fragment.app.Fragment
import com.oulive.jetpack.base.viewmodel.AppViewModel
import com.oulive.jetpack.ext.dismissLoadingExt
import com.oulive.jetpack.ext.showLoadingExt

abstract class BaseJetpackFragment:  Fragment() {

    open fun showLoading(message: String = "请求网络中...") {
        showLoadingExt(message)
    }

    open fun dismissLoading() {
        dismissLoadingExt()
    }

    /**
     * 将非该Fragment绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: AppViewModel) {
        viewModels.forEach { viewModel ->
            //显示弹窗
            viewModel.loadingChange.showDialog.observe(this) {
                showLoading(it)
            }
            //关闭弹窗
            viewModel.loadingChange.dismissDialog.observe(this) {
                dismissLoading()
            }
        }
    }

}