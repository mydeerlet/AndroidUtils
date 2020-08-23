package com.mydeerlet.androidutils

import android.app.Application

class MyApplication :Application() {

    override fun onCreate() {
        super.onCreate()

//        if (Constant.DEBUG_MODE) {           // These two lines must be written before init, otherwise these configurations will be invalid in the init process
//            ARouter.openLog()     // Print log
//            ARouter.openDebug()   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
//        }
//        ARouter.init(this) // As early as possible, it is recommended to initialize in the Application


//        ApiService.init("http://123.56.232.18:8080/serverdemo",null)
    }
}