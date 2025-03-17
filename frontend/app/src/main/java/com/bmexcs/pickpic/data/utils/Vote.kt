package com.bmexcs.pickpic.data.utils

enum class Vote(private val id: String) {
    UPVOTE("upvote"),
    DOWNVOTE("downvote");

    override fun toString(): String = id
}
