package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventContent(
    val event: Event = Event(),
    val image: Image = Image()
)
