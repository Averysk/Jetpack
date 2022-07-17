package com.oulive.jetpack.base.activity

import android.app.Activity
import android.content.Context
import android.os.Process
import java.lang.Exception
import java.util.*
import kotlin.system.exitProcess

class ActivityManager {

    companion object {
        private var activityStack: Stack<Activity?>? = null
        val INSTANCE: ActivityManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityManager()
        }
    }

    /**
     * 添加Activity到堆栈中
     * @param activity
     */
    fun addActivity(activity: Activity?) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack!!.add(activity)
    }

    /**
     * 获取当前Activity
     * @return
     */
    fun currentActivity(): Activity? {
        return activityStack!!.lastElement()
    }

    /**
     * 判断Activity是否存在
     * @param cls
     * @return
     */
    fun isActivityExist(cls: Class<*>): Boolean {
        for (activity in activityStack!!) {
            if (activity!!.javaClass == cls) {
                return true
            }
        }
        return false
    }

    /**
     * 结束当前Activity
     */
    fun finishActivity() {
        val activity = activityStack!!.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定Activity
     * @param activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null) {
            activityStack!!.remove(activity)
            activity.finish()
        }
    }

    /**
     * 移除堆栈中指定Activity,
     * @param activity
     */
    fun removeActivity(activity: Activity?) {
        if (activity != null) {
            activityStack!!.remove(activity)
        }
    }

    /**
     * 结束指定Activity
     * @param cls
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack!!) {
            if (activity!!.javaClass == cls) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 结束所在Activity
     */
    fun finishAllActivity() {
        if (activityStack == null) return
        var i = 0
        val size = activityStack!!.size
        while (i < size) {
            if (null != activityStack!![i]) {
                activityStack!![i]!!.finish()
            }
            i++
        }
        activityStack!!.clear()
    }

    /**
     * 结束除指定Activity外,其它所有Activity
     * @param exceptAct
     */
    fun finishAllActivity(exceptAct: Activity) {
        while (!activityStack!!.isEmpty()) {
            val act = activityStack!!.pop()
            if (act !== exceptAct) {
                act!!.finish()
            }
        }
        activityStack!!.push(exceptAct)
    }

    /**
     * 退出App
     * @param context
     */
    fun appExit(context: Context?) {
        try {
            finishAllActivity()
            Process.killProcess(Process.myPid())
            exitProcess(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}