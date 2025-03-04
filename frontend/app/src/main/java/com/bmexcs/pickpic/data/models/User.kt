package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user_id: String = "",
    val firebase_id: String = "",
    val display_name: String = "",
    val email: String = "",
    val phone: String = "",
    val profile_picture: String = ""
)
