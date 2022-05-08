package com.hadimas.distories.preferences

data class DataLoginModel(
    var userId: String? = null,
    var name: String? = null,
    var token: String? = null,
    var isLogin: Boolean = false
)
