package com.bmexcs.pickpic.data.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

enum class HttpContentType {
    JSON {
        override fun toString() = "application/json"
        override fun toMediaType() = toString().toMediaType()
    },

    OCTET_STREAM {
        override fun toString() = "application/octet-stream"
        override fun toMediaType() = toString().toMediaType()
    };

    abstract override fun toString(): String
    abstract fun toMediaType(): MediaType
}
