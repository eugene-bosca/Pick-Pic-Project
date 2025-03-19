package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserEventInviteLink(
    val invite_link: String
)
