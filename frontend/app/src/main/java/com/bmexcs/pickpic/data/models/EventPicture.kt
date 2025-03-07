package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventPicture(
    val event: EventOwner = EventOwner(),
    val image: ImageOwner = ImageOwner()
)
