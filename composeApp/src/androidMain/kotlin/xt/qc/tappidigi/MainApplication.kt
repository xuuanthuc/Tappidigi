package xt.qc.tappidigi

import android.app.Application
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import xt.qc.tappidigi.di.AppModule

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this@MainApplication)
        AppModule(this@MainApplication).initKoin()
    }
}