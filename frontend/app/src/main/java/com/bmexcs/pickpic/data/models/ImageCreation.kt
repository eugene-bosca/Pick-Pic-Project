package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ImageCreation(
    val file_name: String = "",
    val score: Long = 0
)
