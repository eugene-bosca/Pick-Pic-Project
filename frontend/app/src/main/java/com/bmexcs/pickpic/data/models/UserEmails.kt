package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserEmails(
    val emails: List<String> = listOf()
)
