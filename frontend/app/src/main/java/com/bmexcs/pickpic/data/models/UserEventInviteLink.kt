package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserEventInviteLink(
    val invite_link: String
)
