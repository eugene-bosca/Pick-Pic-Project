package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserEventList(
    val owned_events: List<EventInfo> = listOf(),
    val invited_events: List<EventInfo> = listOf()
)
