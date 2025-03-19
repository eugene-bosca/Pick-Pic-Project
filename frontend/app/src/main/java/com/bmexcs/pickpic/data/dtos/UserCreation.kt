package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserCreation(
    val firebase_id: String = "",
    val display_name: String = "",
    val email: String = "",
    val phone: String = "",
    val profile_picture: String = ""
)
