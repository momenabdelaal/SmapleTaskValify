package com.valify.registrationsdk.domain.model

data class UserRegistration(
    val id: Long = 0,
    val username: String,
    val email: String,
    val phone: String,
    val password: String,
    val deviceId: String,
    val selfieImagePath: String? = null
)
