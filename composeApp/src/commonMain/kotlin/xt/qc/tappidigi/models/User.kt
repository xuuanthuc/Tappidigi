package xt.qc.tappidigi.models

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class User(
    val uid: String? = null,
    val email: String? = null,
    val username: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val bio: String? = null,
) : Parcelable

