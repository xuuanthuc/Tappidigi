package com.example.wibso.screens.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.wibso.utils.toUnicode

@Composable
fun HomeScreen() {
   var fsd = 0x1F4FA.toUnicode()
   Text(text = fsd)
}