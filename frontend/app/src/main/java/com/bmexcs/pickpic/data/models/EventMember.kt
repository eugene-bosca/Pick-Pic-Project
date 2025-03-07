package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventMember(
    val id: Long = 0,
    val event: EventIdName = EventIdName(),
    val user: User = User(),
    val accepted: Boolean = false
)
