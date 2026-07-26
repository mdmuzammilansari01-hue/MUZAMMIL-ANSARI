package com.example

import android.app.Application
import android.util.Log

class JacTestHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("JacTestHubApplication", "JAC Test Hub initialized with Supabase Engine")
    }
}
