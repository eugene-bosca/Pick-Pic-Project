package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable
import com.bmexcs.pickpic.data.serializable.SerializableUUID
import java.util.UUID

@Serializable
data class User(
    val id: SerializableUUID = SerializableUUID(UUID(0, 0)),
    val displayName: String = "",
    val email: String = "", // TODO: use Email class?
    val phone: String = "", // TODO: use Phone class?
    val profilePictureId: SerializableUUID = SerializableUUID(UUID(0, 0))
)
