package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ImageOwner(
    val image_id: String = "",
    val file_name: String = "",
    val score : Int = 0
)
