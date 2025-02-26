package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable
import com.bmexcs.pickpic.data.serializable.SerializableUUID

@Serializable
data class Profile(
    val id: SerializableUUID,
    val displayName: String = "",
    val email: String = "", // TODO: use Email class?
    val phone: String = "", // TODO: user Phone class?
    val profilePictureId: SerializableUUID
)
