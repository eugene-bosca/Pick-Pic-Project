package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventItem(
    val id: Int,
    val event: EventInfo,
    val user: User
)

@Serializable
data class EventInfo(
    val event: String,
    val owner_id: Owner
)

@Serializable
data class Owner(
    val user_id: String,
    val display_name: String,
    val email: String,
    val phone: String,
    val profile_picture: String
)

@Serializable
data class User(
    val user_id: String,
    val display_name: String,
    val email: String,
    val phone: String,
    val profile_picture: String
)