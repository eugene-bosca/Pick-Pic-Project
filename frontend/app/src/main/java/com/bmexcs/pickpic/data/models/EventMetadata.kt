package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.dtos.Event

data class EventMetadata(
    val id: String = "",
    val name: String = "",
    val owner: OwnerMetadata = OwnerMetadata(),
    val lastModified: String = "",
) {
    constructor(eventInfo: Event) : this(
        id = eventInfo.event_id,
        name = eventInfo.event_name,
        owner = OwnerMetadata(eventInfo.owner),
        lastModified = eventInfo.last_modified
    )
}
