package com.hongsu.test

import android.content.Context
import android.widget.Toast

/**
 * @Description:
 * @author maoqitian
 * @date 2021/9/17 0017 10:36
 */
object MavenPublishTool {

    fun tools(context: Context? = null){
        println("MavenPublishTool 被调用了")
        context?.let {
            Toast.makeText(context, "MavenPublishTool 被调用了16", Toast.LENGTH_SHORT).show()
        }
    }
}