package xt.qc.tappidigi

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


class MainActivity : ComponentActivity() {

    protected var mMyApp: MainApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMyApp = this.applicationContext as MainApplication
        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        mMyApp?.setCurrentActivity(this)
    }

    override fun onPause() {
        clearReferences()
        super.onPause()
    }

    override fun onDestroy() {
        clearReferences()
        super.onDestroy()
    }

    private fun clearReferences() {
        val currActivity: Activity? = mMyApp?.getCurrentActivity()
        if (this == currActivity) mMyApp?.setCurrentActivity(null)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}