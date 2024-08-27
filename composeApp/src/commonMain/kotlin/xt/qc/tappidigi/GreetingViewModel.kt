package xt.qc.tappidigi

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GreetingViewModel: ViewModel() {
    var showContent = mutableStateOf(false)

    fun onShowContentClicked() {
        println(hashCode())
        showContent.value = !showContent.value
    }
}