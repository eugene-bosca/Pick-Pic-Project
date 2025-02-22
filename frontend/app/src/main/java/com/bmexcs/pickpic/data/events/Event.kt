package com.bmexcs.pickpic.data.events

// TODO: make better lol
data class Event(
    val id: String = "",
    val userIds: List<String> = listOf(""),
    val ownerId: String = "",
    val imageIds: List<String> = listOf("")
)
