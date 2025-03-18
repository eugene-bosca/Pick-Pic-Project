package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
class InvitedUser (
    val user: User,
    val accepted: Boolean
)