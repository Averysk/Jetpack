package com.oulive.jetpack.ext

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.oulive.jetpack.base.BaseApp
import com.oulive.jetpack.base.viewmodel.AppViewModel
import java.lang.reflect.ParameterizedType


/**
 * 获取当前类绑定的泛型ViewBinding-clazz
 */
@Suppress("UNCHECKED_CAST")
fun <DB> getDbClazz(obj: Any, inflater: LayoutInflater, index: Int): DB {
    val clazz = (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[index] as Class<*>
    val method = clazz.getMethod("inflate", LayoutInflater::class.java)
    return method.invoke(null, inflater) as DB
}

/**
 * 获取当前类绑定的泛型ViewModel-clazz
 */
@Suppress("UNCHECKED_CAST")
fun <VM> getVmClazz(obj: Any, index: Int): VM {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[index] as VM
}

/**
 * 在Activity中得到Application上下文的ViewModel
 */
inline fun <reified VM : AppViewModel> AppCompatActivity.getAppViewModel(): VM {
    (this.application as? BaseApp).let {
        if (it == null) {
            throw NullPointerException("你的Application没有继承框架自带的BaseApp类，暂时无法使用getAppViewModel该方法")
        } else {
            return it.getAppViewModelProvider().get(VM::class.java)
        }
    }
}

/**
 * 在Fragment中得到Application上下文的ViewModel
 * 提示，在fragment中调用该方法时，请在该Fragment onCreate以后调用或者请用by lazy方式懒加载初始化调用，不然会提示requireActivity没有导致错误
 */
inline fun <reified VM : AppViewModel> Fragment.getAppViewModel(): VM {
    (this.requireActivity().application as? BaseApp).let {
        if (it == null) {
            throw NullPointerException("你的Application没有继承框架自带的BaseApp类，暂时无法使用getAppViewModel该方法")
        } else {
            return it.getAppViewModelProvider().get(VM::class.java)
        }
    }
}

/**
 * 得到当前Activity上下文的ViewModel
 */
@Deprecated("已过时的方法，现在可以直接使用Ktx函数 viewmodels()获取")
inline fun <reified VM : AppViewModel> AppCompatActivity.getViewModel(): VM {
    return ViewModelProvider(
        this,
        ViewModelProvider.AndroidViewModelFactory(application)
    ).get(VM::class.java)
}

/**
 * 得到当前Fragment上下文的ViewModel
 * 提示，在fragment中调用该方法时，请在该Fragment onCreate以后调用或者请用by lazy方式懒加载初始化调用，不然会提示requireActivity没有导致错误
 */
@Deprecated("已过时的方法，现在可以直接使用Ktx函数 viewmodels()获取")
inline fun <reified VM : AppViewModel> Fragment.getViewModel(): VM {
    return ViewModelProvider(
        this,
        ViewModelProvider.AndroidViewModelFactory(this.requireActivity().application)
    ).get(VM::class.java)
}

/**
 * 在Fragment中得到父类Activity的共享ViewModel
 * 提示，在fragment中调用该方法时，请在该Fragment onCreate以后调用或者请用by lazy方式懒加载初始化调用，不然会提示requireActivity没有导致错误
 */
@Deprecated("已过时的方法，现在可以直接使用Ktx函数 activityViewModels()获取")
inline fun <reified VM : AppViewModel> Fragment.getActivityViewModel(): VM {
    return ViewModelProvider(requireActivity(),
        ViewModelProvider.AndroidViewModelFactory(this.requireActivity().application)
    ).get(VM::class.java)
}






