package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Owner(
    val user_id: String = "",
    val display_name: String = "",
    val email: String = ""
)
