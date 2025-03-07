package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserEventList(
    val owned_events: List<EventOwner> = listOf(),
    val invited_events: List<EventOwner> = listOf()
)
