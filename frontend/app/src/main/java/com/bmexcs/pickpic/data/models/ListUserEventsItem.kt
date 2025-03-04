package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

// Data type for response from list-user-events
@Serializable
data class ListUserEventsItem(
    val event_id: String = "",
    val name: String = "",
    val owner: String = "",
    val pfp_path: String = ""
)
