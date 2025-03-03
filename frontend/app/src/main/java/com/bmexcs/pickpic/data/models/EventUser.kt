package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventUser (
    val event: Event = Event(),
    val user: User = User()
)
