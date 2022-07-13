package com.example.tutu

import ai.leqi.lib_share_center.helper.ShareCenterHelper
import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ShareCenterHelper.init("CommonKeys.WEIXIN_APP_ID", "LEQIAI")
    }

}