package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.dtos.EventInfo

data class EventMetadata(
    val id: String = "",
    val name: String = "",
    val owner: UserMetadata = UserMetadata(),
    val lastModified: String = "",
) {
    constructor(eventInfo: EventInfo) : this(
        id = eventInfo.event_id,
        name = eventInfo.event_name,
        owner = UserMetadata(eventInfo.owner),
        lastModified = eventInfo.last_modified
    )
}
