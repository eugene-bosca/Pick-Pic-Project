package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class EventLastModified(
    val last_modified: String = ""
)
