package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ImageVote(
    val user_id: String = "",
    val vote: String = "upvote",
)
