package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ImageVote(
    val user_id: String = "",
    val vote: String = "upvote",
)
