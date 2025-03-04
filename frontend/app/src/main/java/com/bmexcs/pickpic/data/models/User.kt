package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable
import com.bmexcs.pickpic.data.serializable.SerializableUUID
import java.util.UUID

@Serializable
data class User(
    val user_id: SerializableUUID = SerializableUUID(UUID(0, 0)),
    val firebase_id: String = "",
    val display_name: String = "",
    val email: String = "",
    val phone: String = "",
    val profile_picture: String = ""
)
