package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventCreation(
    val event_name: String = "",
    val owner: String = ""
)
