package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val image_id: String = "",
    val user: User = User(),
    val file_name: String = "",
    val score: Long = 0
)
