package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserEmails(
    val emails: List<String> = listOf()
)
