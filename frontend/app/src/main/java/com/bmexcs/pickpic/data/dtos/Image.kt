package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val image_id: String = "",
    val owner: User = User(),
    val file_name: String = "",
    val score: Long = 0
)
