package com.bmexcs.pickpic.data.models

enum class VoteKind(private val id: String) {
    UPVOTE("upvote"),
    DOWNVOTE("downvote");

    override fun toString(): String = id
}
