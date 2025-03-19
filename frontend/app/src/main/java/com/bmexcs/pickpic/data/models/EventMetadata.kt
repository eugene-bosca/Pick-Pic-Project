package com.bmexcs.pickpic.data.models

data class EventMetadata(
    val id: String = "",
    val name: String = "",
    val owner: UserMetadata = UserMetadata(),
    val lastModified: String = "",
)
