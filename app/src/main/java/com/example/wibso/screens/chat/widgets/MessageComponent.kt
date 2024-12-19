package com.example.wibso.screens.chat.widgets

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import xt.qc.tappidigi.R
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import org.koin.compose.koinInject
import com.example.wibso.models.Chat
import com.example.wibso.models.Message
import com.example.wibso.models.MessagePosition
import com.example.wibso.models.MessageStatus
import com.example.wibso.models.User
import com.example.wibso.screens.profile.ProfileViewModel
import com.example.wibso.utils.ChatThemes


@OptIn(FormatStringsInDatetimeFormats::class)
fun Instant.toFormatedString(): String {
    val period = this.periodUntil(Clock.System.now(), TimeZone.UTC)
    val todayFormatter = LocalDateTime.Format {
        byUnicodePattern("'Today,' HH:mm")
    }

    val weekFormatter = LocalDateTime.Format {
        dayOfWeek(DayOfWeekNames.ENGLISH_FULL)
        char(',')
        char(' ')
        byUnicodePattern("HH:mm")
    }

    val monthFormatter = LocalDateTime.Format {
        dayOfMonth()
        char(' ')
        monthName(MonthNames.ENGLISH_FULL)
        char(',')
        char(' ')
        byUnicodePattern("HH:mm")
    }

    val yearFormatter = LocalDateTime.Format {
        dayOfMonth()
        char(' ')
        monthName(MonthNames.ENGLISH_FULL)
        char(' ')
        year()
        char(',')
        char(' ')
        byUnicodePattern("HH:mm")
    }

    val formatter: DateTimeFormat<LocalDateTime> = if (period.days == 0) {
        todayFormatter
    } else if (period.days in 1..7) {
        weekFormatter
    } else if (period.days > 7 && period.years == 0) {
        monthFormatter
    } else {
        yearFormatter
    }
    return this.toLocalDateTime(TimeZone.currentSystemDefault()).format(formatter)
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MessageComponent(
    message: Message,
    nextMsg: Message?,
    prevMsg: Message?,
    theme: ChatThemes,
    group: Chat.GroupChat? = null,
    private: Chat.PrivateChat? = null,
    onResend: (Message) -> Unit,
    onShowingDate: (Message?) -> Unit,
    showingDateId: String,
) {
    val showingTime = remember { mutableStateOf(false) }
    val profile = koinInject<ProfileViewModel>()
    val owner = msgOwner(message, group, private)
    val isMe = profile.userState.value?.uid == owner?.uid
    val position: MessagePosition = when {
        prevMsg == null -> MessagePosition.FIRST

        nextMsg == null -> if (message.ownerId != prevMsg.ownerId) MessagePosition.SINGLE else MessagePosition.LAST

        message.ownerId != prevMsg.ownerId && message.ownerId != nextMsg.ownerId -> MessagePosition.SINGLE

        message.ownerId != prevMsg.ownerId -> MessagePosition.FIRST

        message.ownerId != nextMsg.ownerId -> MessagePosition.LAST

        else -> MessagePosition.MIDDLE
    }
    val prevMsgTime: MutableState<Instant> =
        remember { mutableStateOf(Instant.fromEpochSeconds(prevMsg?.createdAt ?: 0)) }
    val msgTime: MutableState<Instant> =
        remember { mutableStateOf(Instant.fromEpochSeconds(message.createdAt)) }
    val period: MutableState<Long> = remember {
        mutableLongStateOf(
            prevMsgTime.value.until(
                msgTime.value, DateTimeUnit.HOUR, TimeZone.UTC
            )
        )
    }
    val fixedDatetimeShowing: MutableState<Boolean> = remember { mutableStateOf(period.value > 0) }

    LaunchedEffect(message) {
        prevMsgTime.value = Instant.fromEpochSeconds(prevMsg?.createdAt ?: 0)
        msgTime.value = Instant.fromEpochSeconds(message.createdAt)
        period.value = prevMsgTime.value.until(msgTime.value, DateTimeUnit.HOUR, TimeZone.UTC)
        fixedDatetimeShowing.value = period.value > 0
        if (fixedDatetimeShowing.value) {
            showingTime.value = true
        } else if (showingDateId == message.id) {
            showingTime.value = true
        } else {
            showingTime.value = false
        }
    }

    LaunchedEffect(showingDateId) {
        if (fixedDatetimeShowing.value) {
            showingTime.value = true
        } else if (showingDateId == message.id) {
            showingTime.value = true
        } else {
            showingTime.value = false
        }
    }


    fun <T> gap(first: T, second: T): T {
        return if (isMe) first else second
    }

    val messageShape = RoundedCornerShape(
        topStart = gap(
            16.dp, when (position) {
                MessagePosition.FIRST -> 16.dp
                MessagePosition.LAST -> 4.dp
                MessagePosition.MIDDLE -> 4.dp
                MessagePosition.SINGLE -> 16.dp
            }
        ), topEnd = when (position) {
            MessagePosition.FIRST -> 16.dp
            MessagePosition.LAST -> gap(4.dp, 16.dp)
            MessagePosition.MIDDLE -> gap(4.dp, 16.dp)
            MessagePosition.SINGLE -> 16.dp
        }, bottomStart = gap(
            16.dp, when (position) {
                MessagePosition.FIRST -> 4.dp
                MessagePosition.LAST -> 8.dp
                MessagePosition.MIDDLE -> 4.dp
                MessagePosition.SINGLE -> 8.dp
            }
        ), bottomEnd = gap(
            when (position) {
                MessagePosition.FIRST -> 4.dp
                MessagePosition.LAST -> 8.dp
                MessagePosition.MIDDLE -> 4.dp
                MessagePosition.SINGLE -> 8.dp
            }, 16.dp
        )
    )

    Column {
        AnimatedVisibility(
            visible = showingTime.value,
            enter = slideInVertically() + expandVertically(expandFrom = Alignment.Top) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp)
            ) {
                Text(
                    msgTime.value.toFormatedString(),
                    style = TextStyle(fontSize = 10.sp, color = Color.Gray)
                )
            }
        }
        Row(
            modifier = Modifier.padding(
                end = gap(0.dp, 80.dp), start = gap(80.dp, 0.dp)
            ).fillMaxWidth(),
            horizontalArrangement = gap(Arrangement.End, Arrangement.Start),
            verticalAlignment = Alignment.Bottom,
        ) {
            MsgAvatar(
                owner,
                isShow = !isMe && owner != null && (position == MessagePosition.SINGLE || position == MessagePosition.LAST)
            )
            Box(
                Modifier.weight(1f).padding(vertical = 2.dp, horizontal = 8.dp),
                contentAlignment = gap(Alignment.CenterEnd, Alignment.CenterStart)
            ) {
                Text(
                    message.content,
                    style = TextStyle(
                        color = gap(Color.White, Color.Black)
                    ),
                    modifier = Modifier.pointerInput(message) {
                        detectTapGestures(
                            onTap = {
                                if (message.status.value == MessageStatus.ERROR) {
                                    onResend.invoke(message)
                                } else if (fixedDatetimeShowing.value || message.status.value == MessageStatus.SENDING) {
                                    return@detectTapGestures
                                } else if (!showingTime.value) {
                                    onShowingDate.invoke(message)
                                } else {
                                    onShowingDate.invoke(null)
                                }
                            },
                        )
                    }.background(
                        gap(
                            theme.ownerColor, theme.otherColor,
                        ).copy(alpha = if (message.status.value == MessageStatus.ERROR) 0.5f else 1f),
                        shape = messageShape
                    ).padding(9.dp),
                )
            }
            AnimatedVisibility(
                visible = message.status.value == MessageStatus.ERROR,
                enter = fadeIn(tween(durationMillis = 300)),
                exit = fadeOut(tween(durationMillis = 300))
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.warning),
                        contentDescription = "",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Red,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
        AnimatedVisibility(
            visible = showingTime.value && !fixedDatetimeShowing.value,
            enter = slideInVertically() + expandVertically(expandFrom = Alignment.Top) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    message.status.value.toContent(), style = TextStyle(
                        color = Color.Gray, fontSize = 10.sp
                    ), modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        MessageStatusComponent(message = message)
        Box(modifier = Modifier.height(if (nextMsg == null) 30.dp else 0.dp))
    }
}

@Composable
fun MsgAvatar(user: User?, isShow: Boolean) {
    Box(
        Modifier.padding(bottom = 2.dp, start = 10.dp)
    ) {
        if (isShow) {
            AsyncImage(
                model = user?.photoUrl,
                contentDescription = null,
                modifier = Modifier.size(30.dp).clip(CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.size(30.dp))
        }
    }
}

fun msgOwner(
    message: Message, group: Chat.GroupChat? = null, private: Chat.PrivateChat? = null
): User? {
    if (private != null) {
        return if (private.sender.uid == message.ownerId) {
            private.sender
        } else if (private.receiver.uid == message.ownerId) {
            private.receiver
        } else {
            null
        }
    } else if (group != null) {
        var user: User? = null
        group.users.forEach {
            if (it.uid == message.ownerId) {
                user = it
            }
        }
        return user
    } else {
        return null
    }
}

