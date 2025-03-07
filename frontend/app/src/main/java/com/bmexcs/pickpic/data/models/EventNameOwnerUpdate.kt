package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventNameOwnerUpdate(
    val event_name: String = "",
    val owner: String = ""
)
