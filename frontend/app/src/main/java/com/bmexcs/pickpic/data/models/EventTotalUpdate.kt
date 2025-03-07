package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventTotalUpdate(
    val event: EventNameOwnerUpdate = EventNameOwnerUpdate(),
    val image: ImageCreation = ImageCreation()
)
