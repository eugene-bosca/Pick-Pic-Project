package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class EventCreation(
    val user_id: String = "",
    val event_name: String = ""
)
