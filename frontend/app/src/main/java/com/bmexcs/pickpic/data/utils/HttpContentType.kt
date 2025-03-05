package com.bmexcs.pickpic.data.utils

import okhttp3.MediaType.Companion.toMediaType

enum class HttpContentType {
    JSON {
        override fun toString() = "application/json"
    },

    OCTET_STREAM {
        override fun toString() = "application/octet-stream"
    },

    PNG {
        override fun toString() = "image/png"
    },

    JPEG {
        override fun toString() = "image/jpeg"
    };

    abstract override fun toString(): String
    fun toMediaType() = toString().toMediaType()
}
