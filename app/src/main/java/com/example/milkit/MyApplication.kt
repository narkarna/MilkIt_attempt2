package com.example.milkit

import android.app.Application
import android.content.Context

class MyApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    companion object {

        lateinit var mInstance: MyApplication

        @Synchronized
        fun getInstance(): MyApplication {
            return mInstance
        }

        val applicationContext: Context
            get() = getInstance().applicationContext
    }

}