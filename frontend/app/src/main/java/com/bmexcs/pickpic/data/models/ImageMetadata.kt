package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.dtos.Image

data class ImageMetadata(
    val id: String = "",
    val uploader: UserMetadata = UserMetadata(),
    val score: Long = 0,
    val dateUploaded: String = "",
) {
    constructor(imageInfo: Image) : this(
        id = imageInfo.image_id,
        uploader = UserMetadata(imageInfo.owner),
        score = imageInfo.score,
        dateUploaded = imageInfo.file_name.substringBeforeLast('.')
    )
}
