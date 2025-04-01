package com.example.wibso.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.wibso.ui.theme.Typography
import xt.qc.tappidigi.R

interface ButtonConfig {
    var colors: ButtonColors?
    var modifier: Modifier?
    var contentPadding: PaddingValues?
    var shape: RoundedCornerShape?
    var painterResource: Int?
    var labelStyle: TextStyle?
    var isFullWidth: Boolean?
}

class IconConfig : ButtonConfig {
    override var colors: ButtonColors? = ButtonColors(
        containerColor = Color.Transparent,
        contentColor = Color.Blue,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.Gray
    )
    override var modifier: Modifier? = Modifier.size(40.dp, 40.dp)
    override var contentPadding: PaddingValues? = PaddingValues(4.dp)
    override var shape: RoundedCornerShape? = RoundedCornerShape(8.dp)
    override var painterResource: Int? = R.drawable.album
    override var labelStyle: TextStyle? = null
    override var isFullWidth: Boolean? = null

    fun copyWith(
        colors: ButtonColors? = null,
        modifier: Modifier? = null,
        contentPadding: PaddingValues? = null,
        shape: RoundedCornerShape? = null,
        painterResource: Int? = null,
        contentColor: Color? = null,
        containerColor: Color? = null,
        disabledContainerColor: Color? = null,
        disabledContentColor: Color? = null,
    ): ButtonConfig {
        this.colors = colors ?: this.colors?.copy(
            containerColor = containerColor ?: this.colors!!.containerColor,
            contentColor = contentColor ?: this.colors!!.contentColor,
            disabledContainerColor = disabledContainerColor ?: this.colors!!.disabledContainerColor,
            disabledContentColor = disabledContentColor ?: this.colors!!.disabledContentColor,
        )
        this.modifier = modifier ?: this.modifier
        this.contentPadding = contentPadding ?: this.contentPadding
        this.shape = shape ?: this.shape
        this.painterResource = painterResource ?: this.painterResource
        return this
    }
}

class TextConfig : ButtonConfig {
    override var colors: ButtonColors? = ButtonColors(
        containerColor = Color.Blue,
        contentColor = Color.White,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.Gray
    )
    override var modifier: Modifier? = Modifier.wrapContentSize()
    override var contentPadding: PaddingValues? = PaddingValues(4.dp)
    override var shape: RoundedCornerShape? = RoundedCornerShape(8.dp)
    override var painterResource: Int? = null
    override var labelStyle: TextStyle? = Typography.bodyLarge
    override var isFullWidth: Boolean? = false

    fun copyWith(
        colors: ButtonColors? = null,
        modifier: Modifier? = null,
        contentPadding: PaddingValues? = null,
        shape: RoundedCornerShape? = null,
        contentColor: Color? = null,
        containerColor: Color? = null,
        disabledContainerColor: Color? = null,
        disabledContentColor: Color? = null,
        isFullWidth: Boolean? = null,
    ): ButtonConfig {
        this.colors = colors ?: this.colors?.copy(
            containerColor = containerColor ?: this.colors!!.containerColor,
            contentColor = contentColor ?: this.colors!!.contentColor,
            disabledContainerColor = disabledContainerColor ?: this.colors!!.disabledContainerColor,
            disabledContentColor = disabledContentColor ?: this.colors!!.disabledContentColor,
        )
        this.modifier = modifier ?: this.modifier
        this.contentPadding = contentPadding ?: this.contentPadding
        this.shape = shape ?: this.shape
        this.isFullWidth = isFullWidth ?: this.isFullWidth
        return this
    }
}

@Composable
fun IconButton(
    decoration: ButtonConfig = IconConfig().copyWith(),
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = decoration.modifier!!,
        contentPadding = decoration.contentPadding!!,
        shape = decoration.shape!!,
        colors = decoration.colors!!,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(decoration.painterResource!!),
                contentDescription = "",
            )
        }
    }
}

@Composable
fun TextButton(
    decoration: ButtonConfig = TextConfig().copyWith(),
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = decoration.modifier!!,
        contentPadding = decoration.contentPadding!!,
        shape = decoration.shape!!,
        colors = decoration.colors!!,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = if (decoration.isFullWidth == true) Modifier.fillMaxWidth() else Modifier
        ) {
            Text(label, style = decoration.labelStyle!!)
        }
    }
}

