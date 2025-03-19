package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.dtos.InvitedUser
import com.bmexcs.pickpic.data.dtos.User

data class UserMetadata(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePicture: String = "",
    val accepted: Boolean = false,
) {
    constructor(user: User) : this(
        id = user.user_id,
        name = user.display_name,
        email = user.email,
        phone = user.phone,
        profilePicture = user.profile_picture,
        accepted = false
    )

    constructor(user: InvitedUser) : this(
        id = user.user.user_id,
        name = user.user.display_name,
        email = user.user.email,
        phone = user.user.phone,
        profilePicture = user.user.profile_picture,
        accepted = user.accepted
    )
}
