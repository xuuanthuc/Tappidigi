package xt.qc.tappidigi.screens.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import xt.qc.tappidigi.utils.toUnicode

@Composable
fun HomeScreen() {
   var fsd = 0x1F4FA.toUnicode()
   Text(text = fsd)
}