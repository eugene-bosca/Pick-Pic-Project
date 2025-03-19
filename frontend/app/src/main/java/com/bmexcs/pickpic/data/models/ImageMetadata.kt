package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.dtos.ImageInfo

data class ImageMetadata(
    val id: String = "",
    val uploader: UserMetadata = UserMetadata(),
    val score: Long = 0,
    val dateUploaded: String = "",
) {
    constructor(imageInfo: ImageInfo) : this(
        id = imageInfo.image.image_id,
        uploader = UserMetadata(imageInfo.image.user),
        score = imageInfo.image.score,
        dateUploaded = imageInfo.image.file_name.substringBeforeLast('.')
    )
}
