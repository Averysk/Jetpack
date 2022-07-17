package com.oulive.jetpack.ext

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.oulive.jetpack.R

//loading框
private var loadingDialog: Dialog? = null

/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "加载中") {
    if (isFinishing) return
    if (loadingDialog == null) {
        loadingDialog = loadingDialog(this, false, message)
    }
    loadingDialog?.show()
}

/**
 * 关闭等待框
 */
fun AppCompatActivity.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}

/**
 * 打开等待框
 */
fun Activity.showLoadingExt(message: String = "加载中") {
    if (loadingDialog == null) {
        loadingDialog = loadingDialog(this, false, message)
    }
    loadingDialog?.show()
}

/**
 * 关闭等待框
 */
fun Activity.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "加载中") {
    if (isDetached) return
    if (loadingDialog == null) {
        loadingDialog = loadingDialog(requireContext(), false, message)
    }
    loadingDialog?.show()
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}


/**
 * 用于网络请求等耗时操作的LoadingDialog
 */
fun loadingDialog(context: Context, cancelable: Boolean, text: String?): Dialog {
    val dialog = Dialog(context, R.style.style_dialog_loading)
    dialog.setContentView(R.layout.dialog_loading)
    dialog.setCancelable(cancelable)
    dialog.setCanceledOnTouchOutside(cancelable)
    if (!TextUtils.isEmpty(text)) {
        val titleView = dialog.findViewById(R.id.text) as TextView
        titleView.text = text
    }
    return dialog
}