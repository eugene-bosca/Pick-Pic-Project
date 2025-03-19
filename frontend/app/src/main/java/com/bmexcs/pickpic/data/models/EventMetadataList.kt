package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.dtos.UserEventList

data class EventMetadataList(
    val ownedBy: List<EventMetadata> = listOf(),
    val invitedTo: List<EventMetadata> = listOf()
) {
    constructor(userEventList: UserEventList) : this(
        ownedBy = userEventList.owned_events.map { EventMetadata(it) },
        invitedTo = userEventList.invited_events.map { EventMetadata(it) }
    )
}
