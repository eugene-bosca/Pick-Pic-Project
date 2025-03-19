package com.bmexcs.pickpic.data.models

data class ImageMetadata(
    val id: String = "",
    val uploader: UserMetadata = UserMetadata(),
    val score: Long = 0
)
