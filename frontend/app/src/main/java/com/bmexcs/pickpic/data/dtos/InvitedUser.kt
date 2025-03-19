package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
class InvitedUser (
    val user: User,
    val accepted: Boolean
)
