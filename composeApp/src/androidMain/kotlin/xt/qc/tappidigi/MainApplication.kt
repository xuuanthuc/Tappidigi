package xt.qc.tappidigi

import android.app.Activity
import android.app.Application
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import xt.qc.tappidigi.di.AppModule


class MainApplication : Application() {

    private var mCurrentActivity: Activity? = null
    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun setCurrentActivity(mCurrentActivity: Activity?) {
        this.mCurrentActivity = mCurrentActivity
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this@MainApplication)
        AppModule(this@MainApplication).initKoin()
    }
}