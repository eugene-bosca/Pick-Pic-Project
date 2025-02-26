package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable
import com.bmexcs.pickpic.data.serializable.SerializableUUID
import java.util.UUID

@Serializable
data class Profile(
    val id: SerializableUUID = SerializableUUID(UUID.fromString("")),
    val displayName: String = "",
    val email: String = "", // TODO: use Email class?
    val phone: String = "", // TODO: user Phone class?
    val profilePictureId: SerializableUUID = SerializableUUID(UUID.fromString(""))
)
