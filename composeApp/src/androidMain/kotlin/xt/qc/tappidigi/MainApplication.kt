package xt.qc.tappidigi

import android.app.Application
import xt.qc.tappidigi.di.AppModule

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule(this@MainApplication).initKoin()
    }
}