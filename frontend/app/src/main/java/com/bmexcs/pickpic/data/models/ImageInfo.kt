package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ImageInfo(
    val id: Long = 0,
    val image: Image = Image()
)
