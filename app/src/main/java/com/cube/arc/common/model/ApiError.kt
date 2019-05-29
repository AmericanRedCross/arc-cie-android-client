package com.cube.arc.common.model

import com.google.gson.annotations.SerializedName

data class ApiError(@SerializedName("status") val _status: Int,
                    @SerializedName("message") val _message: String?) {
    val status get() = _status
    val message get() = _message
}