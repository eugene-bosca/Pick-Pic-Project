package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.serializable.SerializableUUID
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserCreation(
    val firebase_id: SerializableUUID = SerializableUUID(UUID(0, 0)),
    val display_name: String = "",
    val email: String = "",
    val phone: String = "",
    val profile_picture: String = ""
)
