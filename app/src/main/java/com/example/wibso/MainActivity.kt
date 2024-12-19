package com.example.wibso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.wibso.api.ApiProvider
import com.example.wibso.screens.profile.ProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Firebase.initialize(this)
        val appModule: Module = module {
            single { AppViewModel() }
            single { ProfileViewModel() }
            single { ApiProvider() }
        }
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModule)
        }
        setContent {
            App()
        }
    }
}