package com.oulive.jetpack.base.activity

import androidx.appcompat.app.AppCompatActivity
import com.oulive.jetpack.base.viewmodel.AppViewModel
import com.oulive.jetpack.ext.dismissLoadingExt
import com.oulive.jetpack.ext.showLoadingExt

abstract class BaseJetpackActivity: AppCompatActivity() {

    /**
     * 打开等待框
     * 后续实现加载框 或 抽象出去,在子类中实现
     */
    open fun showLoading(message: String = "加载中...") {
        showLoadingExt(message)
    }

    /**
     * 关闭等待框
     * 后续实现关闭框 或 抽象出去,在子类中实现
     */
    open fun dismissLoading() {
        dismissLoadingExt()
    }

    /**
     * 将非该Activity绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
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