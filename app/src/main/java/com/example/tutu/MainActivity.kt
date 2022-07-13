package com.example.tutu

import ai.leqi.lib_share_center.ui.ShareCenterActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, ShareCenterActivity::class.java)
        startActivity(intent)
    }
}