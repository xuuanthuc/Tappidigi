package xt.qc.tappidigi.models

import com.benasher44.uuid.uuid4
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String = uuid4().toString(),
    val content: String? = null,
)