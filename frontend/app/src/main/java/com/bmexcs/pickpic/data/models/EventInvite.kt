package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventInvite(
    val event_id: String,
    val user_id: String
)
