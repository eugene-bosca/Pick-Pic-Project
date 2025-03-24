package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.dtos.User

data class UserMetadata(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePicture: String = "",
) {
    constructor(user: User) : this(
        id = user.user_id,
        name = user.display_name,
        email = user.email,
        phone = user.phone,
        profilePicture = user.profile_picture
    )
}
