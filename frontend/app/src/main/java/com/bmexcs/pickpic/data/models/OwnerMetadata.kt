package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.dtos.Owner

data class OwnerMetadata (
    val id: String = "",
    val name: String = "",
    val email: String = "",
) {
    constructor(owner: Owner): this(
        id = owner.user_id,
        name = owner.display_name,
        email = owner.email
    )
}
