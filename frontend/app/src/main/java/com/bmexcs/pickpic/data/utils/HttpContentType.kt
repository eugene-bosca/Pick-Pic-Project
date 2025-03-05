package com.bmexcs.pickpic.data.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

enum class HttpContentType {
    JSON {
        override fun toMediaType() = "application/json".toMediaType()
    },

    OCTET_STREAM {
        override fun toMediaType() = "application/octet-stream".toMediaType()
    };

    abstract fun toMediaType(): MediaType
}
