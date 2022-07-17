package com.hongsu.project

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.hongsu.test.MavenPublishTool

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_maven).setOnClickListener {
            MavenPublishTool.tools(this)
        }
    }
}