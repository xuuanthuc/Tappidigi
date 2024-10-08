package xt.qc.tappidigi.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.navigation.NavHostController
import dev.gitlive.firebase.auth.GoogleAuthProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xt.qc.tappidigi.AppViewModel
import xt.qc.tappidigi.MainApplication
import xt.qc.tappidigi.screens.authentication.SignInWithGoogleManager
import xt.qc.tappidigi.utils.Platform
import xt.qc.tappidigi.screens.profile.ProfileViewModel


actual class AppModule(private val context: Context) {
    actual val appModule: Module = module {
        singleOf(::Platform)
        singleOf(::AppViewModel)
        singleOf(::ProfileViewModel)
        single { CredentialManager.create((context.applicationContext as MainApplication).getCurrentActivity()!!) }
        single { SignInWithGoogleManager(context, get())  }
    }

    actual fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(context)
            modules(appModule)
        }
    }
}