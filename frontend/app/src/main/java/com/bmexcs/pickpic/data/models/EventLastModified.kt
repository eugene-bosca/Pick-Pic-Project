package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventLastModified(
    val last_modified: String = ""
)
