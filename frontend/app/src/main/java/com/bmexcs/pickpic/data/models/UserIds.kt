package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserIds(
    val users: List<String> = listOf()
)
